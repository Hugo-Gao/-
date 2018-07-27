package com.gyf.miaosha.controller;

import com.gyf.miaosha.domain.MiaoshaClass;
import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.redis.GoodsKey;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.result.Result;
import com.gyf.miaosha.service.GoodsService;
import com.gyf.miaosha.service.MiaoshaUserService;
import com.gyf.miaosha.vo.ClassVo;
import com.gyf.miaosha.vo.GoodsDetailVo;
import com.gyf.miaosha.vo.GoodsVo;
import com.gyf.miaosha.vo.GoodListVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController
{
    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private MiaoshaUserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;


    @RequestMapping(value = "/hotpoint")
    @ResponseBody
    public Result<GoodListVo> getHotPointGoods(HttpServletRequest request, HttpServletResponse response)//由UserArgumentResolver负责解析注入MiaoshaUser
    {
        List<GoodsVo> goodsVoList = goodsService.listHotPointGoodsVo();
        GoodListVo hotPointGoodsVo = new GoodListVo();
        hotPointGoodsVo.setHotpoint(goodsVoList);
        response.setHeader("Access-Control-Allow-Origin","*");
        return Result.success(hotPointGoodsVo);
    }


    @RequestMapping(value = "/listgoods")
    @ResponseBody
    public Result<GoodListVo> listGoods(HttpServletRequest request, HttpServletResponse response)//由UserArgumentResolver负责解析注入MiaoshaUser
    {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo(3);
        GoodListVo goodListVo = new GoodListVo();
        goodListVo.setHotpoint(goodsVoList);
        response.setHeader("Access-Control-Allow-Origin","*");
        return Result.success(goodListVo);
    }


    /**
     * 10*2500 qps:1077
     *
     * 使用缓存：qps:3964
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user)//由UserArgumentResolver负责解析注入MiaoshaUser
    {

        model.addAttribute("user", user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html))
        {
            return html;
        }
        //查询商品表
        List<GoodsVo> goodsList = goodsService.listGoodsVo(1);
        model.addAttribute("goodsList", goodsList);
        // old return "goods_list";
        //手动渲染
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", springWebContext);
        //存缓存
        if (!StringUtils.isEmpty(html))
        {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    @RequestMapping(value = "/classify")
    @ResponseBody
    public Result<ClassVo> getClassify(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user)//由UserArgumentResolver负责解析注入MiaoshaUser
    {
        MiaoshaClass[] allClass = goodsService.getAllClass();
        ClassVo classVo = new ClassVo();
        classVo.setClasses(allClass);
        response.setHeader("Access-Control-Allow-Origin","*");
        return Result.success(classVo);
    }
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId)
    {
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
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
        } else if (now > endAt)
        {
            //秒杀一结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else
        {
            //秒杀正在进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
            log.info("秒杀开始");
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoodsVo(goodsVo);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        vo.setUser(user);
        return Result.success(vo);
    }



    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail2(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId)
    {
        model.addAttribute("user", user);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html))
        {
            return html;
        }

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
        } else if (now > endAt)
        {
            //秒杀一结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else
        {
            //秒杀正在进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
            log.info("秒杀开始");
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        //return "goods_detail";
        //手动渲染
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", springWebContext);
        //存缓存
        if (!StringUtils.isEmpty(html))
        {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }
        return html;
    }
}
