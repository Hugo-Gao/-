package com.gyf.miaosha.controller;

import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.result.Result;
import com.gyf.miaosha.service.GoodsService;
import com.gyf.miaosha.service.MiaoshaUserService;
import com.gyf.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

//just for test
@Controller
@RequestMapping("/user")
public class UserController
{
    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private MiaoshaUserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user)//由UserArgumentResolver负责解析注入MiaoshaUser
    {
        System.out.println("Thread-"+Thread.currentThread().getName());
        return Result.success(user);
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId)
    {
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);
        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus;
        int remainSeconds;
        if (now < startAt)
        {
            //秒杀未开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        }else if(now>endAt) {
            //秒杀一结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀正在进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
            log.info("秒杀开始");
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";
    }

}
