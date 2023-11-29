package fr.aldraredis;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class AldraRedis extends JavaPlugin
{
    private static AldraRedis instance;
    private RedisManager redisManager;

    @Override
    public void onEnable()
    {
        instance = this;
        this.saveDefaultConfig();
        this.redisManager = new RedisManager();
    }

    @Override
    public void onDisable()
    {
        try
        {
            this.redisManager.getPool().close();
            this.redisManager.getPool().destroy();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public RedisDataStorage createNewRedisDataStorage()
    {
        return this.createNewRedisDataStorage(0);
    }

    public RedisDataStorage createNewRedisDataStorage(int defaultDB)
    {
        return new RedisDataStorage(this.redisManager, defaultDB);
    }

    public RedisManager getRedisManager()
    {
        return this.redisManager;
    }

    public static Optional<AldraRedis> instance()
    {
        if(instance == null)
            return Optional.empty();
        return Optional.of(instance);
    }

    public static AldraRedis getInstance()
    {
        final var optional = instance();
        return optional.orElseThrow(() -> new IllegalArgumentException("`AldraRedis.instance` is null!"));
    }
}
