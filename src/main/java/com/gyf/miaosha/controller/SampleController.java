package com.gyf.miaosha.controller;

import com.gyf.miaosha.domain.User;
import com.gyf.miaosha.rabbitmq.MQSender;
import com.gyf.miaosha.redis.RedisService;
import com.gyf.miaosha.redis.UserKey;
import com.gyf.miaosha.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class SampleController
{


    @Autowired
    RedisService  redisService;

    @Autowired
    MQSender mqSender;

//    @RequestMapping("/mq/header")
//    @ResponseBody
//    public Result<String> mq()
//    {
//        mqSender.sendHeader("hello gyf");
//        return Result.success("Hello world");
//    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet()
    {
        User user = redisService.get(UserKey.getById,""+1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet()
    {
        User user = new User();
        user.setId(1);
        user.setName("11111");
        redisService.set(UserKey.getById, "" + 1, user);
        return Result.success(true);
    }


}
