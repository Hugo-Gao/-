package com.gyf.miaosha.rabbitmq;

import com.gyf.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gaoyunfan
 */
@Service
public class MQSender
{
    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RedisService redisService;


    public void sendMiaoshaMessage(MiaoshaMessage message)
    {
        String msg = redisService.beanToString(message);
        log.info("send message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);

    }

//
//    public void send(Object message)
//    {
//        String msg = redisService.beanToString(message);
//        log.info("send message: " + msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }
//
//    public void sendTopic(Object message)
//    {
//        String msg = redisService.beanToString(message);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY1, msg + "1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY2, msg + "2");
//    }
//
//    public void sendFanout(Object message)
//    {
//        String msg = redisService.beanToString(message);
//        log.info("send fanout message: " + msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
//    }
//
//    public void sendHeader(Object message)
//    {
//        String msg = redisService.beanToString(message);
//        log.info("send fanout message: " + msg);
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setHeader("header1","value1");
//        messageProperties.setHeader("header2","value2");
//        Message obj = new Message(msg.getBytes(),messageProperties);
//        amqpTemplate.convertAndSend(MQConfig.HEARDERS_EXCHANGE,"",obj);
//    }


}
