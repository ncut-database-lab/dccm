package cn.ncut.controller.user.userpay;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.shiro.session.Session;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.ncut.annotation.Token;
import cn.ncut.controller.base.BaseController;
import cn.ncut.entity.Page;
import cn.ncut.entity.system.Staff;
import cn.ncut.entity.system.UserDiscount;
import cn.ncut.entity.system.UserDiscountGroup;
import cn.ncut.entity.wechat.pojo.WeChatPayPerformance;
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
import cn.ncut.service.wechat.payPerformance.impl.WeChatPayPerformanceService;
import cn.ncut.util.BigDecimalUtil;
import cn.ncut.util.Const;
import cn.ncut.util.DateUtil;
import cn.ncut.util.Jurisdiction;
import cn.ncut.util.PageData;
import cn.ncut.util.wechat.PrimaryKeyGenerator;

/**
 * 用户缴费管理
 * 
 * @author ljj
 * 
 */
@Controller
@RequestMapping(value = "/userpay")
public class UserPayController extends BaseController {

	String menuUrl = "userpay/show.do"; // 菜单地址(权限用)

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

	@Resource(name = "prestoreService")
	private PreStoreManager prestoreService;

	@Resource(name = "prestoremxService")
	private PreStoreMxManager prestoremxService;

	@Resource(name = "customstoredService")
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

	@Resource(name = "weChatPayPerformanceService")
	private WeChatPayPerformanceService weChatPayPerformanceService;

