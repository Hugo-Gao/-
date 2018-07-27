package com.gyf.miaosha.rabbitmq;

import com.gyf.miaosha.domain.MiaoshaOrder;
import com.gyf.miaosha.domain.MiaoshaUser;
import com.gyf.miaosha.domain.OrderInfo;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.result.CodeMsg;
import com.gyf.miaosha.result.Result;
import com.gyf.miaosha.service.GoodsService;
import com.gyf.miaosha.service.MiaoshaService;
import com.gyf.miaosha.service.OrderService;
import com.gyf.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver
{
    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MiaoshaService miaoshaService;



    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message)
    {
        log.info("receive message " + message);
        MiaoshaMessage mm = redisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = (int) goods.getStockCount();
        if (stock <= 0)
        {
            return ;
        }
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null)
        {
            return ;
        }
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);

    }

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message)
//    {
//        log.info("receive message " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message)
//    {
//        log.info("topic queue1 message " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message)
//    {
//        log.info("topic queue2 message " + message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
//    public void receiveHeaderQueue(byte[] message)
//    {
//        log.info("head queue message " + new String(message));
//    }
}
