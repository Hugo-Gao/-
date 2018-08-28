package com.gyf.miaosha.controller;

import com.gyf.miaosha.access.AccessLimit;
import com.gyf.miaosha.domain.MiaoshaOrder;
import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.domain.OrderInfo;
import com.gyf.miaosha.rabbitmq.MQSender;
import com.gyf.miaosha.rabbitmq.MiaoshaMessage;
import com.gyf.miaosha.redis.*;
import com.gyf.miaosha.result.CodeMsg;
import com.gyf.miaosha.result.Result;
import com.gyf.miaosha.service.GoodsService;
import com.gyf.miaosha.service.MiaoshaService;
import com.gyf.miaosha.service.MiaoshaUserService;
import com.gyf.miaosha.service.OrderService;
import com.gyf.miaosha.util.MD5Util;
import com.gyf.miaosha.util.UUIDUtil;
import com.gyf.miaosha.vo.GoodsVo;
import com.sun.tools.corba.se.idl.constExpr.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaoyunfan
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean
{
    private static Logger log = LoggerFactory.getLogger(MiaoshaController.class);

    @Autowired
    private MiaoshaUserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MiaoshaService miaoshaService;

    @Autowired
    private MQSender sender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 2500*10 qps:1249
     * 优化后：qps:2717
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId, @PathVariable("path") String path)
    {
        model.addAttribute("user", user);
        if (user == null)
        {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if (!check)
        {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //看是否已经秒杀完了,内存标记
        if (localOverMap.get(goodsId))
        {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //返回减完后的值
        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0)
        {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null)
        {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage message = new MiaoshaMessage();
        message.setGoodsId(goodsId);
        message.setUser(user);
        sender.sendMiaoshaMessage(message);
        //返回0表示排队中
        return Result.success(0);
        /*
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = (int) goods.getStockCount();
        if (stock <= 0)
        {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null)
        {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        if(orderInfo==null)
        {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        return Result.success(orderInfo);
        */

    }


//    @RequestMapping(value = "/reset", method = RequestMethod.GET)
//    @ResponseBody
//    public Result<Boolean> reset(Model model)
//    {
//        List<GoodsVo> goodsList = goodsService.listGoodsVo();
//        for (GoodsVo goods : goodsList)
//        {
//            goods.setStockCount(10);
//            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), 10);
//            localOverMap.put(goods.getId(), false);
//        }
//        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
//        redisService.delete(MiaoshaKey.isGoodsOver);
//        miaoshaService.reset(goodsList);
//        return Result.success(true);
//    }

    /**
     * @param model
     * @param user
     * @param goodsId
     * @return orderId:成功 -1：秒杀失败 0：排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId)
    {
        model.addAttribute("user", user);
        if (user == null)
        {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user, @RequestParam("goodsId") long goodsId, @RequestParam(value = "verifyCode",defaultValue = "0") int verifyCode)
    {
        if (user == null)
        {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check)
        {
            return Result.error(CodeMsg.CODE_ERROR);
        }
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser user, @RequestParam("goodsId") long goodsId)
    {
        if (user == null)
        {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
        try
        {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e)
        {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

    /**
     * 系统初始化
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        List<GoodsVo> goodsList = goodsService.listAllGoodVo();
        if (goodsList == null)
        {
            return;
        }
        for (GoodsVo goodsVo : goodsList)
        {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, goodsVo.getId() + "", goodsVo.getStockCount());

            if (goodsVo.getStockCount() > 0)
            {
                localOverMap.put(goodsVo.getId(), false);
            } else
            {
                localOverMap.put(goodsVo.getId(), true);
            }
        }


    }
}
