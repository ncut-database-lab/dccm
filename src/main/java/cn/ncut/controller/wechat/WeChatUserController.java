package cn.ncut.controller.wechat;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import cn.ncut.controller.base.BaseController;
import cn.ncut.entity.wechat.pojo.WeChatOrder;
import cn.ncut.entity.wechat.pojo.WeChatPreStore;
import cn.ncut.entity.wechat.pojo.WeChatUser;
import cn.ncut.entity.wechat.pojo.WeChatUserDiscount;
import cn.ncut.entity.wechat.pojo.WeChatUserStoredCard;
import cn.ncut.service.wechat.discount.impl.WeChatUserDiscountService;
import cn.ncut.service.wechat.mobileInfo.impl.WeChatMobileInfoService;
import cn.ncut.service.wechat.order.impl.WeChatOrderService;
import cn.ncut.service.wechat.preStored.impl.WeChatPreStoredService;
import cn.ncut.service.wechat.user.impl.WeChatUserService;
import cn.ncut.service.wechat.userStoredCard.impl.WeChatUserStoredCardService;
import cn.ncut.util.wechat.HttpUtil;
import cn.ncut.util.wechat.MD5Util;
import cn.ncut.util.wechat.RandomUtil;
import cn.ncut.util.wechat.SpringPropertyResourceReader;
import cn.ncut.util.wechat.WeChatLogUtil;

@Controller
@RequestMapping("/weChatUser")
public class WeChatUserController extends BaseController {
    @Resource(name = "weChatUserService")
    private WeChatUserService weChatUserService;

    @Resource(name = "weChatUserDiscountService")
    private WeChatUserDiscountService weChatUserDiscountService;

    @Resource(name = "weChatUserStoredCardService")
    private WeChatUserStoredCardService weChatUserStoredCardService;

    @Resource(name = "weChatOrderService")
    private WeChatOrderService weChatOrderService;

    @Resource(name = "weChatMobileInfoService")
    private WeChatMobileInfoService weChatMobileInfoService;

    @Resource(name = "weChatPreStoredService")
    private WeChatPreStoredService weChatPreStoredService;

    /**
     * 通过"个人中心"的"个人设置"进入用户基本信息页面;获取session中存放的用户uId
     *
     * @param request 请求
     * @return WechatUser 当前登录微信客户
     * @throws Exception
     */
    @RequestMapping("/userInfo")
    public ModelAndView getUserFromHome(HttpServletRequest request) throws Exception {
        WeChatLogUtil.controllerLog(request, "userInfo");

        HttpSession session = request.getSession();
        Integer uId = (Integer) session.getAttribute("uId");

        WeChatUser weChatUser = this.weChatUserService.getWeChatUserByuId(uId);
        ModelAndView mav = getModelAndView();
        mav.addObject("weChatUser", weChatUser);
        mav.setViewName("wechat/user/personalInfo");
        return mav;
    }

    /**
     * 更新用户基本信息
     *
     * @throws Exception
     */
    @RequestMapping("/updateUser")
    public ModelAndView updateUser(@ModelAttribute WeChatUser weChatUser) throws Exception {
        /**
         * 服务端数据校验
         * */
        String phone = weChatUser.getPhone();
        if (phone != null && !(phone.trim()).equals("") && phone.trim().length() > 7) {
            phone = phone.substring(0, 7);
            if (this.weChatMobileInfoService.getMobileInfoByMobileNum(phone) != null) {
                weChatUser.setCity(this.weChatMobileInfoService.getMobileInfoByMobileNum(phone).getMobileArea());
            } else {
                weChatUser.setCity("未知城市");
            }
        }
        weChatUserService.updateWeChatUser(weChatUser);

        /**
         * 判断当前用户是否有预存账户或者储值卡账户,若有,则修改该用户的账户信息
         * */
        // 更新储值卡账户
        WeChatUserStoredCard weChatUserStoredCard = weChatUserStoredCardService.existOfCreditCardOfUserByUid(weChatUser.getuId());
        if (weChatUserStoredCard != null) {
            weChatUserStoredCard.setPhone(weChatUser.getPhone());
            weChatUserStoredCard.setName(weChatUser.getName());
            weChatUserStoredCard.setuId(weChatUserStoredCard.getUser().getuId());
            weChatUserStoredCardService.updateUserStoredCardPhoneAndName(weChatUserStoredCard);
        }

        // 更新预存账户
        WeChatPreStore weChatPreStore = weChatPreStoredService.getUserPreStoreByUid(weChatUser.getuId());
        if (weChatPreStore != null) {
            weChatPreStore.setPHONE(weChatUser.getPhone());
            weChatPreStore.setUSERNAME(weChatUser.getName());
            weChatPreStoredService.updateUserPreStoreByUid(weChatPreStore);
        }

        ModelAndView mav = getModelAndView();
        mav.addObject("weChatUser", weChatUser);
        mav.setViewName("wechat/user/personalInfo");
        return mav;
    }

