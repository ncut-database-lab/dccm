package cn.ncut.controller.user.customstored;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import cn.ncut.util.Tools;
import cn.ncut.util.wechat.MD5Util;
import cn.ncut.util.TwoDimensionCode;
import cn.ncut.util.wechat.PrimaryKeyGenerator;
import cn.ncut.service.system.ncutlog.NcutlogManager;
import cn.ncut.service.user.customstored.CustomStoredManager;
import cn.ncut.service.user.member.MemberManager;
import cn.ncut.service.user.storeddetail.StoredDetailManager;

/** 
 * 说明：客户储值卡
 * 创建人：FH Q313596790
 * 创建时间：2016-12-26
 */
@Controller
@RequestMapping(value="/customstored")
public class CustomStoredController extends BaseController {
	
	String menuUrl = "customstored/list.do"; //菜单地址(权限用)
	@Resource(name="customstoredService")
	private CustomStoredManager customstoredService;
	
	@Resource(name="memberService")
	private MemberManager memberService;
	
	@Resource(name="storeddetailService")
	private StoredDetailManager storeddetailService;
	
	@Resource(name = "ncutlogService")
	private NcutlogManager NCUTLOG;
	
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增CustomStored");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		
		// 得到当前的客服的门店编号
		Session session = Jurisdiction.getSession();
		Staff staff = ((Staff) session.getAttribute(Const.SESSION_USER));
		
		 
		PageData pd = new PageData();
		pd = this.getPageData();
		
		//查询新增的此用户
		PageData p = new PageData();
		p.put("UID", Integer.parseInt(pd.getString("UID")));
		PageData userpd = memberService.findById(p);
		
		PageData custompd = customstoredService.findByUidGroupByUid(Integer.parseInt(pd.getString("UID")));
		//新增用户储值卡信息时该用户已经存在，则为用户新建立一张储值卡（修改后）
		if(custompd!=null){
			custompd.put("REMAIN_MONEY", Double.parseDouble(pd.getString("REMAIN_MONEY")));
			custompd.put("REMAIN_POINTS", Double.parseDouble(pd.getString("REMAIN_POINTS")));
			custompd.put("STATUS","0");
			custompd.put("NAME",userpd.get("name"));
			pd.put("PASSWORD", MD5Util.MD5Encode("123456", "utf8"));
			customstoredService.save(custompd);
		}else{
			//新增用户储值卡信息时该用户不存在，则新添加一条记录
			pd.put("STATUS", "0");
			pd.put("NAME",userpd.get("name"));
			pd.put("PASSWORD", MD5Util.MD5Encode("123456", "utf8"));
			customstoredService.save(pd);
		}
		
		//不管哪一种，都要插入一条储值明细记录#{UID},	
		/**
		#{STORED_CATEGORY_ID},	
		#{STORE_ID},	
		#{STAFF_ID},
		#{WECHATPAY_MONEY},
		#{ALIPAY_MONEY},
		#{BANKPAY_MONEY},
		#{CASHPAY_MONEY},		
		
		#{CREATE_TIME},	
		#{REMARK},	
		#{STATUS},
		#{STOREDDETAIL_ID}
		**/
		PageData customMxpd = new PageData();
		customMxpd.put("STOREDDETAIL_ID", "OC"+PrimaryKeyGenerator.generateKey());
		customMxpd.put("UID", Integer.parseInt(pd.getString("UID")));
		customMxpd.put("STORE_ID", staff.getSTORE_ID());
		customMxpd.put("STAFF_ID", staff.getSTAFF_ID());
		customMxpd.put("CREATE_TIME", DateUtil.getTime());
		customMxpd.put("REMARK", pd.getString("REMARK"));
		customMxpd.put("TYPE", "3");
		customMxpd.put("MONEY", pd.getString("REMAIN_MONEY"));
		customMxpd.put("POINTS", pd.getString("REMAIN_POINTS"));
		customMxpd.put("REMARK", pd.getString("REMARK"));
		customMxpd.put("STATUS","0");
		storeddetailService.save(customMxpd);
		
