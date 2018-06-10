package cn.ncut.service.wechat.card.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import cn.ncut.dao.DaoSupport;
import cn.ncut.dao.wechat.impl.WeChatJsApiPayOrderDaoImpl;
import cn.ncut.entity.wechat.pojo.PayH5;
import cn.ncut.entity.wechat.pojo.PayResultNotify;
import cn.ncut.entity.wechat.pojo.PayUnifiedOrder;
import cn.ncut.entity.wechat.pojo.PayUnifiedOrderBack;
import cn.ncut.entity.wechat.pojo.WeChatPayHistory;
import cn.ncut.entity.wechat.pojo.WeChatStored;
import cn.ncut.entity.wechat.pojo.WeChatStoredDetail;
import cn.ncut.entity.wechat.pojo.WeChatUser;
import cn.ncut.service.wechat.card.WeChatCardManager;
import cn.ncut.service.wechat.user.WeChatUserManager;
import cn.ncut.util.PageData;
import cn.ncut.util.wechat.MD5Util;
import cn.ncut.util.wechat.RandomUtil;
import cn.ncut.util.wechat.SignGenerateAndCheckUtil;
import cn.ncut.util.wechat.SpringPropertyResourceReader;
import cn.ncut.util.wechat.TypeConvertUtil;

@Service("weChatCardService")
public class WeChatCardService implements WeChatCardManager {
	private static Logger logger = LoggerFactory
			.getLogger(WeChatCardService.class);

	@Resource(name = "weChatUserService")
	private WeChatUserManager weChatUserService;

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	@Resource(name = "sqlSessionTemplate")
	private SqlSessionTemplate sqlSessionTemplate;

	@Resource(name = "txTemplate")
	private TransactionTemplate txTemplate;
	
	@Resource(name = "weChatJsApiPayOrderDaoImpl")
	WeChatJsApiPayOrderDaoImpl weChatJsApiPayOrderDaoImpl;
	@Override
	public PageData getTotalCardMoney(Integer uId) throws Exception {
		PageData pd = (PageData) dao.findForObject(
				"WeChatCardMapper.getCardMoney", uId);
		return pd;
	}

	@Override
	public List<PageData> getCardTypes() throws Exception {
		return (List<PageData>) dao.findForList(
				"WeChatCardMapper.getCardTypes", null);
	}

	@Override
	public List<PageData> getRechargeDetailsByuId(Integer uId) throws Exception {
		return (List<PageData>) dao.findForList(
				"WeChatCardMapper.getRechargeDetailsByuId", uId);
	}

	@Override
	public PageData getCardTypeById(Integer storedCategoryId) throws Exception {
		return (PageData) dao.findForObject("WeChatCardMapper.getCardTypeById",
				storedCategoryId);
	}

	@Override
	public void insertStoredDetail(PageData pd) throws Exception {
		dao.save("WeChatCardMapper.insertStoredDetail", pd);
	}

	@Override
	public Integer updateStoredDetailByOutTradeNo(String out_trade_no)
			throws Exception {
		return (Integer) dao
				.update("WeChatCardMapper.updateStoredDetailByOutTradeNo",
						out_trade_no);
	}

	@Override
	public void updateStoredByDetailOutTradeNo(PageData pd) throws Exception {
		dao.update("WeChatCardMapper.updateStoredByDetailOutTradeNo", pd);
	}

	@Override
	public WeChatStoredDetail selectStoredDetailByOutTradeNo(String out_trade_no)
			throws Exception {
		return (WeChatStoredDetail) dao
				.findForObject(
						"WeChatCardMapper.selectStoredDetailByOutTradeNo",
						out_trade_no);

	}

	@Override
	public WeChatStored createCard(WeChatStored weChatStored) throws Exception {
		dao.save("WeChatCardMapper.createCard", weChatStored);
		return weChatStored;
	}

