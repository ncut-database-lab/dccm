package cn.ncut.controller.finance.serviceall;

import java.io.PrintWriter;
import java.text.DateFormat;
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

import org.codehaus.jackson.map.ObjectMapper;
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
import cn.ncut.service.finance.serviceall.ServiceCostManager;
import cn.ncut.service.finance.serviceall.impl.ServiceProjectService;
import cn.ncut.service.system.ncutlog.NcutlogManager;
import cn.ncut.service.system.staff.impl.StaffService;
import cn.ncut.service.system.store.impl.StoreService;
import cn.ncut.util.AppUtil;
import cn.ncut.util.Const;
import cn.ncut.util.Jurisdiction;
import cn.ncut.util.ObjectExcelView;
import cn.ncut.util.PageData;

/**
 * 说明：定价 创建人：ljj 创建时间：2016-12-19
 */
@Controller
@RequestMapping(value = "/servicecost")
public class ServiceCostController extends BaseController {

    String menuUrl = "servicecost/list.do"; // 菜单地址(权限用)
    @Resource(name = "servicecostService")
    private ServiceCostManager servicecostService;

    @Resource(name = "storeService")
    private StoreService storeService;

    @Resource(name = "serviceprojectService")
    private ServiceProjectService serviceprojectService;

    @Resource(name = "staffService")
    private StaffService staffService;

    @Resource(name = "ncutlogService")
    private NcutlogManager NCUTLOG;