    /**
     * @param uId 用户编号
     * @return 视图
     * @throws Exception
     */
    @RequestMapping("/editUser/{uId}")
    public ModelAndView editUserPage(@PathVariable("uId") int uId) throws Exception {
        WeChatUser weChatUser = this.weChatUserService.getWeChatUserByuId(uId);
        ModelAndView mav = getModelAndView();
        mav.addObject("weChatUser", weChatUser);
        mav.setViewName("wechat/user/editUser");
        return mav;
    }

    /**
     * 导航控制,控制页面转向
     */
    @RequestMapping("/pageNavigate/{method}")
    public ModelAndView pageNavigate(HttpServletRequest request, @PathVariable("method") String method) throws Exception {
        WeChatLogUtil.controllerLog(request, "pageNavigate");

        ModelAndView mav = this.getModelAndView();
        HttpSession session = request.getSession();

        // TODO 设置用户编号,方便调试
        // session.setAttribute("uId", 17);


        Integer uId = (Integer) session.getAttribute("uId");

        System.out.println("-- start pageNavigate() --" + uId);

        /**
         * 列出用户的优惠券信息,储值卡密码设置页面
         * */
        if (method.equals("userCenter")) {
            // 待支付订单的数量
            List<WeChatOrder> prePayOrders = weChatOrderService.getUserPrePayOrCloseOrderByUserId(uId);
            // 已预约订单的数量
            List<WeChatOrder> appointOrders = weChatOrderService.getUserAppointOrOvertimeOrderByUserId(uId);
            // 优惠券的数量
            List<WeChatUserDiscount> discounts = weChatUserDiscountService.getUserUnusedAndUnscannedDiscountsByUserId(uId);
            mav.addObject("prePayOrderAmount", prePayOrders.size());
            mav.addObject("appointOrderAmount", appointOrders.size());
            mav.addObject("discountAmount", discounts.size());
            mav.setViewName("wechat/user/userCenter");
            return mav;
        } else if (method.equals("storedCardPassword")) {
            WeChatUserStoredCard weChatUserStoredCard = weChatUserStoredCardService.existOfCreditCardOfUserByUid(uId);
            int flag = 1;
            if (weChatUserStoredCard == null) {
                // 当前用户并没有购买任何储值卡,提示用户购买储值卡才能设置密码
                flag = 0;

            }
            mav.addObject("flag", flag);
            mav.addObject("weChatUserStoredCard", weChatUserStoredCard);
            mav.setViewName("wechat/user/storedCardPassword");
            return mav;
        }
        return null;
    }

