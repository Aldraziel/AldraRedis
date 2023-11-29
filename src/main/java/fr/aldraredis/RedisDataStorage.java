package fr.aldraredis;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.function.Consumer;
import java.util.function.Function;

public class RedisDataStorage
{
    private final JedisPool pool;
    private final RedisManager redisManager;
    private final int defaultDB;

    public RedisDataStorage(@NotNull RedisManager redisManager)
    {
        this(redisManager, 0);
    }

    public RedisDataStorage(@NotNull RedisManager redisManager, int defaultDB)
    {
        this.redisManager = redisManager;
        this.pool = this.redisManager.getPool();
        this.defaultDB = defaultDB;
    }

    public void executeOnCommonDB(@NotNull Consumer<Jedis> action)
    {
        this.execute(action, 0);
    }

    public void execute(@NotNull Consumer<Jedis> action)
    {
        this.execute(action, this.defaultDB);
    }

    public void execute(@NotNull Consumer<Jedis> action, int db)
    {
        this.execute(action, db, true);
    }

    private void execute(@NotNull Consumer<Jedis> action, int db, boolean reconnect)
    {
        try(Jedis jedis = this.pool.getResource())
        {
            if(jedis.getDB() != db)
                jedis.select(db);
            action.accept(jedis);
        }
        catch (Exception e)
        {
            if(!reconnect)
            {
                e.printStackTrace();
                return;
            }

            AldraRedis.getInstance().getLogger().info("Reconnecting...");
            this.redisManager.connect();
            this.execute(action, db, false);
        }
    }

    public <R> R executeAndGetOnCommonDB(@NotNull Function<Jedis, R> action)
    {
        return this.executeAndGet(action, 0);
    }

    public <R> R executeAndGet(@NotNull Function<Jedis, R> action)
    {
        return this.executeAndGet(action, this.defaultDB);
    }

    public <R> R executeAndGet(@NotNull Function<Jedis, R> action, int db)
    {
        return this.executeAndGet(action, db, true);
    }

    private <R> R executeAndGet(@NotNull Function<Jedis, R> action, int db, boolean reconnect)
    {
        try(Jedis jedis = this.pool.getResource())
        {
            if(jedis.getDB() != db)
                jedis.select(db);
            return action.apply(jedis);
        }
        catch (Exception e)
        {
            if(!reconnect)
            {
                e.printStackTrace();
                return null;
            }

            AldraRedis.getInstance().getLogger().info("Reconnecting...");
            this.redisManager.connect();
            return this.executeAndGet(action, db, false);
        }
    }
}
