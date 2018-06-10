package cn.ncut.controller.finance.valuecard;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import cn.ncut.util.AppUtil;
import cn.ncut.util.Const;
import cn.ncut.util.DateUtil;
import cn.ncut.util.ObjectExcelView;
import cn.ncut.util.PageData;
import cn.ncut.util.Jurisdiction;
import cn.ncut.util.wechat.PrimaryKeyGenerator;
import cn.ncut.service.finance.businesscontact.BusinesscontactManager;
import cn.ncut.service.finance.staffpoint.StaffpointManager;
import cn.ncut.service.finance.valuecard.StoredCategoryManager;
import cn.ncut.service.system.ncutlog.NcutlogManager;
import cn.ncut.service.system.staff.StaffManager;
import cn.ncut.service.user.customstored.CustomStoredManager;
import cn.ncut.service.user.member.MemberManager;
import cn.ncut.service.user.storeddetail.StoredDetailManager;
import cn.ncut.service.user.usergroup.UsergroupManager;

/**
 * 
 * 
 * <p>
 * Title:StoredCategoryController
 * </p>
 * <p>
 * Description:储值卡
 * </p>
 * <p>
 * Company:ncut
 * </p>
 * 
 * @author lph
 * @date 2016-12-20下午10:09:29
 * 
 */
@Controller
@RequestMapping(value = "/storedcategory")
public class StoredCategoryController extends BaseController {

	String menuUrl = "storedcategory/list.do"; // 菜单地址(权限用)
	@Resource(name = "storedcategoryService")
	private StoredCategoryManager storedcategoryService;

	@Resource(name = "usergroupService")
	private UsergroupManager usergroupService;

	@Resource(name = "memberService")
	private MemberManager memberService;

	@Resource(name = "storeddetailService")
	private StoredDetailManager storeddetailService;

	@Resource(name = "customstoredService")
	private CustomStoredManager customstoredService;

	@Resource(name = "businesscontactService")
	private BusinesscontactManager businesscontactService;

	@Resource(name = "staffpointService")
	private StaffpointManager staffpointService;

	@Resource(name = "staffService")
	private StaffManager staffService;
	@Resource(name = "ncutlogService")
	private NcutlogManager NCUTLOG;

	@RequestMapping(value = "/listMemberByPhoneOrName")
	public ModelAndView listMemberByPhoneOrName(Page page) throws Exception {

		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData> userList = (List<PageData>) memberService
				.listCompleteMemberlistPage(page);// 列出Member列表
		List<PageData> storedcategorylist = storedcategoryService.listAll(pd);

		mv.setViewName("finance/valuecard/sell_stored");
		mv.addObject("storedcategorylist", storedcategorylist);
		mv.addObject("userList", userList);
		mv.addObject("pd", pd);

		return mv;
	}

