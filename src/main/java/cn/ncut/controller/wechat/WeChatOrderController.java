package cn.ncut.controller.wechat;

import java.io.BufferedOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import cn.ncut.controller.base.BaseController;
import cn.ncut.entity.wechat.pojo.PayH5;
import cn.ncut.entity.wechat.pojo.PayResultNotify;
import cn.ncut.entity.wechat.pojo.PayUnifiedOrder;
import cn.ncut.entity.wechat.pojo.WeChatCustomComment;
import cn.ncut.entity.wechat.pojo.WeChatOrder;
import cn.ncut.entity.wechat.pojo.WeChatPayDetail;
import cn.ncut.entity.wechat.pojo.WeChatRefund;
import cn.ncut.entity.wechat.pojo.WeChatUser;
import cn.ncut.entity.wechat.pojo.WeChatUserDiscount;
import cn.ncut.entity.wechat.pojo.WeChatUserStoredCard;
import cn.ncut.service.wechat.discount.impl.WeChatUserDiscountService;
import cn.ncut.service.wechat.order.impl.WeChatJsApiPayOrderService;
import cn.ncut.service.wechat.order.impl.WeChatOrderService;
import cn.ncut.service.wechat.payHistory.impl.WeChatPayHistoryService;
import cn.ncut.service.wechat.refund.impl.WeChatRefundService;
import cn.ncut.service.wechat.user.WeChatUserManager;
import cn.ncut.service.wechat.userStoredCard.impl.WeChatUserStoredCardService;
import cn.ncut.util.SequenceUtils;
import cn.ncut.util.wechat.MD5Util;
import cn.ncut.util.wechat.NetworkUtil;
import cn.ncut.util.wechat.SpringPropertyResourceReader;
import cn.ncut.util.wechat.TypeConvertUtil;
import cn.ncut.util.wechat.WeChatLogUtil;