    /**
     * 储值卡密码验证
     */
    @ResponseBody
    @RequestMapping("/passwordAuthorize")
    public String passwordAuthorize(HttpServletRequest request) throws Exception {

        String originPassword = request.getParameter("originPassword");
        Integer uId = null;
        WeChatUserStoredCard weChatUserStoredCard = null;
        if (request.getParameter("uId") != null) {
            uId = Integer.valueOf(request.getParameter("uId"));
        }
        if (uId != null) {
            weChatUserStoredCard = this.weChatUserStoredCardService.existOfCreditCardOfUserByUid(uId);
            String newPassword = MD5Util.MD5Encode(originPassword, "utf8");
            if (newPassword.equals(weChatUserStoredCard.getPassword())) {
                return "{\"result\":\"success\"}";
            }
        }
        return "{\"result\":\"fail\"}";
    }

    /**
     * 设置储值卡密码
     */
    @RequestMapping("/setStoredCardPassword")
    public ModelAndView setStoredCardPassword(HttpServletRequest request, @ModelAttribute
            WeChatUserStoredCard weChatUserStoredCard) throws Exception {

        ModelAndView mav = this.getModelAndView();
        weChatUserStoredCard = this.weChatUserStoredCardService.existOfCreditCardOfUserByUid(weChatUserStoredCard.getuId());
        String newPassword = request.getParameter("newPassword");

        if (weChatUserStoredCard != null) {
            weChatUserStoredCard.setPassword(MD5Util.MD5Encode(newPassword, "utf8"));
            this.weChatUserStoredCardService.updatePasswordOfAllCreditCard(weChatUserStoredCard);

            Integer uId = weChatUserStoredCard.getUser().getuId();

            List<WeChatUserDiscount> discounts = weChatUserDiscountService.getUserUnusedAndUnscannedDiscountsByUserId(uId);
            List<WeChatOrder> prePayOrders = weChatOrderService.getUserPrePayOrCloseOrderByUserId(uId);
            List<WeChatOrder> appointOrders = weChatOrderService.getUserAppointOrOvertimeOrderByUserId(uId);

            mav.addObject("prePayOrderAmount", prePayOrders.size());
            mav.addObject("appointOrderAmount", appointOrders.size());
            mav.addObject("discountAmount", discounts.size());
        }
        mav.setViewName("wechat/user/userCenter");
        return mav;
    }

    /**
     * 忘记密码
     */
    @RequestMapping("/forgetStoredPassword")
    public ModelAndView forgetPassword(HttpServletRequest request) throws Exception {
        ModelAndView mav = this.getModelAndView();

        HttpSession session = request.getSession();
        Integer uId = (Integer) session.getAttribute("uId");

        mav.addObject("uId", uId);
        mav.setViewName("wechat/user/forgetStoredPassword");
        return mav;
    }

    /**
     * 忘记密码,获取验证码
     */
    @ResponseBody
    @RequestMapping("/getCheckCode")
    public String getCheckCode(HttpServletRequest request) throws Exception {
        String phone = (String) request.getParameter("phone");
        String result = "";
        HttpSession session = request.getSession();
        if (session.getAttribute("expireTime") == null && session.getAttribute("checkCode") == null) {
            // 根据session中的expireTime与checkCode判断是否需要重新创建验证码
            String checkCode = RandomUtil.generateMixedCharAndDigit(6);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MINUTE, 3);
            Date expireTime = cal.getTime();

            session.setAttribute("expireTime", expireTime);
            session.setAttribute("checkCode", checkCode);

            Map<String, String> parameters = new LinkedHashMap<String, String>();
            parameters.put("ddtkey", SpringPropertyResourceReader.getProperty("ddtkey"));
            parameters.put("secretkey", MD5Util.MD5Encode(SpringPropertyResourceReader.getProperty("secretkey"), "UTF-8"));
            parameters.put("mobile", phone);
            parameters.put("content", "您的验证码是:" + checkCode + ",3分钟之内有效" + "【大诚中医】");
            parameters.put("sendTime", "");
            parameters.put("extno", "");
            // 验证码的生成
            result = HttpUtil.sendPost(SpringPropertyResourceReader.getProperty("requestUrl"), parameters);
            System.out.println(result);
        }

