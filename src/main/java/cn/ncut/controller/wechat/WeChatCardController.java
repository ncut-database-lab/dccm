package cn.ncut.controller.wechat;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.examples.NewWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import cn.ncut.controller.base.BaseController;
import cn.ncut.entity.wechat.pojo.PayH5;
import cn.ncut.entity.wechat.pojo.PayResultNotify;
import cn.ncut.entity.wechat.pojo.PayResultNotifyResponse;
import cn.ncut.entity.wechat.pojo.WeChatStored;
import cn.ncut.entity.wechat.pojo.WeChatStoredDetail;
import cn.ncut.entity.wechat.pojo.WeChatUser;
import cn.ncut.service.wechat.card.WeChatCardManager;
import cn.ncut.service.wechat.credit.WeChatCreditManager;
import cn.ncut.service.wechat.pay.WeChatPayManager;
import cn.ncut.service.wechat.preStored.WeChatPreStoredManager;
import cn.ncut.service.wechat.user.WeChatUserManager;
import cn.ncut.util.DateUtil;
import cn.ncut.util.PageData;
import cn.ncut.util.UuidUtil;
import cn.ncut.util.wechat.MD5Util;
import cn.ncut.util.wechat.NetworkUtil;
import cn.ncut.util.wechat.SignGenerateAndCheckUtil;
import cn.ncut.util.wechat.SpringPropertyResourceReader;
import cn.ncut.util.wechat.TypeConvertUtil;
import cn.ncut.util.wechat.WeChatLogUtil;

@Controller
@RequestMapping("/weChatCard")
public class WeChatCardController extends BaseController {
	private static final String domainName = SpringPropertyResourceReader.getProperty("domainName");
	private static Logger log = LoggerFactory
			.getLogger(WeChatCardController.class);
	@Resource(name = "weChatCardService")
	private WeChatCardManager weChatCardService;
	@Resource(name = "weChatCreditService")
	private WeChatCreditManager weChatCreditService;
	@Resource(name = "weChatPayService")
	private WeChatPayManager weChatPayService;
	@Resource(name = "weChatUserService")
	private WeChatUserManager weChatUserService;
	
	@Resource(name = "weChatPreStoredService")
	private WeChatPreStoredManager weChatPreStoredService;

	/**
	 * 根据用户id 返回帐号余额和积分总额
	 * 
	 * @throws Exception
	 */
	@RequestMapping("/cardCenter")
	public ModelAndView cardCenter(HttpServletRequest request) throws Exception {
		
		WeChatLogUtil.controllerLog(request, "cardCenter");
		
		
		ModelAndView mv = new ModelAndView();
		Integer uId = (Integer) request.getSession().getAttribute("uId");
		
	//	 Integer uId = 24;
		PageData cardMoney = new PageData();
		Integer totalCredit = 0;
		Double totalPreStored = 0.00;
		if (uId != null) {
			// 总钱数 + 返点
			cardMoney = weChatCardService.getTotalCardMoney(uId);
			// 积分余额
			totalCredit = weChatCreditService.getTotalCredit(uId);
			//预存余额
			totalPreStored = weChatPreStoredService.getTotalPreStored(uId);
			
		} else {

		}

		mv.addObject("cardMoney", cardMoney);
		mv.addObject("totalCredit", totalCredit);
		mv.addObject("totalPreStored", totalPreStored);
		mv.setViewName("wechat/card/cardCenter");
		return mv;
	}

	/**
	 * 充值明细页面
	 * 
	 * @throws Exception
	 */
	@RequestMapping("/rechargeDetails")
	public ModelAndView rechargeDetails(HttpServletRequest request)
			throws Exception {
		
		WeChatLogUtil.controllerLog(request, "rechargeDetails");
		
		ModelAndView mv = new ModelAndView();
		Integer uId = (Integer) request.getSession().getAttribute("uId");
//		 Integer uId = 24;
		List<PageData> rechargeDetails = weChatCardService
				.getRechargeDetailsByuId(uId);
		mv.addObject("rechargeDetails", rechargeDetails);
		mv.setViewName("wechat/card/rechargeDetails");
		return mv;
	}

	/**
	 * 储值明细 stored_deatil,,status 0.已完成 1.待支付 2. STOREDDETAIL_ID 为uuid 充值界面的统一下单
	 * 
	 * @throws Exception
	 */

