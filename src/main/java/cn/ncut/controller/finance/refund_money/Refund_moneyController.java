package cn.ncut.controller.finance.refund_money;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.mail.Session;
import javax.servlet.http.HttpSession;
import cn.ncut.entity.system.Staff;
import net.sf.json.JSONArray;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import sun.tools.tree.ThisExpression;
import cn.ncut.controller.base.BaseController;
import cn.ncut.entity.Page;
import cn.ncut.util.AppUtil;
import cn.ncut.util.Const;
import cn.ncut.util.DateUtil;
import cn.ncut.util.ObjectExcelView;
import cn.ncut.util.PageData;
import cn.ncut.util.Jurisdiction;
import cn.ncut.util.Tools;
import cn.ncut.service.finance.prestore.PreStoreManager;
import cn.ncut.service.finance.refund_money.Refund_moneyManager;
import cn.ncut.service.system.ncutlog.NcutlogManager;
import cn.ncut.service.system.staff.StaffManager;
import cn.ncut.service.system.user.UserManager;
import cn.ncut.service.user.member.MemberManager;

/**
 * 说明：退款 创建人：FH Q313596790 创建时间：2017-02-23
 */
@Controller
@RequestMapping(value = "/refund_money")
public class Refund_moneyController extends BaseController {

	String menuUrl = "refund_money/list.do"; // 菜单地址(权限用)
	@Resource(name = "refund_moneyService")
	private Refund_moneyManager refund_moneyService;

	@Resource(name = "memberService")
	private MemberManager memberService;

	@Resource(name = "staffService")
	private StaffManager staffService;

	@Resource(name = "ncutlogService")
	private NcutlogManager NCUTLOG;

