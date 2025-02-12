package com.base.db;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * This class provides configuration and management for a Redis connection pool.
 */
public class RedisConfig {
    // JedisPool instance
    private static JedisPool jedisPool;

    // Private constructor to prevent instantiation
    private RedisConfig(){}

    /**
     * Returns a synchronized JedisPool instance.
     * If the pool is not already created, it initializes the pool with the specified configuration.
     *
     * @return JedisPool instance
     */
    public static synchronized JedisPool getJedisPool(){
        if(jedisPool == null){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(10); // Set maximum total connections
            config.setMaxIdle(5); // Set maximum idle connections
            config.setMinIdle(2); // Set minimum idle connections
            config.setTestOnBorrow(true); // Validate connection before borrowing
            config.setTestOnReturn(true); // Validate connection before returning
            jedisPool = new JedisPool(config, "localhost", 6379); // Initialize JedisPool with config
        }
        return jedisPool;
    }

    /**
     * Closes the JedisPool if it is not null.
     */
    public static void closePool(){
        if(jedisPool != null){
            jedisPool.close();
        }
    }
}