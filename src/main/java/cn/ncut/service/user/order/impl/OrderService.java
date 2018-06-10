package cn.ncut.service.user.order.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ncut.dao.DaoSupport;
import cn.ncut.entity.Page;
import cn.ncut.entity.system.QueryOrder;
import cn.ncut.service.finance.customappoint.CustomappointManager;
import cn.ncut.service.finance.discount.DiscountManager;
import cn.ncut.service.finance.prestore.PreStoreManager;
import cn.ncut.service.finance.prestoremx.PreStoreMxManager;
import cn.ncut.service.finance.serviceall.ServiceCostManager;
import cn.ncut.service.user.member.MemberManager;
import cn.ncut.service.user.order.OrderManager;
import cn.ncut.service.user.order.OrderMxManager;
import cn.ncut.service.user.userdiscount.UserDiscountManager;
import cn.ncut.service.wechat.userStoredCard.impl.WeChatUserStoredCardService;
import cn.ncut.util.DateUtil;
import cn.ncut.util.PageData;
import cn.ncut.util.StringUtil;
import cn.ncut.util.UuidUtil;
import cn.ncut.util.wechat.CommonUtil;
import cn.ncut.util.wechat.PrimaryKeyGenerator;

/**
 * 说明： 订单 创建人： 创建时间：2016-12-30
 *
 * @version
 */