	// 菜单进入地址，初始化页面
	@RequestMapping(value = "/show")
	public ModelAndView showPayPage(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();

		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();
		Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));

		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}

		page.setPd(pd);

		// 查询用户信息
		List<PageData> userList = (List<PageData>) memberService
				.listCompleteMemberlistPage(page);// 列出Member列表

		// 查询此门店的医生信息
		pd.put("STORE_ID", staff.getSTORE_ID());

		List<PageData> staffPdlist = staffService.listAll(pd);

		mv.addObject("staffPdlist", staffPdlist);
		mv.addObject("userList", userList);
		mv.addObject("pd", pd);
		mv.setViewName("user/userpay/user_pay");
		return mv;
	}

	// 根据医生信息异步出属于他的服务项目
	@RequestMapping(value = "/refreshProjectCostBySId")
	public void refreshProjectCostBySId(HttpServletResponse response)
			throws Exception {

		PageData pd = new PageData();
		pd = this.getPageData();

		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();

		// 在服务标准表中查询所属门店的当前选中医生的所有项目
		List<PageData> pdlist = servicecostService
				.findServiceAndCostByStaff_id(pd);
		String s = new ObjectMapper().writeValueAsString(pdlist);

		response.setContentType("text/xml;charset=UTF-8");
		response.getWriter().write(s);
	}

	// 根据服务项目生成服务价钱
	@RequestMapping(value = "/refreshCostByProjectid")
	public void refreshCostByProjectid(HttpServletResponse response)
			throws Exception {

		PageData pd = new PageData();
		pd = this.getPageData();

		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();
		// pd.put("STORE_ID",
		// ((Staff)session.getAttribute(Const.SESSION_USER)).getSTORE_ID());
		pd.put("PID", pd.get("PROJECT_ID"));

		// 根据服务项目和员工生成服务价钱
		List<PageData> costlist = servicecostService
				.queryPriceByPIDAndStaff(pd);
		String s = new ObjectMapper().writeValueAsString(costlist);

		response.setContentType("text/xml;charset=UTF-8");
		response.getWriter().write(s);
	}

	@RequestMapping(value = "/queryAlreadyAppointAmount")
	public void queryAlreadyAppointAmount(HttpServletResponse response)
			throws Exception {
		PageData pd = new PageData();
		pd = this.getPageData();
		String appointtime = convertDate2(pd.getString("selecttime"),
				pd.getString("id"));
		// 查询预约表中 该医生 该预约时间的有多少人
		pd.put("APPOINT_TIME", appointtime);
		List<PageData> pdlist = customappointService.querySum(pd);

		String responseJson = "{\"count\":\"" + pdlist.size() + "\"}";
		response.getWriter().write(responseJson);

	}

	@RequestMapping(value = "/showAvaliableProject")
	public void showAvaliableProject(HttpServletResponse response)
			throws Exception {

		PageData pd = new PageData();
		pd = this.getPageData();

		pd = discountService.findById(pd);

		String[] projectIds = pd.getString("PROJECT_IDS").split(",");

		// 构造可用项目的List
		List<String> projectNames = new ArrayList<String>();

		for (int i = 0; i < projectIds.length; i++) {
			PageData p = new PageData();
			p.put("PROJECT_ID", projectIds[i]);
			p = serviceprojectService.findById(p);
			projectNames.add(p.getString("PNAME"));
		}
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.getWriter().write(
				JSONArray.fromObject(projectNames).toString());
	}

	/**
	 * 确认订单
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/confirmAndPayOrder")
	@Token(save = true)
	public ModelAndView confirmAndPayOrder() throws Exception {
		ModelAndView mv = this.getModelAndView();

		PageData pd = new PageData();
		pd = this.getPageData();

		// 查询 下订单的用户信息,包括储值卡，用户余额
		PageData userpd = memberService
				.findUserStorededAndPrestoreByUid(Integer.parseInt(pd
						.getString("uid")));

		// 用户个人优惠折扣
		double person_proportion = (Double) userpd.get("proportion");
		// 用户类型的折扣
		double category_proportion = (Double) userpd.get("categoryproportion");
		// 用户最低折扣
		double lowProportion = person_proportion < category_proportion ? person_proportion
				: category_proportion;

		String doctor = "";
		// 分离项目和次数
		BigDecimal sumOrderMoney = new BigDecimal("0");
		Map costMap = new LinkedHashMap();
		JSONArray jsonArr = JSONArray
				.fromObject(pd.getString("SERVICECOST_ID"));
		Iterator<Object> it = jsonArr.iterator();
		while (it.hasNext()) {
			JSONObject obj = (JSONObject) it.next();
			// 算出总价格
			PageData cost_pd = new PageData();
			cost_pd.put("SERVICECOST_ID", obj.get("cost_id"));

			// 查询该用户选择的服务项目收费信息
			PageData costPd = servicecostService.findById(cost_pd);

			// 订单中单次服务项目的价格
			BigDecimal singleprice = (BigDecimal) costPd.get("PRICE");

			doctor = costPd.getString("STAFF_NAME");
			costMap.put(costPd.getString("PNAME") + " / " + singleprice,
					obj.get("cishu"));

			// 打折前价格累加
			BigDecimal OrderMoney = singleprice.multiply(
					new BigDecimal(obj.get("cishu").toString())).setScale(2,
					BigDecimal.ROUND_HALF_UP);

			sumOrderMoney = sumOrderMoney.add(OrderMoney);
		}

		// 订单打折后的价格，即单次服务项目的价格*次数*折扣
		BigDecimal zhekouPrice = sumOrderMoney.multiply(
				new BigDecimal(lowProportion)).setScale(2,
				BigDecimal.ROUND_HALF_UP);

		// 查询该用户有什么优惠券组合
		List<UserDiscountGroup> discountGroupPdList = discountService
				.queryDiscountGroupByUid(Integer.parseInt(pd.getString("uid")));

		// 遍历用户的优惠券组合，往组合里添加该用户拥有的属于组合的优惠券
		for (UserDiscountGroup group : discountGroupPdList) {
			Integer sum = 0;// 定义优惠券组中所有未使用的优惠券个数
			List<UserDiscount> discountPdList = discountService
					.queryDiscountByUidAndGroupid(group);

			for (UserDiscount userdiscount : discountPdList)// 遍历它的每一个优惠券
			{
				sum = sum + userdiscount.getCount();

			}
			group.setSum(sum);

			group.setUserDiscounts(discountPdList);

		}

		mv.addObject("costIdAndNumJson", jsonArr.toString());
		mv.addObject("doctor", doctor);
		mv.addObject("costMap", costMap);
		mv.addObject("userpd", userpd);
		mv.addObject("pd", pd);
		mv.addObject("lowProportion", lowProportion);
		mv.addObject("sumOrderMoney", sumOrderMoney);
		mv.addObject("zhekouPrice", zhekouPrice);
		mv.addObject("discountGroupPdList", discountGroupPdList);
		mv.setViewName("user/userpay/confirmOrder");
		return mv;
	}

	// 创建订单
	@ResponseBody
	@RequestMapping(value = "/createOrder")
	@Token(remove = true)
	public Map<String, String> createOrder(HttpServletResponse response)
			throws Exception {
		Map<String, String> returnMap = new HashMap<>();
		Map<String, List<Double>> map = new LinkedHashMap<>();
		DecimalFormat df = new DecimalFormat("#0.00");   

		PageData pd = new PageData();
		pd = this.getPageData();

		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();
		Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));

		// 用户最低折扣
		double lowProportion = Double.parseDouble(pd.getString("proportion"));
		double needMoney;
		double isSingleProject = 0;
		JSONArray jsonArr;

		// 得到选择的项目和次数，并拼成一个map
		String costIdAndNum = pd.getString("costIdAndNum");

		Map<String, Double> serviceMap = new LinkedHashMap();
		try {
			jsonArr = JSONArray.fromObject(costIdAndNum);

			// 判断是单个项目还是多个项目
			if (jsonArr.size() == 1)
				needMoney = Double.parseDouble(pd.getString("needMoney"));
			else { //多个项目
				needMoney = 0.0;
				isSingleProject = 1; 
			}

			Iterator<Object> it = jsonArr.iterator();
			while (it.hasNext()) {
				JSONObject obj = (JSONObject) it.next();
				for (int i = 0; i < Integer.parseInt(obj.getString("cishu")); i++) {
					PageData cost_pd = new PageData();
					cost_pd.put("SERVICECOST_ID", obj.get("cost_id"));
					cost_pd = servicecostService.findById(cost_pd);
					BigDecimal singleMoney = (BigDecimal)cost_pd.get("PRICE");
					double zhekouSingleMoney = singleMoney.doubleValue() * lowProportion;
					serviceMap.put(obj.getString("cost_id") + "_" + (i+1), zhekouSingleMoney);
					if (isSingleProject == 1)
						needMoney += zhekouSingleMoney;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("msg", "参数传递错误！");
			returnMap.put("code", "500");
			return returnMap;
		}

		// 用户选择的各种支付方式
		List<Double> payList = new LinkedList<>();
		if (!"".equals(pd.getString("PRESTOREPAY_MONEY")) && pd.get("PRESTOREPAY_MONEY") != null)
			payList.add(Double.parseDouble(pd.getString("PRESTOREPAY_MONEY"))); // 余额// 0			
		else
			payList.add(0.0);
		
		if (!"".equals(pd.getString("STOREDPAY_MONEY")) && pd.get("STOREDPAY_MONEY") != null) 
			payList.add(Double.parseDouble(pd.getString("STOREDPAY_MONEY"))); // 储值卡// 1
		else
			payList.add(0.0);
		
		if (!"".equals(pd.getString("BANKPAY_MONEY")) && pd.get("BANKPAY_MONEY") != null) 
			payList.add(Double.parseDouble(pd.getString("BANKPAY_MONEY"))); // 银行卡 2
		else
			payList.add(0.0);
		
		if (!"".equals(pd.getString("ALIPAY_MONEY")) && pd.get("ALIPAY_MONEY") != null ) 
			payList.add(Double.parseDouble(pd.getString("ALIPAY_MONEY"))); // 支付宝 3
		else
			payList.add(0.0);
		
		if (!"".equals(pd.getString("WECHATPAY_MONEY")) && pd.get("WECHATPAY_MONEY") != null)
			payList.add(Double.parseDouble(pd.getString("WECHATPAY_MONEY"))); // 微信// 4		
		else
			payList.add(0.0);
		
		if (!"".equals(pd.getString("CASHPAY_MONEY")) && pd.get("CASHPAY_MONEY") != null)
			payList.add(Double.parseDouble(pd.getString("CASHPAY_MONEY"))); // 现金 5
		else
			payList.add(0.0);

		Double sumMoney = 0.0;
		for (int i = 0; i < payList.size(); i++) {
			sumMoney += payList.get(i);
		}

		if (needMoney > sumMoney) {
			returnMap.put("msg", "用户余额不足！");
			returnMap.put("code", "500");
			return returnMap;
		}

		JSONObject obj = (JSONObject) jsonArr.get(0);
		int n = Integer.parseInt(obj.getString("cishu"));
		
		Iterator<Map.Entry<String, Double>> it = serviceMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Double> entry = it.next();
			//如果是单个项目
			Double thisMoney;
			if (isSingleProject == 0.0) {
				double m = needMoney/(double)n;
				thisMoney = Double.parseDouble(df.format(m));
			}else{
				thisMoney = entry.getValue();
			}
			String thisKey = entry.getKey();
			List<Double> eachPayList = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				if (payList.get(i) >= thisMoney) {
					payList.set(i, payList.get(i) - thisMoney);
					eachPayList.add(thisMoney);
					break;
				} else {
					eachPayList.add(payList.get(i));
					thisMoney -= payList.get(i);
					payList.set(i, 0.0);
				}
			}
			Integer list2Size = eachPayList.size();
			if (list2Size < 6) {
				for (int i = 0; i < 6 - list2Size; i++) {
					eachPayList.add(0.0);
				}
			}

			map.put(thisKey, eachPayList);
			System.out.println(thisKey + ":" + eachPayList);			
		}
		
		PageData paramPd = new PageData();
		paramPd.put("UID", pd.getString("UID"));
		paramPd.put("STORE_ID", staff.getSTORE_ID());
		paramPd.put("REMARK", pd.getString("REMARK"));
		paramPd.put("SERVICE_STAFF_ID", staff.getSTAFF_ID());
		paramPd.put("proportion", lowProportion);
		paramPd.put("isSingleProject", isSingleProject);
		paramPd.put("serviceTime", pd.getString("servicetime"));

		if (isSingleProject == 0) {
			if (Double.parseDouble(pd.getString("DiscountMoney")) != 0
					&& pd.get("DiscountMoney") != null
					&& !"".equals(pd.getString("DiscountMoney"))) {
				BigDecimal DiscountMoney = new BigDecimal(
						pd.getString("DiscountMoney"));
				BigDecimal averageDiscountMoney = DiscountMoney.divide(
						new BigDecimal(n), 2, RoundingMode.HALF_UP);
				paramPd.put("averageDiscountMoney", averageDiscountMoney);
				paramPd.put("DiscountJson", pd.getString("DiscountJson"));
			}		
		}
		// 事务，进行数据库操作
		try {
			orderService.createOrder(map, paramPd);
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("msg", "订单创建失败！");
			returnMap.put("code", "500");
			return returnMap;
		}
		returnMap.put("msg", "操作成功！");
		returnMap.put("code", "200");
		return returnMap;
	}

	/**
	 * 插入订单后插入n条支付方式的明细
	 * 
	 * @param pd
	 * @param method
	 * @throws Exception
	 */
	public void insertPayDetail(PageData pd, int method, String methodText)
			throws Exception {
		PageData ppdd = new PageData();
		ppdd.put("ORDERMX_ID", this.get32UUID());
		ppdd.put("UID", pd.getString("UID"));
		ppdd.put("ORDER_ID", pd.getString("ORDER_ID"));
		ppdd.put("ORDER_MONEY", pd.getString("ORDER_MONEY"));
		ppdd.put("PAY_MONEY", (Double) pd.get(methodText));
		ppdd.put("PAY_METHOD", method);
		ppdd.put("PAY_TIME", pd.getString("CREATE_TIME"));
		ppdd.put("REMARK", "");

		ordermxService.save(ppdd);
	}

	/**
	 * 大表转换时间
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/changeTime")
	public void changeTime(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PageData pd = new PageData();
		pd = this.getPageData();
		String id = (String) pd.get("Id");
		String new_date = convertDate((String) pd.get("THE_DATE"), id);
		// 日期比较，如果s>=e 返回true 否则返回false
		Boolean flag = false;
		if (DateUtil.compareToDate(new_date, DateUtil.getDay()) > 0) {
			flag = true;
		} else if (DateUtil.compareToDate(new_date, DateUtil.getDay()) == 0
				&& Integer.parseInt(id.length() == 4 ? id.substring(0, 2) : id
						.substring(0, 1)) > new Date().getHours() - 1) {
			flag = true;
		} else {
			flag = false;
		}
		String msg = "";
		if (flag) {
			msg = "ok";
		} else {
			msg = "error";
		}
		String hour = id.length() == 4 ? id.substring(0, 2) : ("0" + id
				.substring(0, 1));
		String all = new_date + " " + hour + ":00:00";
		String responseJson = "{\"selectTime\":\"" + all + "\",\"msg\":\""
				+ msg + "\"}";
		response.getWriter().write(responseJson);
	}

	/**
	 * 根据选择的时间id（7，1）得到选择的是YYYY-MM-dd hh:mm:ss的格式
	 * 
	 * @param selectedtime
	 *            选择的日期（String YYYY-MM-dd）
	 * @param idtime
	 *            例如（7，1）
	 * @return
	 * @throws Exception
	 */
	public String convertDate(String selectedtime, String idtime)
			throws Exception {

		// 通过日期获得数据库中周几减一的数字
		String week = new SimpleDateFormat("E").format(DateUtil
				.fomatDate(selectedtime));
		int week_which = DateUtil
				.ChineseToNum(week.substring(week.length() - 1));
		int servicetime_week = Integer.parseInt(idtime.split(",")[1]);
		return DateUtil.getEveryDay(selectedtime,
				(servicetime_week - week_which));
	}

	/**
	 * 根据选择的时间id（7，1）得到选择的是YYYY-MM-dd hh:mm:ss的格式
	 * 
	 * @param selectedtime
	 *            选择的日期（String YYYY-MM-dd）
	 * @param idtime
	 *            例如（7，1）
	 * @return
	 * @throws Exception
	 */
	public String convertDate2(String selectedtime, String idtime)
			throws Exception {

		String[] dd = idtime.split(",");
		String hours = dd[0];
		// 通过日期获得数据库中周几减一的数字
		String week = new SimpleDateFormat("E").format(DateUtil
				.fomatDate(selectedtime));
		int week_which = DateUtil
				.ChineseToNum(week.substring(week.length() - 1));
		int servicetime_week = Integer.parseInt(dd[1]);

		String currentDate = DateUtil.getEveryDay(selectedtime,
				(servicetime_week - week_which));
		if (dd[0].length() < 2) {
			hours = "0" + dd[0];
		}
		return (currentDate + " " + hours + ":00:00");
	}

}