    /**
     * 保存
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/save")
    public ModelAndView save() throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "新增ServiceCost");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
            return null;
        } // 校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
//		// 根据医生查询所属门店
//		String storeid = staffService.queryStoreBySID(pd.getString("STAFF_ID"));
//		// 插入门店的编号
//		pd.put("STORE_ID", storeid);
//		pd.put("SERVICECOST_ID", this.get32UUID()); // 主键
//		servicecostService.save(pd);
//		PageData pdd = staffService.findById(pd);
//		String pname = serviceprojectService.findNameById(pd);
//
//		// 插入日志
//		NCUTLOG.save(Jurisdiction.getUsername(),
//				" 新增服务项目类别价格:" + pdd.getString("STAFF_NAME") + "医生，" + pname+"，"
//						+ pd.getString("PRICE")+ "元,"
//						+ (pd.getString("ISFIRST").equals("0") ? "初诊" : "复诊")
//						, this.getRequest().getRemoteAddr());

        String[] staffIds = ((String) pd.get("staffIds")).split(",");
        for (String staffId : staffIds) {
            String storeid = staffService.queryStoreBySID(staffId);
            pd.put("STORE_ID", storeid);
            pd.put("SERVICECOST_ID", this.get32UUID()); // 主键
            pd.put("STAFF_ID", staffId);
            servicecostService.save(pd);

            PageData pdd = staffService.findById(pd);
            String pname = serviceprojectService.findNameById(pd);

            // 插入日志
            NCUTLOG.save(Jurisdiction.getUsername(),
                    " 新增服务项目类别价格:" + pdd.getString("STAFF_NAME") + "医生，" + pname + "，"
                            + pd.getString("PRICE") + "元,"
                            + (pd.getString("ISFIRST").equals("0") ? "初诊" : "复诊")
                    , this.getRequest().getRemoteAddr());
        }

        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 根据项目类别查询属于该类别的所有项目，并返回json串
     *
     * @param request
     * @throws Exception
     */
    @RequestMapping(value = "/queryProjectByCID")
    public void queryProjectByCID(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {

        String cid = request.getParameter("cid");
        List<PageData> pdlist = serviceprojectService.queryProjectByCID(cid);
        String s = new ObjectMapper().writeValueAsString(pdlist);
        // System.out.println(s);
        response.setContentType("text/html;charset=utf-8");// 解决乱码
        response.getWriter().write(s);

    }

    /**
     * 根据staff_id查project和cost
     *
     * @param
     * @throws Exception
     */
    @RequestMapping(value = "/queryProjectAndCost")
    public void queryProjectAndCost(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        response.setContentType("text/html;charset=utf-8");// 解决乱码
        String json = "";
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            String staff_id = request.getParameter("staff_id");

            pd.put("STAFF_ID", staff_id);
            List<Map<String, Object>> msgList = new ArrayList<Map<String, Object>>();
            List<PageData> servicecostlist = servicecostService
                    .findServiceAndCostByStaff_id(pd);
            // json = JSONArray.fromObject(servicecostlist).toString();
            if (servicecostlist.size() > 0) {
                for (int i = 0; i < servicecostlist.size(); i++) {
                    PageData pd2 = servicecostlist.get(i);
                    Map<String, Object> msgMap = new HashMap<String, Object>();
                    msgMap.put("PID", pd2.get("PID"));
                    msgMap.put("PNAME", pd2.get("PNAME"));
                    msgList.add(msgMap);
                }
                if (msgList.size() > 0) {
                    // json = JSONArray.fromObject(msgList).toString();
                    // msg=JsonUtil.beanToJson(msgList);
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    // sb.append("{");
                    for (Map<String, Object> map : msgList) {
                        sb.append("{");
                        for (String key : map.keySet()) {

                            sb.append("\"").append(key).append("\":\"")
                                    .append(map.get(key));
                            sb.append("\"").append(",");

                        }
                        sb.deleteCharAt(sb.length() - 1);
                        // sb.append("\"").append(",");
                        sb.append("}");
                        sb.append(",");
                    }
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    // sb.append("}");
                    sb.append("]");
                    json = sb.toString();
                }
                // }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(json);
        // json = "[{\"a\":\"1\"}]";
        response.getWriter().write(json);
    }

    @RequestMapping(value = "/queryPriceByPID")
    public void queryPriceByPID(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        response.setContentType("text/html;charset=utf-8");// 解决乱码
        String json = "";
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            String project_id = request.getParameter("project_id");

            pd.put("PID", project_id);
            List<Map<String, Object>> msgList = new ArrayList<Map<String, Object>>();
            List<PageData> servicecostlist = servicecostService
                    .queryPriceByPID(pd);
            // json = JSONArray.fromObject(servicecostlist).toString();
            if (servicecostlist.size() > 0) {
                for (int i = 0; i < servicecostlist.size(); i++) {
                    PageData pd2 = servicecostlist.get(i);
                    Map<String, Object> msgMap = new HashMap<String, Object>();
                    msgMap.put("PID", pd2.get("PID"));
                    msgMap.put("PRICE", pd2.get("PRICE"));
                    msgList.add(msgMap);
                }
                if (msgList.size() > 0) {
                    // json = JSONArray.fromObject(msgList).toString();
                    // msg=JsonUtil.beanToJson(msgList);
                    StringBuilder sb = new StringBuilder();
                    sb.append("[");
                    // sb.append("{");
                    for (Map<String, Object> map : msgList) {
                        sb.append("{");
                        for (String key : map.keySet()) {

                            sb.append("\"").append(key).append("\":\"")
                                    .append(map.get(key));
                            sb.append("\"").append(",");

                        }
                        sb.deleteCharAt(sb.length() - 1);
                        // sb.append("\"").append(",");
                        sb.append("}");
                        sb.append(",");
                    }
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    // sb.append("}");
                    sb.append("]");
                    json = sb.toString();
                }
                // }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(json);
        // json = "[{\"a\":\"1\"}]";
        response.getWriter().write(json);
    }

    /**
     * 删除
     *
     * @param out
     * @throws Exception
     */
    @RequestMapping(value = "/delete")
    public void delete(PrintWriter out) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "删除ServiceCost");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
            return;
        } // 校验权限
        PageData pd = new PageData();
        pd = this.getPageData();
        servicecostService.delete(pd);
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
        logBefore(logger, Jurisdiction.getUsername() + "修改ServiceCost");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
            return null;
        } // 校验权限
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();

        String[] staffIds = ((String) pd.get("staffIds")).split(",");
        String storeid = staffService.queryStoreBySID(staffIds[0]);
        pd.put("STAFF_ID", staffIds[0]);

