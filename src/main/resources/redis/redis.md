## Redis
    Redis是一个使用C语言开发的高性能的key-value数据库。与传统数据库不同的是Redis的数据是存在内存中的所以读写速度非常快。
+ **数据类型**   
    * String：   
        是Redis中最常使用的类型，内部实现通过SDS(Simple Dynamic String)来存储。SDS类似于Java中的ArrayList，可以通过预分配冗余空间的方式减少内存的频繁分配。
        最简单的类型，value其实不仅是字符串，也可以是数字，String值的最大存储为512Mb。
    * Hash：   
        hash是一个类似于map的键值对(key-value)组合，这个一般是将一个可以结构化的数据(比如一个对象，前提是这个对象没有嵌套其他数据)给缓存到Redis里。
        然后每次读缓存的时候，可以操作hash里的某个字段。
        ```
        hset person name bingo
        hset person age 20
        hset person id 1
        hget person name
        
        (person = {
          "name": "bingo",
          "age": 20,
          "id": 1
        })
        ```
    * List:    
        List是一个有序列表(按照插入数据排序)。常用命令：lpush（添加左边元素）,rpush（添加左边元素）,lpop（移除左边第一个元素）,rpop,lrange（获取列表片段，LRANGE key start stop）等。
        List的应用场景非常多：如关注列表，粉丝列表，评论列表等。可以通过lrange获取某个闭区间内的元素;可以基于list实现分页查询。
        比如，我们常用的博客网站的文章列表，当用户量越来越多时，而且每一个用户都有自己的文章列表，而且当文章多时，都需要分页展示，这时可以考虑使用Redis的列表，
        列表不但有序同时还支持按照范围内获取元素，可以完美解决分页查询功能。大大提高查询效率。
        ```
        # 0开始位置，-1结束位置，结束位置为-1时，表示列表的最后一个位置，即查看所有。
        lrange mylist 0 -1
        复制代码
        ```                                   
        可以使用list实现简单的消息队列：
        ```
        lpush mylist 1
        lpush mylist 2
        lpush mylist 3 4 5
        
        # 1
        rpop mylist
        ```
    * Set:   
        set是String的无序组合，元素自动去重。常用命令：sadd,spop,smembers,sunion 等。set是一个集合，是一堆不重复值的组合，可以存放一些集合性数据。
        可以基于set做交集，并集，差集操作，比如交集：可以把两个人的粉丝列表交集，可以看两个人的共同好友。
        ```
        # 判断是否包含某个值
        sismember mySet 3
        
        # 删除某个/些元素
        srem mySet 1
        srem mySet 2 4
        
        # 查看元素个数
        scard mySet
        
        # 随机删除一个元素
        spop mySet
        
        #-------操作多个set-------
        # 将一个set的元素移动到另外一个set
        smove yourSet mySet 2
        
        # 求两set的交集
        sinter yourSet mySet
        
        # 求两set的并集
        sunion yourSet mySet
        
        # 求在yourSet中而不在mySet中的元素
        sdiff yourSet mySet
        ```
    * Sort Set(ZSet):
        Sort Set是排序的Set，去重，可以排序，写进去的时候给一个分数，自动根据分数进行排序。和Set相比，Sort Set关联了一个double类型的权重参数score，
        使得集合可以按照score进行排序，Redis正是通过分数为集合进行从小到大的排序。ZSet的成员是唯一的，但是score可以重复。   
        使用场景：   
        排行榜：有序集合经典使用场景。例如视频网站需要对用户上传的视频做排行榜，榜单维护可能是多方面：按照时间、按照播放量、按照获得的赞数等。   
        用Sorted Sets来做带权重的队列，比如普通消息的score为1，重要消息的score为2，然后工作线程可以选择按score的倒序来获取工作任务。让重要的任务优先执行。   
    * Bitmap:
        通过一个bit位来表示某个元素对应的值或状态。Bitmap的底层数据结构用的是String类型的SDS数据结构来保存数组，Redis把每个字节组的8个bit位利用起来，
        每个bit位表示一个元素的位置状态(0或者1);   
        可以将bitmap看成是一个bit为单位的数组，数组的每个位置只存储0或者1，数组的下标在bitmap中叫偏移量offset。   
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/redis/bitmap.png)   
        8个bit组成一个byte,所以bitmap会极大的节省空间。   
        [bitmap可以判断用户是否登录，记录签到状态等。](https://segmentfault.com/a/1190000040177140)   
        Bitmap提供了GETBIT，SETBIT操作，通过一个偏移值offset对bit数组的offset位置的bit位进行读写操作，需要注意的是offset是从0开始的。   
        判断用户是否登录：只需要设置一个key=login_status表示存储用户登录状态的集合，_将用户ID(唯一值)作为offset_，在就设置为1，下线就设置为0。通过GETBIT判断对应的用户是否在线。   
        SETBIT命令：   
        ```
        SETBIT <key> <offset> <value>
        ```
        设置或者清空key的value在offset处的bit值（只能是0或者1）。   
        GETBIT命令：   
        ```
        GETBIT <key> <offset>
        ```
        获取key的value在在offset处的bit位的值，当key不存在是返回0。   
        BITCOUNT命令：   
        ```
        BITCOUNT <key>
        ```
        统计给的bit数组中值等于1的bit位的数量(可用于统计签到次数)。
        BITPOS命令： 
        ```
        BITPOS key bitValue [start] [end]
        ```
        返回数据表示Bitmap中第一个值为bitValue的offset位置，在默认情况先将检索整个试图，可以通过start，end指定检测范围。
    * HyperLogLog    
        统计一个集合中不重复的元素的个数，比较适合做大规模的数据去重,例如统计uv。HyperLogLog只做基数运算，不保存元数据。HyperLogLog每个KEY最多占用12K的内存空间。
        基数计算的结果是一个标准误差为0.81%的近似值，当数据量不大的时候，得到的结果也可能是一个准确值。   
        常用命令：   
        ```
        PFADD key element [element …]
        ```
        将所有元素参数element添加到键为key的HyperLogLog数据结构中。
        ```
        PFCOUNT key [key …]
        ```
        当PFCOUNT使用单个key时，返回存在给的键的HyperLogLog数据结构的近似值，如果键不在返回0；   
        当PFCOUNT使用多个key时，返回存储在给定的所有HyperLogLog数据结构的并集的近似基数，也就是把所有的HyperLogLog数据结构合并到一个临时的HyperLogLog数据结构，然后计算近似基数。   
        ```
        PFMERGE destkey sourcekey [sourcekey ...]
        ```
        把多个HyperLogLog数据结构合并为一个新的键为destkey的HyperLogLog数据结构，合并后的HyperLogLog的基数接近于所有输入HyperLogLog的可见集合（Observed Set）的并集的基数。
    * Geospatial   
        可以用来保存地理位置，并做位置距离计算或者根据半径计算位置等。
+ **Redis数据过期淘汰**
    Redis会维护一个过期字典来保存数据过期时间。过期字典的键指向Redis数据库中key，过期字典的value是一个long long 类型的整数，保存了键值指向数据库key的过期时间。
    过期字典在redisDb里：
    ```
    typedef struct redisDb {
        ...
    
        dict *dict;     //数据库键空间,保存着数据库中所有键值对
        dict *expires   // 过期字典,保存着键的过期时间
        ...
    } redisDb;  
    ```
    Redis的过期删除策略：   
        1、惰性删除：只会在取出key的时候才会对数据进行过去检查。这样对CPU友好，但是可能会造成大量过期的key没有被删除。   
        2、定期删除：每隔一段时间抽出一部分过期的key执行删除操作。并且Redis底层会通过限制删除操作执行时长和频率来减少删除操作对CPU的影响。   
        定期删除对CPU优化，定期删除对内存友好。Redis采用的是惰性删除+定期删除。   
        但是，仅仅通过给 key 设置过期时间还是有问题的。因为还是可能存在定期删除和惰性删除漏掉了很多过期key的情况。这样就导致大量过期key堆积在内存里，然后就OOM了。
        可以通过Redis内存淘汰机制解决。   
    Redis内存淘汰机制：   
        1、volatile-lru（least recently used）：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据进行淘汰。   
        2、volatile-ttl：从已设置过去时间的数据集中挑选要过期的数据淘汰。   
        3、volatile-random：从已设置过去时间的数据集中随机选择数据淘汰。   
        4、allkeys-lru（least recently used）：当内存不足以容纳写入新数据时，移除最近最少使用的key。      
        5、allkeys-random：从数据集（server.db[i].dict）中随机挑选数据进行淘汰。   
        6、no-eviction：禁止驱逐数据，当内存不足以容纳写入新数据时，新写入操作会报错。   
        7、volatile-lfu（least frequently used）：从已设置过期时间的数据集（server.db[i].expires）中挑选最不经常使用的数据淘汰。   
        8、allkeys-lfu（least frequently used）：当内容不足以写入新数据时，移除最不经常使用的数据。
+ **Redis持久化**
    * RDB   
    工作原理：   
        1、Redis调用fork()，产生一个子进程。   
        2、子进程把数据写入一个临时的RDB文件。   
        3、当子进程写完新的RDB文件之后，把旧的RDB删除。   
    优点：RDB是一个很简洁的文件，它保存了某个时间点的Redis数据，适合做备份。RDB的性能更好，需要备份的时候会fork一个子进程，然后把工作交给子进程操作。比起AOF在数据量大的情况下,RDB启动更快。   
    缺点：RDB容易造成数据丢失，因为他是每隔一段时间保存一次。如果Redis出现故障，那么上次数据保存到故障这段时间的数据会丢失；RDB使用fork()产生子进程进行数据的持久化
    如果数据量大的时候会花费很多时间,造成Redis停止服务一段时间，如果数据量很大且CPU性能不是很好的时候，停止服务的时间甚至会到1秒。   
    * AOF   
    优点：AOF提供了更可靠的持久化方式，每当Redis接收到修改数据的命令时，就会把命令追加到AOF文件，当redis重启的时候，会优先选择AOF进行数据恢复。  
    比RDB可靠，可以制定不同的fsync策略：不进行fsync，每秒fsync一次和每次查询进行fsync，默认为每秒一次fsync。这意味着最大丢失一秒的数据。AOF是一个追加文件，
    就是突然停电也不会出现日志的定位或者损坏。甚至如果因为某些原因(例如磁盘满了)命令只写到一半到日志文件，也可以通过redis-check-aof进行修复。
    AOF把操作命令以简单易懂的格式一条条保存到文件里，很容易导出来进行数据恢复。   
    缺点：在相同数据集下，AOF的文件一般比RDB大。在某些fsync策略下，AOF的速度会比RDB慢，通常fsync设置为每秒一次就能获得比较高的性能，而在禁止fsync的情况下速度可以达到RDB的水平。
    AOF数据因为bug导致数据不一致
    * AOF日志重写   
    1、Redis调用fork(),产生一个子进程。   
    2、子进程把新的AOF写到一个临时文件里。   
    3、主进程持续把新的变动写入到内存里的Buffer,同时也会把新的变动写到旧的AOF里，这样即使重写失败也能保证数据安全。   
    4、当子进程完成文件重写后，主进程会获得一个信号，然后把内存Buffer的数据写到新的AOF文件里。   
    我们可以配置日志文件的重写条件   
    ```
    # Redis会记住自从上一次重写后AOF文件的大小（如果自Redis启动后还没重写过，则记住启动时使用的AOF文件的大小）。
    # 如果当前的文件大小比起记住的那个大小超过指定的百分比，则会触发重写。
    # 同时需要设置一个文件大小最小值，只有大于这个值文件才会重写，以防文件很小，但是已经达到百分比的情况。
    
    auto-aof-rewrite-percentage 100
    auto-aof-rewrite-min-size 64mb
    ```
    禁用重写
    ```
    auto-aof-rewrite-percentage 0
    ```
    Redis 2.4以上才可以自动进行日志重写，之前的版本需要手动运行BGREWRITEAOF这个命令。
  