	/**
	 * 售卖储值卡
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/sellStored")
	public void sellStored(HttpServletResponse response, HttpSession session)
			throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "向指定用户售卖储值卡");
		String str = "{\"error\":\"error\"}";
		PageData pd = new PageData();
		pd = this.getPageData();

		pd.put("UID", pd.get("uid"));
		pd.put("REMARK", pd.get("remark"));
		pd.put("STOREDCATEGORY_ID", pd.get("category_id"));

		PageData userpd = memberService.findById(pd);// 得到用户信息
		PageData categorypd = storedcategoryService.findById(pd);// 得到储值卡信息
		String storeddetail_id = "OC"+PrimaryKeyGenerator.generateKey();
		pd.put("STOREDDETAIL_ID", storeddetail_id); // 主键

		pd.put("UID", userpd.get("uid")); // 用户id
		pd.put("NAME", userpd.get("name")); // 用户id
		pd.put("PHONE", userpd.get("phone")); // 用户id
		pd.put("STORED_CATEGORY_ID", categorypd.get("STOREDCATEGORY_ID")); // 储值卡类型编号
		pd.put("STORE_ID", ((Staff) session.getAttribute(Const.SESSION_USER))
				.getSTORE_ID()); // 门店编号
		pd.put("STAFF_ID", ((Staff) session.getAttribute(Const.SESSION_USER))
				.getSTAFF_ID()); // 员工编号
		pd.put("WECHATPAY_MONEY", pd.get("wechatpay_money")); // 微信支付
		if ("".equals(pd.get("WECHATPAY_MONEY"))
				|| pd.get("WECHATPAY_MONEY") == null) {
			pd.put("WECHATPAY_MONEY", 0);
		}
		pd.put("ALIPAY_MONEY", pd.get("alipay_money")); // 支付宝支付
		if ("".equals(pd.get("ALIPAY_MONEY")) || pd.get("ALIPAY_MONEY") == null) {
			pd.put("ALIPAY_MONEY", 0);
		}
		pd.put("BANKPAY_MONEY", pd.get("bankpay_money")); // 银行卡支付
		if ("".equals(pd.get("BANKPAY_MONEY"))
				|| pd.get("BANKPAY_MONEY") == null) {
			pd.put("BANKPAY_MONEY", 0);
		}
		pd.put("CASHPAY_MONEY", pd.get("cashpay_money")); // 现金支付
		if ("".equals(pd.get("CASHPAY_MONEY"))
				|| pd.get("CASHPAY_MONEY") == null) {
			pd.put("CASHPAY_MONEY", 0);
		}
		pd.put("CREATE_TIME", DateUtil.getTime()); // 创建时间
		
		pd.put("STATUS", "0"); // 状态
		pd.put("TYPE", "1"); // 线下充值
		pd.put("MONEY", categorypd.get("STORED_MONEY"));//金额
		pd.put("POINTS",  categorypd.get("RETURN_POINTS"));//返点
		//storeddetailService.save(pd);

		pd.put("UID", userpd.get("uid")); // 用户id
		//PageData customstoredpd = customstoredService.findById(pd);// 查看之前是否有记录
		/*
		if (customstoredpd != null) {
			pd.put("REMAIN_MONEY", categorypd.get("STORED_MONEY")); // 余额
			pd.put("REMAIN_POINTS", categorypd.get("RETURN_POINTS"));
			customstoredService.edit(pd);
			str = "{\"success\":\"ok\"}";

		} else {
			pd.put("REMAIN_MONEY", categorypd.get("STORED_MONEY")); // 余额
			pd.put("REMAIN_POINTS", categorypd.get("RETURN_POINTS")); // 剩余点数
			pd.put("STATUS", "0"); // 状态
			customstoredService.save(pd);
			str = "{\"success\":\"ok\"}";
		}
*/
		/*// 返点给客服
		int staffpoint = 0;
		Double tostaff = businesscontactService.findTostaff();
		if (tostaff != null) {
			staffpoint = (int) ((((BigDecimal) categorypd.get("STORED_MONEY"))
					.doubleValue()) * (tostaff));
		}

		PageData pp = new PageData();
		pp.put("item_id", this.get32UUID());
		pp.put("points", staffpoint);
		pp.put("staff_id", pd.get("STAFF_ID"));
		PageData staff = staffService.queryById((String) pd.get("STAFF_ID"));
		pp.put("staff_name", staff.get("STAFF_NAME"));

		String date = (DateUtil.getDay());
		String[] day = date.split("-");
		String year = DateUtil.getYear();
		String month = null;
		for (int i = 0; i < day.length; i++) {
			month = day[1];
		}
		pp.put("the_year", year);
		pp.put("the_month", month);
		pp.put("createtime", DateUtil.getTime());

		staffpointService.save(pp);*/
		
