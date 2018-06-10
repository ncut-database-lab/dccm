package cn.ncut.service.user.customstored.impl;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import cn.ncut.dao.DaoSupport;
import cn.ncut.entity.Page;
import cn.ncut.util.PageData;
import cn.ncut.service.user.customstored.CustomStoredManager;

/** 
 * 说明： 客户储值卡
 * 创建人：FH Q313596790
 * 创建时间：2016-12-26
 * @version
 */
@Service("customstoredService")
public class CustomStoredService implements CustomStoredManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("CustomStoredMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("CustomStoredMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("CustomStoredMapper.edit", pd);
	}
	
	/**修改密码
	 * @param pd
	 * @throws Exception
	 */
	public void editPsd(PageData pd)throws Exception{
		dao.update("CustomStoredMapper.editPsd", pd);
	}
	
	/**修改 传入的钱数和返点数是已经修改好的数据
	 * @param pd
	 * @throws Exception
	 */
	public void editSubMoney(PageData pd)throws Exception{
		dao.update("CustomStoredMapper.editSubMoney", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("CustomStoredMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("CustomStoredMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("CustomStoredMapper.findById", pd);
	}
	
	/**通过CARD_ID获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByCARDId(PageData pd)throws Exception{
		return (PageData) dao.findForObject("CustomStoredMapper.findByCARDId", pd);
	}
	
	/**通过CARD_ID获取数据
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> findByPhone(String phone)throws Exception{
		return (List<PageData>)dao.findForList("CustomStoredMapper.findByPhone", phone);
	}
	
	
	public List<PageData> findByUid(int uid)throws Exception{
		return (List<PageData>)dao.findForList("CustomStoredMapper.findByUid", uid);
	}
	
	public PageData findByUidGroupByUid(int uid)throws Exception{
		return (PageData)dao.findForObject("CustomStoredMapper.findByUidGroupByUid", uid);
	}
	
	public PageData findStoredByUidGroupByUid(int uid)throws Exception{
		return (PageData)dao.findForObject("CustomStoredMapper.findByUidGroupByUid", uid);
	}
	
	@Override
	public void updateStatusByCardId(PageData pd)throws Exception{
		dao.update("CustomStoredMapper.updateStatusByCardId", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("CustomStoredMapper.deleteAll", ArrayDATA_IDS);
	}

	
	public List<PageData> finaAll(Page page) throws Exception {
		 
		return (List<PageData>)dao.findForList("CustomStoredMapper.findAlllistPage", page);
	}


	@Override
	public List<PageData> findStorededMxByUid(Page page) throws Exception {
		// TODO Auto-generated method stub
		return (List<PageData>)dao.findForList("StoredDetailMapper.findStorededMxByUidlistPage", page);
	}


	@Override
	public void saveMx(PageData pd) throws Exception {
		dao.save("CustomStoredMapper.saveMx", pd);
	}

	@Override
	public void updateUserStoreded(PageData sell_pd,  PageData categorypd)
			throws Exception {
		dao.save("StoredDetailMapper.save", sell_pd);
	
			sell_pd.put("REMAIN_MONEY", categorypd.get("STORED_MONEY")); // 余额
			sell_pd.put("REMAIN_POINTS", categorypd.get("RETURN_POINTS")); // 剩余点数
			sell_pd.put("STATUS", "0"); // 状态
			dao.save("CustomStoredMapper.save", sell_pd);
		
	}

	public void updateMember(PageData pd)throws Exception {
		dao.update("CustomStoredMapper.editMember", pd);	
		
	}

	@Override
	public PageData checkpassword(PageData pd) throws Exception {
		return (PageData) dao.findForObject("CustomStoredMapper.checkpassword", pd);
	}
	
	public List<PageData> finaAllExcel(PageData pd) throws Exception {
		 
		return (List<PageData>)dao.findForList("CustomStoredMapper.findAll", pd);
	}

}

