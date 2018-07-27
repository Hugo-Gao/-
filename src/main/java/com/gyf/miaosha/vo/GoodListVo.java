package com.gyf.miaosha.vo;

import java.util.List;

public class GoodListVo
{
    private List<GoodsVo> hotpoint;
    private List<GoodsVo> detailGoodsList;
    public List<GoodsVo> getHotpoint()
    {
        return hotpoint;
    }

    public void setHotpoint(List<GoodsVo> hotpoint)
    {
        this.hotpoint = hotpoint;
    }

    public List<GoodsVo> getDetailGoodsList()
    {
        return detailGoodsList;
    }

    public void setDetailGoodsList(List<GoodsVo> detailGoodsList)
    {
        this.detailGoodsList = detailGoodsList;
    }
}
