package com.gyf.miaosha.vo;

import com.gyf.miaosha.domain.Goods;

import java.util.Date;

//将商品和秒杀商品信息合并
public class GoodsVo extends Goods
{
    private Double miaoshaPrice;
    private long stockCount;
    private Date startDate;
    private Date endDate;
    public Double getMiaoshaPrice()
    {
        return miaoshaPrice;
    }

    @Override
    public String toString()
    {
        return "GoodsVo{" + "miaoshaPrice=" + miaoshaPrice + ", stockCount=" + stockCount + ", startDate=" + startDate + ", endDate=" + endDate + '}';
    }

    public void setMiaoshaPrice(Double miaoshaPrice)
    {
        this.miaoshaPrice = miaoshaPrice;
    }

    public long getStockCount()
    {
        return stockCount;
    }

    public void setStockCount(long stockCount)
    {
        this.stockCount = stockCount;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }


}
