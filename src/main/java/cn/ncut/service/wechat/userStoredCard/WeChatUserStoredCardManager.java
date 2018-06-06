package cn.ncut.service.wechat.userStoredCard;

import cn.ncut.entity.wechat.pojo.WeChatUserStoredCard;

import java.util.Map;

public interface WeChatUserStoredCardManager {
    /**
     * 根据uId获取用户储值账户信息
     *
     * @param uId
     * @return weChatUserStoredCard
     * @throws Exception
     */
    public abstract WeChatUserStoredCard getUserStoredCardByUid(Integer uId) throws Exception;

    /**
     * 创建一个新的储值账户
     *
     * @param weChatUserStoredCard
     * @throws Exception
     */
    public abstract void createNewUserStoredCard(WeChatUserStoredCard weChatUserStoredCard) throws Exception;

    /**
     * 根据uId更新储值账户的密码信息
     *
     * @param weChatUserStoredCard
     * @throws Exception
     */
    public abstract void updateUserStoredCardPassword(WeChatUserStoredCard weChatUserStoredCard) throws Exception;

    /**
     * 根据uId更新储值账户的手机号,密码信息
     *
     * @param weChatUserStoredCard
     * @throws Exception
     */
    public abstract void updateUserStoredCardPhoneAndName(WeChatUserStoredCard weChatUserStoredCard) throws Exception;

    /**
     * 根据uId更新某用户所有的储值卡密码信息
     *
     * @param card 储值卡等信息
     * @throws Exception 数据操作异常
     */
    void updatePasswordOfAllCreditCard(WeChatUserStoredCard card) throws Exception;

    /**
     * 根据uId查询某用户是否至少购买了一张储值卡
     * @param uId 用户编号
     * @return 储值信息
     * @throws Exception 数据操作异常
     */
    WeChatUserStoredCard existOfCreditCardOfUserByUid(Integer uId) throws Exception;

    /**
     * 对外接口, 扣除储值卡余额与返点
     *
     * @param uId 用户编号
     * @param money 支付金额
     * @param storeId 门店编号
     * @param staffId 员工编号
     * @param extension 扩展
     * @throws Exception 数据操作异常
     */
    boolean countCzkMoney(int uId, double money, String storeId, String staffId, Map<String, Object>
            extension) throws Exception;
}
