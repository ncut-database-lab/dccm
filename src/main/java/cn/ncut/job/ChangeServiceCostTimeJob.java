package cn.ncut.job;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import cn.ncut.service.finance.serviceall.ServiceCostManager;
import cn.ncut.util.PageData;

public class ChangeServiceCostTimeJob {
	
	@Resource(name = "servicecostService")
	private ServiceCostManager servicecostService;
	
	
	/**
	 * 遍历服务定价表，判断该条记录的生效时间有没有到，如果到了生效时间，则把状态置为1，
	 * 把同样的记录之前生效时间的置为0
	 * @throws Exception
	 */
	public void changeProjectPrice() throws Exception{
	
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		List<PageData> pdlist =  servicecostService.listValidAll(new PageData());
		
		//要批量置为无效的项目集合
		List<PageData> batchlist = new ArrayList<PageData>();
		
		for(PageData p : pdlist){
			//存在到了生效时间的记录，把状态置为1
			if(df.parse(p.get("EFFECTIVE_DATE").toString()).getTime()<new Date().getTime() && df.parse(p.get("INEFFECTIVE_DATE").toString()).getTime() > new Date().getTime()){
				p.put("STATUS", "1");
				
				
				//同时根据该记录的STORE_ID、STAFF_ID、PID查出之前生效的相同的项目，把它们置为无效
				List<PageData> samerecordlist = servicecostService.queryProjectByThreeParameters(p);
				
				for(PageData sp:samerecordlist){
					batchlist.add(sp);
				}
				//修改当前记录为有效
				servicecostService.edit(p);
			}
		}
		if(batchlist.size()>0){
			this.servicecostService.batchUpdateStatusToInvalid(batchlist);
		}
		
	}
}
