package vn.ethicconsultant.commons.memcachedpool2;

import java.util.HashMap;
import java.util.Map;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class MemcachedPool extends Pool<MemcachedClient> {

    private static final Logger _logger = LoggerFactory.getLogger(MemcachedPool.class);

    @Override
    protected void closeInternalPool() throws Exception {
        super.closeInternalPool(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void returnBrokenResourceObject(MemcachedClient resource) throws Exception {
        super.returnBrokenResourceObject(resource); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void destroy() throws Exception {
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void returnResource(MemcachedClient resource) {
        try {
            super.returnResource(resource); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception ex) {
            _logger.error("Exception when return resource: {}", ex);
        }
    }

    @Override
    public void returnBrokenResource(MemcachedClient resource) {
        try {
            super.returnBrokenResource(resource); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception ex) {
            _logger.error("Exception when return broken resource: {}", ex);
        }
    }

    @Override
    public void returnResourceObject(MemcachedClient resource) throws Exception {
        super.returnResourceObject(resource); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MemcachedClient getResource()  {
        try {
            return super.getResource(); //To change body of generated methods, choose Tools | Templates.
        } catch (Exception ex) {
            _logger.error("Exception when get resource from pool: {}", ex);
        }
        return null;
    }

    /* init all connection for the pool
     * before return to client
     * otherwise spymemcache only establish a few connections 
     * time to establish a memcached connetion can casuse application to be 
     * blocked
    */ 
    

    @Override
    public void initPool(GenericObjectPoolConfig poolConfig, PooledObjectFactory<MemcachedClient> factory) {
        super.initPool(poolConfig, factory); 
        _logger.info("Initialize Memcache connection pool....");
        HashMap<Integer, MemcachedClient> clientMap = new HashMap<>();
        for (int i = 0; i < poolConfig.getMaxTotal(); i++) {
            _logger.info("Activate conntion id #" + i);
            MemcachedClient resource = getResource();
            clientMap.put(i, resource);
            resource.get("SOMETHINGTOINITIALIZE");
        }
        for (Map.Entry<Integer, MemcachedClient> entrySet : clientMap.entrySet()) {
            Integer key = entrySet.getKey();
            returnResource(clientMap.get(key));
        }
        clientMap.clear();
    }

    /**
     *
     * @param poolConfig
     * @param host
     * @param port
     */
}
