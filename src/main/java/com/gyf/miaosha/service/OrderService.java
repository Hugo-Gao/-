package com.gyf.miaosha.service;

import com.gyf.miaosha.controller.MiaoshaController;
import com.gyf.miaosha.dao.OrderDao;
import com.gyf.miaosha.domain.MiaoshaOrder;
import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.domain.OrderInfo;
import com.gyf.miaosha.redis.OrderKey;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService
{

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsService goodsService;
    //改为查缓存
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId)
    {
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
//        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
    }


    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods)
    {
        //写订单Info
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insert(orderInfo);
        //写秒杀order
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);
        return orderInfo;
    }


    public OrderInfo getOrderById(long orderId)
    {
        return orderDao.getOrderById(orderId);
    }

    public void deleteOrders()
    {
        orderDao.deleteOrderInfo();
        orderDao.deleteMiaoshaOrder();
    }
}