		NCUTLOG.save(
				Jurisdiction.getUsername(),
				" 给用户" + userpd.getString("USERNAME")+"的储值卡充值：余额增加了"+pd.get("REMAIN_MONEY") + "元，返点增加了"+pd.getString("REMAIN_POINTS")+"元", 
				this.getRequest().getRemoteAddr());
		
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	public void delete(PrintWriter out) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除CustomStored");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		customstoredService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改CustomStored");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String PASSWORD = MD5Util.MD5Encode(pd.getString("PASSWORD"), "utf8");
		pd.put("PASSWORD", PASSWORD);
		customstoredService.editPsd(pd);
		
		//查询用户信息
		PageData p = new PageData();
		p.put("UID", Integer.parseInt(pd.getString("UID")));
		PageData userpd = memberService.findById(p);
		
		//日志
		NCUTLOG.save(
				Jurisdiction.getUsername(),
				" 修改了用户：" + userpd.getString("USERNAME")+"的储值卡密码", 
				this.getRequest().getRemoteAddr());
		
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表CustomStored");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = customstoredService.finaAll(page);	//列出CustomStored列表
		/*List<PageData>	memberList = new ArrayList<PageData>();
		for(PageData ppdd : varList){
			PageData	member = memberService.findByuid(ppdd.get("UID"));		
			if(member != null){
				ppdd.put("UID", member.get("name"));
			}
			memberList.add(ppdd);
		
		}*/
		mv.setViewName("user/customstored/customstored_list");
		
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
	
	
	
	/**去查看用户明细页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goScanStorededMx")
	public ModelAndView goScanStorededMx(Page page)throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		page.setPd(pd);
		
		//查询该用户有哪些储值明细
		List<PageData> varList = customstoredService.findStorededMxByUid(page);
		
		mv.setViewName("user/customstored/storededmx_list");
		mv.addObject("msg", "save");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		return mv;
	}	
	
	/**去查看用户二维码页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goTwoDimensionCode")
	public ModelAndView goTwoDimensionCode(Page page,HttpServletRequest request)throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		page.setPd(pd);
		String uid = pd.getString("UID");//得到用户的uid
		TwoDimensionCode.encoderQRCode("您的用户凭据为："+uid, request.getSession().getServletContext().getRealPath("/uploadFiles/code/twoDimensionCode.png"));

		
		mv.setViewName("user/customstored/showTwoDimensionCode");
		
		return mv;
	}
	
	
	/**
	 * 检查用户密码
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value="/checkPassword")
	public String checkPassword(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String password = request.getParameter("psd");
		String uid = request.getParameter("uid");
		
		PageData pd = new PageData();
		pd = this.getPageData();
		
		pd.put("UID", uid);
		pd.put("PASSWORD", MD5Util.MD5Encode(password, "utf8"));
		
		PageData newpd = customstoredService.checkpassword(pd);
		
		if(newpd!=null){
			return "{\"result\":\"success\"}";
		}else{
			return "{\"result\":\"error\"}";
		}
		
	}
	
	
	/**去新增页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goAdd")
	public ModelAndView goAdd()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("user/customstored/customstored_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	 /**根据phone查用户的姓名
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/findName")
	public void queryChildByParentID(HttpServletRequest request, HttpServletResponse response)throws Exception{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");//解决乱码 
	    String json=""; 
		PageData pd = new PageData();
	
		
		Map<String,Object> pp = new HashMap<String,Object>();
		pd = this.getPageData();
				
		 try{
				String PHONE =  request.getParameter("PHONE");			
				pd.put("PHONE", PHONE);
	           PageData ppdd = memberService.findName(pd);
	         
	            if(ppdd!=null){
	            	pp.put("UID",(Integer)ppdd.get("uid"));
	                pp.put("NAME", (String)ppdd.get("name"));
	            }	 
	            
	            StringBuilder sb = new StringBuilder();
	         	 sb.append("[");
	         	 sb.append("{");       
	         	 
	         	 for (String key : pp.keySet()) {
	             	
	                 sb.append("\"").append(key).append("\":\"").append(pp.get(key));
	                 sb.append("\"").append(",");                          
	             }
	         	sb.deleteCharAt(sb.length() - 1);
	         	
	         	 sb.append("}");
                
	         	 sb.append("]");
               
	         	json = sb.toString();
		 }catch (Exception e) {  
	            e.printStackTrace();  
	        } 
			response.getWriter().write(json);
			
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
		pd = customstoredService.findByCARDId(pd);	//根据ID读取
		mv.setViewName("user/customstored/updatePassword");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"批量删除CustomStored");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			customstoredService.deleteAll(ArrayDATA_IDS);
			pd.put("msg", "ok");
		}else{
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/customedexcel")
	public ModelAndView exportExcel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出CustomStored到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		//Page page = new Page();
		pd = this.getPageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		//page.setPd(pd);
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("客户姓名");	//1
		titles.add("手机号");	//2
		titles.add("用户昵称");
		titles.add("余额");	//3
		titles.add("剩余点数");	//4
		titles.add("用户类型");	//5
		dataMap.put("titles", titles);
		List<PageData> varOList = customstoredService.finaAllExcel(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			if(varOList.get(i).get("NAME")==null){
				vpd.put("var1","该用户暂未填写姓名");
			}else{
				vpd.put("var1", varOList.get(i).get("NAME").toString());
			}
			//vpd.put("var1", varOList.get(i).get("NAME").toString());	//1
			if(varOList.get(i).get("NAME")==null){
				vpd.put("var2","该用户暂未填写手机号");
			}else{
				vpd.put("var2", varOList.get(i).get("PHONE").toString());
			}
			//vpd.put("var2", varOList.get(i).get("PHONE").toString());	//2
			vpd.put("var3", varOList.get(i).get("username").toString());	//3
			vpd.put("var4", varOList.get(i).get("REMAIN_MONEY").toString());	//4
			vpd.put("var5", varOList.get(i).get("REMAIN_POINTS").toString());	//4
			vpd.put("var6", varOList.get(i).get("CATEGORY_NAME").toString());	//5
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
