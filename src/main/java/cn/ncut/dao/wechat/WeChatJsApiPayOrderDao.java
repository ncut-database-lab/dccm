package cn.ncut.dao.wechat;

import java.util.Map;

import cn.ncut.entity.wechat.pojo.PayResultNotify;

public interface WeChatJsApiPayOrderDao {
    /**
     * 处理微信通知，使用事务控制几张表之间的DML语句
     * 若用户使用优惠券，则更新优惠券信息
     * 修改订单状态
     * 往预约表插入一条记录
     * 往支付明细表插入一条记录
     *
     * @param payResultNotify 微信返回的通知
     */
    public void processOrderNotifyUsedTransaction(PayResultNotify payResultNotify);

    public String storedCardPayOrder(Map<String, Object> map);

    String payOfStoredCard(Map<String, Object> map) throws Exception;
}
