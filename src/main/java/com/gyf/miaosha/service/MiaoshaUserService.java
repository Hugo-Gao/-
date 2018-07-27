package com.gyf.miaosha.service;

import com.gyf.miaosha.dao.MiaoshaUserDao;
import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.exception.GlobalException;
import com.gyf.miaosha.redis.MiaoshaUserKey;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.result.CodeMsg;
import com.gyf.miaosha.util.MD5Util;
import com.gyf.miaosha.util.UUIDUtil;
import com.gyf.miaosha.vo.LoginVo;
import com.sun.tools.javac.jvm.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Service
public class MiaoshaUserService
{
    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private MiaoshaUserDao miaoshaUserDao;

    @Autowired
    private RedisService redisService;

    public MiaoshaUser getById(long id)
    {
        //取缓存
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (user != null)
        {
            return user;
        }
        //取数据库
         user = miaoshaUserDao.getById(id);
        if (user != null)
        {
            redisService.set(MiaoshaUserKey.getById, id + "", user);
        }
        return user;
    }

    /**
     * @param response
     * @param user
     * @return 返回token,异常直接由异常拦截器返回
     */
    public String register(HttpServletResponse response,MiaoshaUser user)
    {
        if (user == null)
        {
            throw new GlobalException(CodeMsg.USER_NULL);
        }
        String salt = UUIDUtil.randomSalt();
        user.setSalt(salt);
        String dbPass = MD5Util.formPassToDBPass(user.getPassword(), salt);
        user.setPassword(dbPass);
        user.setRegisterDate(new Date());
        int count = miaoshaUserDao.insertUser(user);
        if (count != 1)
        {
            throw new GlobalException(CodeMsg.MOBIE_REPEAT);
        }
        String token = UUIDUtil.uuid();
        //用token来标志每一个用户信息
        addCookie(response, token, user);
        return token;
    }

    public boolean updatePassword(String token,long id, String formPass)
    {
        MiaoshaUser user = getById(id);
        if (user == null)
        {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新缓存
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(user.getId());
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById, id+"");
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token,user);
        return true;
    }

    public String login(HttpServletResponse response, LoginVo loginVo)
    {
        if (loginVo == null)
        {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        //前端已经做了一次md5
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user==null)
        {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass))
        {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUtil.uuid();
        //用token来标志每一个用户信息
        addCookie(response, token, user);
        return token;
    }
    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        redisService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token)
    {
        if (StringUtils.isEmpty(token))
        {
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if (user != null)
        {
            addCookie(response, token, user);
        }
        return user;
    }
}
