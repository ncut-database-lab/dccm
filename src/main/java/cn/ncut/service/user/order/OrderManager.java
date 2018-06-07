package cn.ncut.service.user.order;

import java.util.List;
import java.util.Map;

import cn.ncut.entity.Page;
import cn.ncut.entity.system.QueryOrder;
import cn.ncut.util.PageData;


/** 
 * 说明： 订单接口
 * 创建人：FH Q313596790
 * 创建时间：2016-12-30
 * @version
 */
public interface OrderManager{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception;
	
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception;
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception;
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception;
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;

	public void updateStatus4(PageData pd)throws Exception;
	public void updateStatus3(PageData pd)throws Exception;
	public void updateStatus2(PageData pd)throws Exception;
	public void updateRemark(PageData pd)throws Exception;


	public List<PageData> findAll(Page page)throws Exception;
	public void updateTime(PageData pd)throws Exception;

	

	/**
	 * 查询今日订单总额
	 */
	public String queryTodayTotalMoney(String d) throws Exception;
	
	

	/**
	 * 查询历史订单总额
	 */
	public String queryhistoryTotalMoney(String d) throws Exception;

	/**
	 * 
	 *<p>Tittle:findAllCanRefund</p>
	 *<p>Description:查找可以退款的订单信息</p>
	 *@param page
	 *@return
	 *@author lph
	 *@date 2017-2-21下午3:55:42
	 */
	public List<PageData> findAllCanRefund(Page page) throws Exception;
	
	/**退款时修改订单状态并插入退款金额
	 * @param pd
	 * @throws Exception
	 */
	public void editStatusAndRefund(PageData pd)throws Exception;
	
	
	/**
	 * 查询统计模块，查询订单分页
	 */
	public List<QueryOrder> quaryAllOrder(Page page) throws Exception;
	/**
	 * 根据项目名称查询项目cost——id
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> queryServiceCostByPName(PageData pd) throws Exception;

	/**
	 * 查询所有符合条件的结果
	 * @throws Exception 
	 */
	public List<QueryOrder> quaryAllOrder(PageData pd) throws Exception;

	/**
	 * 查询订单明细根据pmethod
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> queryOrderMxBypMethod(PageData pd)throws Exception;

	/**根据订单ids统计应收金额，优惠总额，实收金额，退款总额
	 * @param pdd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> queryOrderSum(PageData pdd)throws Exception;

	/**根据订单ids查询订单明细 根据不同的支付方式sum
	 * @param pdd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> queryOrderMxSum(PageData pdd) throws Exception;
	/**
	 *统计各个门店的各种状态的的订单数量和钱 
	 */
	public List<PageData> staticticsOrdersSum(PageData pd) throws Exception;


	public List<PageData> staticticsOrderByStaff(PageData pd)throws Exception;


	/**
	 *查询时间段、门店和医生的订单来源 
	 */
	public List<PageData> staticticsOrdersSource(PageData pd) throws Exception; 

	/**
	 *统计各个门店的各种支付方式下的实收的钱
	 */
	public List<PageData> staticticsOrdersPayMethod(PageData pd) throws Exception;
	/**
	 *统计的客户来源全国
	 */
	public List<PageData> staticticsUserSource(PageData pd) throws Exception;

	/**
	 *统计的客户来源按照省份
	 */
	public List<PageData> staticticsUserSourceProvince(PageData pd) throws Exception;
	/**
	 * 生成订单的去重用户的总数
	 */
	public Integer selectUniqueUserOrder() throws Exception;
	/**
	 * 注册的用户的个数
	 */
	public Integer selectRegistUserSum() throws Exception;
	/**
	 * 完善用户信息的用户的个数
	 */
	public Integer selectCompleteUserSum() throws Exception;
	/**
	 * 查询关注公众号的人数
	 */
	public Integer selectWechatUserSum() throws Exception;

	public List<PageData> staticticsService(PageData pd)throws Exception;

	/**
	 * 
	 *<p>Tittle:查找本项目剩余次数</p>
	 *<p>Description:</p>
	 *@param pd
	 *@return
	 *@throws Exception
	 *@author lph
	 *@date 2017-7-30下午9:15:44
	 */
	public Integer findCountByUser(PageData pd) throws Exception;

	public void createOrder(Map<String, List<Double>> map, PageData pd) throws Exception;


}

