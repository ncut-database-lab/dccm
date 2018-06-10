package cn.ncut.service.user.customstored;

import java.util.List;
import cn.ncut.entity.Page;
import cn.ncut.util.PageData;

/** 
 * 说明： 客户储值卡接口
 * 创建人：FH Q313596790
 * 创建时间：2016-12-26
 * @version
 */
public interface CustomStoredManager{

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
	
	/**修改密码
	 * @param pd
	 * @throws Exception
	 */
	public void editPsd(PageData pd)throws Exception;
	
	
	/**修改 传入的钱数和返点数是已经修改好的数据
	 * @param pd
	 * @throws Exception
	 */
	public void editSubMoney(PageData pd)throws Exception;
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
	
	/**通过CARD_id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByCARDId(PageData pd)throws Exception;
	
	/**通过phone获取数据
	 * @param String phone
	 * @throws Exception
	 */
	public List<PageData> findByPhone(String phone)throws Exception;
	
	
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	/**自己写的 根据员工编号和项目编号查询价格
	 * @param Pd
	 *  
	 * @throws Exception
	 */

	public List<PageData> finaAll(Page page)throws Exception;
	public List<PageData> findByUid(int uid)throws Exception;

	public void saveMx(PageData pd) throws Exception;
	
	/**
	 * 查询用户储值明细
	 * @param pd.UID
	 * @return
	 * @throws Exception
	 */
	public List<PageData> findStorededMxByUid(Page page) throws Exception;
	
	public void updateMember(PageData pd) throws Exception;
	
	
	/**
	 * 带事务的售卖储值卡
	 * @param sell_pd
	 * @param customstoredpd
	 * @throws Exception
	 */
	public void updateUserStoreded(PageData sell_pd,  PageData categorypd) throws Exception;

	/**
	 * 检查用户储值卡密码
	 * @param pd
	 */
	public PageData checkpassword(PageData pd) throws Exception;
	
	
	/**
	 * excel导出
	 * @param pd
	 */
	public List<PageData> finaAllExcel(PageData pd) throws Exception;
	
	public PageData findByUidGroupByUid(int uid)throws Exception;
	
	public PageData findStoredByUidGroupByUid(int uid)throws Exception;
	
	void updateStatusByCardId(PageData pd)throws Exception;
}

