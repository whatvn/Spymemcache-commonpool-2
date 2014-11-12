package vn.ethicconsultant.commons.memcachedpool2;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * PoolableObjectFactory custom impl.
 */
public class MemcachedPoolableFactory implements PooledObjectFactory<MemcachedClient> {

    private final String host;
    private final int port;

    public MemcachedPoolableFactory(final String host, final int port) {
        super();
        this.host = host;
        this.port = port;
    }

    @Override
    public void activateObject(PooledObject<MemcachedClient> pooledMemcached)
            throws Exception {
        final MemcachedClient mc = pooledMemcached.getObject();
    }

    @Override
    public void destroyObject(PooledObject<MemcachedClient> poolMemcached) throws Exception {
        final MemcachedClient mc = poolMemcached.getObject();
        if (mc.getAvailableServers().size() > 0) {
            try {
                try {
                    mc.shutdown();
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public PooledObject<MemcachedClient> makeObject() throws Exception {
        String addr = String.format("%s:%s", this.host, this.port);
        final MemcachedClient mc = new MemcachedClient(new ConnectionFactoryBuilder(new BinaryConnectionFactory(1000, 65536))
                .setDaemon(true)
                .setFailureMode(FailureMode.Retry)
                .build(),
                AddrUtil.getAddresses(addr));

        return new DefaultPooledObject<MemcachedClient>(mc);
    }

    @Override
    public void passivateObject(PooledObject<MemcachedClient> pooledMemcached)
            throws Exception {
        // TODO 
    }

    @Override
    public boolean validateObject(PooledObject<MemcachedClient> pooledMemcached) {
        final MemcachedClient mc = pooledMemcached.getObject();
        try {
            if (mc.getAvailableServers().size() > 0) {
                return true;
            }
        } catch (final Exception e) {
            return false;
        }
        return false;
    }

}
