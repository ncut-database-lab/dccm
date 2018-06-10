package cn.ncut.controller.querystatictics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.ncut.controller.base.BaseController;
import cn.ncut.entity.Page;
import cn.ncut.entity.system.Staff;
import cn.ncut.service.system.servicemodule.ServicemoduleManager;
import cn.ncut.service.system.staff.StaffManager;
import cn.ncut.service.system.store.StoreManager;
import cn.ncut.service.user.member.MemberManager;
import cn.ncut.service.user.storeddetail.StoredDetailManager;
import cn.ncut.service.user.usercategory.impl.UsercategoryService;
import cn.ncut.service.user.usergroup.UsergroupManager;
import cn.ncut.util.Const;
import cn.ncut.util.Jurisdiction;
import cn.ncut.util.ObjectExcelView;
import cn.ncut.util.PageData;


@Controller
@RequestMapping(value = "/queryStoreddetail")
public class QueryStoredDetail extends BaseController{
	                 
	String menuUrl = "queryStoreddetail/list.do"; // 菜单地址(权限用)

	@Resource(name = "storeddetailService")
	private StoredDetailManager storeddetailService;
	@Resource(name = "staffService")
	private StaffManager staffService;
	@Resource(name = "storeService")
	private StoreManager storeService;
	@Resource(name = "servicemoduleService")
	private ServicemoduleManager servicemoduleService;
	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page,HttpSession session) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Member");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		PageData pdd = new PageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pdd.put("keywords", keywords.trim());
		}
		Staff sessionstaff = (Staff) session.getAttribute(Const.SESSION_USER);
		String storeId=sessionstaff.getSTORE_ID();
		String mId = storeId.substring(0, 1);//0,0,1.
		String store = pd.getString("store");
		if(mId.equals("0")){
			mId=storeId.substring(0,2);//01,02
			store=storeId;//0201
			pdd.put("mId", mId);
			pdd.put("store", store);
		}else{
			//总店张
			mId =pd.getString("mId");
			pdd.put("mId", mId);
			store = pd.getString("store");
		}
		//pd.put("store", sessionstaff.getSTORE_ID());
		// 添加检索条件
		if (null != store && !"".equals(store)) {
			pdd.put("store", store.trim());
		} else {
			if (null != mId && !"".equals(mId)) {
				pdd.put("mId", mId.trim());
				List<PageData> stores = storeService.selectStoresBymId(pdd);
				ArrayList<String> storeArray = new ArrayList<>();
				for (Iterator<PageData> ite = stores.iterator(); ite.hasNext();) {
					PageData next = ite.next();
					storeArray.add((String) next.get("STORE_ID"));
				}
				storeArray.add("1");
				pdd.put("storeIds", storeArray);
			}
		}
		String firstDate = pd.getString("firstDate");
		if (null != firstDate && !"".equals(firstDate)) {
			pdd.put("firstDate", firstDate.trim());
		}
		String lastDate = pd.getString("lastDate");
		if (null != lastDate && !"".equals(lastDate)) {
			pdd.put("lastDate", lastDate.trim());
		}
		String STATUS = pd.getString("STATUS");
		if (null != STATUS && !"".equals(STATUS)) {
			pdd.put("STATUS", STATUS.trim());
		}
		String staffName = pd.getString("staffName");
		if (null != staffName && !"".equals(staffName)) {
			pdd.put("staffName", staffName.trim());
		}
		
		List<PageData> serviceModules = servicemoduleService.listAll0(pdd);
		List<PageData> storeList = storeService.findAllNames(pdd);
		
		page.setPd(pdd);
		pdd.put("ROLE_ID", "1");
		List<PageData> varList = storeddetailService.listdetail(page); // 列出Member列表
		mv.setViewName("querysatistics/storeddetail");
		mv.addObject("varList", varList);
		mv.addObject("storeList", storeList);
		mv.addObject("serviceModules", serviceModules);
		mv.addObject("pd", pdd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}
	
	

	@RequestMapping(value="/storeddetailexcel")
	public ModelAndView storeddetailExcel() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "导出Member到excel");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
			return null;
		}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		PageData pdd = new PageData();
		pd = this.getPageData();
		
		// 添加检索条件
		String mId = pd.getString("mId");
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
	
		String store = pd.getString("store");
		if (null != store && !"".equals(store)) {
			pd.put("store", store.trim());
		} else {
			if (null != mId && !"".equals(mId)) {
				pd.put("mId", mId.trim());
				List<PageData> stores = storeService.selectStoresBymId(pd);
				ArrayList<String> storeArray = new ArrayList<>();
				for (Iterator<PageData> ite = stores.iterator(); ite.hasNext();) {
					PageData next = ite.next();
					storeArray.add((String) next.get("STORE_ID"));
				}
				pd.put("storeIds", storeArray);
			}
		}
		String firstDate = pd.getString("firstDate");
		if (null != firstDate && !"".equals(firstDate)) {
			pd.put("firstDate", firstDate.trim());
		}
		String lastDate = pd.getString("lastDate");
		if (null != lastDate && !"".equals(lastDate)) {
			pd.put("lastDate", lastDate.trim());
		}
		String STATUS = pd.getString("STATUS");
		if (null != STATUS && !"".equals(STATUS)) {
			pd.put("STATUS", STATUS.trim());
		}
		String staffName = pd.getString("staffName");
		if (null != staffName && !"".equals(staffName)) {
			pd.put("staffName", staffName.trim());
		}
		
		/*List<PageData> serviceModules = servicemoduleService.listAll0(pd);
		List<PageData> storeList = storeService.findAllNames(pd);
		*/
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<String> titles = new ArrayList<String>();
		
		titles.add("用户姓名"); // 2
		titles.add("昵称"); // 3
		titles.add("手机号"); 
		titles.add("门店"); // 7
		titles.add("客服"); // 8
		titles.add("创建日期"); // 9
		titles.add("储值"); // 10
		titles.add("返点"); // 12
		titles.add("余额消费");
		titles.add("返点消费");
		dataMap.put("titles", titles);
		List<PageData> varOList = storeddetailService.selectstoredAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for (int i = 0; i < varOList.size(); i++) {
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).get("name"));
			vpd.put("var2", varOList.get(i).get("username"));
			vpd.put("var3", varOList.get(i).get("phone"));
			vpd.put("var4", varOList.get(i).get("STORE_NAME"));
			vpd.put("var5", varOList.get(i).get("STAFF_NAME"));
			vpd.put("var6", varOList.get(i).get("CREATE_TIME"));
		    java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#.00");
			String type = varOList.get(i).get("TYPE").toString();

		    if(type.equals("0") || type.equals("1") || type.equals("3")){
				vpd.put("var7", String.valueOf(varOList.get(i).get("MONEY"))); // 7
				vpd.put("var8", String.valueOf(varOList.get(i).get("POINTS")));
				vpd.put("var9", String.valueOf(0)); // 7
				vpd.put("var10", String.valueOf(0));
			}else{
				vpd.put("var7", String.valueOf(0)); // 7
				vpd.put("var8", String.valueOf(0));
				vpd.put("var9", String.valueOf(varOList.get(i).get("MONEY"))); // 7
				vpd.put("var10", String.valueOf(varOList.get(i).get("POINTS")));
			}
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv, dataMap);
		return mv;
	}

	/**
	 * 根据门店查询医生姓名，客服姓名等
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/findstaffProjectServiceStaffByStoreId")
	@ResponseBody
	public String findstaffProjectServiceStaffByStoreId() throws Exception {
		PageData pd = this.getPageData();
		String storeId = (String) pd.get("storeId");
		pd.put("DEPART_ID", "06");
		pd.put("STORE_ID", storeId);
		List<PageData> doctors = staffService.findstaffsByStoreId(pd);
		pd.put("DEPART_ID", "01");
		List<PageData> serviceStaffs = staffService.findstaffsByStoreId(pd);
		ArrayList<List<PageData>> al = new ArrayList();
		al.add(doctors);
		al.add(serviceStaffs);
		String jsonString = new ObjectMapper().writeValueAsString(al);
		return jsonString;
	}
	
	@RequestMapping(value="/satisticsStoredDetail")
	public ModelAndView satisticsStoredDetail(Page page) throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		String firstDate = pd.getString("firstDate");
		if (null != firstDate && !"".equals(firstDate)) {
			pd.put("firstDate", firstDate.trim());
		}
		String lastDate = pd.getString("lastDate");
		if (null != lastDate && !"".equals(lastDate)) {
			pd.put("lastDate", lastDate.trim());
		}
	
		List<PageData> storedDetail = storeddetailService.selectstoreddetailGroups(pd);
		List<PageData> storeddetail = new ArrayList<PageData>();
		for(PageData pp:storedDetail){
			if(pp.get("STORE_ID")==null){
				pp.put("STORE_ID", -1);
				pp.put("STORE_NAME", "微信端");
			}
			storeddetail.add(pp);
		}
		
		mv.addObject("storeddetail", new ObjectMapper().writeValueAsString(storeddetail));
		mv.setViewName("querysatistics/storeddetail_satistics");
		mv.addObject("pd", pd);
		return mv;
	}
	
	@RequestMapping(value = "/selectStoredCategory")
	@ResponseBody
	public String selectStoredCategory() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = this.getPageData();
		List<PageData> storedDetails = storeddetailService.selectStoredCategory(pd);
		
		
		String detail = new ObjectMapper().writeValueAsString(storedDetails);
		return detail;
	}
	
	
}

