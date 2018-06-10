package cn.ncut.controller.user.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
import cn.ncut.service.finance.customappoint.CustomappointManager;
import cn.ncut.service.finance.prestore.PreStoreManager;
import cn.ncut.service.finance.prestoremx.PreStoreMxManager;
import cn.ncut.service.finance.refund.RefundManager;
import cn.ncut.service.system.ncutlog.NcutlogManager;
import cn.ncut.service.system.staff.StaffManager;
import cn.ncut.service.system.store.StoreManager;
import cn.ncut.service.user.customstored.CustomStoredManager;
import cn.ncut.service.user.member.impl.MemberService;
import cn.ncut.service.user.order.OrderManager;
import cn.ncut.service.user.order.OrderMxManager;
import cn.ncut.util.AppUtil;
import cn.ncut.util.Const;
import cn.ncut.util.DateUtil;
import cn.ncut.util.Jurisdiction;
import cn.ncut.util.ObjectExcelView;
import cn.ncut.util.PageData;


/**     
 * 说明：订单    
 * 创建人：
 * 创建时间：2016-12-30
 */
@Controller
@RequestMapping(value="/order")
public class OrderController extends BaseController {
	
	String menuUrl = "order/list.do"; //菜单地址(权限用)
	@Resource(name="orderService")
	private OrderManager orderService;
	
	@Resource(name="ordermxService")
	private OrderMxManager ordermxService;
	@Resource(name = "staffService")
	private StaffManager staffService;
	@Resource(name="storeService")
	private StoreManager storeService;
	
	@Resource(name="memberService")
	private MemberService memberService;
	
	@Resource(name="prestoremxService")
	private PreStoreMxManager prestoremxService;
	
	@Resource(name="prestoreService")
	private PreStoreManager prestoreService;
	
	@Resource(name = "customstoredService")
	private CustomStoredManager customstoredService;
	
	@Resource(name = "customappointService")
	private CustomappointManager customappointService;
	
	@Resource(name="refundService")
	private RefundManager refundService;
	
