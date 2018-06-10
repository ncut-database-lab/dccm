package cn.ncut.controller.wechat;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.ncut.controller.base.BaseController;
import cn.ncut.service.wechat.credit.WeChatCreditManager;
import cn.ncut.util.PageData;
import cn.ncut.util.wechat.WeChatLogUtil;

@Controller
@RequestMapping("/weChatCredit")
public class WeChatCreditController extends BaseController {
	@Resource(name="weChatCreditService")
	private WeChatCreditManager weChatCreditService;
	/**
	 * 积分明细
	 * @throws Exception 
	 */
	@RequestMapping("/creditDetails")
	public ModelAndView integralDetails(HttpServletRequest request) throws Exception{
		
		WeChatLogUtil.controllerLog(request, "creditDetails");
		
		ModelAndView mv = new ModelAndView();
		Integer uId = (Integer) request.getSession().getAttribute("uId");
		
		List<PageData> creditDetails = weChatCreditService.getCreditDetails(uId);
		
		mv.addObject("creditDetails", creditDetails);
		mv.setViewName("wechat/credit/creditDetails");
		return mv;
	}
	
	/**
	 * modified by scott
	 * 积分使用说明,暂时设置为静态页面
	 * @throws Exception 
	 */
	@RequestMapping("/creditUsedInfo")
	public ModelAndView creditUsedInfo() throws Exception{
		ModelAndView mav = this.getModelAndView();
		mav.setViewName("wechat/credit/creditUsedInfo");
		return mav;
	}
}