	@RequestMapping("/packageUnifiedOrder")
	@ResponseBody
	public PayH5 packageUnifiedOrder(HttpServletRequest request) {
		String notifyUrl = "http://DOMAINNAME/dccm/weChatCard/rechargeNotify".replace("DOMAINNAME",domainName);
		PayH5 payH5 = new PayH5();
		Integer storedCategoryId = Integer.parseInt(request
				.getParameter("storedCategoryId"));
		String payMethod = request.getParameter("payMethod");
		Integer uId = (Integer) request.getSession().getAttribute("uId");
		log.debug("储值统一下单前获取的uId"+uId);
		// Integer uId = 24;
		try {
			WeChatUser weChatUser = this.weChatUserService.getWeChatUserByuId(uId);
			if (null != weChatUser && null != storedCategoryId && null != payMethod) {
				// 插入一个储值卡记录 uid,储值卡类型编号，创建时间，根据储值卡id得到的金额和点数，状态
				PageData cardType = weChatCardService
						.getCardTypeById(storedCategoryId);
				BigDecimal bigDecimal = (BigDecimal) cardType.get("storedMoney");
				int total_fee = (int) (((bigDecimal).doubleValue()) * 100);//
				// 订单实付金额分
//				int total_fee = 1;
				log.debug("购买的储值卡下单钱"+total_fee);
				PageData pd = new PageData();
				String storedDetailId =  "OC" + DateUtil.getDays() + ((int)(Math.random()*900000)+100000)+"";;
				pd.put("STOREDDETAIL_ID", storedDetailId);
				pd.put("UID", uId);
				pd.put("STORED_CATEGORY_ID", storedCategoryId);
				pd.put("CREATE_TIME", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date()));
				pd.put("WECHATPAY_MONEY", bigDecimal);
				pd.put("MONEY", bigDecimal);
				pd.put("POINTS", (BigDecimal)cardType.get("returnPoints"));
				pd.put("STATUS", 1);
				pd.put("TYPE", 0);
				// 插入储值记录
				weChatCardService.insertStoredDetail(pd);
				log.debug("插入储值记录" + pd.toString());

				//插入待充钱的账户
				WeChatStored weChatStored = new WeChatStored();
				weChatStored.setuId(uId);
				weChatStored.setName(weChatUser.getName());
				weChatStored.setPhone(weChatUser.getPhone());
				weChatStored.setRemainMoney(bigDecimal);
				weChatStored.setRemainPoints((BigDecimal)cardType.get("returnPoints"));
				//2代表待充钱
				weChatStored.setStatus(2);
				weChatStored.setPassWord(MD5Util.MD5Encode("123456",
						"utf-8"));
				weChatCardService.createCard(weChatStored);
				logger.debug("创建一个储值账户" + weChatStored);

				String openId = weChatUser.getOpenId();
				String spbillCreateIp = NetworkUtil.getIpAddress(request);
				String body = "大诚中医-储值卡充值";// 商品描述
				String outTradeNo = storedDetailId;// 订单号
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("payMethod", "0");
				map.put("prePayMoney", String.valueOf((bigDecimal).doubleValue()));
				map.put("orderId", storedDetailId);
				Gson gson = new Gson();
				String attach = gson.toJson(map);
				logger.debug("统一下单生成的attache"+attach);
				
				
				payH5 = weChatPayService.packageUnifiedOrderByJsAPI(body,
						outTradeNo, total_fee, spbillCreateIp, notifyUrl, openId,
						attach);
				
				WeChatLogUtil.controllerLog(request, "packageUnifiedOrder");
				log.debug("微信端统一下单返回的给页面的payH5" + payH5);
				return payH5;
			}
		} catch (Exception e) {
			WeChatLogUtil.controllerError(e);
		}
		return null;
	}
	
	@RequestMapping("/rechargeNotify")
	public String rechargeNotify(HttpServletRequest request){
		PayResultNotify payResultNotify = TypeConvertUtil
				.transformRequestToBean(PayResultNotify.class, request);
		String backSign = SignGenerateAndCheckUtil.createSign("UTF-8",
				SignGenerateAndCheckUtil.sortBeanAttributes(payResultNotify));
		PayResultNotifyResponse payResultNotifyResponse = new PayResultNotifyResponse();
		if (null != payResultNotify
				&& payResultNotify.getReturn_code().equals("SUCCESS")) {
			if (payResultNotify.getSign().equals(backSign)) {
				if (payResultNotify.getResult_code().equals("SUCCESS")) {
					weChatCardService.rechargeNotify(payResultNotify);
				}
				payResultNotifyResponse.setReturn_code("SUCCESS");
				payResultNotifyResponse.setReturn_msg("OK");

			} else {
				payResultNotifyResponse.setReturn_code("FAIL");
				payResultNotifyResponse.setReturn_msg("SIGNERROR");
			}
		} else {
			// 通信失败 需要返回什么呢
		}
		logger.debug("支付结果返回为微信的信息:" + payResultNotifyResponse.toString());
		return TypeConvertUtil.transformBeanToXml(payResultNotifyResponse);
	}

	
	
	
	@RequestMapping("/test")
	public ModelAndView hello() throws Exception {
		WeChatStoredDetail storedDetail = weChatCardService
				.selectStoredDetailByOutTradeNo("bb4af63621354da3bac90994f3a4fd9c");
		Integer storedDetailStatus = storedDetail.getStatus();
		logger.debug("该储值记录" + "bb4af63621354da3bac90994f3a4fd9c" + "需要更新----"
				+ ((storedDetailStatus == 1) ? "需要" : "不需要"));

		WeChatStored weChatStored = weChatCardService.selectStoredByuId(366);
		logger.debug("根据uid查询账户" + weChatStored);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("wechat/success");
		return mav;
	}
	
}