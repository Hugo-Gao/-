package com.gyf.miaosha.vo;

import com.gyf.miaosha.domain.MiaoshaUser;

public class GoodsDetailVo
{
    private int miaoshaStatus=0;
    private int remainSeconds=0;
    private GoodsVo goodsVo;
    private MiaoshaUser user;

    public MiaoshaUser getUser()
    {
        return user;
    }

    public void setUser(MiaoshaUser user)
    {
        this.user = user;
    }

    public int getMiaoshaStatus()
    {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus)
    {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds()
    {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds)
    {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoodsVo()
    {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo)
    {
        this.goodsVo = goodsVo;
    }
}
