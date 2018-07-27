package com.gyf.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.redis.AccessKey;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.result.CodeMsg;
import com.gyf.miaosha.result.Result;
import com.gyf.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author gaoyunfan
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter
{
    @Autowired
    private MiaoshaUserService userService;

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (handler instanceof HandlerMethod)
        {
            MiaoshaUser user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            //查看方法是否需要拦截
            if (accessLimit == null)
            {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin)
            {
                if (user == null)
                {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            } else
            {
                //do nothing
            }
            AccessKey ak = AccessKey.withExpire(seconds);
            //记录访问量
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null)
            {
                redisService.set(ak, key, 1);
            } else if (count < maxCount)
            {
                redisService.incr(ak, key);
            } else
            {
                //防止某个用户频繁访问
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cmg) throws Exception
    {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cmg));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response)
    {
        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken))
        {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0)
        {
            return null;
        }
        for (Cookie cookie : request.getCookies())
        {
            if (cookie.getName().equals(cookieNameToken))
            {
                return cookie.getValue();
            }
        }
        return null;
    }

}