        // 根据医生查询所属门店
//        String storeid = staffService.queryStoreBySID(pd.getString("STAFF_ID"));
        // 插入门店的编号
        pd.put("STORE_ID", storeid);
        servicecostService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }

    /**
     * 列表
     *
     * @param page
     * @throws Exception
     */
    @RequestMapping(value = "/list")
    public ModelAndView list(Page page, HttpSession session) throws Exception {
        logBefore(logger, Jurisdiction.getUsername() + "列表ServiceCost");
        // if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
        // //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
        ModelAndView mv = this.getModelAndView();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        PageData pd = new PageData();
        pd = this.getPageData();
        String keywords = pd.getString("keywords"); // 关键词检索条件
        if (null != keywords && !"".equals(keywords)) {
            pd.put("keywords", keywords.trim());
        }
        Staff sessionstaff = (Staff) session.getAttribute(Const.SESSION_USER);
        pd.put("STORE_ID", sessionstaff.getSTORE_ID());
        List<PageData> storeList = storeService.findAllNames(pd);
        pd.put("STOREID", pd.get("STOREID"));
        page.setPd(pd);
        List<PageData> varList = servicecostService.list(page); // 列出ServiceCost列表
        List<PageData> newlist = new ArrayList<PageData>();
        for (PageData p : varList) {
            if (df.parse(p.get("EFFECTIVE_DATE").toString()).getTime() > new Date().getTime()) {
                p.put("STATUS", "0");
            }
            servicecostService.edit(p);
            newlist.add(p);
        }
        mv.setViewName("finance/servicecost/servicecost_list");
        mv.addObject("varList", newlist);
        mv.addObject("storeList", storeList);
        mv.addObject("pd", pd);
        mv.addObject("STORE_ID", pd.get("STOREID"));
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
    public ModelAndView goAdd(HttpSession session) throws Exception {
        ModelAndView mv = this.getModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        Staff sessionstaff = (Staff) session.getAttribute(Const.SESSION_USER);
        pd.put("STORE_ID", sessionstaff.getSTORE_ID());
        // 查询所有门店(自己门店)
        // 查询当前用户的门店, 注意是否是管理员, 若是管理员可以查看所有门店信息
        List<PageData> storePdlist = storeService.listAll(pd);
        // 查询所有的项目
        // 查询所有的项目, 不根据当前用户所在的门店做出区分
        List<PageData> projectPdList = serviceprojectService.listAll(pd);
        // 查询员工表中的医生（自己门店的）
        // 查询当前门店所拥有的医生, 在登录用户为管理员时, 列举所有员工
        List<PageData> staffPdlist = staffService.listAll(pd);

        mv.setViewName("finance/servicecost/servicecost_edit_extension");
        mv.addObject("storePdlist", storePdlist);
        mv.addObject("projectPdList", projectPdList);
        mv.addObject("staffPdlist", staffPdlist);
        mv.addObject("msg", "save");
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

        // 查询员工表中的医生
//        List<PageData> staffPdlist = staffService.listAll(pd);
//
//        pd = servicecostService.findById(pd); // 根据ID读取
//        mv.setViewName("finance/servicecost/servicecost_edit_extension");
//        mv.addObject("staffPdlist", staffPdlist);

        // 服务项目信息
        pd = servicecostService.findById(pd);
        // 项目对应的医生信息
        PageData staff = staffService.findById(pd);
        // 医生的门店信息
        PageData store = storeService.findById(pd);

        staff.put("STORE_NAME", store.get("STORE_NAME"));
        List<PageData> staffPdlist = new ArrayList<>();

        staffPdlist.add(staff);

        mv.setViewName("finance/servicecost/servicecost_edit_extension");
        mv.addObject("staffPdlist", staffPdlist);

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
        logBefore(logger, Jurisdiction.getUsername() + "批量删除ServiceCost");
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
            servicecostService.deleteAll(ArrayDATA_IDS);
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
        logBefore(logger, Jurisdiction.getUsername() + "导出ServiceCost到excel");
        if (!Jurisdiction.buttonJurisdiction(menuUrl, "cha")) {
            return null;
        }
        ModelAndView mv = new ModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<String> titles = new ArrayList<String>();
        titles.add("门店"); // 1
        titles.add("项目名称"); // 2
        titles.add("主治医生"); // 3
        titles.add("创建时间"); // 4
        titles.add("价格"); // 5
        titles.add("类型"); // 6
        titles.add("状态"); // 7
        dataMap.put("titles", titles);
        List<PageData> varOList = servicecostService.listAll(pd);
        List<PageData> varList = new ArrayList<PageData>();
        for (int i = 0; i < varOList.size(); i++) {
            PageData vpd = new PageData();
            vpd.put("var1", varOList.get(i).getString("STORE_ID")); // 1
            vpd.put("var2", varOList.get(i).get("PID").toString()); // 2
            vpd.put("var3", varOList.get(i).getString("STAFF_ID")); // 3
            vpd.put("var4", varOList.get(i).getString("CREATETIME")); // 4
            vpd.put("var5", varOList.get(i).get("PRICE").toString()); // 5
            vpd.put("var6", varOList.get(i).get("ISFIRST").toString()); // 6
            vpd.put("var7", varOList.get(i).get("STATUS").toString()); // 7
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
