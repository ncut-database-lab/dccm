package cn.ncut.controller.user.paymedicinal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.ncut.controller.base.BaseController;
import cn.ncut.entity.Page;
import cn.ncut.entity.system.Staff;
import cn.ncut.service.finance.customappoint.CustomappointManager;
import cn.ncut.service.finance.discount.DiscountManager;
import cn.ncut.service.finance.prestore.PreStoreManager;
import cn.ncut.service.finance.prestoremx.PreStoreMxManager;
import cn.ncut.service.finance.serviceall.ServiceCostManager;
import cn.ncut.service.finance.serviceall.ServiceProjectManager;
import cn.ncut.service.system.ncutlog.NcutlogManager;
import cn.ncut.service.system.staff.StaffManager;
import cn.ncut.service.user.customstored.CustomStoredManager;
import cn.ncut.service.user.member.MemberManager;
import cn.ncut.service.user.order.OrderManager;
import cn.ncut.service.user.order.OrderMxManager;
import cn.ncut.service.user.storeddetail.StoredDetailManager;
import cn.ncut.service.user.userdiscount.UserDiscountManager;
import cn.ncut.service.wechat.userStoredCard.impl.WeChatUserStoredCardService;
import cn.ncut.util.BigDecimalUtil;
import cn.ncut.util.Const;
import cn.ncut.util.DateUtil;
import cn.ncut.util.Jurisdiction;
import cn.ncut.util.PageData;
import cn.ncut.util.wechat.PrimaryKeyGenerator;

@Controller
@RequestMapping(value = "/paymedicinal")
public class PayMedicinalController extends BaseController {

	String menuUrl = "paymedicinal/show.do"; // 菜单地址(权限用)

	@Resource(name = "customappointService")
	private CustomappointManager customappointService;

	@Resource(name = "staffService")
	private StaffManager staffService;

	@Resource(name = "servicecostService")
	private ServiceCostManager servicecostService;

	@Resource(name = "memberService")
	private MemberManager memberService;

	@Resource(name = "orderService")
	private OrderManager orderService;

	@Resource(name = "ordermxService")
	private OrderMxManager ordermxService;
	
	@Resource(name="prestoreService")
	private PreStoreManager prestoreService;
	
	@Resource(name="prestoremxService")
	private PreStoreMxManager prestoremxService;
	
	@Resource(name="customstoredService")
	private CustomStoredManager customstoredService;
	
	@Resource(name = "ncutlogService")
	private NcutlogManager NCUTLOG;
	
	@Resource(name = "discountService")
	private DiscountManager discountService;
	
	@Resource(name = "userdiscountService")
	private UserDiscountManager userdiscountService;
	
	@Resource(name = "serviceprojectService")
	private ServiceProjectManager serviceprojectService;

	@Resource(name = "storeddetailService")
	private StoredDetailManager storeddetailService;
	
	@Resource(name = "weChatUserStoredCardService")
	private WeChatUserStoredCardService weChatUserStoredCardService;

