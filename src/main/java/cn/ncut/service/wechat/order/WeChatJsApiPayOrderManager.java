package cn.ncut.service.wechat.order;

import java.util.Map;

import cn.ncut.entity.wechat.pojo.PayH5;
import cn.ncut.entity.wechat.pojo.PayResultNotify;
import cn.ncut.entity.wechat.pojo.PayUnifiedOrder;

/**
 * 公众号网页下单
 */
public interface WeChatJsApiPayOrderManager {
    /**
     * 构造统一下单bean,访问微信接口,构造PayH5
     *
     * @param payUnifiedOrder 统一下单bean
     * @return 返回的PayH5
     */
    public abstract PayH5 payUnifiedOrderByJsApi(PayUnifiedOrder payUnifiedOrder, String orderId) throws Exception;

    /**
     * 处理微信通知
     *
     * @param payResultNotify 微信通知bean
     * @return 返回给微信端的状态信息, 成功或者失败
     * @throws Exception
     */
    public String processOrderNotify(PayResultNotify payResultNotify) throws Exception;

    /**
     * 储值卡支付
     *
     * @param 订单,优惠券,实付金额,支付方式
     * @return 返回给前端页面的消息
     * @throws Exception
     */
    public String storedCardPayOrder(Map<String, Object> map) throws Exception;

    /**
     * 储值卡支付
     *  原因:为避免在储值卡支付方法上直接修改,重新编写储值卡支付
     *  根本原因:储值卡支付方式改变导致逻辑修改,为避免修改代码带来各种问题,此处新建方法
     *
     * @param map 参数信息,包含订单号,优惠券,实付金额,支付方式,当前用户
     * @return 支付状态
     * @throws Exception 异常
     */
    String payOfStoredCard(Map<String, Object> map) throws Exception;
}
