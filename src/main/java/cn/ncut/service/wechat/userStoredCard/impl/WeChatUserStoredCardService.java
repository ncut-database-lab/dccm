package cn.ncut.service.wechat.userStoredCard.impl;

import javax.annotation.Resource;

import cn.ncut.entity.wechat.pojo.WeChatStoredDetailCustom;
import cn.ncut.util.wechat.PrimaryKeyGenerator;
import org.springframework.stereotype.Service;

import cn.ncut.dao.DaoSupport;
import cn.ncut.entity.wechat.pojo.WeChatUserStoredCard;
import cn.ncut.service.wechat.userStoredCard.WeChatUserStoredCardManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(value = "weChatUserStoredCardService")
public class WeChatUserStoredCardService implements WeChatUserStoredCardManager {
    @Resource(name = "daoSupport")
    private DaoSupport dao;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public WeChatUserStoredCard getUserStoredCardByUid(Integer uId)
            throws Exception {
        return (WeChatUserStoredCard) dao.findForObject("WeChatUserStoredCardMapper.getUserStoredCardByUid", uId);
    }

    @Override
    public void createNewUserStoredCard(
            WeChatUserStoredCard weChatUserStoredCard) throws Exception {
        dao.save("WeChatUserStoredCardMapper.createNewUserStoredCard", weChatUserStoredCard);
    }

    @Override
    public void updateUserStoredCardPassword(
            WeChatUserStoredCard weChatUserStoredCard) throws Exception {
        dao.update("WeChatUserStoredCardMapper.updateUserStoredCardPassword", weChatUserStoredCard);
    }

    @Override
    public void updateUserStoredCardPhoneAndName(
            WeChatUserStoredCard weChatUserStoredCard) throws Exception {
        dao.update("WeChatUserStoredCardMapper.updateUserStoredCardPhoneAndName", weChatUserStoredCard);
    }

    @Override
    public void updatePasswordOfAllCreditCard(WeChatUserStoredCard card) throws Exception {
        dao.update("WeChatUserStoredCardMapper.updatePasswordOfAllCreditCard", card);
    }

    @Override
    public WeChatUserStoredCard existOfCreditCardOfUserByUid(Integer uId) throws Exception {
        return (WeChatUserStoredCard) dao.findForObject(
                "WeChatUserStoredCardMapper.existOfCreditCardOfUserByUid", uId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean countCzkMoney(int uId, double money, String storeId, String staffId, Map<String, Object> extension)
            throws Exception {
        // 根据用户的储值卡余额与返点计算扣款
        WeChatUserStoredCard moneyOfCard = (WeChatUserStoredCard) dao.findForObject("WeChatUserStoredCardMapper." +
                "getUserAssetsOfCreditCard", uId);

        // 待支付金额
        BigDecimal prePayMoney = new BigDecimal(money);

        // 有效的储值卡金额
        
        System.out.println(moneyOfCard.getRemainMoney());
        System.out.println("*************************************************");
        System.out.println(moneyOfCard.getRemainPoints());
        System.out.println("*************************************************");
        
        BigDecimal validMoney = moneyOfCard.getRemainMoney().add(moneyOfCard.getRemainPoints());

        if (validMoney.compareTo(prePayMoney) < 0) {
            // 储值卡有效余额与返点不足
            return false;
        }

        // 用户有效的储值记录
        List<WeChatUserStoredCard> cards = (List<WeChatUserStoredCard>) dao.findForList(
                "WeChatUserStoredCardMapper.queryCreditCardOfUserByUid", uId);

        final BigDecimal zero = new BigDecimal("0.00");

        WeChatStoredDetailCustom weChatStoredDetailCustom = new WeChatStoredDetailCustom();

        // 根据订单金额计算每张储值卡的使用情况
        int index = 0;
        for (WeChatUserStoredCard card : cards) {
            index++;
            BigDecimal assets = card.getRemainMoney().add(card.getRemainPoints());
            if (assets.compareTo(prePayMoney) < 0) {

                if (weChatStoredDetailCustom.getMoney() == null) {
                    weChatStoredDetailCustom.setMoney(card.getRemainMoney());
                } else {
                    weChatStoredDetailCustom.setMoney(weChatStoredDetailCustom.getMoney().add(card.
                            getRemainMoney()));
                }

                if (weChatStoredDetailCustom.getPoints() == null) {
                    weChatStoredDetailCustom.setPoints(card.getRemainPoints());
                } else {
                    weChatStoredDetailCustom.setPoints(weChatStoredDetailCustom.getPoints().add(card.
                            getRemainPoints()));
                }

                // 当前储值卡余额与返点不足以支付订单金额,储值卡状态设置为1
                card.setStatus(1);
                card.setRemainMoney(zero);
                card.setRemainPoints(zero);

                prePayMoney = prePayMoney.subtract(assets);
            } else if (assets.compareTo(prePayMoney) >= 0) {
                // 当前储值卡足够支付订单金额,计算余额与返点需要扣多少
                if (card.getRemainMoney().compareTo(prePayMoney) >= 0) {
                    if (weChatStoredDetailCustom.getMoney() == null) {
                        weChatStoredDetailCustom.setMoney(prePayMoney);
                    } else {
                        weChatStoredDetailCustom.setMoney(weChatStoredDetailCustom.getMoney().add(prePayMoney));
                    }
                    if (weChatStoredDetailCustom.getPoints() == null) {
                        weChatStoredDetailCustom.setPoints(zero);
                    }
                    card.setRemainMoney(card.getRemainMoney().subtract(prePayMoney));
                } else {
                    if (weChatStoredDetailCustom.getMoney() == null) {
                        weChatStoredDetailCustom.setMoney(card.getRemainMoney());
                    } else {
                        weChatStoredDetailCustom.setMoney(weChatStoredDetailCustom.getMoney().
                                add(card.getRemainMoney()));
                    }
                    prePayMoney = prePayMoney.subtract(card.getRemainMoney());
                    card.setRemainMoney(zero);
                    if (weChatStoredDetailCustom.getPoints() == null) {
                        weChatStoredDetailCustom.setPoints(card.getRemainPoints().subtract(prePayMoney));
                    } else {
                        weChatStoredDetailCustom.setPoints(weChatStoredDetailCustom.getPoints().add(prePayMoney));
                    }
                    card.setRemainPoints(card.getRemainPoints().subtract(prePayMoney));
                }
                if (card.getRemainPoints().compareTo(zero) == 0) {
                    card.setStatus(1);
                }
                break;
            }
        }

        weChatStoredDetailCustom.setStoredDetailId("OC" + PrimaryKeyGenerator.generateKey());
        // 线上消费
        weChatStoredDetailCustom.setType(5);
        // 已支付
        weChatStoredDetailCustom.setStatus(0);
        weChatStoredDetailCustom.setCreateTime(sdf.format(new Date()));
        weChatStoredDetailCustom.setStoreId(storeId);
        weChatStoredDetailCustom.setuId(uId);
        weChatStoredDetailCustom.setStaffId(staffId);

        // 持久化储储值明细
        dao.save("WeChatStoredDetailCustomMapper.saveStoredDetailCustom", weChatStoredDetailCustom);

        // 持久化储值卡余额与返点变更
        for (int i = 0; i < index; i++) {
            dao.update("WeChatUserStoredCardMapper.updateUserCreditCardByCardId", cards.get(i));
        }
        return true;
    }
}
