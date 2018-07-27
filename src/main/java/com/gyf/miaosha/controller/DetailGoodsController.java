package com.gyf.miaosha.controller;

import com.gyf.miaosha.result.Result;
import com.gyf.miaosha.service.GoodsService;
import com.gyf.miaosha.vo.GoodListVo;
import com.gyf.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/detailgoods")
public class DetailGoodsController
{

    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value = "/list/{classId}")
    @ResponseBody
    public Result<GoodListVo> listGoods(HttpServletResponse response, @PathVariable("classId") int classId)//由UserArgumentResolver负责解析注入MiaoshaUser
    {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo(classId);
        GoodListVo goodListVo = new GoodListVo();
        goodListVo.setDetailGoodsList(goodsVoList);
        response.setHeader("Access-Control-Allow-Origin", "*");
        return Result.success(goodListVo);
    }
}
