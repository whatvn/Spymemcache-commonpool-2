package vn.ethicconsultant.commons.memcachedpool2.Test;

import net.spy.memcached.MemcachedClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import vn.ethicconsultant.commons.memcachedpool2.MemcachedPool;
import vn.ethicconsultant.commons.memcachedpool2.MemcachedPoolableFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hungnguyen
 */
public class Test {

    public static void main(String[] args) throws Exception {
        String host = "192.168.10.225";
        int port = 11211;
        MemcachedPool memcachedPool = new MemcachedPool();
        MemcachedPoolableFactory memcachedPoolableFactory = new MemcachedPoolableFactory(host, port);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxWaitMillis(500);
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(10);
        //poolConfig.setTestOnBorrow(true);
        //poolConfig.setTestOnCreate(true);
        memcachedPool.initPool(poolConfig, memcachedPoolableFactory);

        MemcachedClient mc = memcachedPool.getResource();
        mc.set("hung1334", 3600, "heo");
        mc.set("hung1335", 3600, "heo");
        mc.set("hung1336", 3600, "heo");
        memcachedPool.returnResource(mc);
        mc = memcachedPool.getResource();
        System.out.println(mc.get("hung1334"));
        memcachedPool.returnResource(mc);
        mc = memcachedPool.getResource();

        System.out.println(mc.get("hung1335"));
        memcachedPool.returnResource(mc);
        mc = memcachedPool.getResource();
        System.out.println(mc.get("hung1336"));
        memcachedPool.returnResource(mc);

    }

}
