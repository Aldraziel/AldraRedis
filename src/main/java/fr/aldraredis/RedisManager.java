package fr.aldraredis;

import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.logging.Level;

public class RedisManager
{
    private final JedisPoolConfig jedisPoolConfig;
    private JedisPool pool;

    public RedisManager()
    {
        this.jedisPoolConfig = new JedisPoolConfig();
        this.connect();
    }

    void connect()
    {
        final var plugin = AldraRedis.getInstance();
        final var logger = plugin.getLogger();
        final var config = plugin.getConfig();

        this.pool = new JedisPool(this.jedisPoolConfig, config.getString("redis.host"), config.getInt("redis.port"), 2000, config.getString("redis.pass"));

        try (final Jedis jedis = this.pool.getResource())
        {
            logger.info("Connection set with Redis, on database " + jedis.getDB());
        }
        catch (final Exception e)
        {
            logger.log(Level.SEVERE, "An error occurred during connecting to Redis ! (" + e.getMessage() + "). Shutdown...");
            Bukkit.shutdown();
        }
    }

    public JedisPool getPool()
    {
        return this.pool;
    }
}
