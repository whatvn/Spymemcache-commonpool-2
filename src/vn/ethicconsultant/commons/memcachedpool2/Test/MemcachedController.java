package vn.ethicconsultant.commons.memcachedpool2.Test;

import java.util.HashMap;
import java.util.Map;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import vn.ethicconsultant.commons.memcachedpool2.MemcachedPool;
import vn.ethicconsultant.commons.memcachedpool2.MemcachedPoolableFactory;

public class MemcachedController {

    private static final Logger _logger = LoggerFactory.getLogger(MemcachedController.class);
    private MemcachedPool memcachedPool = null;
    private GenericObjectPoolConfig poolConfig = null;
    private MemcachedPoolableFactory memcachedPoolableFactory = null;
    private static final Map<String, MemcachedController> mapsInstance = new HashMap<>();

    protected MemcachedController(String configName) {
        String host = "192.168.10.225";
        int port = 11211;
        memcachedPool = new MemcachedPool();
        memcachedPoolableFactory = new MemcachedPoolableFactory(host, port);
        poolConfig = new GenericObjectPoolConfig();
        // set max connection in the pool
        // usually < 20 is enough 
        poolConfig.setMaxTotal(10);
        // should setMaxIdle to max connection
        // to persistent all connections in the pool
        poolConfig.setMaxIdle(10);
        /*
        Never 
        1.setTestOnBorrow
        2.setTestOnCreate
        Those options cause performace decreases about 4x times
        */
        // set timeout 
        poolConfig.setMaxWaitMillis(500);
        memcachedPool.initPool(poolConfig, memcachedPoolableFactory);
        // Test pool
        MemcachedClient resource = memcachedPool.getResource();
        if (resource != null) {
            memcachedPool.returnResource(resource);
        } else {
            _logger.error("Cannot initialize memcache connection pool");
        }
    }

    public static MemcachedController getInstance(String configName) {
        MemcachedController instance = mapsInstance.get(configName);
        if (instance == null) {
            instance = new MemcachedController(configName);
            if (instance != null) {
                mapsInstance.put(configName, instance);
            }
        }
        return instance;
    }

    public String get(String key) {
        MemcachedClient mcClient = null;
        try {
            mcClient = memcachedPool.getResource();
            return (String) mcClient.get(key);
        } catch (Exception ex) {
            _logger.error("{MemcachedController} Exception when get key: {}, {}", key, ex);
            memcachedPool.returnBrokenResource(mcClient);
            return null;
        } finally {
            if (mcClient != null) {
                memcachedPool.returnResource(mcClient);
            }
        }
    }

    public int getInt(String key) {
        MemcachedClient mcClient = null;
        try {
            mcClient = memcachedPool.getResource();
            Object value = mcClient.get(key);
            if (value == null) return 0;
            return (int) value;
        } catch (Exception ex) {
            mcClient = null;
            memcachedPool.returnBrokenResource(mcClient);
            _logger.error("{MemcachedController} Exception when get key: {}, {}", key, ex);
            return 0;
        } finally {
            if (mcClient != null) {
                memcachedPool.returnResource(mcClient);
            }
        }
    }

    public void set(String key, int exp, Object value) {
        MemcachedClient mcClient = null;
        try {
            mcClient = memcachedPool.getResource();
            mcClient.set(key, exp, value);
        } catch (Exception ex) {
            memcachedPool.returnBrokenResource(mcClient);
            _logger.error("{MemcachedController} Exception when set key: {}, exp: {}, value: {}",
                    new Object[]{key, ex, String.valueOf(value)});
        } finally {
            if (mcClient != null) {
                memcachedPool.returnResource(mcClient);
            }
        }
    }

    public void delete(String key) {
        MemcachedClient mcClient = null;
        try {
            mcClient = memcachedPool.getResource();
            mcClient.delete(key);
        } catch (Exception ex) {
            memcachedPool.returnBrokenResource(mcClient);
            _logger.error("{MemcachedController} Exception when delete key: {},"
                    + "Exception: ", key, ex);
        } finally {
            if (mcClient != null) {
                memcachedPool.returnResource(mcClient);
            }
        }
    }

    public static void main(String[] args) {
        MemcachedController _mcTest = MemcachedController.getInstance("memcache_delivery");
        _mcTest.set("key", 0, "value");
        _mcTest.set("key1", 0, 1);
        String key = _mcTest.get("key");
        int key1 = _mcTest.getInt("key1");
        System.out.println(key);
        System.out.println(key1);
        _mcTest.delete("key");
        _mcTest.delete("key1");
        key = _mcTest.get("key");
        key1 = _mcTest.getInt("key1");
        System.out.println(key);
        System.out.println(key1);
    }

}
