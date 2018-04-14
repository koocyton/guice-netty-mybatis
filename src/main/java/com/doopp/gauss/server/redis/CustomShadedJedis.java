package com.doopp.gauss.server.redis;

import com.doopp.gauss.common.util.SerializeUtils;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class CustomShadedJedis {

    private ShardedJedisPool shardedJedisPool;

    public void set(String key, String value) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        shardedJedis.set(key, value);
        shardedJedis.close();
    }

    public String get(String key) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        String value = shardedJedis.get(key);
        shardedJedis.close();
        return value;
    }

    public void del(String... keys) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        for (String key : keys) {
            shardedJedis.del(key);
        }
        shardedJedis.close();
    }

    public void set(byte[] key, Object object) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        byte[] _object = SerializeUtils.serialize(object);
        shardedJedis.set(key, _object);
        shardedJedis.close();
    }

    public Object get(byte[] key) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        byte[] _object = shardedJedis.get(key);
        if (_object==null) {
            return null;
        }
        Object object = SerializeUtils.deSerialize(_object);
        shardedJedis.close();
        return object;
    }

    public void del(byte[]... keys) {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        for (byte[] key : keys) {
            shardedJedis.del(key);
        }
        shardedJedis.close();
    }

    public void test() {
        ShardedJedis shardedJedis = shardedJedisPool.getResource();
        // shardedJedis.get
        shardedJedis.close();
    }

    public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }
}
