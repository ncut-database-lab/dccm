package cn.ncut.service.wechat.order.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.ncut.dao.DaoSupport;
import cn.ncut.dao.wechat.WeChatJsApiPayOrderDao;
import cn.ncut.entity.wechat.pojo.PayErrorMsg;
import cn.ncut.entity.wechat.pojo.PayH5;
import cn.ncut.entity.wechat.pojo.PayResultNotify;
import cn.ncut.entity.wechat.pojo.PayResultNotifyResponse;
import cn.ncut.entity.wechat.pojo.PayUnifiedOrder;
import cn.ncut.entity.wechat.pojo.PayUnifiedOrderBack;
import cn.ncut.entity.wechat.pojo.WeChatOrder;
import cn.ncut.service.wechat.order.WeChatJsApiPayOrderManager;
import cn.ncut.util.wechat.RandomUtil;
import cn.ncut.util.wechat.SignGenerateAndCheckUtil;
import cn.ncut.util.wechat.SpringPropertyResourceReader;
import cn.ncut.util.wechat.TypeConvertUtil;

@Service("weChatJsApiPayOrderService")
public class WeChatJsApiPayOrderService implements WeChatJsApiPayOrderManager {
    //private static Logger logger = LoggerFactory.getLogger(WeChatJsApiPayOrderService.class);
    private static Logger logger = LoggerFactory.getLogger("weChatService");
    @Resource(name = "weChatJsApiPayOrderDaoImpl")
    private WeChatJsApiPayOrderDao weChatJsApiPayOrderDaoImpl;

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    /**
     * 构造统一下单bean,访问微信接口,构造PayH5
     *
     * @param payUnifiedOrder 统一下单bean
     * @return 返回的PayH5
     */
    @Override
    public PayH5 payUnifiedOrderByJsApi(PayUnifiedOrder payUnifiedOrder, String orderId)
            throws Exception {
        /**
         * 设置payUnifiedOrder固定参数
         * */
        payUnifiedOrder.setAppid(SpringPropertyResourceReader.getProperty("appId"));
        payUnifiedOrder.setMch_id(SpringPropertyResourceReader.getProperty("mch_id"));
        payUnifiedOrder.setDevice_info("WEB");
        String nonceStr = RandomUtil.generateMixedCharAndDigit(32);
        payUnifiedOrder.setNonce_str(nonceStr);
        payUnifiedOrder.setFee_type("CNY");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        // 订单有效时间
        Date date = new Date();
        String time_start = simpleDateFormat.format(date.getTime());
        int order_time_space = Integer.parseInt(SpringPropertyResourceReader.getProperty("order_time_space"));
        String time_expire = simpleDateFormat.format(date.getTime() + order_time_space);

        payUnifiedOrder.setTime_start(time_start);
        payUnifiedOrder.setTime_expire(time_expire);
        payUnifiedOrder.setTrade_type("JSAPI");

        // 生成sign;根据payUnifiedOrder中属性信息按照一定规则生成
        TreeMap<Object, Object> sortMap = SignGenerateAndCheckUtil
                .sortBeanAttributes(payUnifiedOrder);
        String sign = SignGenerateAndCheckUtil.createSign("UTF-8", sortMap);
        payUnifiedOrder.setSign(sign);
        logger.debug("--- start 统一下单微信请求参数 payUnifiedOrder ---");
        logger.debug(payUnifiedOrder.toString());
        logger.debug("--- end 统一下单微信请求参数 payUnifiedOrder ---");

        // 访问微信统一下单接口,暂时存放在这
        String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        PayUnifiedOrderBack payUnifiedOrderBack = TypeConvertUtil
                .transformBeanToBeanFactory(requestUrl, payUnifiedOrder, PayUnifiedOrderBack.class);
        logger.debug("--- start 统一下单微信返回参数 payUnifiedOrderBack ---");
        logger.debug(payUnifiedOrderBack.toString());
        logger.debug("--- end 统一下单微信返回参数 payUnifiedOrderBack ---");

        // 检测统一下单微信返回参数
        if ("SUCCESS".equals(payUnifiedOrderBack.getReturn_code())) {
            logger.debug("--- 通讯成功 ---");
            if ("SUCCESS".equals(payUnifiedOrderBack.getResult_code())) {
                // 签名认证
                String backSign = SignGenerateAndCheckUtil.createSign("UTF-8",
                        SignGenerateAndCheckUtil
                                .sortBeanAttributes(payUnifiedOrderBack));
                if (payUnifiedOrderBack.getSign().equals(backSign)) {
                    logger.debug("--- 微信统一下单sign签名验证成功 ---");

                    // 构造PayH5
                    logger.debug("--- 构造PayH5 ---");
                    PayH5 payH5 = new PayH5();
                    payH5.setAppId(payUnifiedOrderBack.getAppid());
                    payH5.setNonceStr(RandomUtil.generateMixedCharAndDigit(32));
                    payH5.setPayPackage(payUnifiedOrderBack.getPrepay_id());
                    payH5.setSignType("MD5");
                    payH5.setTimeStamp(Long.toString((new Date().getTime()) / 1000));
                    String generateSign = SignGenerateAndCheckUtil.createSign("UTF-8",
                            SignGenerateAndCheckUtil.sortBeanAttributes(payH5));
                    payH5.setPaySign(generateSign);
                    logger.debug("--- 检测PayH5参数信息 ---" + payH5);
                    return payH5;
                }
            } else {
                logger.debug("--- 统一下单错误,错误信息如下 ---");
                logger.debug("--- err_code ---" + payUnifiedOrderBack.getErr_code());
                logger.debug("--- err_code_des ---" + payUnifiedOrderBack.getErr_code_des());

                PayErrorMsg payErrorMsg = new PayErrorMsg();
                payErrorMsg.setErrorCode(payUnifiedOrderBack.getErr_code());
                payErrorMsg.setErrorDes(payUnifiedOrderBack.getErr_code_des());

                // 若当前的错误是商户订单号重复,则将该订单的状态设置为"1",即已关闭,不再允许用户支付
                WeChatOrder weChatOrder = new WeChatOrder();
                weChatOrder.setOrderId(orderId);
                weChatOrder = (WeChatOrder) dao.findForObject("WeChatOrderMapper.getOrderByOrderId", orderId);
                int orderStatus = weChatOrder.getOrderStatus();
                // 防止用户返回时,再次支付之前的订单导致订单关闭,其实用户已经支付过了
                if (orderStatus == 0) {
                    weChatOrder.setOrderStatus(1);
                    logger.debug("--- 更新订单的状态为已关闭,防止用户再支付 ---");

                    dao.update("WeChatOrderMapper.batchUpdateOrderStatus", weChatOrder);
                }

                logger.debug("--- 返回统一下单错误信息 ---" + payErrorMsg.toString());
                return payErrorMsg;
            }
        } else {
            logger.debug("--- 通讯失败,失败原因是 ---" + payUnifiedOrderBack.getReturn_msg());
            PayErrorMsg payErrorMsg = new PayErrorMsg();
            payErrorMsg.setErrorCode(payUnifiedOrderBack.getErr_code());
            payErrorMsg.setErrorDes(payUnifiedOrderBack.getReturn_msg());
            logger.debug("--- 返回统一下单错误信息 ---" + payErrorMsg.toString());
            return payErrorMsg;
        }
        return null;
    }

