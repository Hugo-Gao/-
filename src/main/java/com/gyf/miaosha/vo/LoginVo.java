package com.gyf.miaosha.vo;

import com.gyf.miaosha.validator.IsMobile;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class LoginVo
{
    @Override
    public String toString()
    {
        return "LoginVo{" + "mobile='" + mobile + '\'' + ", password='" + password + '\'' + '}';
    }

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
