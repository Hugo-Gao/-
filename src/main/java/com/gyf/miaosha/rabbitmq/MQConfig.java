package com.gyf.miaosha.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoyunfan
 */
@Configuration
public class MQConfig
{
    public static final String QUEUE = "queue";
    public static final String MIAOSHA_QUEUE = "miaosha.queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String HEADER_QUEUE = "header.queue1";
    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String HEARDERS_EXCHANGE = "headersExchange";
    public static final String ROUTING_KEY1 = "topic.key1";
    public static final String ROUTING_KEY2 = "topic.#";

    @Bean
    public Queue miaoshaQueue()
    {
        return new Queue(MIAOSHA_QUEUE,true);
    }

//--------------------------------------------------------------------------------
    /**
     * Direct模式 交换机Exchange 一对一
     */
    @Bean
    public Queue queue()
    {
        return new Queue(QUEUE,true);
    }

    //--------------------------------------------------------------------------------

    /**
     * Topic模式 交换机Exchange 可选Queue
     */
    @Bean
    public Queue topicQueue1()
    {
        return new Queue(TOPIC_QUEUE1,true);
    }

    /**
     * Topic模式 交换机Exchange
     */
    @Bean
    public Queue topicQueue2()
    {
        return new Queue(TOPIC_QUEUE2,true);
    }

    @Bean
    public TopicExchange topicExchange()
    {
        return new TopicExchange(TOPIC_EXCHANGE);
    }


    @Bean
    public Binding topicBinding1()
    {

        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }

    @Bean
    public Binding topicBinding2()
    {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
    }

    //--------------------------------------------------------------------------------

    /**
     * Fanout模式 交换机Exchange  广播
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    //--------------------------------------------------------------------------------

    /**
     * Header模式 交换机Exchange
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEARDERS_EXCHANGE);
    }

    @Bean
    public Queue headerQueue1()
    {
        return new Queue(HEADER_QUEUE,true);
    }

    @Bean
    public Binding headerBinding() {
        Map<String, Object> map = new HashMap<>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headerQueue1()).to(headersExchange()).whereAny(map).match();
    }


}
