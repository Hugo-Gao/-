package com.gyf.miaosha.util;

import java.util.Random;
import java.util.UUID;

public class UUIDUtil
{
    public static String uuid()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String randomSalt()
    {
        StringBuilder saltBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++)
        {
            saltBuilder.append((char)('A'+random.nextInt(26)));
        }
        return saltBuilder.toString();
    }

}