	@Resource(name = "prestoreService")
	private PreStoreManager prestoreService;

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/listAll")
	public ModelAndView listAll(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Refund_money");
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
		List<PageData> varList = refund_moneyService.list(page); // 列出Refund_money列表
		for (PageData var : varList) {
			String staff = staffService.getStaffNameById((String) var
					.get("SERVICE_ID"));
			String username = memberService.findById(var).getString("username");
			String name = memberService.findById(var).getString("name");
			var.put("STAFF_NAME", staff);
			var.put("USER_NAME", name + "（" + username + "）");
		}
		mv.setViewName("finance/refund_money/refund_money_listAll");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增Refund_money");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("REFUND_MONEY_ID", this.get32UUID()); // 主键
		refund_moneyService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername() + "删除Refund_money");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		refund_moneyService.delete(pd);
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
		logBefore(logger, Jurisdiction.getUsername() + "修改Refund_money");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		refund_moneyService.edit(pd);
		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 财务退余额
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/caiwuedit")
	public ModelAndView caiwuedit() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "修改Refund_money");

		ModelAndView mv = this.getModelAndView();
		PageData pd = this.getPageData();
		Staff staff = (Staff) (this.getRequest().getSession()
				.getAttribute(Const.SESSION_USER));
		// 1、插入退款表
		PageData refund_money_insert = new PageData();

		refund_money_insert.put("REFUND_MONEY_ID", this.get32UUID());
		refund_money_insert.put("TIME", DateUtil.getTime());
		refund_money_insert.put("REFUND_REMAIN_MONEY", 0.00);
		refund_money_insert.put("REFUND_PRESTORE_MONEY", pd.get("tuikuan"));
		refund_money_insert.put("SHOUXUFEI", pd.get("shouxufei"));
		refund_money_insert.put("SERVICE_ID", staff.getSTAFF_ID());
		refund_money_insert.put("UID", pd.get("uid"));

		// 2、插入订单表
		PageData tb_order_insert = new PageData();

		tb_order_insert.put("ORDER_ID", "OR" + this.get32UUID());
		tb_order_insert.put("UID", pd.get("uid"));
		tb_order_insert.put("SERVICECOST_ID", -1);
		tb_order_insert.put("ORDER_STATUS", 5);
		tb_order_insert.put("ORDER_MONEY", pd.get("shouxufei"));
		tb_order_insert.put("PAY_MONEY", pd.get("shouxufei"));
		tb_order_insert.put("SERVICE_ID", staff.getSTAFF_ID());
		tb_order_insert.put("CREATE_TIME", DateUtil.getTime());
		tb_order_insert.put("URL", "2");
		tb_order_insert.put("STAFF_ID", staff.getSTAFF_ID());
		tb_order_insert.put("STORE_ID", staffService.findById(tb_order_insert)
				.getString("STORE_ID"));
		tb_order_insert.put("WECHAT_NAME", pd.getString("name"));
		tb_order_insert.put("WECHAT_PHONE", pd.getString("phone"));
		tb_order_insert.put("DISCOUNT_ID", 0);
		tb_order_insert.put("PROPORTION", 1.0);
		// 3、插入余额明细表
		PageData tb_ordermx_insert = new PageData();
		tb_ordermx_insert.put("PRESTOREMX_ID", this.get32UUID());
		tb_ordermx_insert.put("UID", pd.get("uid"));
		tb_ordermx_insert.put("PHONE", pd.get("phone"));
		tb_ordermx_insert.put("USERNAME", pd.get("name"));
		tb_ordermx_insert.put("PRESTOREMONEY",
				(0 - Double.parseDouble(pd.getString("tuikuan"))));
		tb_ordermx_insert.put("STAFF_ID", staff.getSTAFF_ID());
		tb_ordermx_insert.put("CREATE_TIME", DateUtil.getTime());
		tb_ordermx_insert.put("TYPE", 3);
		tb_ordermx_insert.put("PRESTORE_ID",
				prestoreService
						.findByUid(Integer.parseInt(pd.getString("uid")))
						.getString("PRESTORE_ID"));

		// 4、删除余额表
		// 4步使用上面的pd，到service中执行。
		refund_moneyService.updateCaiwuPrestoreMoney(refund_money_insert,
				tb_order_insert, tb_ordermx_insert);

		NCUTLOG.save(
				Jurisdiction.getUsername(),
				"给用户<b>" + pd.getString("name") + "</b>，退钱包余额"
						+ Double.parseDouble(pd.getString("tuikuan"))
						+ "元，扣除手续费" + pd.getString("shouxufei") + "元", this
						.getRequest().getRemoteAddr());

		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 财务退储值卡
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/caiwueditPrestore")
	public ModelAndView caiwueditPrestore() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "修改Refund_money");

		ModelAndView mv = this.getModelAndView();
		PageData pd = this.getPageData();
		Staff staff = (Staff) (this.getRequest().getSession()
				.getAttribute(Const.SESSION_USER));

		// 1、插入退款表
		PageData refund_money_insert = new PageData();

		refund_money_insert.put("REFUND_MONEY_ID", this.get32UUID());
		refund_money_insert.put("TIME", DateUtil.getTime());
		refund_money_insert.put("REFUND_REMAIN_MONEY", pd.get("tuikuan"));
		refund_money_insert.put("REFUND_PRESTORE_MONEY", 0.00);
		refund_money_insert.put("SHOUXUFEI", pd.get("shouxufei"));
		refund_money_insert.put("SERVICE_ID", staff.getSTAFF_ID());
		refund_money_insert.put("UID", pd.get("uid"));

		// 2、插入订单表
		PageData tb_order_insert = new PageData();

		tb_order_insert.put("ORDER_ID", "OR" + this.get32UUID());
		tb_order_insert.put("UID", pd.get("uid"));
		tb_order_insert.put("SERVICECOST_ID", -1);
		tb_order_insert.put("ORDER_STATUS", 5);
		tb_order_insert.put("ORDER_MONEY", pd.get("shouxufei"));
		tb_order_insert.put("PAY_MONEY", pd.get("shouxufei"));
		tb_order_insert.put("SERVICE_ID", staff.getSTAFF_ID());
		tb_order_insert.put("CREATE_TIME", DateUtil.getTime());
		tb_order_insert.put("URL", "2");
		tb_order_insert.put("STAFF_ID", staff.getSTAFF_ID());
		tb_order_insert.put("STORE_ID", staffService.findById(tb_order_insert)
				.getString("STORE_ID"));
		tb_order_insert.put("WECHAT_NAME", pd.getString("name"));
		tb_order_insert.put("WECHAT_PHONE", pd.getString("phone"));
		tb_order_insert.put("DISCOUNT_ID", 0);
		tb_order_insert.put("PROPORTION", 1.0);
		tb_order_insert.put("CARD_ID",pd.get("CARD_ID"));

		// 3、删除储值卡
		// 4、插入储值卡明细
		PageData tb_storedDetail_insert = new PageData();
		tb_storedDetail_insert.put("STOREDDETAIL_ID", this.get32UUID());
		tb_storedDetail_insert.put("UID", pd.get("uid"));
		tb_storedDetail_insert.put("STAFF_ID", staff.getSTAFF_ID());
		tb_storedDetail_insert.put("CREATE_TIME", DateUtil.getTime());
		tb_storedDetail_insert.put("MONEY",
				(0 - Double.parseDouble(pd.getString("tuikuan"))));
		tb_storedDetail_insert.put("POINTS",
				(0 - Double.parseDouble(pd.getString("REMAIN_POINTS"))));
		tb_storedDetail_insert.put("TYPE", 2);
		tb_storedDetail_insert.put("STATUS", 0);
		tb_storedDetail_insert.put("STORE_ID",
				staffService.findById(tb_order_insert).getString("STORE_ID"));

		refund_moneyService.updateCaiwuCustomStoredMoney(refund_money_insert,
				tb_order_insert, tb_storedDetail_insert);

		NCUTLOG.save(
				Jurisdiction.getUsername(),
				"给用户<b>" + pd.getString("name") + "</b>，退储值卡余额"
						+ Double.parseDouble(pd.getString("tuikuan"))
						+ "元，扣除手续费" + pd.getString("shouxufei") + "元", this
						.getRequest().getRemoteAddr());

		mv.addObject("msg", "success");
		mv.setViewName("save_result");
		return mv;
	}

	/**
	 * 退还余额页面
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Refund_money");
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
		List<PageData> varList = refund_moneyService.memberRefundlistPage(page); // 列出Refund_money列表
		mv.setViewName("finance/refund_money/refund_money_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 财务看到的退还余额页面
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/caiwu_list")
	public ModelAndView caiwu_list(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Refund_money");
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
		List<PageData> varList = refund_moneyService.memberRefundlistPage(page); // 列出Refund_money列表
		mv.setViewName("finance/refund_money/caiwu_refund_money_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 退还储值卡页面
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/RefundPrestoreList")
	public ModelAndView RefundPrestoreList(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Refund_money");
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
		List<PageData> varList = refund_moneyService
				.memberRefundPrestorelistPage(page); // 列出Refund_money列表
		mv.setViewName("finance/refund_money/refund_prestore_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 财务退还储值卡页面
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/caiwuRefundPrestoreList")
	public ModelAndView caiwuRefundPrestoreList(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Refund_money");
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
		List<PageData> varList = refund_moneyService
				.memberRefundPrestorelistPage(page); // 列出Refund_money列表
		mv.setViewName("finance/refund_money/caiwu_refund_prestore_list");
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
		mv.setViewName("finance/refund_money/refund_money_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 去退储值卡页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
	public ModelAndView goEdit() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("CARD_ID",(pd.get("REFUND_MONEY_ID").toString().split("_"))[1]);
		pd.put("REFUND_MONEY_ID",(pd.get("REFUND_MONEY_ID").toString().split("_"))[0]);
		
		pd = refund_moneyService.findById(pd); // 根据ID读取
		pd.put("TIME", DateUtil.getTime());
		pd.put("STAFF_NAME", Jurisdiction.getUsername());
		mv.setViewName("finance/refund_money/refund_prestore_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 去退余额页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEditMoney")
	public ModelAndView goEditMoney() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = refund_moneyService.findMoneyById(pd); // 根据ID读取
		pd.put("TIME", DateUtil.getTime());
		pd.put("STAFF_NAME", Jurisdiction.getUsername());
		mv.setViewName("finance/refund_money/refund_money_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 去财务退余额页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/caiWuGoEditMoney")
	public ModelAndView caiWuGoEditMoney() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = refund_moneyService.findMoneyById(pd); // 根据ID读取
		pd.put("TIME", DateUtil.getTime());
		pd.put("STAFF_NAME", Jurisdiction.getUsername());
		pd.put("shouxufei", 5.00);
		mv.setViewName("finance/refund_money/caiwu_refund_money_edit");
		mv.addObject("msg", "caiwuedit");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 去财务退储值卡页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/caiWuGoEditPrestore")
	public ModelAndView caiWuGoEditPrestore() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		PageData pdd = new PageData();
		pd.put("CARD_ID",(pd.get("REFUND_MONEY_ID").toString().split("_"))[1]);
		pd.put("REFUND_MONEY_ID",(pd.get("REFUND_MONEY_ID").toString().split("_"))[0]);
		pdd.put("CARD_ID",pd.get("CARD_ID"));
		pdd.put("REFUND_MONEY_ID",pd.get("REFUND_MONEY_ID"));
		pd = refund_moneyService.findById(pd); // 根据ID读取
		pd.put("TIME", DateUtil.getTime());
		pd.put("STAFF_NAME", Jurisdiction.getUsername());
		pd.put("shouxufei", 5.00);
		pd.put("CARD_ID",pdd.get("CARD_ID"));
		pd.put("REFUND_MONEY_ID",pdd.get("REFUND_MONEY_ID"));
		mv.setViewName("finance/refund_money/caiwu_refund_prestore_edit");
		mv.addObject("msg", "caiwueditPrestore");
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
		logBefore(logger, Jurisdiction.getUsername() + "批量删除Refund_money");
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
			refund_moneyService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername() + "导出Refund_money到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("用户ID"); // 1
		titles.add("可退余额"); // 2
		titles.add("可退储值卡"); // 3
		titles.add("退款时间"); // 4
		titles.add("操作员"); // 5
		dataMap.put("titles", titles);
		List<PageData> varOList = refund_moneyService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).get("UID").toString()); // 1
			vpd.put("var2", varOList.get(i).get("REFUND_PRESTORE_MONEY")
					.toString()); // 2
			vpd.put("var3", varOList.get(i).get("REFUND_REMAIN_MONEY")
					.toString()); // 3
			vpd.put("var4", varOList.get(i).getString("TIME")); // 4
			vpd.put("var5", varOList.get(i).getString("SERVICE_ID")); // 5
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

	@RequestMapping(value = "/getSumMoneyByShouXuFei", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object getSumMoneyByShouXuFei(double SUM_MONEY, double shouxufei)
			throws Exception {

		double tuikuan = SUM_MONEY - shouxufei;
		JSONArray jsonArr = JSONArray.fromObject("[{tuikuan:" + "\"" + tuikuan
				+ "\"}]");
		return jsonArr;
	}

}
