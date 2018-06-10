package cn.ncut.controller.user.membercallback;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
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
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.shiro.session.Session;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import cn.ncut.controller.base.BaseController;
import cn.ncut.entity.Page;
import cn.ncut.entity.system.Staff;
import cn.ncut.entity.system.UserDiscount;
import cn.ncut.entity.system.UserDiscountGroup;
import cn.ncut.util.AppUtil;
import cn.ncut.util.BigDecimalUtil;
import cn.ncut.util.Const;
import cn.ncut.util.DateUtil;
import cn.ncut.util.ObjectExcelView;
import cn.ncut.util.PageData;
import cn.ncut.util.Jurisdiction;
import cn.ncut.util.Tools;
import cn.ncut.util.UuidUtil;
import cn.ncut.util.wechat.PrimaryKeyGenerator;
import cn.ncut.util.wechat.TimeAdjust;
import cn.ncut.service.finance.customappoint.CustomappointManager;
import cn.ncut.service.finance.discount.DiscountManager;
import cn.ncut.service.finance.serviceall.ServiceCostManager;
import cn.ncut.service.finance.serviceall.ServiceProjectManager;
import cn.ncut.service.system.ncutlog.NcutlogManager;
import cn.ncut.service.system.staff.StaffManager;

import cn.ncut.service.user.member.MemberManager;

import cn.ncut.service.system.store.StoreManager;

import cn.ncut.service.user.membercallback.MemberCallBackManager;
import cn.ncut.service.user.order.OrderManager;
import cn.ncut.service.user.order.OrderMxManager;
import cn.ncut.service.user.usercategory.UsercategoryManager;
import cn.ncut.service.user.userdiscount.UserDiscountManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 说明：用户回电 创建人：FH Q313596790 创建时间：2016-12-19
 */
@Controller
@RequestMapping(value = "/membercallback")
public class MemberCallBackController extends BaseController {

	String menuUrl = "membercallback/list.do"; // 菜单地址(权限用)
	@Resource(name = "membercallbackService")
	private MemberCallBackManager membercallbackService;
	@Resource(name = "staffService")
	private StaffManager staffService;
	@Resource(name = "servicecostService")
	private ServiceCostManager servicecostService;
	@Resource(name = "orderService")
	private OrderManager orderService;

	@Resource(name = "ordermxService")
	private OrderMxManager ordermxService;
	@Resource(name = "memberService")
	private MemberManager memberService;

	@Resource(name = "customappointService")
	private CustomappointManager customappointService;

	@Resource(name = "storeService")
	private StoreManager storeService;
	@Resource(name = "usercategoryService")
	private UsercategoryManager usercategoryService;

	@Resource(name = "ncutlogService")
	private NcutlogManager NCUTLOG;

	@Resource(name = "serviceprojectService")
	private ServiceProjectManager serviceProjectService;
	@Resource(name = "discountService")
	private DiscountManager discountService;

