## Redis

+ **Redis分布式锁**   
    分布式锁是控制分布式系统之间同步访问共享资源。
    Redis实现锁的命令：   
    1、SETNX,用法SETNX key value。SETNX是[set if not exits]，如果不存在就执行set操作，设置成功返回1，否则返回0；
    SETNX没有设置过期时间，会导致线程一直持有锁。
    2、EXPIRE,用法 EXPIRE Key Seconds 给某一个key设置过期时间。
    加锁可以分为两个操作：   
    ```
    `SETNX Key 1`
    `EXPIRE Key Seconds`
    ```
    可以设置过期时间，但是整个操作不是原子性的，如果执行完SETNX之后出现异常，设置的锁没有过期时间，会导致线程线程一直持有锁。   
    **解决**：
    可以用相关lua脚本解决原子性问题：
    ```
    if (redis.call('setnx', KEYS[1], ARGV[1]) < 1)
    then return 0;
    end;
    redis.call('expire', KEYS[1], tonumber(ARGV[2]));
    return 1;
    
    // 使用实例
    EVAL "if (redis.call('setnx',KEYS[1],ARGV[1]) < 1) then return 0; end; redis.call('expire',KEYS[1],tonumber(ARGV[2])); return 1;" 1 key value 100  
    ```
    可以使用`set key value [EX seconds][PX milliseconds][NX|XX]`命令解决。   
    EX seconds:设置过期时间，单位为秒。   
    PX milliseconds：设置过期时间，单位为毫秒。   
    NX：当key不存在是设置值。   
    XX：当可以存在时设置值。   
    加锁：
    给每个锁一个唯一ID，加锁是生成，解锁是判断。给锁设置对应的过期时间。
    ```java
    public static boolean tryLock(String key, String uniqueId, int seconds) { 
      return "OK".equals(jedis.set(key, uniqueId, "NX", "EX", seconds));
    }
    ```
    解锁：   
    解锁时使用lua脚本，先判断唯一ID是否相同，在进行相关解锁操作。
    ```java
    public static boolean releaseLock(String key, String uniqueId) { 
      String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " + "return redis.call('del', KEYS[1]) else return 0 end";
       return jedis.eval( luaScript, Collections.singletonList(key), Collections.singletonList(uniqueId) ).equals(1L);
    }
    ```
    [Redis分布式锁问题：](https://xiaomi-info.github.io/2019/12/17/redis-distributed-lock/)   
    1、锁的时间大小设置问题；若时间设置过短，线程A还在执行但是锁已经释放，此时线程B也能获取到该锁，会导致数据问题。   
        将A的线程过期时间设置足够长。    
        为获取锁的线程增加守护线程，为将要过期但未释放的锁。   
    2、锁误解除；如果不加唯一ID，同样是线程A还在执行但是锁已经释放，此时线程B也能获取到该锁，此时A进行删除锁操作，但是B还没有执行完成，此时删除的是B的锁。   
        增加唯一ID可以解决。      
    3、不可重入,当前线程持有锁的情况下，再次请求加锁，如果一个锁支持一个线程多次加锁，那么这个锁就是可重入的。如果一个不可重入锁被再次加锁，由于该锁已经被持有，再次加锁会失败。   
        Redis可以通过对锁进行重入计数，加锁+1，释放锁-1，直到为0锁释放完成。   
    4、客户端无法等待锁释放：上述命令执行都是立即返回的，如果客户端可以等待锁释放就无法使用。   
        可以通过客户端轮询的方式解决该问题，当未获取到锁时，等待一段时间重新获取锁，直到成功获取锁或等待超时。这种方式比较消耗服务器资源，当并发量比较大时，会影响服务器的效率。   
        另一种方式是使用 Redis 的发布订阅功能，当获取锁失败时，订阅锁释放消息，获取锁成功后释放时，发送锁释放消息。   
    5、redis集群锁：
        _主备切换问题_。Redis主从方式部署，主从同步数据有同步和异步两种方式，redis将指令记录在本地Buffer中，然后异步将Buffer中的指令同步到从节点，从节点一边
        执行同步指令来达到和主节点状态一致，一边向主节点反馈同步情况。
        当主节点挂掉之后，从节点会取而代之，但是客户端无明显感知。当A节点获取到锁，指令还未同步，此时A节点挂掉，从节点提升为主节点，新的主节点没有锁的数据，此时B仍能获取到锁。
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/redis/redis-lock-07.png)   
        _集群脑裂_。脑裂问题指因为网络原因，导致redis的master节点跟slave和sentinel集群处于不同的网络分区。因为sentinel无法感知到master的存在，
        所以讲slave节点提升为master节点，此时存在两个不同的master节点。Redis Cluster 集群部署方式同理。当不同的客户端连接到不同的master时两个客户端可以持有一把锁。
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/redis/redis-lock-08.png)   
    _Redis 以其高性能著称，但使用其实现分布式锁来解决并发仍存在一些困难。Redis 分布式锁只能作为一种缓解并发的手段，如果要完全解决并发问题，仍需要数据库的防并发手段。_
    Redisson分布式锁：   
        Redisson是一个在Redis的基础上实现的Java驻内存数据网格（In-Memory Data Grid）。它不仅提供了一系列的分布式的Java常用对象，
        还实现了可重入锁（Reentrant Lock）、公平锁（Fair Lock、联锁（MultiLock）、 红锁（RedLock）、 读写锁（ReadWriteLock）等，还提供了许多分布式服务。
        Redisson提供了使用Redis的最简单和最便捷的方法。     
        RedissonLock同样没有解决节点挂掉的时候，存在锁丢失的风险。而现实情况是有一些场景无法容忍的，所以 Redisson 提供了实现了redlock算法的 RedissonRedLock，
        RedissonRedLock 真正解决了单点失败的问题，代价是需要额外的为 RedissonRedLock 搭建Redis环境。所以，如果业务场景可以容忍这种小概率的错误，则推荐使用 RedissonLock， 
        如果无法容忍，则推荐使用 RedissonRedLock。  
    redlock算法：   
        1,获取当前Unix时间，以毫秒为单位。   
        2,依次尝试从5个实例，使用相同的key和具有唯一性的value（例如UUID）获取锁。当向Redis请求获取锁时，客户端应该设置一个尝试从某个Reids实例获取锁的最大等待时间
        （超过这个时间，则立马询问下一个实例），这个超时时间应该小于锁的失效时间。例如你的锁自动失效时间为10秒，则超时时间应该在5-50毫秒之间。
        这样可以避免服务器端Redis已经挂掉的情况下，客户端还在死死地等待响应结果。如果服务器端没有在规定时间内响应，客户端应该尽快尝试去另外一个Redis实例请求获取锁。   
        3,客户端使用当前时间减去开始获取锁时间（步骤1记录的时间）就得到获取锁消耗的时间。当且仅当从大多数（N/2+1，这里是3个节点）的Redis节点都取到锁，
        并且使用的总耗时小于锁失效时间时，锁才算获取成功。   
        4,如果取到了锁，key的真正有效时间 = 有效时间（获取锁时设置的key的自动超时时间） – 获取锁的总耗时（询问各个Redis实例的总耗时之和）（步骤3计算的结果）。   
        5,如果因为某些原因，最终获取锁失败（即没有在至少 “N/2+1 ”个Redis实例取到锁或者“获取锁的总耗时”超过了“有效时间”），客户端应该在所有的Redis实例上进行解锁
        （即便某些Redis实例根本就没有加锁成功，这样可以防止某些节点获取到锁但是客户端没有得到响应而导致接下来的一段时间不能被重新获取锁）。   
        
    