	// 菜单进入地址，初始化页面
	@RequestMapping(value = "/show")
	public ModelAndView showPayPage(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		
		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();
		Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));
		
		PageData pd = new PageData();
		pd = this.getPageData();
		
		page.setPd(pd);
		
		//查询用户信息
		List<PageData> userList = (List<PageData>) memberService.listCompleteMemberlistPage(page);// 列出Member列表
		
		//查询此门店的医生信息
		pd.put("STORE_ID", staff.getSTORE_ID());
		
		List<PageData> staffPdlist = staffService.listAll(pd);

		mv.addObject("staffPdlist", staffPdlist);
		mv.addObject("userList", userList);
		mv.setViewName("user/paymedicinal/user_pay");
		return mv;
	}
	@RequestMapping(value = "/show2")
	public ModelAndView showPayPage2(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		
		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();
		Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));
		
		PageData pd = new PageData();
		pd = this.getPageData();
		
		page.setPd(pd);
		
		//查询用户信息
		List<PageData> userList = (List<PageData>) memberService.listCompleteMemberlistPage(page);// 列出Member列表
		
		//查询此门店的医生信息
		pd.put("STORE_ID", staff.getSTORE_ID());
		
		List<PageData> staffPdlist = staffService.listAll(pd);

		mv.addObject("staffPdlist", staffPdlist);
		mv.addObject("userList", userList);
		mv.setViewName("user/paymedicinal/user_pay2");
		return mv;
	}
	 // 创建订单
	 @RequestMapping(value = "/createOrder2")
	 public void createOrder2(HttpServletResponse response) throws Exception{

		PageData pd = new PageData();
		pd = this.getPageData();
		
		DecimalFormat df = new DecimalFormat("######0.00");
				
		//得到当前用户的信息
		PageData userpd = new PageData();
		userpd = memberService.findUserStorededAndPrestoreByUid(Integer.parseInt(pd.getString("UID")));
		
	
		
		
		//药品价格
		//BigDecimal singleprice = new BigDecimal(str1);
		
		
		
		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();
		Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));

		
		//得到用户的储值卡信息
		PageData czkpd = new PageData();
		czkpd.put("UID", pd.getString("UID"));
		//czkpd = customstoredService.findById(czkpd);
		czkpd = customstoredService.findStoredByUidGroupByUid(Integer.parseInt(czkpd.getString("UID")));
		double czkRemainMoney;
		if(czkpd!=null){
		czkRemainMoney = (Double)czkpd.get("REMAIN_MONEY");
		}else{
			czkRemainMoney = 0;
		}
		
		//得到用户的余额信息
		PageData prepd = prestoreService.findByUid(Integer.parseInt(pd.getString("UID")));
		//得到医生信息
		PageData staffpd = new PageData();
		staffpd.put("STAFF_ID", pd.get("STAFF_ID"));
		staffpd = staffService.findById(staffpd);

	
		
		/**
		 * 获取用户的优惠券信息，分两种情况创建订单：
		 * 1、没有使用优惠券，只计算折扣价
		 * 2、用了优惠券，订单中插入优惠券金额，并且将用的优惠券置为已使用
		 */
	
		
		logger.debug("--- start 开始创建订单---");
		
		
			// 插入订单记录
			PageData order_pd = new PageData();
			String orderId = "OM"+PrimaryKeyGenerator.generateKey();
			order_pd.put("ORDER_ID", orderId);
			order_pd.put("UID", pd.getString("UID"));
			order_pd.put("STORE_ID", staffpd.get("STORE_ID"));//选择的医生的门店
			order_pd.put("SERVICECOST_ID", "-3");//服务项目编号为-3 消耗品
			order_pd.put("STAFF_ID", pd.getString("STAFF_ID"));//医生编号
			order_pd.put("SERVICE_STAFF_ID", staff.getSTAFF_ID());//客服编号
			order_pd.put("ORDER_MONEY", df.format(Double.parseDouble(pd.getString("PRICE")))); //每一次应收,打过折没用优惠券的价格
			order_pd.put("PAY_MONEY", df.format(Double.parseDouble(pd.getString("PRICE"))));        //减过优惠券的价格
			order_pd.put("PROPORTION", "1.0");        //用户最低折扣
			order_pd.put("CREATE_TIME", DateUtil.getTime()); // 创建时间
			order_pd.put("WECHAT_NAME", userpd.getString("name"));
			order_pd.put("WECHAT_PHONE", userpd.getString("phone"));
			order_pd.put("URL", "3");//url为3是线下买药
			order_pd.put("DISCOUNT_ID", "0.00");//不能使用优惠券
			order_pd.put("REFUND", Double.parseDouble("0.00"));
			order_pd.put("ORDER_STATUS", 5); // 已完成状态
			
		
			order_pd.put("REMARK", pd.getString("REMARK"));
			orderService.save(order_pd);
			
			/**
			 * /插入各种支付方式的支付详情记录
			 */
			//微信支付
			if (!"".equals(pd.getString("WECHATPAY_MONEY")) && !"0".equals(pd.getString("WECHATPAY_MONEY"))&& pd.get("WECHATPAY_MONEY") != null) {
				order_pd.put("WECHATPAY_MONEY", pd.getString("WECHATPAY_MONEY"));
				insertPayDetail(order_pd,0,df.format(Double.parseDouble(order_pd.getString("WECHATPAY_MONEY"))));
			}
			
			//支付宝支付
			if (!"".equals(pd.getString("ALIPAY_MONEY")) && !"0".equals(pd.getString("ALIPAY_MONEY")) && pd.get("ALIPAY_MONEY") != null ) {
				order_pd.put("ALIPAY_MONEY", pd.getString("ALIPAY_MONEY"));
				insertPayDetail(order_pd,1,df.format(Double.parseDouble(order_pd.getString("ALIPAY_MONEY"))));
			}
			
			//银行卡支付
			if (!"".equals(pd.getString("BANKPAY_MONEY")) && !"0".equals(pd.getString("BANKPAY_MONEY")) && pd.get("BANKPAY_MONEY") != null) {
				order_pd.put("BANKPAY_MONEY", pd.getString("BANKPAY_MONEY"));
				insertPayDetail(order_pd,4,df.format(Double.parseDouble(order_pd.getString("BANKPAY_MONEY"))));
			}
			
			//现金支付
			if (!"".equals(pd.getString("CASHPAY_MONEY")) && !"0".equals(pd.getString("CASHPAY_MONEY")) && pd.get("CASHPAY_MONEY") != null) {
				order_pd.put("CASHPAY_MONEY", pd.getString("CASHPAY_MONEY"));
				insertPayDetail(order_pd,3,df.format(Double.parseDouble(order_pd.getString("CASHPAY_MONEY"))));
			}
			
			//用户余额支付
			if (!"".equals(pd.getString("PRESTOREPAY_MONEY")) && !"0".equals(pd.getString("PRESTOREPAY_MONEY")) && pd.get("PRESTOREPAY_MONEY") != null) {
				
				
				order_pd.put("PRESTOREPAY_MONEY", pd.getString("PRESTOREPAY_MONEY"));
				insertPayDetail(order_pd,5,df.format(Double.parseDouble(order_pd.getString("PRESTOREPAY_MONEY"))));
			
				//插入预存消费明细记录，预存的总金额需要一次扣除n个订单的
				PageData premxpd = new PageData();
				
				premxpd.put("UID",pd.getString("UID"));
				premxpd.put("PRESTOREMX_ID", this.get32UUID());
				premxpd.put("PRESTORE_ID", prepd.getString("PRESTORE_ID"));
				premxpd.put("TYPE", 2);
				premxpd.put("STAFF_ID", staff.getSTAFF_ID());
				premxpd.put("PHONE", userpd.getString("phone"));
				premxpd.put("USERNAME", userpd.getString("username"));
				premxpd.put("CREATE_TIME", DateUtil.getTime());
				premxpd.put("PRESTOREMONEY", df.format(Double.parseDouble(pd.getString("PRESTOREPAY_MONEY"))));
				prestoremxService.save(premxpd);
				
			}
			
			
			//储值卡支付，两种明细 
			//2 代表储值卡余额支付    6代表储值卡返点支付
			if (!"".equals(pd.getString("STOREDPAY_MONEY")) && !"0".equals(pd.getString("STOREDPAY_MONEY")) && pd.get("STOREDPAY_MONEY") != null) {
				//插入储值卡支付的订单明细
				order_pd.put("STOREDPAY_MONEY", pd.getString("STOREDPAY_MONEY"));
				insertPayDetail(order_pd,2,df.format(Double.parseDouble(order_pd.getString("STOREDPAY_MONEY"))));
				
				//扣除储值卡的钱，并插入明细
				weChatUserStoredCardService.countCzkMoney(Integer.parseInt(order_pd.getString("UID")), Double.parseDouble(order_pd.getString("STOREDPAY_MONEY")), order_pd.getString("STORE_ID"), order_pd.getString("SERVICE_STAFF_ID"), null);
			}
			
			
			NCUTLOG.save(
					Jurisdiction.getUsername(),
					"用户：" + (userpd.getString("name")!=null?userpd.getString("name"):userpd.getString("username")) + "，手机号："
							+ userpd.getString("phone") + "。到店支付了："
							+ order_pd.get("PAY_MONEY") + "元，项目为：划价 ,订单号为："
							+ order_pd.getString("ORDER_ID"), this.getRequest()
							.getRemoteAddr());
		
		
		
		/**
		 * 关于用户储值卡的金额
		 * 有n次订单，使用储值卡总金额为money,那么储值卡金额要减去money
		 * 但是要插入n次订单支付方式和n次储值卡明细，每次的金额为money/n
		 */
	/*	if (!"".equals(pd.getString("STOREDPAY_MONEY")) && !"0".equals(pd.getString("STOREDPAY_MONEY")) && pd.get("STOREDPAY_MONEY") != null) {
			
			//用户用储值卡支付的钱
			double user_STOREDPAY_MONEY = Double.parseDouble(pd.getString("STOREDPAY_MONEY"));
			
			//如果用户支付的钱<储值卡余额的钱，即储值卡的钱够支付这n次订单,那么直接减去用户支付的钱
			if(user_STOREDPAY_MONEY < (Double)czkpd.get("REMAIN_MONEY")){
				czkpd.put("REMAIN_MONEY", (Double)czkpd.get("REMAIN_MONEY")-user_STOREDPAY_MONEY);
				czkpd.put("REMAIN_POINTS", (Double)czkpd.get("REMAIN_POINTS"));
			}else{
				//储值卡余额的钱不够支付这n次订单，需要先把储值卡的钱扣为0，再扣除余额的钱
				double hm = user_STOREDPAY_MONEY - (Double)czkpd.get("REMAIN_MONEY");  
				double pointsmm = (Double)czkpd.get("REMAIN_POINTS") - hm;
				czkpd.put("REMAIN_MONEY", Double.parseDouble("0.00"));
				czkpd.put("REMAIN_POINTS", pointsmm);
			}
			customstoredService.editSubMoney(czkpd);
		}*/
		
		
		if (!"".equals(pd.getString("PRESTOREPAY_MONEY")) && !"0".equals(pd.getString("PRESTOREPAY_MONEY")) && pd.get("PRESTOREPAY_MONEY") != null) {
			double pre_mm = (Double)prepd.get("SUM_MONEY") - Double.parseDouble(pd.getString("PRESTOREPAY_MONEY"));
			prepd.put("SUM_MONEY", pre_mm);
			prestoreService.edit(prepd);
		}
		
		
		
		logger.debug("--- end 创建订单---");
		
		response.setContentType("text/xml;charset=UTF-8");
		response.getWriter().write("{\"success\":\"ok\"}");
		
	}
	 
	/**
	 * 插入订单后插入n条支付方式的明细
	 * @param pd
	 * @param method
	 * @throws Exception
	 */
	public void insertPayDetail(PageData pd, int method, String methodText) throws Exception{
		PageData ppdd = new PageData();
		ppdd.put("ORDERMX_ID", this.get32UUID());
		ppdd.put("UID", pd.getString("UID"));
		ppdd.put("ORDER_ID", pd.getString("ORDER_ID"));
		ppdd.put("ORDER_MONEY", pd.getString("ORDER_MONEY"));
		ppdd.put("PAY_MONEY", methodText);
		ppdd.put("PAY_METHOD", method);
		ppdd.put("PAY_TIME", pd.getString("CREATE_TIME"));
		ppdd.put("REMARK", "");
		
		ordermxService.save(ppdd);
	}
	 // 创建订单
	 @RequestMapping(value = "/createOrder")
	 public void createOrder(HttpServletResponse response) throws Exception{

		PageData pd = new PageData();
		pd = this.getPageData();
		
		DecimalFormat df = new DecimalFormat("######0.00");
				
		//得到当前用户的信息
		PageData userpd = new PageData();
		userpd = memberService.findUserStorededAndPrestoreByUid(Integer.parseInt(pd.getString("UID")));
		
	
		
		
		//药品价格
		//BigDecimal singleprice = new BigDecimal(str1);
		
		
		
		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();
		Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));

		
		//得到用户的储值卡信息
		PageData czkpd = new PageData();
		czkpd.put("UID", pd.getString("UID"));
		czkpd = customstoredService.findStoredByUidGroupByUid(Integer.parseInt(czkpd.getString("UID")));
		double czkRemainMoney;
		if(czkpd!=null){
		czkRemainMoney = (Double)czkpd.get("REMAIN_MONEY");
		}else{
			czkRemainMoney = 0;
		}
		
		//得到用户的余额信息
		PageData prepd = prestoreService.findByUid(Integer.parseInt(pd.getString("UID")));
		//得到医生信息
		PageData staffpd = new PageData();
		staffpd.put("STAFF_ID", pd.get("STAFF_ID"));
		staffpd = staffService.findById(staffpd);

	
		
		/**
		 * 获取用户的优惠券信息，分两种情况创建订单：
		 * 1、没有使用优惠券，只计算折扣价
		 * 2、用了优惠券，订单中插入优惠券金额，并且将用的优惠券置为已使用
		 */
	
		
		logger.debug("--- start 开始创建订单---");
		
		
			// 插入订单记录
			PageData order_pd = new PageData();
			String orderId = "OM"+PrimaryKeyGenerator.generateKey();
			order_pd.put("ORDER_ID", orderId);
			order_pd.put("UID", pd.getString("UID"));
			order_pd.put("STORE_ID", staffpd.get("STORE_ID"));//选择的医生的门店
			order_pd.put("SERVICECOST_ID", "-2");//服务项目编号为-2,-2为药品
			order_pd.put("STAFF_ID", pd.getString("STAFF_ID"));//医生编号
			order_pd.put("SERVICE_STAFF_ID", staff.getSTAFF_ID());//客服编号
			order_pd.put("ORDER_MONEY", df.format(Double.parseDouble(pd.getString("PRICE")))); //每一次应收,打过折没用优惠券的价格
			order_pd.put("PAY_MONEY", df.format(Double.parseDouble(pd.getString("PRICE"))));        //减过优惠券的价格
			order_pd.put("PROPORTION", "1.0");        //用户最低折扣
			order_pd.put("CREATE_TIME", DateUtil.getTime()); // 创建时间
			order_pd.put("WECHAT_NAME", userpd.getString("name"));
			order_pd.put("WECHAT_PHONE", userpd.getString("phone"));
			order_pd.put("URL", "3");//url为3是线下买药
			order_pd.put("DISCOUNT_ID", "0.00");//不能使用优惠券
			order_pd.put("REFUND", Double.parseDouble("0.00"));
			order_pd.put("ORDER_STATUS", 5); // 已完成状态
			
		
			order_pd.put("REMARK", pd.getString("REMARK"));
			orderService.save(order_pd);
			
			/**
			 * /插入各种支付方式的支付详情记录
			 */
			//微信支付
			if (!"".equals(pd.getString("WECHATPAY_MONEY")) && !"0".equals(pd.getString("WECHATPAY_MONEY"))&& pd.get("WECHATPAY_MONEY") != null) {
				order_pd.put("WECHATPAY_MONEY", pd.getString("WECHATPAY_MONEY"));
				insertPayDetail(order_pd,0,df.format(Double.parseDouble(order_pd.getString("WECHATPAY_MONEY"))));
			}
			
			//支付宝支付
			if (!"".equals(pd.getString("ALIPAY_MONEY")) && !"0".equals(pd.getString("ALIPAY_MONEY")) && pd.get("ALIPAY_MONEY") != null ) {
				order_pd.put("ALIPAY_MONEY", pd.getString("ALIPAY_MONEY"));
				insertPayDetail(order_pd,1,df.format(Double.parseDouble(order_pd.getString("ALIPAY_MONEY"))));
			}
			
			//银行卡支付
			if (!"".equals(pd.getString("BANKPAY_MONEY")) && !"0".equals(pd.getString("BANKPAY_MONEY")) && pd.get("BANKPAY_MONEY") != null) {
				order_pd.put("BANKPAY_MONEY", pd.getString("BANKPAY_MONEY"));
				insertPayDetail(order_pd,4,df.format(Double.parseDouble(order_pd.getString("BANKPAY_MONEY"))));
			}
			
			//现金支付
			if (!"".equals(pd.getString("CASHPAY_MONEY")) && !"0".equals(pd.getString("CASHPAY_MONEY")) && pd.get("CASHPAY_MONEY") != null) {
				order_pd.put("CASHPAY_MONEY", pd.getString("CASHPAY_MONEY"));
				insertPayDetail(order_pd,3,df.format(Double.parseDouble(order_pd.getString("CASHPAY_MONEY"))));
			}
			
			//用户余额支付
			if (!"".equals(pd.getString("PRESTOREPAY_MONEY")) && !"0".equals(pd.getString("PRESTOREPAY_MONEY")) && pd.get("PRESTOREPAY_MONEY") != null) {
				
				
				order_pd.put("PRESTOREPAY_MONEY", pd.getString("PRESTOREPAY_MONEY"));
				insertPayDetail(order_pd,5,df.format(Double.parseDouble(order_pd.getString("PRESTOREPAY_MONEY"))));
			
				//插入预存消费明细记录，预存的总金额需要一次扣除n个订单的
				PageData premxpd = new PageData();
				
				premxpd.put("UID",pd.getString("UID"));
				premxpd.put("PRESTOREMX_ID", this.get32UUID());
				premxpd.put("PRESTORE_ID", prepd.getString("PRESTORE_ID"));
				premxpd.put("TYPE", 2);
				premxpd.put("STAFF_ID", staff.getSTAFF_ID());
				premxpd.put("PHONE", userpd.getString("phone"));
				premxpd.put("USERNAME", userpd.getString("username"));
				premxpd.put("CREATE_TIME", DateUtil.getTime());
				premxpd.put("PRESTOREMONEY", df.format(Double.parseDouble(pd.getString("PRESTOREPAY_MONEY"))));
				prestoremxService.save(premxpd);
				
			}
			
			
			//储值卡支付，两种明细 
			if (!"".equals(pd.getString("STOREDPAY_MONEY")) && !"0".equals(pd.getString("STOREDPAY_MONEY")) && pd.get("STOREDPAY_MONEY") != null) {
				//插入储值卡支付的订单明细
				
				order_pd.put("STOREDPAY_MONEY", pd.getString("STOREDPAY_MONEY"));
				insertPayDetail(order_pd,2,df.format(Double.parseDouble(order_pd.getString("STOREDPAY_MONEY"))));
				
				//扣除储值卡的钱，并插入明细
				weChatUserStoredCardService.countCzkMoney(Integer.parseInt(order_pd.getString("UID")), Double.parseDouble(order_pd.getString("STOREDPAY_MONEY")), order_pd.getString("STORE_ID"), order_pd.getString("SERVICE_STAFF_ID"), null);
			}
			//2 代表储值卡余额支付    6代表储值卡返点支付
			/*if (!"".equals(pd.getString("STOREDPAY_MONEY")) && !"0".equals(pd.getString("STOREDPAY_MONEY")) && pd.get("STOREDPAY_MONEY") != null) {
				//减少该用户的储值卡金额，先扣除储值卡余额，再扣除返点余额,插入储值卡的明细
				
				//该次订单储值卡需要支付的金额
				double czkMoney = Double.parseDouble(df.format(Double.parseDouble(pd.getString("STOREDPAY_MONEY"))));//这次需要交的储值卡的钱
				
				if(czkMoney<=czkRemainMoney){
					//储值卡本身的余额已经足够支付这次,先插入订单支付明细   
					order_pd.put("STOREDPAY_MONEY", czkMoney);
					insertPayDetail(order_pd,2,df.format(order_pd.get("STOREDPAY_MONEY")));
					
					//再减去储值卡本身的余额	
					czkRemainMoney = Double.parseDouble(df.format(czkRemainMoney-czkMoney));//000000000
					
					
					//插入储值卡消费明细
					PageData czkmx_pd = new PageData();
					czkmx_pd.put("STOREDDETAIL_ID", "OC"+PrimaryKeyGenerator.generateKey());
					czkmx_pd.put("UID", pd.getString("UID"));
					czkmx_pd.put("STORE_ID", staff.getSTORE_ID());
					czkmx_pd.put("STAFF_ID", staff.getSTAFF_ID());
					czkmx_pd.put("CREATE_TIME", DateUtil.getTime());
					czkmx_pd.put("MONEY", czkMoney);
					czkmx_pd.put("POINTS", 0);
					czkmx_pd.put("TYPE", 5);
					czkmx_pd.put("STATUS", 0);//0是成功
					storeddetailService.save(czkmx_pd);
				}else{
					//储值卡本身的余额不够支付这次，需要从返点金额里扣除不足的部分，同时插入用返点支付的订单明细
					//还需支付的钱
					double hm = Double.parseDouble(df.format(czkMoney - czkRemainMoney));  
					//double pointsmm = (Double)czkpd.get("REMAIN_POINTS") - hm;
					
					//先插入储值卡余额的消费明细
					if(czkRemainMoney>0){
						order_pd.put("STOREDPAY_MONEY",czkRemainMoney);
						insertPayDetail(order_pd,2,df.format((order_pd.get("STOREDPAY_MONEY"))));
						
					}
					
					//再插入储值卡返点的消费明细
					order_pd.put("POINTS_MONEY",Double.parseDouble(df.format(hm)));
					insertPayDetail(order_pd,6,df.format((order_pd.get("POINTS_MONEY"))));
					
					
					//插入储值卡消费明细
					PageData czkmx_pd = new PageData();
					czkmx_pd.put("STOREDDETAIL_ID", "OC"+PrimaryKeyGenerator.generateKey());
					czkmx_pd.put("UID", pd.getString("UID"));
					czkmx_pd.put("STORE_ID", staff.getSTORE_ID());//客服的门店编号
					czkmx_pd.put("STAFF_ID", staff.getSTAFF_ID());
					czkmx_pd.put("CREATE_TIME", DateUtil.getTime());
					czkmx_pd.put("MONEY", czkRemainMoney);   //修改了
					czkmx_pd.put("POINTS", Double.parseDouble(df.format(hm)));
					czkmx_pd.put("TYPE", 5);//线下消费
					czkmx_pd.put("STATUS", 0);
					storeddetailService.save(czkmx_pd);
					
					czkRemainMoney = Double.parseDouble("0.00");//000000000
				}
			}
			
			
			NCUTLOG.save(
					Jurisdiction.getUsername(),
					"用户：" + (userpd.getString("name")!=null?userpd.getString("name"):userpd.getString("username")) + "，手机号："
							+ userpd.getString("phone") + "。到店支付了："
							+ order_pd.get("PAY_MONEY") + "元，项目为：划价 ,订单号为："
							+ order_pd.getString("ORDER_ID"), this.getRequest()
							.getRemoteAddr());
		
		
		
		*//**
		 * 关于用户储值卡的金额
		 * 有n次订单，使用储值卡总金额为money,那么储值卡金额要减去money
		 * 但是要插入n次订单支付方式和n次储值卡明细，每次的金额为money/n
		 *//*
		if (!"".equals(pd.getString("STOREDPAY_MONEY")) && !"0".equals(pd.getString("STOREDPAY_MONEY")) && pd.get("STOREDPAY_MONEY") != null) {
			
			//用户用储值卡支付的钱
			double user_STOREDPAY_MONEY = Double.parseDouble(pd.getString("STOREDPAY_MONEY"));
			
			//如果用户支付的钱<储值卡余额的钱，即储值卡的钱够支付这n次订单,那么直接减去用户支付的钱
			if(user_STOREDPAY_MONEY < (Double)czkpd.get("REMAIN_MONEY")){
				czkpd.put("REMAIN_MONEY", (Double)czkpd.get("REMAIN_MONEY")-user_STOREDPAY_MONEY);
				czkpd.put("REMAIN_POINTS", (Double)czkpd.get("REMAIN_POINTS"));
			}else{
				//储值卡余额的钱不够支付这n次订单，需要先把储值卡的钱扣为0，再扣除余额的钱
				double hm = user_STOREDPAY_MONEY - (Double)czkpd.get("REMAIN_MONEY");  
				double pointsmm = (Double)czkpd.get("REMAIN_POINTS") - hm;
				czkpd.put("REMAIN_MONEY", Double.parseDouble("0.00"));
				czkpd.put("REMAIN_POINTS", pointsmm);
			}
			customstoredService.editSubMoney(czkpd);
		}*/
		
		
		if (!"".equals(pd.getString("PRESTOREPAY_MONEY")) && !"0".equals(pd.getString("PRESTOREPAY_MONEY")) && pd.get("PRESTOREPAY_MONEY") != null) {
			double pre_mm = (Double)prepd.get("SUM_MONEY") - Double.parseDouble(pd.getString("PRESTOREPAY_MONEY"));
			prepd.put("SUM_MONEY", pre_mm);
			prestoreService.edit(prepd);
		}
		
		
		
		logger.debug("--- end 创建订单---");
		
		response.setContentType("text/xml;charset=UTF-8");
		response.getWriter().write("{\"success\":\"ok\"}");
		
	}
	 
	
}
