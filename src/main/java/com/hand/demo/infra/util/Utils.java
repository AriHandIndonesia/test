package com.hand.demo.infra.util;

import io.choerodon.mybatis.util.StringUtil;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Utils
 */
@Component
public class Utils {
    @Autowired
    private RedisHelper redisHelper;


    public String getRedis(String key){
        return redisHelper.strGet(key);
    }

    public void updateRedis(String key, String value){
        redisHelper.strSet(key, value, 300, TimeUnit.SECONDS);
    }
}