@Service("orderService")
public class OrderService implements OrderManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	@Autowired
	private ServiceCostManager serviceCostService;
	
	@Autowired
	private MemberManager memberService;
	
	@Autowired
	private OrderMxManager ordermxService;
	
	@Autowired
	private PreStoreManager prestoreService;
	
	@Autowired
	private PreStoreMxManager prestoremxService;
	
	@Autowired
	private CustomappointManager customappointService;
	
	@Autowired
	private UserDiscountManager userdiscountService;
		
	@Autowired
	private DiscountManager discountService;
	
	@Autowired
	private WeChatUserStoredCardService weChatUserStoredCardService;
	
	/**
	 * 新增
	 *
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd) throws Exception {
		dao.save("OrderMapper.save", pd);
	}

	/**
	 * 删除
	 *
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd) throws Exception {
		dao.delete("OrderMapper.delete", pd);
	}

	/**
	 * 修改
	 *
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd) throws Exception {
		dao.update("OrderMapper.edit", pd);
	}

	/**
	 * 更改状态为4
	 *
	 * @param pd
	 * @throws Exception
	 */
	public void updateStatus4(PageData pd) throws Exception {
		dao.update("OrderMapper.updateStatus4", pd);
	}

	/**
	 * 更改状态为3
	 *
	 * @param pd
	 * @throws Exception
	 */
	public void updateStatus3(PageData pd) throws Exception {
		dao.update("OrderMapper.updateStatus3", pd);
	}

	public void updateStatus2(PageData pd) throws Exception {
		dao.update("OrderMapper.updateStatus2", pd);
	}

	/**
	 * 更改预约时间
	 *
	 * @param pd
	 * @throws Exception
	 */
	public void updateTime(PageData pd) throws Exception {
		dao.update("OrderMapper.updateTime", pd);
	}

	/**
	 * 更改订单备注
	 *
	 * @param pd
	 * @throws Exception
	 */
	public void updateRemark(PageData pd) throws Exception {
		dao.update("OrderMapper.updateRemark", pd);
	}

	/**
	 * 列表
	 *
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page) throws Exception {
		return (List<PageData>) dao.findForList("OrderMapper.datalistPage",
				page);
	}

	/**
	 * 列表(全部)
	 *
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("OrderMapper.listAll", pd);
	}

	/**
	 * 通过id获取数据
	 *
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd) throws Exception {
		return (PageData) dao.findForObject("OrderMapper.findById", pd);
	}

	/**
	 * 批量删除
	 *
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
		dao.delete("OrderMapper.deleteAll", ArrayDATA_IDS);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> findAll(Page page) throws Exception {

		return (List<PageData>) dao.findForList("OrderMapper.findAlllistPage",
				page);
	}

	/**
	 * 查询今日订单总额
	 */
	public String queryTodayTotalMoney(String d) throws Exception {
		return (String) dao
				.findForObject("OrderMapper.queryTodayTotalMoney", d);
	}

	/**
	 * 查询历史订单总额
	 */
	public String queryhistoryTotalMoney(String d) throws Exception {
		return (String) dao.findForObject("OrderMapper.queryhistoryTotalMoney",
				d);
	}

	/**
	 * 查找所有可以退款的订单信息
	 */
	@Override
	public List<PageData> findAllCanRefund(Page page) throws Exception {
		return (List<PageData>) dao.findForList(
				"OrderMapper.findAllRefundlistPage", page);
	}

	/**
	 * 退款时修改订单状态并插入退款金额
	 *
	 * @param pd
	 * @throws Exception
	 */
	public void editStatusAndRefund(PageData pd) throws Exception {
		dao.update("OrderMapper.editStatusAndRefund", pd);
	}

	@Override
	public List<QueryOrder> quaryAllOrder(Page page) throws Exception {
		List<QueryOrder> orders= (List<QueryOrder>) dao.findForList(
				"QueryOrderMapper.queryOrderlistPage", page);
		return orders;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PageData> queryServiceCostByPName(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList(
				"QueryOrderMapper.queryServiceCostByPName", pd);
	}

	@Override
	public List<QueryOrder> quaryAllOrder(PageData pd) throws Exception {
		return (List<QueryOrder>) dao.findForList(
				"QueryOrderMapper.queryOrderAll", pd);
	}

	@Override
	public List<PageData> queryOrderMxBypMethod(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList(
				"QueryOrderMapper.queryOrderMxBypMethod", pd);
	}

	@Override
	public List<PageData> queryOrderSum(PageData pdd) throws Exception {
		return (List<PageData>) dao.findForList(
				"QueryOrderMapper.queryOrderSum", pdd);
	}

	@Override
	public List<PageData> queryOrderMxSum(PageData pdd) throws Exception {
		return (List<PageData>) dao.findForList(
				"QueryOrderMapper.queryOrderMxSum", pdd);
	}

	@Override
	public List<PageData> staticticsOrdersSum(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList(
				"StaticticsOrderMapper.orderCountsByStore", pd);
	}

	@Override
	public List<PageData> staticticsOrderByStaff(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList(
				"StaticticsOrderMapper.orderCountsByStaff", pd);
	}

	@Override
	public List<PageData> staticticsOrdersPayMethod(PageData pd)
			throws Exception {
		return (List<PageData>) dao.findForList(
				"StaticticsOrderMapper.orderPayMethodByStore", pd);
	}

	@Override
	public List<PageData> staticticsUserSource(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList(
				"StaticticsOrderMapper.UserSourceProvinceSum", pd);
	}

	@Override
	public List<PageData> staticticsUserSourceProvince(PageData pd)
			throws Exception {
		return (List<PageData>) dao.findForList(
				"StaticticsOrderMapper.UserSourceCitySumByProvince", pd);
	}

	@Override
	public List<PageData> staticticsOrdersSource(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList(
				"StaticticsOrderMapper.staticticsOrdersSource", pd);
	}

	@Override
	public List<PageData> staticticsService(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList(
				"StaticticsOrderMapper.orderCountsByService", pd);
	}

	@Override
	public Integer selectUniqueUserOrder() throws Exception {
		return (Integer) dao.findForObject(
				"StaticticsOrderMapper.selectUniqueUserOrder", null);
	}

	@Override
	public Integer selectRegistUserSum() throws Exception {
		return (Integer) dao.findForObject(
				"StaticticsOrderMapper.selectRegistUserSum", null);
	}

	@Override
	public Integer selectCompleteUserSum() throws Exception {
		return (Integer) dao.findForObject(
				"StaticticsOrderMapper.selectCompleteUserSum", null);
	}

	@Override
	public Integer selectWechatUserSum() throws Exception {
		String requestUrl = "https://api.weixin.qq.com/datacube/getusercumulate?access_token=ACCESS_TOKEN"
				.replace("ACCESS_TOKEN", CommonUtil.getToken().getAccessToken());
		Date currentTime = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(currentTime);
		calendar.add(calendar.DATE, -1);
		currentTime = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("begin_date", dateString);
		jsonObject.put("end_date", dateString);
		JSONObject httpsRequest = CommonUtil.httpsRequest(requestUrl, "POST",
				jsonObject.toString());
		Map m = httpsRequest;
		List list = (List) m.get("list");
		return (Integer) ((Map) list.get(0)).get("cumulate_user");
	}

	@Override
	public Integer findCountByUser(PageData pd) throws Exception {
		return (Integer) dao.findForObject("OrderMapper.findCountByUser", pd);
	}

	
	@Override
	public void createOrder(Map<String, List<Double>> map, PageData pd) throws Exception{
		
		PageData userpd = new PageData();
		userpd = memberService.findById(pd);
		
		for (Map.Entry<String, List<Double>> entry : map.entrySet()) {  
			String cost_id = entry.getKey().substring(0, entry.getKey().lastIndexOf("_")); 
			List<Double> list= entry.getValue();  
						
			PageData cost_pd = new PageData();
			pd.put("SERVICECOST_ID", cost_id);
			cost_pd = serviceCostService.findById(pd);
			
			//1.插入订单表
			PageData order_pd = new PageData();
			String orderId = "OD"+PrimaryKeyGenerator.generateKey();
			order_pd.put("ORDER_ID", orderId);
			order_pd.put("UID", pd.getString("UID"));
			order_pd.put("STORE_ID",  pd.getString("STORE_ID"));//对应医生的门店编号
			order_pd.put("SERVICECOST_ID", cost_id);
			order_pd.put("STAFF_ID", cost_pd.getString("STAFF_ID"));
			order_pd.put("SERVICE_STAFF_ID", pd.getString("SERVICE_STAFF_ID"));
			order_pd.put("ORDER_MONEY", ((BigDecimal)cost_pd.get("PRICE")).toString()); //每一次应收,打过折没用优惠券的价格
			order_pd.put("PAY_MONEY", StringUtil.countList(list));        //减过优惠券的价格
			order_pd.put("PROPORTION", (Double)pd.get("proportion"));        //用户最低折扣
			order_pd.put("CREATE_TIME", DateUtil.getTime()); // 创建时间
			order_pd.put("WECHAT_NAME", userpd.getString("name"));
			order_pd.put("WECHAT_PHONE", userpd.getString("phone"));
			order_pd.put("URL", "1");
			if(0==(Double)pd.get("isSingleProject")){ //单个项目
				order_pd.put("DISCOUNT_ID", pd.get("averageDiscountMoney")); // 存入该订单使用的优惠券金额
			}else{ //多个项目
				order_pd.put("DISCOUNT_ID", "0.00");
			}
			order_pd.put("REFUND", Double.parseDouble("0.00"));
			order_pd.put("ORDER_STATUS", 2); // 状态
			order_pd.put("RECOMMEND_TIME", pd.get("serviceTime"));//预约时间
			order_pd.put("REMARK", pd.getString("REMARK"));
			
			
			this.save(order_pd);
			
			//使用了余额支付，插入余额支付的订单明细，并对用户余额进行处理
			if(list.get(0)!=0){				
				//把余额的钱减了，并插入用户的余额消费明细
				//插入余额支付的订单明细
				order_pd.put("PRESTOREPAY_MONEY", list.get(0));
				insertPayDetail(order_pd,5,"PRESTOREPAY_MONEY");
				
				PageData prepd = prestoreService.findByUid(Integer.parseInt(pd.getString("UID")));
				prepd.put("SUM_MONEY", (Double)prepd.get("SUM_MONEY") -list.get(0));
				prestoreService.edit(prepd);
				if(prepd!=null){
					//插入预存消费明细记录，预存的总金额需要一次扣除n个订单的
					PageData premxpd = new PageData();				
					premxpd.put("UID",pd.getString("UID"));
					premxpd.put("PRESTOREMX_ID",UuidUtil.get32UUID());
					premxpd.put("PRESTORE_ID", prepd.getString("PRESTORE_ID"));
					premxpd.put("TYPE", 2);
					premxpd.put("STAFF_ID", pd.getString("SERVICE_STAFF_ID"));
					premxpd.put("PHONE", userpd.getString("phone"));
					premxpd.put("USERNAME", userpd.getString("username"));
					premxpd.put("CREATE_TIME", DateUtil.getTime());
					premxpd.put("PRESTOREMONEY",list.get(0));
					prestoremxService.save(premxpd);
				}
			}
			
			//使用了储值卡支付，并对用户储值卡钱进行处理
			if(list.get(1) != 0){
				//插入储值卡支付的订单明细
				order_pd.put("STOREDPAY_MONEY", list.get(1));
				insertPayDetail(order_pd,2,"STOREDPAY_MONEY");
				
				//扣除储值卡的钱，并插入明细
				weChatUserStoredCardService.countCzkMoney(Integer.parseInt(pd.getString("UID")), list.get(1), pd.getString("STORE_ID"), pd.getString("SERVICE_STAFF_ID"), null);
			}
			
			
			//使用了银行卡支付，直接插入订单明细
			if(list.get(2) != 0){
				order_pd.put("BANKPAY_MONEY", list.get(2));
				insertPayDetail(order_pd,4,"BANKPAY_MONEY");
			}
			
			//使用了支付宝支付，直接插入订单明细
			if(list.get(3) != 0){
				order_pd.put("ALIPAY_MONEY", list.get(3));
				insertPayDetail(order_pd,1,"ALIPAY_MONEY");
			}
			
			//使用了微信支付，直接插入订单明细
			if(list.get(4) != 0){
				order_pd.put("WECHATPAY_MONEY", list.get(4));
				insertPayDetail(order_pd,0,"WECHATPAY_MONEY");
			}
			
			//使用了现金支付，直接插入订单明细
			if(list.get(5) != 0){
				order_pd.put("CASHPAY_MONEY", list.get(5));
				insertPayDetail(order_pd,3,"CASHPAY_MONEY");
			}
		
			//3.插入预约
			PageData appoint_pd = new PageData();
			appoint_pd.put("U_ID", pd.getString("UID"));
			appoint_pd.put("CUSTOMAPPOINT_ID", UuidUtil.get32UUID());
			appoint_pd.put("APPOINT_CODE", (char)(Math.random()*900000000)+10000000);
			appoint_pd.put("SERVICE_STAFF_ID",pd.getString("SERVICE_STAFF_ID"));
			
			appoint_pd.put("APPOINT_TIME", pd.get("serviceTime"));
			appoint_pd.put("EXPIRE_TIME", DateUtil.caculateGuoqiTime(pd.getString("serviceTime")));
			
			appoint_pd.put("ORDER_ID", orderId);
			customappointService.save(appoint_pd);									
		}
		
		//最后消掉本次所有订单中使用的用户的优惠券
		if(0==(Double)pd.get("isSingleProject")){ //单个项目
			if(pd.getString("DiscountJson")!=null ){
				//消掉用户的优惠券
				JSONArray data = JSONArray.fromObject(pd.getString("DiscountJson"));
				for(int j=0; j<data.size(); j++){
					
					JSONObject obj =  (JSONObject) data.get(j);
					
				    String group_discount = obj.getString("discountid");
				    String discount_id = group_discount.substring(group_discount.indexOf("-")+1);
				    int number = Integer.parseInt(obj.getString("number"));
				    
				    //从tb_user_discount表里查出优惠券，置为已使用
				    PageData dis_pd = new PageData();
				    dis_pd.put("UID", pd.getString("UID"));
				    dis_pd.put("DISCOUNT_ID", discount_id);
				    
				    List<PageData> discountList = discountService.findByUidAndDiscountId(dis_pd);
				    for(int t=0; t<number; t++){
				    	PageData already_dis_pd = discountList.get(t);
				    	already_dis_pd.put("isUsed", 1);		
				    	userdiscountService.edit(already_dis_pd);
				    }
				}
			}
		}
	}
	
	@Override
	public void createOrderHuiDian(Map<String, Double> map, PageData pd) throws Exception{
		
		PageData userpd = new PageData();
		userpd = memberService.findById(pd);
		
		for (Map.Entry<String, Double> entry : map.entrySet()) {  
			
			String cost_id = entry.getKey().substring(0, entry.getKey().lastIndexOf("_")); 
			Double singlemoney= entry.getValue();  
			
			PageData cost_pd = new PageData();
			pd.put("SERVICECOST_ID", cost_id);
			cost_pd = serviceCostService.findById(pd);
			
			//判断是否为0元订单
			if(singlemoney!=0.0){//不是0元订单
			//1.插入订单表
			PageData order_pd = new PageData();
			String orderId = "OD"+PrimaryKeyGenerator.generateKey();
			order_pd.put("ORDER_ID", orderId);
			order_pd.put("UID", pd.getString("UID"));
			order_pd.put("STORE_ID",  pd.getString("STORE_ID"));//对应医生的门店编号
			order_pd.put("SERVICECOST_ID", cost_id);
			order_pd.put("STAFF_ID", cost_pd.getString("STAFF_ID"));
			order_pd.put("SERVICE_STAFF_ID", pd.getString("SERVICE_STAFF_ID"));
			order_pd.put("ORDER_MONEY", ((BigDecimal)cost_pd.get("PRICE")).toString()); //每一次应收,打过折没用优惠券的价格
			order_pd.put("PAY_MONEY", singlemoney);        //减过优惠券的价格
			order_pd.put("PROPORTION", (Double)pd.get("proportion"));        //用户最低折扣
			order_pd.put("CREATE_TIME", DateUtil.getTime()); // 创建时间
			order_pd.put("WECHAT_NAME", userpd.getString("name"));
			order_pd.put("WECHAT_PHONE", userpd.getString("phone"));
			order_pd.put("URL", "1");
			if(0==(Double)pd.get("isSingleProject")){ //单个项目
				order_pd.put("DISCOUNT_ID", pd.get("averageDiscountMoney")); // 存入该订单使用的优惠券金额
			}else{ //多个项目
				order_pd.put("DISCOUNT_ID", "0.00");
			}
			order_pd.put("REFUND", Double.parseDouble("0.00"));
			order_pd.put("ORDER_STATUS", 0); // 未支付订单
			order_pd.put("RECOMMEND_TIME", pd.get("serviceTime"));//预约时间
			order_pd.put("REMARK", pd.getString("REMARK"));
			
			
			this.save(order_pd);
			
		
		
											
		}else{//是0元订单
			//1.插入订单表
			PageData order_pd = new PageData();
			String orderId = "OD"+PrimaryKeyGenerator.generateKey();
			order_pd.put("ORDER_ID", orderId);
			order_pd.put("UID", pd.getString("UID"));
			order_pd.put("STORE_ID",  pd.getString("STORE_ID"));//对应医生的门店编号
			order_pd.put("SERVICECOST_ID", cost_id);
			order_pd.put("STAFF_ID", cost_pd.getString("STAFF_ID"));
			order_pd.put("SERVICE_STAFF_ID", pd.getString("SERVICE_STAFF_ID"));
			order_pd.put("ORDER_MONEY", ((BigDecimal)cost_pd.get("PRICE")).toString()); //每一次应收,打过折没用优惠券的价格
			order_pd.put("PAY_MONEY", 0.00);        //减过优惠券的价格
			order_pd.put("PROPORTION", (Double)pd.get("proportion"));        //用户最低折扣
			order_pd.put("CREATE_TIME", DateUtil.getTime()); // 创建时间
			order_pd.put("WECHAT_NAME", userpd.getString("name"));
			order_pd.put("WECHAT_PHONE", userpd.getString("phone"));
			order_pd.put("URL", "1");
			if(0==(Double)pd.get("isSingleProject")){ //单个项目
				order_pd.put("DISCOUNT_ID", pd.get("averageDiscountMoney")); // 存入该订单使用的优惠券金额
			}else{ //多个项目
				order_pd.put("DISCOUNT_ID", "0.00");
			}
			order_pd.put("REFUND", Double.parseDouble("0.00"));
			order_pd.put("ORDER_STATUS", 2); // 已支付订单
			order_pd.put("RECOMMEND_TIME", pd.get("serviceTime"));//预约时间
			order_pd.put("REMARK", pd.getString("REMARK"));
			
			
			this.save(order_pd);
			//插入订单明细
			order_pd.put("CASHPAY_MONEY", 0);
			insertPayDetail(order_pd,3,"CASHPAY_MONEY");
			//插入预约表
			//3.插入预约
			PageData appoint_pd = new PageData();
			appoint_pd.put("U_ID", pd.getString("UID"));
			appoint_pd.put("CUSTOMAPPOINT_ID", UuidUtil.get32UUID());
			appoint_pd.put("APPOINT_CODE", (char)(Math.random()*900000000)+10000000);
			appoint_pd.put("SERVICE_STAFF_ID",pd.getString("SERVICE_STAFF_ID"));
			
				appoint_pd.put("APPOINT_TIME", pd.get("serviceTime"));
				appoint_pd.put("EXPIRE_TIME", DateUtil.caculateGuoqiTime(pd.getString("serviceTime")));
			
			
			appoint_pd.put("ORDER_ID", orderId);
			customappointService.save(appoint_pd);	
		
		}
		}
		
		//最后消掉本次所有订单中使用的用户的优惠券
		if(0==(Double)pd.get("isSingleProject")){ //单个项目
			if(pd.getString("DiscountJson")!=null ){
				//消掉用户的优惠券
				JSONArray data = JSONArray.fromObject(pd.getString("DiscountJson"));
				for(int j=0; j<data.size(); j++){
					
					JSONObject obj =  (JSONObject) data.get(j);
					
				    String group_discount = obj.getString("discountid");
				    String discount_id = group_discount.substring(group_discount.indexOf("-")+1);
				    int number = Integer.parseInt(obj.getString("number"));
				    
				    //从tb_user_discount表里查出优惠券，置为已使用
				    PageData dis_pd = new PageData();
				    dis_pd.put("UID", pd.getString("UID"));
				    dis_pd.put("DISCOUNT_ID", discount_id);
				    
				    List<PageData> discountList = discountService.findByUidAndDiscountId(dis_pd);
				    for(int t=0; t<number; t++){
				    	PageData already_dis_pd = discountList.get(t);
				    	already_dis_pd.put("isUsed", 1);		
				    	userdiscountService.edit(already_dis_pd);
				    }
				}
			}
		}
	}
	
	public void insertPayDetail(PageData pd, int method, String methodText) throws Exception{
		PageData ppdd = new PageData();
		ppdd.put("ORDERMX_ID", UuidUtil.get32UUID());
		ppdd.put("UID", pd.getString("UID"));
		ppdd.put("ORDER_ID", pd.getString("ORDER_ID"));
		ppdd.put("ORDER_MONEY", pd.getString("ORDER_MONEY"));
		ppdd.put("PAY_MONEY", (Double)pd.get(methodText));
		ppdd.put("PAY_METHOD", method);
		ppdd.put("PAY_TIME", pd.getString("CREATE_TIME"));
		ppdd.put("REMARK", "");
		
		ordermxService.save(ppdd);
	}

}
