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
    - 局部顺序   
    指同一个队列的消息有序，可以在发送消息时指定队列，在消费消息时也按顺序消费。例如同一个订单 ID 的消息要保证有序，不同订单的消息没有约束，
    相互不影响，不同订单 ID 之间的消息时并行的。   
    
    **保证消息顺序**同一类消息发送到
        
    * [消息不丢失](https://www.cnblogs.com/goodAndyxublog/p/12563813.html)
    
    * 消息发送过程   
    一条消息从发送到消费，一共经历三个阶段：   
     ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/mq/mq-message.jpeg)   
     - 生产阶段，Producer新建消息，然后通过网络将消息投递给MQ Broker。   
     - 存储阶段，消息将会存储在Broker端的磁盘中。   
     - 消费阶段，Consumer会从Broker中拉取消息进行消费。   
     
     - 生产阶段：   
     生产者（Producer） 通过网络发送消息给 Broker，当Broker收到消息后，将会返回确认相应信息给Producer。所以生产者只有收到确认响应，就代表消息在生产阶段未丢失。   
     ```java
      DefaultMQProducer mqProducer=new DefaultMQProducer("test");
      // 设置 nameSpace 地址
      mqProducer.setNamesrvAddr("namesrvAddr");
      mqProducer.start();
      Message msg = new Message("test_topic" /* Topic */,
              "Hello World".getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
      );
      // 发送消息到一个Broker
      try {
          SendResult sendResult = mqProducer.send(msg);
      } catch (RemotingException e) {
          e.printStackTrace();
      } catch (MQBrokerException e) {
          e.printStackTrace();
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
     ```
    `send()`是一个同步操作，只要这个方法不抛出任何异常，就代表消息已经发送成功。  
    消息发送成功仅代表消息已经到了 Broker 端，Broker 在不同配置下，可能会返回不同响应状态:
    SendStatus.SEND_OK   
    SendStatus.FLUSH_DISK_TIMEOUT   
    SendStatus.FLUSH_SLAVE_TIMEOUT   
    SendStatus.SLAVE_NOT_AVAILABLE    
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/mq/send-message.jpeg)   
    
    - 存储阶段   
    默认情况下，消息只要到了Broker端，将会优先保存到内存中，然后立即返回响应给生产者。随后Broker定期将一组数据从内存异步刷新到磁盘。这种方式的优点是
    减少IO次数，提高性能，但是如果Broker端发送宕机，则会出现未刷新的数据丢失的问题。      
    若要保证消息存储端不丢失，保证消息可靠性，可以将消息保存机制改完同步刷新，即消息同步磁盘成功，才会返回成功确认信息。   
    修改 Broker 端配置如下：
    `
    ## 默认情况为 ASYNC_FLUSH 
    flushDiskType = SYNC_FLUSH
    ` 
    
    若 Broker 未在同步刷盘时间内（默认为 5s）完成刷盘，将会返回 SendStatus.FLUSH_DISK_TIMEOUT 状态给生产者。   
    集群部署的情况下，Broker通常采用一主多从的部署方式。为了保证消息不丢失，消息还需要复制到slave节点。默认情况下，消息写入master成功，就可以返回
    给生产者确认信息，接着消息会异步刷新到slave节点。
    此时若 master 突然宕机且不可恢复，那么还未复制到 slave 的消息将会丢失。   
    为了提高可用性，可以采用同步复制的方式，master节点将会等到salve节点同步成功才会返回确认信息。   
    结合生产阶段与存储阶段，若需要严格保证消息不丢失，broker 需要采用如下配置：
    
    `
    ## master 节点配置
    flushDiskType = SYNC_FLUSH
    brokerRole=SYNC_MASTER
    ## slave 节点配置
    brokerRole=slave
    flushDiskType = SYNC_FLUSH
    `
    这个过程还需要生产者配合，判断返回状态是否为SendStatus.SEND_OK。若是其他状态，就需要考虑补偿重试。   
    
    - 消费阶段   
    消费者从Broker拉取信息，然后执行相关业务逻辑。一旦执行成功，将会返回ConsumeConcurrentlyStatus.CONSUME_SUCCESS 状态给 Broker。   
    如果Broker未收到确认信息，或收到的信息为其他状态，消费者下次还会拉取这条信息，进行重试。这样有效的避免了消费者消费过程中出现异常,或者消息在网络中丢失的情况。   
    ```java
    // 实例化消费者
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test_consumer");
    
    // 设置NameServer的地址
    consumer.setNamesrvAddr("namesrvAddr");
    
    // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
    consumer.subscribe("test_topic", "*");
    // 注册回调实现类来处理从broker拉取回来的消息
    consumer.registerMessageListener(new MessageListenerConcurrently() {
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
            // 执行业务逻辑
            // 标记该消息已经被成功消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    });
    // 启动消费者实例
    consumer.start();
    ```
  以上消费消息过程的，我们需要注意返回消息状态。只有当业务逻辑真正执行成功，我们才能返回 ConsumeConcurrentlyStatus.CONSUME_SUCCESS。
  否则我们需要返回 ConsumeConcurrentlyStatus.RECONSUME_LATER，稍后再重试。   
  
  * 重复消息问题   
  1，当系统的调用链路比较长的时候，比如系统A调用系统B，系统B再把消息发送到RocketMQ中，在系统A调用系统B的时候，如果系统B处理成功，
    但是迟迟没有将调用成功的结果返回给系统A的时候，系统A就会尝试重新发起请求给系统B，造成系统B重复处理，发起多条消息给RocketMQ造成重复消费。   
  2，在系统B发送消息给RocketMQ的时候，也有可能会发生和上面一样的问题，消息发送超时，结果系统B重试，导致RocketMQ接收到了重复的消息。   
  3，当RocketMQ成功接收到消息，并将消息交给消费者处理，如果消费者消费完成后还没来得及提交offset给RocketMQ，自己宕机或者重启了，
    那么RocketMQ没有接收到offset，就会认为消费失败了，会重发消息给消费者再次消费。   
  保证消息不被重复消费，需要通过**幂等性**(对于同一个系统，在同样条件下，一次请求和重复多次请求对资源的影响是一致的)来实现。
  可以在消费端根据业务ID判断该操作是否已经执行，如果已经执行过了就不再执行。举个例子，比如每条消息都会有一条唯一的消息ID，消费者接收到消息会存储消息日志，
  如果日志中存在相同ID的消息，就证明这条消息已经被处理过了。   
    
    