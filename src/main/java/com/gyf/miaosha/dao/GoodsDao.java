package com.gyf.miaosha.dao;

import com.gyf.miaosha.domain.MiaoshaClass;
import com.gyf.miaosha.domain.MiaoshaGoods;
import com.gyf.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsDao
{
    /**
     * 获取商品的所有信息
     * @return
     */
    @Select("select g.* , mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id=g.id where class_id=#{classId}")
    public List<GoodsVo> listGoodVo(int classId);

    @Select("select g.* , mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id=g.id")
    public List<GoodsVo> listAllGoodVo();

    /**
     * 获取热门商品的所有信息
     * @return
     */
    @Select("select g.* , mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id=g.id where g.recommend=1")
    public List<GoodsVo> listHotPointGoodVo();

//    @Select("select g.* , mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id=g.id where g.id=#{goodsId}")
//    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    /**
     * 根据商品Id获取商品
     * @param goodsId
     * @return
     */
    @Select("select g.*,mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);


    /**
     * 减少库存
     * @param g
     * @return
     */
    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    int reduceStock(MiaoshaGoods g);

    /**
     * 重置库存
     * @param goods
     * @return
     */
    @Update("update miaosha_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    int resetStock(MiaoshaGoods goods);

    /**
     * 获取所有商品分类
     * @return
     */
    @Select("select id,classname from miaosha_class")
    MiaoshaClass[] getClassName();


}
