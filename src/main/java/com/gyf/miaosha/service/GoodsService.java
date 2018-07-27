package com.gyf.miaosha.service;

import com.gyf.miaosha.dao.GoodsDao;
import com.gyf.miaosha.domain.Goods;
import com.gyf.miaosha.domain.MiaoshaClass;
import com.gyf.miaosha.domain.MiaoshaGoods;
import com.gyf.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoodsService
{
    @Autowired
    private GoodsDao goodsDao;

    public MiaoshaClass[] getAllClass()
    {
        return goodsDao.getClassName();
    }

    public List<GoodsVo> listGoodsVo(int classId)
    {
        return goodsDao.listGoodVo(classId);
    }

    public List<GoodsVo> listAllGoodVo()
    {
        return goodsDao.listAllGoodVo();
    }

    public List<GoodsVo> listHotPointGoodsVo()
    {
        return goodsDao.listHotPointGoodVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId)
    {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public int reduceStock(GoodsVo goods)
    {
        MiaoshaGoods g = new MiaoshaGoods();
        g.setGoodsId(goods.getId());
        return goodsDao.reduceStock(g);
    }

    public void resetStock(List<GoodsVo> goodsList)
    {
        for (GoodsVo goodsVo : goodsList)
        {
            MiaoshaGoods goods = new MiaoshaGoods();
            goods.setGoodsId(goodsVo.getId());
            goods.setStockCount(goodsVo.getStockCount());
            goodsDao.resetStock(goods);
        }
    }
}
