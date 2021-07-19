## Redis

* **Redis集群**   
    * 主从(master-slave)架构   
    单机的 Redis，能够承载的 QPS 大概就在上万到几万不等。对于缓存来说，一般都是用来支撑读高并发的。因此架构做成主从(master-slave)架构，
    一主多从，主负责写，并且将数据复制到其它的 slave 节点，从节点负责读。所有的读请求全部走从节点。这样也可以很轻松实现水平扩容，支撑读高并发。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/redis/redis-master-slave.png)     
    读写分离：在主从架构中，master节点负责处理写请求，Salve负责处理读请求，对于读请求多，写请求少的场景下可以大幅度提高并发量，通过增加redis从节点的数量可以使得redis的QPS达到10W+。   
    主从同步的方法：   
    + 增量同步：主节点将对自己状态产生修改的指令记录在本地Buffer内存中,然后异步将Buffer中的指令同步到slave中，从节点一边同步指令流来达到和主节点一样的状态，
        一边向主节点反馈自己同步到哪里了(偏移量)，从节点数据同步不会影响主节点正常工作，也不会影响自己对外提供服务，从节点会用旧数据提供服务，当同步完成后，需要删除旧数据，
        这时会暂停对外提供服务。因为Buffer是有限的，所以redis不能将所有指令都存在Buffer中，redis 的复制内存 buffer 是一个定长的环形数组，如果数组内容满了，就会从头开始覆盖前面的内容。    
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/redis/ring.png)     
    + 快照同步：如果节点间网络通讯不好，那么从节点的速度跟不上主节点接收新数据的速度，Buffer中的指令会丢失一部分，从节点的数据会和主节点不一致，此时会触发快照同步。
        快照同步非常消耗资源，首先需要在主节点上进行一次bgsave将内存中的数据全部快照到RDB文件中，然后将RDB文件传输到子节点中，子节点收到RDB文件后,立即执行一次全量加载。
        加载之前先将当前内存数据清空，加载完成之后，再通知主节点进行增量同步。在整个快照同步进行的过程中，主节点的复制 buffer 还在不停的往前移动，
        如果快照同步的时间过长或者复制 buffer 太小，都会导致同步期间的增量指令在复制 buffer 中被覆盖，这样就会导致快照同步完成后无法进行增量复制，
        然后会再次发起快照同步，如此极有可能会陷入快照同步的死循环。所以需要配置一个合适的复制 buffer 大小参数，避免快照复制的死循环。     
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/redis/rdb.png)    
    + 无盘复制   
        主节点一边变量内存，一边将序列化内容发送到从节点,而不是完整的RDB文件后才进行IO传输从节点，还是和之前一样，先将接收到的内容存储到磁盘文件中，再进行一次性加载。   
    * 哨兵模式    
    在主从模式中master节点挂掉之后，会造成redis集群不可用的状态。哨兵模式可以解决主从中节点宕机的情况,当master节点出现宕机后，可以通过一系列机制选用某了salve节点做为新master节点。   
    Redis Sentinel是一个分布式系统，，为Redis提供高可用性解决方案。可以再一个架构中运行多个Sentinel进程，这些进程使用流言协议(gossip protocols)来接收
    关于主服务器是否下线的信息，并通过投票协议(agreement protocols)来决定是否执行自动故障迁移，以及选择哪个服务器作为主服务器。   
    Redis哨兵具有以下几个功能：
        + 监控（Monitoring）：持续监控Redis主节点，从节点是否处于预期的工作状态。   
        + 通知（Notification）：哨兵可以把Redis实例的运行故障信息通过API通知监控系统或者其他应用程序。   
        + 自动故障恢复（Automatic failover）：当主节点运行故障时，哨兵会启动自动故障修复流程，某个从节点会升级为主节点，其他从节点会使用新的主节点进行主从复制。
            通知客户端使用新的主节点。    
        + 配置中心（Configuration provider）：哨兵可以作为客户端服务发现的授权源，客户端连接到哨兵请求给定服务的Redis节点地址，如果发生故障转义
            哨兵会通知新的地址。这里要注意：哨兵并不是Redis代理，只是为客户端提供了Redis主从节点的地址信息。   
    + 哨兵模式的leader选举算法   
        1，每个在线的哨兵都可能成为领导者，当它确认主节点下线时，它会向其他哨兵发送`is-master-down-by-addr`命令，征求判断自己成为新的领导者，由领导者处理故障转移；
        2，当其他哨兵收到is-master-down-by-addr命令时，可以同意或者拒绝它成为领导者；   
        3，如果命令发送者哨兵发现自己的选票大于或等于num(sentinels)/2 + 1时将成为领导者，否则将继续选举；   
    主观下线：单个sentinel认为某个服务下线(有可能是接收不到订阅，之间的网络不通等等原因)。sentinel会以每秒一次的频率向所有与其建立了命令链接的实例（master，从服务，其他sentinel）
    发送ping命令，通过判断ping命令是否有效回复还是无效回复来判断实例是否在线（对该sentinel来说是主观在线）。
    客观下线：当主观下线的节点是主节点时，此时哨兵会通过`Sentinel is-masterdown-by-addr`寻找其他哨兵节点对主节点的判断，如果其他节点也认为该节点主观下线了，
    且认为其主观下线的票数超过选举数，此时哨兵节点认为该节点确实有问题，这样就客观下线了。大部分哨兵节点都认为下线了是客观下线。   
    脑裂问题：因为网络原因，导致Redis主节点跟从节点和Sentinel集群处于不同的网络分区，此时Sentinel无法感知到主节点的存在，就会进行选举，将某一个salve选举为
    master，此时就存在两个不同的master，就像一个大脑分裂成了两个。   
    脑裂环境中，如果客户端还在原来的主节点继续写数据，新的主节点无法同步相应的数据，当网络问题解决后，Sentinel将原来的主节点降级为salve节点，此时再从新的
    master中同步数据，则会造成大量数据丢失。   
    配置参数：   
    min-slaves-to-write 1   
    min-slaves-max-lag 10   
    要求至少有一个salve，数据同步和延迟不能超过10s，如果超过一个salve数据复制和同步都超过10s,这时master不接受任何请求。   
    [Redis节点数量问题(* 不一定准确 *)](https://cloud.tencent.com/developer/article/1535967)     
    [Redis选举和Raft算法](https://www.cnblogs.com/myd620/p/7811156.html)   
+ [**Redis 管道（Pipelining）**](https://www.huaweicloud.com/articles/70e7811129c8f1061e64862a8d3f6e79.html)     
    Pipelining是将一组命令进行打包，然后一次性通过网络发送给Redis，同时将返回的结果批量返回。   