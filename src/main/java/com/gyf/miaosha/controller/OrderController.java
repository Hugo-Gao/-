package com.gyf.miaosha.controller;

import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.domain.OrderInfo;
import com.gyf.miaosha.redis.GoodsKey;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.result.CodeMsg;
import com.gyf.miaosha.result.Result;
import com.gyf.miaosha.service.GoodsService;
import com.gyf.miaosha.service.MiaoshaUserService;
import com.gyf.miaosha.service.OrderService;
import com.gyf.miaosha.vo.GoodsDetailVo;
import com.gyf.miaosha.vo.GoodsVo;
import com.gyf.miaosha.vo.OrderDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController
{
    private static Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private MiaoshaUserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;


    /**
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(MiaoshaUser user, @RequestParam("orderId") long orderId)
    {
        if (user == null)
        {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null)
        {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goods);
        vo.setOrder(order);
        return Result.success(vo);
    }

}
