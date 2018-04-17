package cn.ncut.service.finance.prestore.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.ncut.dao.DaoSupport;
import cn.ncut.entity.Page;
import cn.ncut.service.finance.prestore.PreStoreManager;
import cn.ncut.util.PageData;


/** 
 * 说明： 预存总金额
 * 创建人：FH Q313596790
 * 创建时间：2017-01-06
 * @version
 */
@Service("prestoreService")
public class PreStoreService implements PreStoreManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("PreStoreMapper.save", pd);
	}
	
	
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("PreStoreMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("PreStoreMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("PreStoreMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("PreStoreMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("PreStoreMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("PreStoreMapper.deleteAll", ArrayDATA_IDS);
	}
	
	public PageData findByUid(int uid)throws Exception{
		return (PageData)dao.findForObject("PreStoreMapper.findByUid", uid);
	}

	@Override
	public void updateMember(PageData pd) throws Exception {	
		dao.update("PreStoreMapper.editMember", pd);	
	}

	/**通过phone获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByPhone(PageData pd)throws Exception{
		return (PageData)dao.findForObject("PreStoreMapper.findByPhone", pd);
	}
	@Override
	public void updatePrestoreByPhone(PageData pd) throws Exception {	
		dao.update("PreStoreMapper.updatePrestoreByPhone", pd);	
	}
	
}

