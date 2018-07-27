package com.gyf.miaosha.controller;

import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.result.Result;
import com.gyf.miaosha.service.MiaoshaService;
import com.gyf.miaosha.service.MiaoshaUserService;
import com.gyf.miaosha.vo.LoginVo;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author gaoyunfan
 * 负责登录和注册
 */
@Controller
@RequestMapping("/login")
public class LoginController
{
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin()
    {
        return "login";
    }

    @RequestMapping("/to_register")
    public String toRegister()
    {
        return "register";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo)
    {
        log.info(loginVo.toString());
        //登录
        //若登录不成功，会产生异常由拦截器返回
        String  token = userService.login(response, loginVo);
        return Result.success(token);
    }

    @RequestMapping("/do_register")
    @ResponseBody
    public Result<String> doRegister(HttpServletResponse response, HttpServletRequest request)
    {
        log.info(request.getParameter("nickname") + "请求注册");
        MiaoshaUser user = new MiaoshaUser();
        user.setId(Long.parseLong(request.getParameter("mobile")));
        user.setNickname(request.getParameter("nickname"));
        user.setPassword(request.getParameter("password"));
        String token = userService.register(response, user);
        return Result.success(token);
    }

}
