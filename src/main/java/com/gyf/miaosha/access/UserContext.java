package com.gyf.miaosha.access;

import com.gyf.miaosha.domain.MiaoshaUser;

/**
 * @author gaoyunfan
 */
public class UserContext
{
    private static ThreadLocal<MiaoshaUser> userHolder = new InheritableThreadLocal<>();

    //存进去马上又取出来，所以用ThreadLocal
    public  static void setUser(MiaoshaUser user)
    {
        userHolder.set(user);
    }

    public  static MiaoshaUser getUser()
    {
        return userHolder.get();
    }
}