    /**
     * 处理微信通知
     *
     * @param payResultNotify 微信通知bean
     * @return 返回给微信端的状态信息, 成功或者失败
     * @throws Exception
     */
    public String processOrderNotify(PayResultNotify payResultNotify) throws Exception {
        PayResultNotifyResponse payResultNotifyResponse = new PayResultNotifyResponse();
        if ("SUCCESS".equals(payResultNotify.getReturn_code())) {
            logger.debug("--- 成功接收到微信通知 ---");
            if ("SUCCESS".equals(payResultNotify.getResult_code())) {
                logger.debug("--- 用户已成功付款 ---");
                // 签名认证
                String backSign = SignGenerateAndCheckUtil.createSign("UTF-8",
                        SignGenerateAndCheckUtil
                                .sortBeanAttributes(payResultNotify));
                if (backSign.equals(payResultNotify.getSign())) {
                    logger.debug("--- 微信通知签名验证成功 ---");

                    // 事务控制,当前操作要么全部成功,要么全部回滚
                    logger.debug("--- start updateOrderAndAppointAndPayDetail() ---");

                    this.weChatJsApiPayOrderDaoImpl.processOrderNotifyUsedTransaction(payResultNotify);

                    logger.debug("--- end updateOrderAndAppointAndPayDetail() ---");

                    payResultNotifyResponse.setReturn_code("SUCCESS");
                    payResultNotifyResponse.setReturn_msg("OK");
                } else {
                    logger.debug("--- 微信通知签名验证失败 ---");
                    // 签名失败时的业务操作
                    payResultNotifyResponse.setReturn_code("FAIL");
                    payResultNotifyResponse.setReturn_msg("SIGNERROR");
                }
            } else {
                logger.debug("--- 用户付款失败 ---");
                payResultNotifyResponse.setReturn_code("FAIL");
                payResultNotifyResponse.setReturn_msg("SIGNERROR");
            }
        } else {
            logger.debug("--- 接收微信通知失败,失败原因如下---");
            logger.debug("--- " + payResultNotify.getReturn_msg() + " ---");
            payResultNotifyResponse.setReturn_code("FAIL");
            payResultNotifyResponse.setReturn_msg("SIGNERROR");
        }
        return TypeConvertUtil.transformBeanToXml(payResultNotifyResponse);
    }

    @Override
    public String storedCardPayOrder(Map<String, Object> map) throws Exception {
        return this.weChatJsApiPayOrderDaoImpl.storedCardPayOrder(map);
    }

    @Override
    public String payOfStoredCard(Map<String, Object> map) throws Exception {
        return this.weChatJsApiPayOrderDaoImpl.payOfStoredCard(map);
    }
}