	@Resource(name = "ncutlogService")
	private NcutlogManager NCUTLOG;
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增Order");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ORDER_ID", this.get32UUID());	//主键
		orderService.save(pd);
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	@ResponseBody
	public Object delete(){
		logBefore(logger, Jurisdiction.getUsername()+"删除Order");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null ;} //校验权限
		Map<String,String> map = new HashMap<String,String>();
		PageData pd = new PageData();
		pd = this.getPageData();
		String errInfo = "success";
		
		try{
			//有订单明细，先删除订单明细
			if(Integer.parseInt(ordermxService.findCount(pd).get("zs").toString()) > 0){
				ordermxService.delete(pd);
			}
			//有预约信息，删除预约信息
			if(Integer.parseInt(customappointService.findCount(pd).get("zs").toString()) > 0){
				customappointService.delete(pd);
			}
			
			orderService.delete(pd);
		}catch(Exception e){
			errInfo = "false";
			logger.info("订单删除失败！");
		}
		
		map.put("result", errInfo);
		return AppUtil.returnObject(new PageData(), map);
	}

	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改Order");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		orderService.edit(pd);
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page, HttpSession session) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表Order");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}

		Staff sessionstaff = (Staff) session.getAttribute(Const.SESSION_USER);
		pd.put("STORE_ID", sessionstaff.getSTORE_ID());//默认选择的是自己的门店
		List<PageData>	storeList = storeService.findAllNames(pd);//得到所有门店
		pd.put("STOREID", pd.get("STOREID"));
		//pd.put("STORE_ID", pd.get("STOREID"));
		page.setPd(pd);
		
		//查询所有客服
		List<PageData> kefuList = new ArrayList<PageData>();
		kefuList = staffService.listSelfStoreKefu(pd);
				
		List<PageData>	varList = orderService.findAll(page);	//列出Order列表
		for(PageData var : varList){
			PageData staff = new PageData();
			String doctorname = staffService.getStaffNameById((String)var.get("STAFF_ID"));
			String servicename = staffService.getStaffNameById((String)var.get("SERVICE_STAFF_ID"));
			var.put("SERVICE_STAFF_NAME", servicename);
			var.put("STAFF_NAME", doctorname);
			if(var.get("WECHAT_PHONE")==null){
				var.put("WECHAT_PHONE", var.get("phone"));
			}
			if(var.get("name")==null){
				var.put("name", var.get("WECHAT_NAME"));
			}
			
		}
		
		/**
		String todayTotalMoney = orderService.queryTodayTotalMoney(DateUtil.getDay());
		if(todayTotalMoney==null || todayTotalMoney==""){
			todayTotalMoney="0";
		}
		String historyTotalMoney = orderService.queryhistoryTotalMoney(DateUtil.getDay());
		if(historyTotalMoney==null || historyTotalMoney==""){
			historyTotalMoney="0";
		}	
		**/	
		
		
		
		mv.setViewName("user/order/order_list");
		mv.addObject("varList", varList);
		//mv.addObject("todayTotalMoney",todayTotalMoney);
		//mv.addObject("historyTotalMoney",historyTotalMoney);
		mv.addObject("storeList", storeList);
		mv.addObject("kefuList", kefuList);
		mv.addObject("pd", pd);
		mv.addObject("STORE_ID",pd.get("STOREID"));
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限 
		return mv;
	}
	
	/**退款页列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/refund")
	public ModelAndView refund(Page page, HttpSession session) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表Order");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		
		Staff sessionstaff = (Staff) session.getAttribute(Const.SESSION_USER);
		pd.put("STORE_ID", sessionstaff.getSTORE_ID());
		List<PageData>	storeList = storeService.findAllNames(pd);
		pd.put("STOREID", pd.get("STOREID"));
		page.setPd(pd);
		List<PageData>	varList = orderService.findAllCanRefund(page);	//列出Order列表
		for(PageData var : varList){
			PageData staff = new PageData();
			String doctorname = staffService.getStaffNameById((String)var.get("STAFF_ID"));
			String servicename = staffService.getStaffNameById((String)var.get("SERVICE_STAFF_ID"));
			var.put("SERVICE_STAFF_NAME", servicename);
			var.put("STAFF_NAME", doctorname);
			if(var.get("WECHAT_PHONE")==null){
				var.put("WECHAT_PHONE", var.get("phone"));
			}
			if(var.get("name")==null){
				var.put("name", var.get("WECHAT_NAME"));
			}
			
		}	
		mv.setViewName("finance/refund/order_list");
		mv.addObject("varList", varList);
		mv.addObject("storeList", storeList);
		mv.addObject("pd", pd);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限 
		return mv;
	}
	
	
	/**去修改备注页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/updateRemark")
	public ModelAndView updateRemark() throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String remark = pd.getString("REMARK");
		pd = orderService.findById(pd);
		pd.put("REMARK", remark);
		orderService.edit(pd);
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**去修改备注页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goAdd")
	public ModelAndView goAdd()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("user/order/order_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	/**去修改备注页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goUpdateRemark")
	public ModelAndView goUpdateRemark()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = orderService.findById(pd);
		mv.setViewName("user/order/order_remark");
		mv.addObject("msg", "updateRemark");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	
	
	 /**去修改页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	public ModelAndView goEdit()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = orderService.findById(pd);	//根据ID读取
		mv.setViewName("user/order/order_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	 /**去订单明细页面准备退款
		 * @param
		 * @throws Exception
		 * 确认往储值卡，储值卡返点、预存各退多少钱
		 */
		@RequestMapping(value="/goEditRefund")
		public ModelAndView goEditRefund()throws Exception{
			ModelAndView mv = this.getModelAndView();
			PageData pd = new PageData();
			pd = this.getPageData();
			pd = orderService.findById(pd);	//根据ID读取
			
			List<PageData> pdd =  ordermxService.findAllByOrderId(pd);
			double refund_money = 0;
			double refund_card_money = 0;
			double refund_card_points = 0;
			for(int i = 0 ; i < pdd.size() ; i++) {
				int pay_method = -1;
				if (pdd.get(i).get("PAY_METHOD") instanceof Long) 
					pay_method = Integer.parseInt(pdd.get(i).get("PAY_METHOD").toString());
				if (pdd.get(i).get("PAY_METHOD") instanceof String) 
					pay_method = Integer.parseInt(pdd.get(i).getString("PAY_METHOD"));
				if (pdd.get(i).get("PAY_METHOD") instanceof Integer) 
					pay_method = Integer.parseInt(pdd.get(i).get("PAY_METHOD").toString());
				if(pay_method==2&&pdd.get(i).getString("PAY_MONEY")!=null){
					refund_card_money += Double.parseDouble(pdd.get(i).getString("PAY_MONEY"));
				}else if (pay_method==6&&pdd.get(i).getString("PAY_MONEY")!=null) {
					refund_card_points += Double.parseDouble(pdd.get(i).getString("PAY_MONEY"));
				}else if(pdd.get(i).getString("PAY_MONEY")!=null){
					refund_money += Double.parseDouble(pdd.get(i).getString("PAY_MONEY"));
				}
				}

			/*if(refund_money>=5){
				refund_money-=5;
			}else if(refund_card_money>=5){
				refund_card_money-=5;
			}else if(refund_card_points>=5){
				refund_card_points-=5;
			}*/
			double total_money = refund_card_money+refund_card_points+refund_money;
			mv.setViewName("finance/refund/order_edit");
			mv.addObject("msg", "editRefund");
			mv.addObject("pd", pd);
			mv.addObject("refund_money",refund_money);
			mv.addObject("refund_card_money",refund_card_money);
			mv.addObject("refund_card_points",refund_card_points);
			mv.addObject("total_money",total_money);
			return mv;
		}
		
		/**修改退款信息
		 * @param
		 * @throws Exception
		 */
		@RequestMapping(value="/editRefund")
		public ModelAndView editRefund() throws Exception{
			logBefore(logger, Jurisdiction.getUsername()+"修改Order");
			ModelAndView mv = this.getModelAndView();
			PageData pd = new PageData();
			pd = this.getPageData();
			pd.put("PRESTOREMX_ID", this.get32UUID());
			PageData pdd = memberService.findById(pd);
			// 得到当前的客服的门店编号
			Session session = Jurisdiction.getSession();
			Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));
			pd.put("STAFF_ID", staff.getSTAFF_ID());	//员工编号
			pd.put("CREATE_TIME", DateUtil.getTime());
			pd.put("TYPE", 3);
			pd.put("PHONE", pdd.get("phone"));
			pd.put("USERNAME", pdd.getString("username"));
			pd.put("NAME", pdd.getString("name"));
			pd.put("PRESTOREMONEY", pd.get("refund_money"));
			
			//1、向预存表、预存明细表插入退还余额
			// 判断预存表中是否有这个用户，如果有就update 没有就新增一条记录
			PageData presotrepd = prestoreService.findByUid(Integer.parseInt(pd.getString("UID")));
			PageData pre_pd = new PageData();
			String uuid = this.get32UUID();
			if (presotrepd == null) {//该用户没有预存
				pre_pd.put("PRESTORE_ID", uuid);
				pre_pd.put("UID", Integer.parseInt(pd.getString("UID"))); 
				pre_pd.put("USERNAME", pdd.getString("username")); 
				pre_pd.put("PHONE", pdd.getString("phone")); 
				pre_pd.put("SUM_MONEY", pd.get("refund_money"));
				prestoreService.save(pre_pd);
				
				pd.put("PRESTORE_ID", uuid);
			} else {//该用户已经有了预存
				Double sum_money = Double.parseDouble(pd.getString("PRESTOREMONEY")) + (Double)presotrepd.get("SUM_MONEY");
				pre_pd.put("PRESTORE_ID", presotrepd.getString("PRESTORE_ID"));
				pre_pd.put("SUM_MONEY", sum_money); 
				pre_pd.put("UID", pd.getString("UID")); 
				pre_pd.put("USERNAME", pd.getString("USERNAME")); 
				pre_pd.put("PHONE", pd.getString("PHONE")); 
				prestoreService.edit(pre_pd);
				
				pd.put("PRESTORE_ID", presotrepd.getString("PRESTORE_ID"));
			}
			//插入预存明细记录
			pd.put("TYPE", 4);
			prestoremxService.save(pd);	
			
			pd.put("STATUS", "0");
			
			//2、向储值卡中返钱返点
			PageData customstoredpd = customstoredService.findById(pd);// 查看之前是否有记录
			if (customstoredpd != null) {
				pd.put("REMAIN_MONEY", pd.get("refund_card_money")); // 余额
				pd.put("REMAIN_POINTS", pd.get("refund_card_points"));
				customstoredService.edit(pd);

			} else {
				pd.put("REMAIN_MONEY", pd.get("refund_card_money")); // 余额
				pd.put("REMAIN_POINTS", pd.get("refund_card_points"));
				customstoredService.save(pd);
			}
			
			//3、向储值明细中插入数据
			PageData pddd = new PageData();
			pddd.put("ORDER_ID", pd.get("ORDER_ID"));
			pddd.put("STORE_ID", orderService.findById(pddd).get("STORE_ID"));
			pddd.put("STOREDDETAIL_ID","OC"+this.get32UUID());
			pddd.put("UID", pd.getString("UID"));
			pddd.put("STAFF_ID", pd.getString("STAFF_ID"));
			pddd.put("CREATE_TIME",DateUtil.getTime());
			pddd.put("MONEY", Double.parseDouble(pd.getString("REMAIN_MONEY")));
			pddd.put("TYPE", 6);
			pddd.put("POINTS",pd.get("refund_card_money"));
			
			if (Double.parseDouble(pd.getString("REMAIN_MONEY"))!=0&&Double.parseDouble(pd.getString("refund_card_money"))!=0) 
			customstoredService.saveMx(pddd);
			
			
			//4、写退款表
			pd.put("REFUND_ID", this.get32UUID());
			refundService.save(pd);
			
			//5、修改订单表状态、并添加金额
			pd.put("ORDER_STATUS", 6);
			orderService.editStatusAndRefund(pd);
			
			NCUTLOG.save(Jurisdiction.getUsername(),
					"给用户<b>" + pd.getString("USERNAME") + "</b>，退订单"
							+ pd.getString("ORDER_ID"), this
							.getRequest().getRemoteAddr());
			
			mv.addObject("msg","success");
			mv.setViewName("save_result");
			return mv;
		}
		
		
		/**批量退款
		 * @param
		 * @throws Exception
		 */
		@RequestMapping(value="/deleteAll")
		public ModelAndView deleteAll() throws Exception{
			ModelAndView mv = this.getModelAndView();
			PageData pd = this.getPageData();
			double refund_money = 0;
			double refund_card_money = 0;
			double refund_card_points = 0;
			
			String DATA_IDS = pd.getString("ORDER_ID");
			String[] orderIdArray = DATA_IDS.split(",");
			for (int i = 0; i < orderIdArray.length; i++) {
				PageData pddd = new PageData();
				pddd.put("ORDER_ID", orderIdArray[i]);
				pd = orderService.findById(pddd);	//根据ID读取
				List<PageData> pdd =  ordermxService.findAllByOrderId(pddd);
				
				for(int j = 0 ; j < pdd.size() ; j++) {
					int pay_method = -1;
					if (pdd.get(j).get("PAY_METHOD") instanceof Long) 
						pay_method = Integer.parseInt(pdd.get(j).get("PAY_METHOD").toString());
					if(pay_method==2&&pdd.get(j).getString("PAY_MONEY")!=null){
						refund_card_money += Double.parseDouble(pdd.get(j).getString("PAY_MONEY"));
					}else if (pay_method==6&&pdd.get(j).getString("PAY_MONEY")!=null) {
						refund_card_points += Double.parseDouble(pdd.get(j).getString("PAY_MONEY"));
					}else if(pdd.get(j).getString("PAY_MONEY")!=null){
						refund_money += Double.parseDouble(pdd.get(j).getString("PAY_MONEY"));
					}
					}
			}

			double total_money = refund_card_money+refund_card_points+refund_money;
			mv.setViewName("finance/refund/order_edit");
			mv.addObject("msg", "editRefund");
			//mv.addObject("pd", pd);
			mv.addObject("refund_money",refund_money);
			mv.addObject("refund_card_money",refund_card_money);
			mv.addObject("refund_card_points",refund_card_points);
			mv.addObject("total_money",total_money);
			return mv;
		}

	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出Order到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("会员");	//1
		titles.add("门店");	//2
		titles.add("服务项目");	//3
		titles.add("客服");	//4
		titles.add("创建时间");	//5
		titles.add("订单状态");	//6
		titles.add("订单URL");	//7
		titles.add("评价时间");	//8
		titles.add("过期时间");	//9
		titles.add("备注");	//10
		titles.add("医生");	//11
		titles.add("应收金额");	//12
		titles.add("折扣");	//13
		titles.add("使用积分");	//14
		titles.add("优惠券");	//15
		titles.add("实收金额");	//16
		titles.add("退款金额");	//17
		dataMap.put("titles", titles);
		List<PageData> varOList = orderService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).get("UID").toString());	//1
			vpd.put("var2", varOList.get(i).getString("STORE_ID"));	    //2
			vpd.put("var3", varOList.get(i).get("PROJECT_ID").toString());	//3
			vpd.put("var4", varOList.get(i).getString("SERVICE_STAFF_ID"));	    //4
			vpd.put("var5", varOList.get(i).getString("CREATE_TIME"));	    //5
			vpd.put("var6", varOList.get(i).get("ORDER_STATUS").toString());	//6
			vpd.put("var7", varOList.get(i).getString("URL"));	    //7
			vpd.put("var8", varOList.get(i).getString("RECOMMEND_TIME"));	    //8
			vpd.put("var9", varOList.get(i).getString("EXPIRE_TIME"));	    //9
			vpd.put("var10", varOList.get(i).getString("REMARK"));	    //10
			vpd.put("var11", varOList.get(i).getString("STAFF_ID"));	    //11
			vpd.put("var12", varOList.get(i).getString("ORDER_MONEY"));	    //12
			vpd.put("var13", varOList.get(i).getString("PROPORTION"));	    //13
			vpd.put("var14", varOList.get(i).get("CREDIT").toString());	//14
			vpd.put("var15", varOList.get(i).getString("DISCOUNT_ID"));	    //15
			vpd.put("var16", varOList.get(i).getString("PAY_MONEY"));	    //16
			vpd.put("var17", varOList.get(i).getString("REFUND"));	    //17
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