@Controller
@RequestMapping("/weChatOrder")
public class WeChatOrderController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(WeChatOrderController.class);
	private static final String domainName = SpringPropertyResourceReader.getProperty("domainName");
	@Resource(name = "weChatOrderService")
	private WeChatOrderService weChatOrderService;
	
	@Resource(name = "weChatUserDiscountService")
	private WeChatUserDiscountService weChatUserDiscountService;
	
	@Resource(name = "weChatJsApiPayOrderService")
	private WeChatJsApiPayOrderService weChatJsApiPayOrderService;
	
	@Resource(name = "weChatUserService")
	private WeChatUserManager weChatUserService;
	
	@Resource(name = "weChatUserStoredCardService")
	private WeChatUserStoredCardService weChatUserStoredCardService;
	
	@Resource(name = "weChatPayHistoryService")
	private WeChatPayHistoryService weChatPayHistoryService;
	
	@Resource(name = "weChatRefundService")
	private WeChatRefundService weChatRefundService;
	
	/**
	 * sequenceUtils根据数据库中设置的type来生成相应的主键编号
	 * */
	@Resource(name="sequenceUtils")
	private SequenceUtils sequenceUtils;
	
	/**
	 * 查询某用户的所有订单,订单信息包括门店名称,项目名称,医生名称,预约时间,验证码,订单金额,该订单的状态与是否评价
	 * @param request 请求参数
	 * @param id 请求id
	 * @return mav
	 * */
	@RequestMapping("/listOrders/{id}")
	public ModelAndView getUserOrdersByUserId(HttpServletRequest request,@PathVariable("id") int id) throws Exception{
		WeChatLogUtil.controllerLog(request, "listOrders");
		
		HttpSession session = request.getSession();
		Integer uId = (Integer)session.getAttribute("uId");	
				
		ModelAndView mav = this.getModelAndView();
		
		List<WeChatOrder> weChatOrders = null;
		List<WeChatOrder> intercepter = new ArrayList<WeChatOrder>();
		
		/**
		 * 根据id值判断是查询"我的订单","我的预约","待支付"
		 * 	1 : 我的订单
		 * 	2 : 我的预约
		 *  3 : 待支付
		 * 根据查询条件将HashMap对象加入intercepter
		 * 	订单状态:
		 * 		待支付 0
		 * 		已关闭 1
		 * 		已预约 2
		 * 		已过期 3
		 * 		待评价 4
		 * 		已完成 5
		 * 		已取消 6
		 * */
		
		switch(id){
			case 1 :
				/**
				 * 查询待评价,已完成,已取消"订单",待评价订单与已完成订单在"预约表"中都有记录的
				 * */
				weChatOrders = this.weChatOrderService.getUserPreCommentOrDoneOrderByUserId(uId);
				// 查询划价以及消耗品的订单信息,并插入到weChatOrders中
				List<WeChatOrder> exception = this.weChatOrderService.getOrdersWhereServiceCostIdIn2Or3ByUid(uId);
				if (exception.size() > 0) {
					for (WeChatOrder weChatOrder : exception) {
						weChatOrders.add(weChatOrder);
					}
				}
				intercepter = weChatOrders;
				mav.setViewName("wechat/order/preCommentOrder");
				break;
			case 2 :
				/**
				 * 查询已预约与已过期"订单",已预约订单与已过期订单在"预约表"中都有记录的
				 * */
				weChatOrders = this.weChatOrderService.getUserAppointOrOvertimeOrderByUserId(uId);
				intercepter = weChatOrders;				
				mav.setViewName("wechat/order/appointOrder");
				break;
			case 3 :
				weChatOrders = this.weChatOrderService.getUserPrePayOrCloseOrderByUserId(uId);
				intercepter = weChatOrders;
				mav.setViewName("wechat/order/prePayOrder");
				break;
			default :
				break;
		}
		mav.addObject("intercepter", intercepter);
		mav.addObject("size",intercepter.size());
		return mav;
	}
	
	/**
	 * 查询订单详情
	 * @param request 请求参数
	 * @param id 请求id
	 * @return mav
	 * */
	@RequestMapping("/showOrderDetails/{orderId}")
	public ModelAndView getUserOrderDetails(@PathVariable("orderId") String orderId) throws Exception {
		WeChatOrder weChatOrder = this.weChatOrderService.getUserOrderDetails(orderId);
		
		List<WeChatPayDetail> weChatPayDetails = weChatOrder.getPayDetails();
		BigDecimal payMoney = new BigDecimal("0.00");
		String payTime = null;
		Integer payMethod = null;
		for(WeChatPayDetail weChatPayDetail : weChatPayDetails){
			payMoney = payMoney.add(new BigDecimal(weChatPayDetail.getPayMoney()));
			payTime = weChatPayDetail.getPayTime();
			payMethod = weChatPayDetail.getPayMethod();
		}
		DecimalFormat df = new DecimalFormat("######0.00");
		
		/**
		 * 考虑用户使用优惠券的场景,后台初始化订单时discountId为"";
		 * discountAmount代表着该笔订单的优惠金额
		 * */
		BigDecimal discountAmount = null;
		if(weChatOrder.getDiscountId() != null && !"".equals(weChatOrder.getDiscountId())){
			discountAmount = new BigDecimal(weChatOrder.getDiscountId());
		}
		
		/**
		 * 查询该单的退款详情,退款订单的状态为6
		 * */
		WeChatRefund weChatRefund = null;
		if(weChatOrder.getOrderStatus() == 6){
			weChatRefund = this.weChatRefundService.getWeChatRefundByOrderId(weChatOrder.getOrderId());
		}
		
		ModelAndView mav = this.getModelAndView();
		
		mav.addObject("weChatOrder", weChatOrder);
		mav.addObject("payMoney",df.format(payMoney.doubleValue()));
		mav.addObject("payTime",payTime);
		mav.addObject("payMethod",payMethod);
		
		if(discountAmount != null){
			mav.addObject("discountAmount", df.format(discountAmount));
		}
		
		// 简要处理金额小数点
		if(weChatRefund != null){
			mav.addObject("weChatRefund", weChatRefund);
			BigDecimal refundMoney = new BigDecimal(weChatRefund.getPAY_MONEY());
			BigDecimal storeCardMoney = new BigDecimal(weChatRefund.getPRECARD_ADD());
			BigDecimal storeCardPoint = new BigDecimal(weChatRefund.getPRECARD_POINTS_ADD());
			mav.addObject("refundMoney", df.format(refundMoney.doubleValue()));
			mav.addObject("storeCardMoney", df.format(storeCardMoney));
			mav.addObject("storeCardPoint", df.format(storeCardPoint));
		}
		
		mav.setViewName("wechat/order/orderDetails");
		return mav;
	}
	
	/**
	 * 导航到"订单评价"页面
	 * */
	@RequestMapping("/navigateCommentPage/{orderId}")
	public ModelAndView navigateCommentPage(@PathVariable("orderId") String orderId) throws Exception{
 		WeChatOrder weChatOrder = this.weChatOrderService.getUserOrderDetails(orderId);
		ModelAndView mav = this.getModelAndView();
		mav.addObject("weChatOrder", weChatOrder);
		mav.setViewName("wechat/order/commentOrder");
		return mav;
	}
	
	/**
	 * 订单评价,评价完返回上一个页面
	 * */
	@RequestMapping("/commentOrder")
	public ModelAndView commentOrder(HttpServletRequest request,@ModelAttribute WeChatCustomComment customComment) throws Exception {
		HttpSession session = request.getSession();
		Integer uId = (Integer)session.getAttribute("uId");		
		
		customComment.setMessageId(this.sequenceUtils.getNextId("CC"));
		customComment.setuId(uId);
		customComment.setmId(customComment.getStoreId().substring(0, 2));
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		customComment.setCreateTime(sdf.format(now));
		this.weChatOrderService.insertOrderCommentInfoToCustomComment(customComment);
				
		ModelAndView mav = this.getModelAndView();
		
		List<WeChatOrder> weChatOrders = null;
		List<WeChatOrder> intercepter = new ArrayList<WeChatOrder>();
		weChatOrders = this.weChatOrderService.getUserPreCommentOrDoneOrderByUserId(uId);
		intercepter = weChatOrders;
		
		mav.addObject("intercepter", intercepter);
		mav.setViewName("wechat/order/preCommentOrder");
		return mav;
	}
	
	/**
	 * 导航到"订单支付"页面
	 * 这里分为两种情况,即从"待支付"页面点击支付导航到支付页面;用户选择优惠券之后导航到原来的支付页面
	 * 注意 	优惠券的使用目前设置在客服给客户下单时使用,所有第一个if是不会执行的
	 * @param request
	 * @param whetherOrderId 当前是否是订单号;用户选择优惠券之后,whetherOrderId的值是"selectDiscount"
	 * @return mav 逻辑视图
	 * */
	@RequestMapping("/navigatePayPage/{whetherOrderId}")
	public ModelAndView navigatePayPage(HttpServletRequest request,@PathVariable("whetherOrderId") String whetherOrderId)
			throws Exception{
		WeChatOrder weChatOrder = null;
		WeChatUserDiscount weChatUserDiscount = null;
		String[] orderIds = null;
		StringBuilder orderIdsBulder = new StringBuilder();
		StringBuilder projectIdsBuilder = new StringBuilder();
		List<WeChatOrder> weChatOrders = new ArrayList<WeChatOrder>();
		if(whetherOrderId.equals("selectDiscount")){
			String orderId = request.getParameter("orderId");
			String discountId = request.getParameter("discountId");
			orderIds = orderId.split(",");
			weChatUserDiscount = this.weChatUserDiscountService.getUserDiscountId(discountId);
		}else{
			orderIds = whetherOrderId.split(",");
		}
		
		int boundary = orderIds.length - 1;
		int i = 0;
		BigDecimal totalMoney = new BigDecimal("0.00");
		for(String orderId : orderIds){
			weChatOrder = this.weChatOrderService.getOrderByOrderId(orderId);
			weChatOrder = process(weChatOrder);
			orderIdsBulder.append(weChatOrder.getOrderId());
			projectIdsBuilder.append(weChatOrder.getServiceProject().getProjectId());
			if(i < boundary){
				orderIdsBulder.append(",");
				projectIdsBuilder.append(",");
			}
			weChatOrders.add(weChatOrder);
			totalMoney = totalMoney.add(new BigDecimal(weChatOrder.getPayMoney()));
			++ i;
		}
				
		// 小数点后两位 
		DecimalFormat df = new DecimalFormat("######0.00");
		
		Integer uId = (Integer)request.getSession().getAttribute("uId");
		WeChatUserStoredCard weChatUserStoredCard = this.weChatUserStoredCardService.existOfCreditCardOfUserByUid(uId);
		
		ModelAndView mav = this.getModelAndView();
		mav.addObject("weChatOrders", weChatOrders);
		mav.addObject("orderIds", orderIdsBulder);
		mav.addObject("projectIds", projectIdsBuilder);
		mav.addObject("totalMoney", df.format(totalMoney.doubleValue()));
		if(weChatUserDiscount != null){
			mav.addObject("weChatUserDiscount",weChatUserDiscount);
		}
		if(weChatUserStoredCard != null){
			mav.addObject("weChatUserStoredCard", weChatUserStoredCard);
		}
		mav.setViewName("wechat/order/payOrder");
		return mav;
	}
	
	/**
	 * 订单支付,前端页面会返回四个参数
	 * orderId 订单编号
	 * discountId 用户拥有的优惠券编号(可为空)
	 * prePayMoney 实付金额
	 * prePayMethod 支付方式	
	 * 		0:微信支付  	1:支付宝 		2:储值卡 		
	 * 		3：现金 		4：银行卡 		5:预存支付
	 * */
	@ResponseBody
	@RequestMapping("/payOrder")
	public String payOrder(HttpServletRequest request) throws Exception{
		String orderId = request.getParameter("orderId");
		String discountId = request.getParameter("discountId");
		String prePayMoney = request.getParameter("prePayMoney");
		String payMethod = request.getParameter("payMethod");
		
		int index = orderId.indexOf(",");
		String parentOrderId = null;
		if(index != -1){
			parentOrderId = orderId.substring(0,orderId.indexOf(","));
		}else{
			parentOrderId = orderId;
		}
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("payMethod", payMethod);
		map.put("prePayMoney", prePayMoney);
		map.put("orderId", orderId);
		map.put("parentOrderId", parentOrderId);
		
		/**
		 * 根据订单号查询订单,并设置该订单的支付方式与实收金额
		 * 优惠券判断是否为空
		 * 注意 	优惠券的使用目前设置在客服给客户下单时使用
		 * */
		if(discountId != null){
			map.put("discountId", discountId);
		}
				
		Integer uId = (Integer)request.getSession().getAttribute("uId");
		
		
		WeChatOrder weChatOrder = this.weChatOrderService.getOrderByOrderId(parentOrderId);
		WeChatUser weChatUser = this.weChatUserService.getWeChatUserByuId(uId);	
		
		map.put("openId", weChatUser.getOpenId());
		
		/**
		 *  存储待支付订单信息,并返回该记录的主键
		 */
		Integer payHistoryId = weChatPayHistoryService.savePayHistory(map);
		
		map.clear();
		map.put("payHistoryId", String.valueOf(payHistoryId));
		
		
		StringBuilder body = new StringBuilder("大诚中医-");
		String outTradeNo = weChatOrder.getOrderId();
		
		body.append(weChatOrder.getServiceProject().getpName());
		
		// 注意将金额的转换异常...
		int totalFee = (int) (Double.valueOf(prePayMoney) * 100);
//		int totalFee = 1;
		String spbillCreateIp = NetworkUtil.getIpAddress(request);
		
		String notifyUrl = "http://DOMAINNAME/dccm/weChatOrder/payOrderNotify".replace("DOMAINNAME", domainName);
		String openId = weChatUser.getOpenId();
	
		PayUnifiedOrder payUnifiedOrder = new PayUnifiedOrder();
		payUnifiedOrder.setBody(body.toString());
		payUnifiedOrder.setOut_trade_no(outTradeNo);
		payUnifiedOrder.setTotal_fee(totalFee);
		payUnifiedOrder.setSpbill_create_ip(spbillCreateIp);
		payUnifiedOrder.setNotify_url(notifyUrl);
		payUnifiedOrder.setOpenid(openId);
		
		Gson gson = new Gson();
		
		logger.debug("--- 生成的attach信息如下 ---");
		logger.debug(gson.toJson(map));
		
		payUnifiedOrder.setAttach(gson.toJson(map));
		
		PayH5 payH5 = this.weChatJsApiPayOrderService.payUnifiedOrderByJsApi(payUnifiedOrder,parentOrderId);
		
		logger.debug("--- 返回的payH5信息如下 ---");
		logger.debug(payH5.toString());
		
		return gson.toJson(payH5);
	}
	
	/**
	 * 储值卡支付
	 * */
	@ResponseBody
	@RequestMapping("/storedCardPayOrder")
	public String storedCardPayOrder(HttpServletRequest request) throws Exception{
		HttpSession session = request.getSession();
		Integer uId = (Integer)session.getAttribute("uId");	
				
		// 订单号
		String orderId = request.getParameter("orderId");
		// 优惠券id
		String discountId = request.getParameter("discountId");
		// 实付金额
		String prePayMoney = request.getParameter("prePayMoney");
		// 支付方式
		String payMethod = request.getParameter("payMethod");
		// 支付密码
		String password = request.getParameter("password");
		
		String result = null;
		
		WeChatUserStoredCard weChatUserStoredCard = this.weChatUserStoredCardService.existOfCreditCardOfUserByUid(uId);
		String newPassword = MD5Util.MD5Encode(password, "utf8");
		if(!weChatUserStoredCard.getPassword().equals(newPassword)){
			result = "{\"errorCode\":\"fail\",\"errorDes\":\"密码错误,请重新输入\"}";
			return result;
		}
		
		// 必要参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderId);
		map.put("payMethod", payMethod);
		map.put("prePayMoney", prePayMoney);
		if(discountId != null){
			map.put("discountId", discountId);
		}
		map.put("uId", uId);
		
		logger.debug("--- 订单,支付方式,实付金额,优惠券信息如下 ---");
		logger.debug(map.toString());
		
		result = this.weChatJsApiPayOrderService.payOfStoredCard(map);
		
		return result;
	}
	
	/**
	 * 微信回调通知
	 * 通过获取微信返回的订单号,用户优惠券编号修改数据库相应的表
	 * 	即修改
	 * 		订单表订单状态为"1",已预约
	 * 		预约表新增一条记录
	 * 		支付明细表新增一条记录
	 * 
	 * 特别需要注意的是，返回给微信的通知消息以流的形式返回
	 * @return 
	 * @throws Exception 
	 * */
	@RequestMapping("/payOrderNotify")
	public String payOrderResponse(HttpServletRequest request,HttpServletResponse response) throws Exception{
		PayResultNotify payResultNotify = TypeConvertUtil.transformRequestToBean(PayResultNotify.class, request);
		String result = this.weChatJsApiPayOrderService.processOrderNotify(payResultNotify);
		System.out.println("--- 当前返回给微信的信息为 ---");
		System.out.println(result);
		
		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());  
        out.write(result.getBytes());  
        out.flush();  
        out.close();
		
		return result;
	}
	
	/**
	 * 处理应付金额与实付金额之间的关系
	 * 实付金额 = 应付金额 * 折扣
	 * 修改:由于优惠券已在客服下单时减免,此时也应该除去优惠券的金额信息
	 * */
	private WeChatOrder process(WeChatOrder weChatOrder){
		DecimalFormat df = new DecimalFormat("######0.00");
		if(weChatOrder != null && weChatOrder.getOrderMoney() != null && weChatOrder.getProportion() != null){
			BigDecimal orderMoney = new BigDecimal(weChatOrder.getOrderMoney());
			BigDecimal proportion = new BigDecimal(weChatOrder.getProportion());
			BigDecimal discountAmount = new BigDecimal("0.00");
			if(weChatOrder.getDiscountId() != null){
				discountAmount = new BigDecimal(weChatOrder.getDiscountId());
			}
 			Double payMoney = (orderMoney.multiply(proportion)).subtract(discountAmount).doubleValue();
 			// 判断下支付金额是否小于零
 			if(payMoney < 0.00000001){
 				weChatOrder.setPayMoney("0.00");
 			}else{
 				weChatOrder.setPayMoney(df.format(payMoney));
 			}
 		}
		return weChatOrder;
	}
	
	/**
	 * 查看划价以及消耗品订单详情
	 * @param orderId 订单编号
	 * @return
	 * @throws Exception
	 * */
	@RequestMapping("/showOrderDetailsPatch/{orderId}")
	public ModelAndView showOrderDetailsPatch(@PathVariable("orderId") String orderId) throws Exception {
		WeChatOrder weChatOrder = this.weChatOrderService.showOrderDetailsPatch(orderId);
		
		List<WeChatPayDetail> weChatPayDetails = weChatOrder.getPayDetails();
		BigDecimal payMoney = new BigDecimal("0.00");
		String payTime = null;
		Integer payMethod = null;
		for(WeChatPayDetail weChatPayDetail : weChatPayDetails){
			payMoney = payMoney.add(new BigDecimal(weChatPayDetail.getPayMoney()));
			payTime = weChatPayDetail.getPayTime();
			payMethod = weChatPayDetail.getPayMethod();
		}
		DecimalFormat df = new DecimalFormat("######0.00");
		
		/**
		 * 考虑用户使用优惠券的场景,后台初始化订单时discountId为"";
		 * discountAmount代表着该笔订单的优惠金额
		 * */
		BigDecimal discountAmount = null;
		if(weChatOrder.getDiscountId() != null && !"".equals(weChatOrder.getDiscountId())){
			discountAmount = new BigDecimal(weChatOrder.getDiscountId());
		}
		
		/**
		 * 查询该单的退款详情,退款订单的状态为6
		 * */
		WeChatRefund weChatRefund = null;
		if(weChatOrder.getOrderStatus() == 6){
			weChatRefund = this.weChatRefundService.getWeChatRefundByOrderId(weChatOrder.getOrderId());
		}
		
		ModelAndView mav = this.getModelAndView();
		
		mav.addObject("weChatOrder", weChatOrder);
		mav.addObject("payMoney",df.format(payMoney.doubleValue()));
		mav.addObject("payTime",payTime);
		mav.addObject("payMethod",payMethod);
		
		if(discountAmount != null){
			mav.addObject("discountAmount", df.format(discountAmount));
		}
		
		// 简要处理金额小数点
		if(weChatRefund != null){
			mav.addObject("weChatRefund", weChatRefund);
			BigDecimal refundMoney = new BigDecimal(weChatRefund.getPAY_MONEY());
			BigDecimal storeCardMoney = new BigDecimal(weChatRefund.getPRECARD_ADD());
			BigDecimal storeCardPoint = new BigDecimal(weChatRefund.getPRECARD_POINTS_ADD());
			mav.addObject("refundMoney", df.format(refundMoney.doubleValue()));
			mav.addObject("storeCardMoney", df.format(storeCardMoney));
			mav.addObject("storeCardPoint", df.format(storeCardPoint));
		}
		
		mav.setViewName("wechat/order/orderDetailsPatch");
		return mav;
	}
}