        String json = "{\"result\":\"" + result + "\",\"code\":\"513214\"}";
        return json;
    }

    /**
     * 密码设置页面
     */
    @RequestMapping("/pageNagivatePasswordSet/{uId}")
    public ModelAndView pageNagivatePasswordSet(@PathVariable("uId") Integer uId) {
        ModelAndView mav = this.getModelAndView();
        mav.addObject("uId", uId);
        mav.setViewName("wechat/user/passwordSet");
        return mav;
    }

    @ResponseBody
    @RequestMapping("/checkCodeIsRight")
    public String checkCodeIsRight(HttpServletRequest request) throws Exception {
        // 前端用户传送的验证码
        String userCheckCode = request.getParameter("checkCode");

        // 获取session中的验证码
        HttpSession session = request.getSession();
        String checkCode = (String) session.getAttribute("checkCode");
        Date expireTime = (Date) session.getAttribute("expireTime");
        Date now = new Date();

        // 检测验证码是否有效
        String result = "";
        if (checkCode.equals(userCheckCode) && now.compareTo(expireTime) < 0) {
            // 用户输入的验证码正确,也在有效时间内
            result = "{\"errorCode\":\"success\"}";
            // 验证码失效
            session.setAttribute("expireTime", null);
            session.setAttribute("checkCode", null);
        } else if (!checkCode.equals(userCheckCode) && now.compareTo(expireTime) < 0) {
            // 用户输入的验证码不正确,但是在有效时间内
            result = "{\"errorCode\":\"fail\",\"errorDes\":\"您输入的验证码不正确,请核实\"}";
            // 无需创建新的验证码
        } else if (checkCode.equals(userCheckCode) && now.compareTo(expireTime) > 0) {
            // 用户输入的验证码正确,但是不在有效时间内
            result = "{\"errorCode\":\"fail\",\"errorDes\":\"您输入的验证码已过期,请重新获取验证码\"}";
            // 需要创建新的验证码
            session.setAttribute("expireTime", null);
            session.setAttribute("checkCode", null);
        } else if (!checkCode.equals(userCheckCode) && now.compareTo(expireTime) > 0) {
            // 用户输入的验证码不正确,也不在有效时间内
            result = "{\"errorCode\":\"fail\",\"errorDes\":\"您输入的验证码错误并且已过期,请重新获取验证码\"}";
            session.setAttribute("expireTime", null);
            session.setAttribute("checkCode", null);
        }

        return result;
    }

    @RequestMapping("/repeatSetStoredCardPassword")
    public ModelAndView repeatSetStoredCardPassword(HttpServletRequest request) throws Exception {
        ModelAndView mav = this.getModelAndView();

        Integer uId = Integer.valueOf(request.getParameter("uId"));

        // 查看当前用户是否购买了至少一张储值卡
        WeChatUserStoredCard weChatUserStoredCard =
                this.weChatUserStoredCardService.existOfCreditCardOfUserByUid(uId);

        String newPassword = request.getParameter("newPassword");

        if (weChatUserStoredCard != null) {
            weChatUserStoredCard.setPassword(MD5Util.MD5Encode(newPassword, "utf8"));
            weChatUserStoredCard.setStatus(0);
            // 更新此用户所有的储值卡密码
            this.weChatUserStoredCardService.updatePasswordOfAllCreditCard(weChatUserStoredCard);

            List<WeChatUserDiscount> discounts = weChatUserDiscountService.getUserUnusedAndUnscannedDiscountsByUserId(uId);
            List<WeChatOrder> prePayOrders = weChatOrderService.getUserPrePayOrCloseOrderByUserId(uId);
            List<WeChatOrder> appointOrders = weChatOrderService.getUserAppointOrOvertimeOrderByUserId(uId);

            mav.addObject("prePayOrderAmount", prePayOrders.size());
            mav.addObject("appointOrderAmount", appointOrders.size());
            mav.addObject("discountAmount", discounts.size());
        }
        mav.setViewName("wechat/user/userCenter");
        return mav;
    }
}