	@Override
	public WeChatStored selectStoredByuId(Integer storedDetailuId)
			throws Exception {
		return (WeChatStored) dao.findForObject(
				"WeChatCardMapper.selectStoredByuId", storedDetailuId);
	}
	
	
	
	
	@Override
	public void rechargeNotify(final PayResultNotify payResultNotify) {
		Logger logger1 = LoggerFactory.getLogger("weChatService");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = null;
		try {
			date = sdf.parse(payResultNotify.getTime_end());
		} catch (ParseException e1) {
			logger.debug("--- 日期解析出现异常 ---");
		}
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Map<String,Object> map = weChatJsApiPayOrderDaoImpl.parsePayResultNotify(payResultNotify);
		
		// 判断当前是否是第一次接收到通知
		final String outTradeNo = (String)map.get("outTradeNo");
		WeChatPayHistory weChatPayHistory = weChatJsApiPayOrderDaoImpl.getPayHistoryByOrderId(outTradeNo);
		
		if(weChatPayHistory != null){
			logger.debug("--- 当前并非是第一次接受微信通知 ---");
			return;
		}
		
		weChatJsApiPayOrderDaoImpl.savePayHistory(map);
		
		
		
		int retVal = 1;
		try {
			retVal = (int) txTemplate.execute(new TransactionCallback() {
				@Override
				public Object doInTransaction(TransactionStatus arg0) {
					updateStoredDetailAndStored(payResultNotify);
					return 0;
				}
			});
		} catch (Exception e) {
			logger1.error("--- 处理微信储值卡回调通知发生的异常 --处理用户付完款,但是储值明细中的订单号："+payResultNotify.getOut_trade_no()+" 数据未更新的--需要更新该订单储值明细状态和账户信息 ---异常信息为:"+e.getMessage());

		}
		
		
		if(retVal == 1){
			weChatPayHistory =weChatJsApiPayOrderDaoImpl.getPayHistoryByOrderId(outTradeNo);
			weChatPayHistory.setStatus(1);
			weChatJsApiPayOrderDaoImpl.updatePayHistoryStatus(weChatPayHistory);
			logger.debug("--- 更新储值记录不成功 ---");
		}
		
	}

	protected void updateStoredDetailAndStored(PayResultNotify payResultNotify)  {
		// 真正的业务，找到订单号，是否是提交过 ，更改状态,将储值明细填充到账户中
		String out_trade_no = payResultNotify.getOut_trade_no();
		PageData pd = new PageData();
		pd.put("outTradeNo", out_trade_no);
		logger.debug("支付结果通知——————储值订单号:" + out_trade_no);
		SqlSession session = this.sqlSessionTemplate.getSqlSessionFactory().openSession(false);
		WeChatStoredDetail storedDetail = (WeChatStoredDetail) session.selectOne("WeChatCardMapper.selectStoredDetailByOutTradeNo",out_trade_no);
		if (null != storedDetail) {
			Integer storedDetailStatus = storedDetail.getStatus();
			logger.debug("该储值记录" + out_trade_no + "需要更新----"
					+ ((storedDetailStatus == 1) ? "需要" : "不需要"));
			if (storedDetailStatus == 1) {
				session.update("WeChatCardMapper.updateStoredDetailByOutTradeNo",
						out_trade_no);
				logger.debug("该储值记录:"+out_trade_no+"首次更新");
				// 如果没有账户的创建账户,先查询
				Integer storedDetailuId = storedDetail.getuId();
				logger.debug("根据储值订单号查询uid" + storedDetailuId);
				pd.put("uId", storedDetailuId);

				// 根据uId查询账户
				/*
				WeChatStored weChatStored = (WeChatStored) session.selectOne("WeChatCardMapper.selectStoredByuId", storedDetailuId);
				logger.debug("根据uid:"+storedDetailuId+"查询账户" + weChatStored);
				if (null == weChatStored) {
					WeChatUser weChatUser = (WeChatUser)session.selectOne("WeChatUserMapper.getWeChatUserByuId", storedDetailuId);
					if (null != weChatUser) {
						//更新待充钱账户为正常
						WeChatStored weChatStored = new WeChatStored();
						weChatStored.setuId(storedDetailuId);
						weChatStored.setName(weChatUser.getName());
						weChatStored.setPhone(weChatUser.getPhone());
						weChatStored.setRemainMoney(new BigDecimal(0));
						weChatStored.setRemainPoints(new BigDecimal(0));
						weChatStored.setStatus(0);
						weChatStored.setPassWord(MD5Util.MD5Encode("123456",
								"utf-8"));
						session.insert("WeChatCardMapper.createCard", weChatStored);
						logger.debug("创建一个储值账户" + weChatStored);
					}
				}
			*/
				// 更新账户通过储值订单号
				logger.debug("start-将储值明细中对应的订单号：--" + out_trade_no
						+ "---中的钱更新到账户:---" + storedDetailuId);
				session.update("WeChatCardMapper.updateStoredByDetailOutTradeNo", pd);
				logger.debug("end-将储值明细中对应的订单号：--" + out_trade_no
						+ "---中的钱更新到账户:---" + storedDetailuId);
			}
		}
		session.flushStatements();
		session.commit();
		session.clearCache();
		logger.debug("--- 储值更新关闭session ---");
		session.close();

	}
}
