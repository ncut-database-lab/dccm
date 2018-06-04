package cn.ncut.service.wechat.userStoredCard.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.ncut.dao.DaoSupport;
import cn.ncut.entity.wechat.pojo.WeChatUserStoredCard;
import cn.ncut.service.wechat.userStoredCard.WeChatUserStoredCardManager;

@Service(value = "weChatUserStoredCardService")
public class WeChatUserStoredCardService implements WeChatUserStoredCardManager {
    @Resource(name = "daoSupport")
    private DaoSupport dao;

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
}