		try{
		customstoredService.updateUserStoreded(pd, categorypd);
		str = "{\"success\":\"ok\"}";
		
		NCUTLOG.save(
				Jurisdiction.getUsername(),
				"向用户：" + (userpd.getString("name")!=null?userpd.getString("name"):userpd.getString("username")) + "，手机号："
						+ userpd.getString("phone") + 
				", 售卖了储值卡, 储值卡："+ categorypd.get("STORED_MONEY")+", 返点："+categorypd.get("RETURN_POINTS"), this.getRequest()
						.getRemoteAddr());
		
		response.setContentType("text/javascript; charset=utf-8");
		response.getWriter().write(str);
		
		}catch(Exception e){
			str = "{\"error\":\"error\"}";
			response.setContentType("text/javascript; charset=utf-8");
			response.getWriter().write(str);
		}
		
	}

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增StoredCategory");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Session session = Jurisdiction.getSession();
		pd.put("STAFF_ID", ((Staff) session.getAttribute(Const.SESSION_USER))
				.getSTAFF_ID());
		pd.put("CREATE_TIME", DateUtil.getTime());
		storedcategoryService.save(pd);

		NCUTLOG.save(
				Jurisdiction.getUsername(),
				"新建储值卡，充值：" + pd.getString("STORED_MONEY") + "，返点："
						+ pd.getString("RETURN_POINTS"), this.getRequest()
						.getRemoteAddr());

		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/savem")
	public ModelAndView savem() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增Member");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		// pd.put("UID", this.get32UUID()); //主键
		memberService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername() + "删除StoredCategory");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		storedcategoryService.delete(pd);
		out.write("success");
		out.close();
	}

	/**
	 * 修改
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit")
	public ModelAndView edit() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "修改StoredCategory");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Session session = Jurisdiction.getSession();
		pd.put("STAFF_ID", ((Staff) session.getAttribute(Const.SESSION_USER))
				.getSTAFF_ID());
		pd.put("CREATE_TIME", DateUtil.getTime());
		storedcategoryService.edit(pd);
		
		NCUTLOG.save(
				Jurisdiction.getUsername(),
				"修改储值卡，充值：" + pd.getString("STORED_MONEY") + "，返点："
						+ pd.getString("RETURN_POINTS"), this.getRequest()
						.getRemoteAddr());
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 修改
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/editm")
	public ModelAndView editm() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "修改Member");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		memberService.edit(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 售卖储值卡 自己写的
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/gosell")
	public ModelAndView gosell() throws Exception {
		PageData pd = new PageData();
		pd = this.getPageData();// 得到uid
		List<PageData> storedcategorylist = storedcategoryService.listAll(pd);

		ModelAndView mv = this.getModelAndView();
		mv.addObject("storedcategorylist", storedcategorylist);
		// memberService.edit(pd);
		mv.addObject("msg", "success");
		mv.setViewName("finance/valuecard/sell_stored");
		return mv;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表StoredCategory");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData> varList = storedcategoryService.findStafflistPage(page);
		mv.setViewName("finance/valuecard/storedcategory_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
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
		mv.setViewName("finance/valuecard/storedcategory_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 去新增页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goAddm")
	public ModelAndView goAddm() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> groupList = usergroupService.listAllGroup(pd);
		mv.setViewName("finance/valuecard/member_edit");
		mv.addObject("groupList", groupList);
		mv.addObject("msg", "savem");
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
		pd = storedcategoryService.findById(pd); // 根据ID读取
		mv.setViewName("finance/valuecard/storedcategory_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 去修改会员页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEditm")
	public ModelAndView goEditm() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = memberService.findMemberAndGroupById(pd); // 根据ID读取
		List<PageData> groupList = usergroupService.listAllGroup(pd);
		mv.setViewName("finance/valuecard/member_edit");
		mv.addObject("groupList", groupList);
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
		logBefore(logger, Jurisdiction.getUsername() + "批量删除StoredCategory");
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
			storedcategoryService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername() + "导出StoredCategory到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("储值金额"); // 1
		titles.add("返点金额"); // 2
		titles.add("创建日期"); // 3
		titles.add("状态 0 正常 1 过期"); // 4
		titles.add("员工id"); // 5
		dataMap.put("titles", titles);
		List<PageData> varOList = storedcategoryService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).get("STORED_MONEY").toString()); // 1
			vpd.put("var2", varOList.get(i).get("RETURN_POINTS").toString()); // 2
			vpd.put("var3", varOList.get(i).getString("CREATE_TIME")); // 3
			vpd.put("var4", varOList.get(i).get("STATUS").toString()); // 4
			vpd.put("var5", varOList.get(i).getString("STAFF_ID")); // 5
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
}