	@Resource(name = "serviceprojectService")
	private ServiceProjectManager serviceprojectService;
	@Resource(name = "userdiscountService")
	private UserDiscountManager userdiscountService;
	
	
	
	

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增MemberCallBack");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("MEMBERCALLBACK_ID", this.get32UUID()); // 主键
		/*
		 * pd.put("UID", "0"); //uid pd.put("NAME", ""); //客户姓名
		 * pd.put("STORE_ID", ""); //门店编号 pd.put("STAFF_ID", ""); //员工编号
		 * pd.put("PHONE", ""); //电话号码
		 */membercallbackService.save(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	public void delete(PrintWriter out) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "删除MemberCallBack");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		membercallbackService.delete(pd);
		out.write("success");
		out.close();
	}

	/**
	 * 大表转换时间
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/changeTheTime")
	public void changeTheTime(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PageData pd = new PageData();
		pd = this.getPageData();
		String id = (String) pd.get("Id");
		String the_date = (String) pd.get("THE_DATE");
		if (id != null && the_date != null) {
			String new_date = convertDate(the_date, id);
			System.out.print(new_date);
			// response.getWriter().write("{\"s\":" + "\"" +new_date + "\"}");
			response.getWriter().write(new_date);
		} else {
			response.getWriter().write("");
		}
	}

	
	/**
	 * 确认订单
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/confirmAndPayOrder")
	public ModelAndView confirmAndPayOrder(HttpSession session,
			HttpServletResponse response) throws Exception{
		ModelAndView mv = this.getModelAndView();
		
		PageData pd = new PageData();
		
		pd = this.getPageData();
		
		//查询 下订单的用户信息,包括储值卡，用户余额
		PageData userpd = memberService.findUserStorededAndPrestoreByUid(Integer.parseInt(pd.getString("UID")));
		
		//用户个人优惠折扣
		double  person_proportion = (Double)userpd.get("proportion");
		//用户类型的折扣
		double  category_proportion = (Double)userpd.get("categoryproportion");
		//用户最低折扣
		double lowProportion = person_proportion < category_proportion ? person_proportion : category_proportion;
		
		String doctor = "";
		// 分离项目和次数
		BigDecimal sumOrderMoney = new BigDecimal("0");
		Map costMap = new LinkedHashMap();
		JSONArray jsonArr = JSONArray.fromObject(pd.getString("projectcost_id"));
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
			costMap.put(costPd.getString("PNAME") + " / " + singleprice, obj.get("cishu"));

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
		
		//查询该用户有什么优惠券组合
		List<UserDiscountGroup> discountGroupPdList = discountService.queryDiscountGroupByUid(Integer.parseInt(pd.getString("UID")));
		
		//遍历用户的优惠券组合，往组合里添加该用户拥有的属于组合的优惠券
		for(UserDiscountGroup group : discountGroupPdList){
			Integer sum = 0;//定义优惠券组中所有未使用的优惠券个数
			List<UserDiscount> discountPdList = discountService.queryDiscountByUidAndGroupid(group);
			
			for(UserDiscount userdiscount : discountPdList)//遍历它的每一个优惠券
			{
				sum = sum + userdiscount.getCount();
				}
			group.setSum(sum);
			group.setUserDiscounts(discountPdList);			
		}
		
		mv.addObject("isSingleProject", costMap.size());
		mv.addObject("costIdAndNumJson", jsonArr.toString());
		mv.addObject("doctor", doctor);
		mv.addObject("costMap", costMap);
		mv.addObject("lowProportion",lowProportion);
		mv.addObject("pd", pd);
		mv.addObject("userpd", userpd);
		mv.addObject("sumOrderMoney",sumOrderMoney);
		mv.addObject("zhekouPrice",zhekouPrice);
		mv.addObject("discountGroupPdList", discountGroupPdList);
		mv.setViewName("user/membercallback/confirmOrder");
		return mv;
	}
	
	@RequestMapping(value = "/judgeAvaliableProject")
	public void judgeAvaliableProject(HttpServletResponse response) throws Exception{
		
		PageData pd = new PageData();
		pd = this.getPageData();
		String pid = pd.getString("PID");
		String group_discount = pd.getString("DISCOUNT_ID");
		String discount_id = group_discount.substring(group_discount.indexOf("-")+1);
		pd.put("DISCOUNT_ID", discount_id);
		pd = discountService.findById(pd);
		
		String[] projectIds = pd.getString("PROJECT_IDS").split(",");
		boolean flag = false;
		
		for(int i=0; i<projectIds.length; i++){
			System.out.println(projectIds[i]);
			if(pid.equals(projectIds[i])){
				flag = true;
				break;
			}
		}
		if(flag==true){
			String responseJson = "{\"result\":\"" + flag + "\"}";
			response.getWriter().write(responseJson);
		}
		else{
			String responseJson = "{\"result\":\"" + flag + "\"}";
			response.getWriter().write(responseJson);
		}
	}
	/**
	 * 创建待支付订单
	 * 
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/createOrder")
	public Map<String,String> createOrder(HttpSession session,
			HttpServletResponse response) throws Exception {
		
		Map<String, String> returnMap = new HashMap<>();
		Map<String, List<Double>> map = new LinkedHashMap<>();
		DecimalFormat df = new DecimalFormat("#0.00");   

		PageData pd = new PageData();
		pd = this.getPageData();

		// 得到当前的客服的门店编号
		Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));

		// 用户最低折扣
		double lowProportion = Double.parseDouble(pd.getString("proportion"));
		double needMoney = 0.0;
		double isSingleProject = 0;
		JSONArray jsonArr;

		// 得到选择的项目和次数，并拼成一个map
		String costIdAndNum = pd.getString("costIdAndNum");

		Map<String, Double> serviceMap = new LinkedHashMap();
		try {
			jsonArr = JSONArray.fromObject(costIdAndNum);

			// 判断是单个项目还是多个项目
			double averageSingleMoney = 0.0;
			if (jsonArr.size() == 1){
				needMoney = Double.parseDouble(pd.getString("needMoney"));
				JSONObject obj = (JSONObject) jsonArr.get(0);
				int n = Integer.parseInt(obj.getString("cishu"));
				averageSingleMoney = Double.parseDouble(df.format(needMoney/n));
			}else { //多个项目
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
					if(isSingleProject == 1)
						serviceMap.put(obj.getString("cost_id") + "_" + (i+1), zhekouSingleMoney);
					else
						serviceMap.put(obj.getString("cost_id") + "_" + (i+1), averageSingleMoney);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("msg", "参数传递错误！");
			returnMap.put("code", "500");
			return returnMap;
		}
		
		JSONObject obj = (JSONObject) jsonArr.get(0);
		int n = Integer.parseInt(obj.getString("cishu"));
					
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
			orderService.createOrderHuiDian(serviceMap, paramPd);
			
			membercallbackService.updatestatus(pd);
			
			/*// /以下修改回电表状态
			PageData staffPd = staffService.findById(pd);
			PageData serviceCostPd = servicecostService.findById(pd);

			NCUTLOG.save(Jurisdiction.getUsername(),
					("新建微信端客户订单:用户名：" + (pd.getString("NAME") != null ? pd
							.getString("NAME") : pd.getString("WECHAT_NAME")) + " ，手机号 "
							+ (pd.getString("WECHAT_PHONE") == null ? pd
							.getString("PHONE") : pd.getString("WECHAT_PHONE"))
							+ "，并预约了" + staffPd.getString("STAFF_NAME") + "医生"
							+ "的"
							+ serviceCostPd.getString("PNAME")+"项目"), this
							.getRequest().getRemoteAddr());*/			
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
	 * 增加时间
	 * 
	 * @param
	 * @throws Exception
	 */
	public static String addDate(String day, int x) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 24小时制
	
		Date date = null;
		try {
			date = format.parse(day);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (date == null)
			return "";
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, x);// 24小时制
		
		date = cal.getTime();
		cal = null;
		return format.format(date);
	}

	/**
	 * 修改
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit")
	public ModelAndView edit() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "修改MemberCallBack");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		membercallbackService.edit(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
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

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page, HttpSession session) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表MemberCallBack");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}

		// List<PageData> varList = membercallbackService.list(page);
		// //列出MemberCallBack列表
		Staff sessionstaff = (Staff) session.getAttribute(Const.SESSION_USER);
		pd.put("STORE_ID", sessionstaff.getSTORE_ID());//客服本来的门店
		List<PageData>	storeList = storeService.findAllNames(pd);
		pd.put("STOREID", pd.get("STOREID"));
		pd.put("STORE_ID", pd.get("STORE_ID"));
		page.setPd(pd);
		
		List<PageData>	varList = membercallbackService.listStaffAndStore(page);
		
		mv.setViewName("user/membercallback/membercallback_list");
		mv.addObject("varList", varList);
		mv.addObject("storeList", storeList);
		mv.addObject("pd", pd);
		mv.addObject("STORE_ID",pd.get("STOREID"));
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 去新增页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goAdd")
	public ModelAndView goAdd() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("user/membercallback/membercallback_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 预约 创建订单和预约记录
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/makeOrderandAppoint")
	public ModelAndView makeOrderandAppoint(HttpSession session)
			throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = membercallbackService.findById(pd);// 根据id读取
		Staff sessionstaff = (Staff) session.getAttribute(Const.SESSION_USER);
		pd.put("STORE_ID", sessionstaff.getSTORE_ID());
		List<PageData> staffPdlist = staffService.listSelfStoreStaff(pd);// 查询自己门店中工作中的医生
		List<PageData> Initiallist = servicecostService
				.findServiceAndCostByStaff_id(pd);
		mv.setViewName("user/membercallback/membercallback_createOrderAndAppoint");
		mv.addObject("staffPdlist", staffPdlist);
		mv.addObject("Initiallist", Initiallist);
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 去修改页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
	public ModelAndView goEdit() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = membercallbackService.findById(pd); // 根据ID读取

		PageData ppd = new PageData();
		PageData staffname = staffService.findById(pd);
		PageData storename = storeService.findById(pd);
		pd.put("STAFF_NAME", staffname.get("STAFF_NAME"));
		pd.put("STORE_NAME", storename.get("STORE_NAME"));

		mv.setViewName("user/membercallback/membercallback_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 批量删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "批量删除MemberCallBack");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return null;
		} // 校验权限
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (null != DATA_IDS && !"".equals(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			membercallbackService.deleteAll(ArrayDATA_IDS);
			pd.put("msg", "ok");
		} else {
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}

	/**
	 * 导出到excel
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel")
	public ModelAndView exportExcel() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "导出MemberCallBack到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("uid"); // 1
		titles.add("客户姓名"); // 2
		titles.add("门店编号"); // 3
		titles.add("员工编号"); // 4
		titles.add("电话号码"); // 5
		dataMap.put("titles", titles);
		List<PageData> varOList = membercallbackService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).get("UID").toString()); // 1
			vpd.put("var2", varOList.get(i).getString("NAME")); // 2
			vpd.put("var3", varOList.get(i).getString("STORE_ID")); // 3
			vpd.put("var4", varOList.get(i).getString("STAFF_ID")); // 4
			vpd.put("var5", varOList.get(i).getString("PHONE")); // 5
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,
				true));
	}
	public void insertPayDetail(PageData pd, int method, String methodText) throws Exception{
		PageData ppdd = new PageData();
		ppdd.put("ORDERMX_ID", this.get32UUID());
		ppdd.put("UID", pd.getString("UID"));
		ppdd.put("ORDER_ID", pd.getString("ORDER_ID"));
		//ppdd.put("ORDER_MONEY", pd.getString("ORDER_MONEY"));
		ppdd.put("PAY_MONEY", (Double)pd.get(methodText));
		ppdd.put("PAY_METHOD", method);
		ppdd.put("PAY_TIME", pd.getString("CREATE_TIME"));
		ppdd.put("REMARK", "");
		
		ordermxService.save(ppdd);
	}
}
