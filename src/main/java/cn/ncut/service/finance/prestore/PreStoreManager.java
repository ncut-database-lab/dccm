package cn.ncut.service.finance.prestore;

import java.util.List;

import cn.ncut.entity.Page;
import cn.ncut.util.PageData;


/** 
 * 说明： 预存总金额接口
 * 创建人：FH Q313596790
 * 创建时间：2017-01-06
 * @version
 */
public interface PreStoreManager{

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
	
	
	/**
	 * 根据用户id查询其预存记录
	 * @throws Exception
	 */
	public PageData findByUid(int uid)throws Exception;
	
	public void updateMember(PageData pd)throws Exception;
	
	public PageData findByPhone(PageData pd)throws Exception;
	public void updatePrestoreByPhone(PageData pd) throws Exception;
	
}

