## MQ
    消息队列（Message Queue
+ [使用场景](https://github.com/doocs/advanced-java/blob/main/docs/high-concurrency/why-mq.md)   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/mq/mq.png)     
    当不需要立即会获得结果，但是并发量又需要控制的时候，差不多就是使用消息队列的时候。   
    消息队列主要使用场景：   
    1，应用解耦。在应用和应用之间，提供了异构系统之间的消息通讯的机制，通过消息中间件解决多个系统或异构系统之间除了RPC之外另一种单向通讯的机制。
    2，异步处理。针对不需要立即处理消息，尤其那种非常耗时的操作，通过消息队列提供了异步处理机制，通过额外的消费线程接管这部分进行异步操作处理。   
    3，限流削峰。避免流量过大导致应用系统挂掉的情况；   
    4，消息驱动的系统。系统分为消息队列、消息生产者、消息消费者，生产者负责产生消息，消费者(可能有多个)负责对消息进行处理；     
+ 消息队列带来的问题   
    系统可用性降低：系统可用性在某些程度上降低了，在引入MQ之前，不用考虑消息丢失或者MQ出现宕机的情况，但是引如之后就需要考虑。      
    系统复杂性提高：引入MQ之后，需要保证消息有没有被重复消费，消息丢失的情况，消息的投递顺序等。    
    一致性问题：消息队列带来的异步确实可以提高系统响应速度。但是，万一消息的真正消费者并没有正确消费消息怎么办？这样就会导致数据不一致的情况。   
+ RocketMQ   
    RocketMQ是阿里开源的使用Java开发的一个MQ；特点是吞吐量高，支持分布式架构。   
    RocketMQ服务端核心组件有三个NameServer，Broker，FilterServer（可选，部署于和Broker同一台机器）。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/mq/rocket-mq.jpeg)     
    * NameServer   
    + NameServer是RocketMQ的寻址服务。用于把MQ的路由信息做聚合。客户端依靠NameServer决定去获取对应的Topic路由信息，从而决定对哪些broker做连接。   
    + NameServer是一个几乎无状态的节点，NameServer直接采用share-nothing的设计，互不通讯。    
    + 对应一个NameServer集群列表，客户端连接Name Server的时候，只会随机选择一个节点进行访问，以达到负载均衡。   
    + NameServer的所有状态都由Broker上报,本身不存储任何状态，所有数据均在内存中。   
    + 如果中途所有的NameServer全都挂了，影响到路由信息的更新， 不会影响和broker的通信。   
    * Broker   
    + Broker是处理消息存储，转发等处理的服务器。   
    + Broker以group分开，每个group只允许一个master若干和slave。   
    + 只有master运行写如操作，slave不允许。   
    + slave从master同步数据，同步策略取决于master，可以配置同步双写，异步复制两种。   
    + 客户端可以和master和slave消费。在默认情况下，消费者都从master消费，在master挂掉之后，客户端由于可以从NameServer感知到Broker挂机,就会从slave消费。  
    + Broker向所有的NameServer节点建立长链接，注册topic信息。   
    * Filter Server（可选）   
    + RocketMQ可以允许消费者上传一个Java类给Filter Server进行过滤。   
    + Filter Server只能起在Broker所在的机器
    + 可以有若干个Filter Server进程。   
    + 拉取消息的时候，消息先经过Filter Server，Filter Server靠上传的Java类过滤消息后才推给Consumer消费。   
    + 客户端完全可以消费消息的时候做过滤，不需要Filter Server。   
    + FilterServer存在的目的是用Broker的CPU资源换取网卡资源。因为Broker的瓶颈往往在网卡，而且CPU资源很闲。在客户端过滤会导致无需使用的消息在占用网卡资源。    
    + 使用 Java 类上传作为过滤表达式是一个双刃剑，一方面方便了应用的过滤操作且节省网卡资源，另一方面也带来了服务器端的安全风险，这需要足够谨慎，
        消费端上传的class要保证过滤的代码足够安全——例如在过滤程序里尽可能不做申请大内存，创建线程等操作，避免 Broker 服务器资源泄漏。   
           
    * 消息顺序   
    消息顺序指的是一类消息消费时，能按照发送的顺序来消费。顺序信息包括全局消息顺序和分区消息顺序；全局消息顺序指某个topic下所有的消息都要保证顺序；
    分区消息顺序指保证某一组消息被顺序执行即可。  
    - 全局顺序   
    对于指定的一个topic所有的消息都按FIFO的顺序进行发布和消费。适用场景：性能要求不高，所有的消息严格按照 FIFO 原则进行消息发布和消费的场景。   
    - 分区顺序   
    对于指定一个topic，所有的消息都是按照sharding key进行区块区分。同一个分区区块内的消息严格按照FIFO顺序进行发布和消费。Sharding key是顺序消息
    中用来区分不同分区额关键字，和普通的消息key是完全不同的概念。    
    * [消息不丢失](https://www.cnblogs.com/goodAndyxublog/p/12563813.html)
    
    
    