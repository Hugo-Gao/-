package com.gyf.miaosha.vo;

import com.gyf.miaosha.domain.Goods;
import com.gyf.miaosha.domain.MiaoshaClass;

import java.util.Date;
import java.util.List;

//将商品和秒杀商品信息合并
public class ClassVo
{
    private MiaoshaClass[] classes;


    public MiaoshaClass[] getClasses()
    {
        return classes;
    }

    public void setClasses(MiaoshaClass[] classes)
    {
        this.classes = classes;
    }
}
