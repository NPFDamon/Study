# Dubbo
    Dubbo是一款高性能的RPC框架。它实现了面向接口代理的RPC调用，服务注册和发现，负载均衡，容错，扩展性等功能。
    
*  **RPC**
    * RMI介绍：   
    RMI(Remote Method Invocation)远程方法调用，也就是RPC的实现方式。在JDK 1.2的时候，引入到Java体系的。当应用比较小，性能要求不高的情况下，
    使用RMI还是挺方便快捷的。下面先看看RMI的调用流程。    
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/dubbo/rmi.png)   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/dubbo/rmi-req.png)       
    stub(桩)：stub实际就是远程过程在客户端上的一个代理proxy。当我们的客户端代码调用API接口提供的方法的时候，RMI生成的stub代码块会将请求数据序列化，
    交给远程服务端处理，然后将结果反序列化之后返回给客户端。这些处理对客户端来说是无感知的。   
    remote:底层网络处理，RMI对用户屏蔽了这层细节。stub通过remote来和远程服务端进行链接通信。   
    skeleton(骨架)：和stub相似，skeleton则是服务端生产的一个代理proxy。当客户端通过stub发送请求到服务端，则交给skeleton处理，其会根据指定的方法
    服务来反序列化请求，然后调用具体的请求，然后将结果返回给客户端。    
    registry(服务发现)：借助JNDI发布并调用了rmi服务。实际上，JNDI是一个注册表，服务端将服务对象放入注册表中，客户端将从服务表中获取对象。rmi服务，
    在服务端实现之后需要注册到rmi server上，然后客户端从指定的rmi地址上lookup服务，调用该服务对应的方法即可完成远程方法调用。registry是个很重要的功能，
    当服务端开发完服务之后，要对外暴露，如果没有服务注册，则客户端是无从调用的，即使服务端的服务就在那里。    
    * RPC架构：   
    远程过程调用RPC就是本地动态代理隐藏通讯细节，通过组件序列化请求，走网络到服务端，执行真正的代码，然后将结果返回给客户端，反序列化数据给调用方法的过程。
    RPC主要使用动态代理来实现生，生产clint stub和server stub的时候需要用到动态代理。**利用动态代理，创建代理类去实现RMI调用相关细节，把接口(方法)
    作为参数传递，而不是绑定方法。** 这样，需要远程方法调用的接口(方法)只是一个参数，全部细节都可以在代理类中实现，并且一个代理类可以处理很多方法，**从而
    把远程访问代码和本地代码解耦**，便于项目维护和扩展。        
    具体的RPC：   
    1，首先如代码所示，我们只需要接口，不需要知道任何实现类的信息就可以创建一个接口的代理实现。就和生成RemoteService remoteService一样。   
    2，因为所有的接口都在指定目录下，我们可以扫描该目录下的所有接口，批量生成所有接口的实例，并把生成的bean都放入spring中管理。这样，用户就可以用autowair注入所有实现。而实际上我们的代理proxy就成了所有接口的实现。   
    3，用户调用任何接口时，都调用了我们生成的bean实现。其实都进入了我们相同的handler实现，实现中我们可以知道用户想要调用的完整方法名称(从它的目录路径可以分析出目标应用名)、参数。然后序列化后去远端调用并返回结果即可。    
    
    RPC序列化协议：   
    * XML是一种常用的序列化和反序列化协议，具有跨机器，跨语言等优点。狭义web service就是基于SOAP消息传递协议（一个基于XML的可扩展消息信封格式）来进行数据交换的。   
    * Hessian是一个动态类型，简洁的，可移植到各个语言的二进制序列化协议。采用简单的结构化标记、采用定长的字节记录值、采用引用取代重复遇到的对象。   
    * SON（Javascript Object Notation）起源于弱类型语言Javascript， 是采用"Attribute－value"的方式来描述对象协议。与XML相比，其协议比较简单，解析速度比较快。   
    * Protocol Buffers 是google提供的一个开源序列化框架，是一种轻便高效的结构化数据存储格式，可以用于结构化数据串行化，或者说序列化。
    它很适合做数据存储或 RPC 数据交换格式。可用于通讯协议、数据存储等领域的语言无关、平台无关、可扩展的序列化结构数据格式。同 XML 相比，
     Protobuf 的主要优点在于性能高。它以高效的二进制方式存储，比 XML 小 3 到 10 倍，快 20 到 100 倍。    
    * Thrift 既是rpc框架，同时也具有自己内部定义的传输协议规范(TProtocol)和传输数据标准(TTransports)，通过IDL脚本对传输数据的数据结构(struct) 
    和传输数据的业务逻辑(service)根据不同的运行环境快速的构建相应的代码，并且通过自己内部的序列化机制对传输的数据进行简化和压缩提高高并发、 大型系统中数据交互的成本。    
    
+  **Dubbo**   
    Dubbo组件：   
    Provider:服务提供者。   
    Consumer：服务消费者。   
    Container：服务运行容器。   
    Monitor：监控中心和访问次数统计。   
    Registry: 服务注册与发现中心。      
    Dubbo运行流程：   
    1,服务容器负责启动，加载，运行服务提供者。   
    2，服务提供者Provider在启动时会向注册中心Registry注册自己提供的服务。   
    3，服务消费者Consumer在启动时，向注册中心订阅自己所需的服务。   
    4，注册中心Registry返回服务提供者的地址列表给消费者，如果有变更，Registry以**长连接的方式**推送变更数据给消费者。   
    5，服务消费者Consumer，从注册中心地址列表中，基于软负载均衡算法，选取一台提供者进行调用，如果调用失败，则选另一台调用。   
    6，服务提供者Provider和服务消费者Consumer，在内存中累计调用测试和调用时间，定时每分钟发送一次统计数据到监控中心Monitor。   
    
    Dubbo大致可以分为三层：   
    1，业务层。   
    2，RPC层。   
    3，Remoting层。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/dubbo/dubbo-all.png)     
    三层架构中包含了Dubbo的核心组件。其主要功能为：   
    Service:业务层，业务代码的实现，应用开发工作者就是在这一层。   
    Config:配置层，用来初始化配置信息，用来管理Dubbo的配置。以ServiceConfig, ReferenceConfig为中心，可以直接初始化配置类，也可以通过spring配置生成配置类。      
    Proxy：代理层，Dubbo中生产者(provider)和消费者(consumer)都会生产Proxy，它用来调用远程接口，就像调用本地接口一样。使得调用透明。生产客户端stub和服务端Skeleton
    以ServiceProxy 为中心，扩展接口为ProxyFactory。      
    Registry:注册层，负责框架的服务注册和发现。   
    Cluster:集群容错层，负责远程调用的容错策略，负载均衡策略以及路由策略。以Invoker为中心，扩展接口为 Cluster, Directory, Router, LoadBalance。      
    Monitor:监控层，负责监控调用次数和调用时间。   
    Protocol:远程调用层，封装调用的具体过程。   
    Exchange:信息交换层，简历Request-Response模型，封装请求响应模式。例如转换同步请求为异步请求。    
    Transport：网络传输层，Dubbo将网络传输封装成统一的接口。   
    Serialize:序列化层，负责网络传输的序列化/反序列化。   
    
+ **Dubbo工作流**   
    Dubbo框架是用来处理分布式系统中，服务发现与注册以及调用问题的，并且管理调用过程。   
     ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/dubbo/dubbo.png)     
     工作流涉及到服务提供者(provider)，注册中心(Registration)，网络和服务消费者：    
     - 服务提供者在启动的时候会读取一些配置将服务实例化。   
     - Proxy封装服务调用接口，方便调用者调用。客户端获取Proxy时，可以像调用本地服务一样调用远程服务。   
     - Proxy在封装时，需要调用Protocol定义协议格式，如：Dubbo Protocol。   
     - 将Proxy封装成Invoker，它是真实服务调用的示例。    
     - 将Invoker转化成Exporter，Exporter只是把Invoker包装了一层，是为了在注册中心暴露自己，方便消费者使用。    
     - 将封装好的Exporter注册到注册中心。   
     - 服务消费者建立好实例，会到服务注册中心订阅服务提供者的元数据。元数据包括服务IP和端口以及调用方式(Proxy)。    
     - 消费者会通过Proxy进行调用。通过服务提供方包装过程可以知道，Proxy实际包装了Invoker实体，因此需要使用Invoker进行调用。    
     - 在调用之前通过Directory获取服务提供者的Invoker列表。在分布式服务中可能出现一个服务在不同的节点上。   
     - 通过路由规则了解，服务需要从哪些节点获取。   
     - Invoker在调用中通过Cluster 进行容错，如果遇到失败则进行重试。   
     - 调用中，由于一个服务可能会分布到不同的节点，就要通过Cluster LoadBalance 来实现负载均衡。   
     - Invoker调用之后还要经过一个过滤器，用来处理上下文，限流和计数工作。   
     - 生产过滤后的Invoker。   
     - 用Client进行数据传输。   
     - Codec会根据Protocol定义的协议，进行协议的构造。   
     - 构造完成的数据通过序列化Serialization传输给服务提供者。    
     - Request已经到达了服务提供者，它会被分配到线程池中进行处理。   
     - Server拿到请求后查找对应的Exporter(包含有Invoker)。   
     - 由于Exporter也会被过滤器包裹。      
     - 通过过滤器获得Invoker。   
     - 最后，对服务提供者实体进行调用。   
    服务暴露：
     ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/dubbo/dubbo-provider.png)    
     服务暴露的整体机制：   
     ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/dubbo/dubbo-provider-1.png)     
     在服务提供者初始化的时候，会通过 Config 组件中的 ServiceConfig 读取服务的配置信息。   
     这个配置信息有三种形式，分别是 XML 文件，注解（Annoation）和属性文件（Properties 和 yaml）。   
     在读取配置文件生成服务实体以后，会通过 ProxyFactory 将 Proxy 转换成 Invoker。   
     此时，Invoker 会被定义 Protocol，之后会被包装成 Exporter。最后，Exporter 会发送到注册中心，作为服务的注册信息。上述流程主要通过 ServiceConfig 中的 doExport 完成。   
    注册中心：  
    动态载入服务。服务提供者通过注册中心，把自己暴露给消费者，无须消费者逐个更新配置文件。       
    动态发现服务。消费者动态感知新的配置，路由规则和新的服务提供者。   
    参数动态调整。支持参数的动态调整，新参数自动更新到所有服务节点。   
    服务统一配置。统一连接到注册中心的服务配置。   
    Dubbo注册调用流程：   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/dubbo/reg-req.png)     
    服务提供者(provider)启动时，会向注册中心写入自己的元数据信息(调用方式)。   
    服务消费者(consumer)启动时，也会向注册中心写入自己的元数据信息，并且订阅服务提供者，路由和元数据信息。   
    服务治理中心(duubo-admin)启动时，会同时订阅所有的消费者，提供者，路由和配置元数据信息。   
    当提供者离开或者有新提供者加入时，注册中心发现变化会通知消费者和服务治理中心。   
    Dubbo负载均衡策略：   
    Random LoadBalance：随机，按照权重设置随机概率做负载均衡。   
    RoundRobinLoadBalance：轮询，按照公约后的比例设置轮询比例。   
    LeastActiveLoadBalance：按照活跃数调用，活跃度差的被调用的次数多，活跃度相同的进行随机调用。   
    ConsistentHashLoadBalance：一致性hash，相同参数的请求总是发送给同一个提供者。   
    > https://www.modb.pro/db/12097
    
    Dubbo通讯协议:   
    dubbo协议：单一长连接和NIO异步通讯，适合大并发量小数据量的服务调用，已经消费者远大于服务提供者。传输协议采用tcp，异步Hessian序列化。   
    RMI协议： 采用JDK标准的RMI协议，传输参数和返回参数都需要实现Serializable 接口，使用标准Java序列化机制，采用阻塞式断连接，传输数据包大小混合，消费者和提供者个数差不多
    可传文件，传输协议为tcp。多个短链接tcp协议传输，同步传输，适合常规的远程服务调用和RMI交互操作。在依赖低版本的 Common-Collections 包，Java 序列化存在安全漏洞。   
    WebService：基于 WebService 的远程调用协议，集成 CXF 实现，提供和原生 WebService 的互操作。多个短连接，基于 HTTP 传输，同步传输，适用系统集成和跨语言调用。   
    HTTP： 基于 Http 表单提交的远程调用协议，使用 Spring 的 HttpInvoke 实现。多个短连接，传输协议 HTTP，传入参数大小混合，提供者个数多于消费者，需要给应用程序和浏览器 JS 调用。   
    Hessian：集成 Hessian 服务，基于 HTTP 通讯，采用 Servlet 暴露服务，Dubbo 内嵌 Jetty 作为服务器时默认实现，提供与 Hession 服务互操作。
    多个短连接，同步 HTTP 传输，Hessian 序列化，传入参数较大，提供者大于消费者，提供者压力较大，可传文件。   
    Memcache：基于 Memcache实现的 RPC 协议。   
    Dubbo序列化方式：   
    dubbo序列化：阿里尚未开发成熟的高效java序列化实现，阿里不建议在生产环境使用它。   
    hessian2序列化：hessian是一种跨语言的高效的二进制序列化方式。但是这里实际不是原生的hessian2序列化，而是阿里修改过的hessian lite，是**dubbo默认的序列化方式**。   
    json序列化：目前有两种实现，一种是采用的阿里的fastjson库，另一种是采用dubbo中自己实现的简单json库，但其实现都不是特别成熟，而且json这种文本序列化性能一般不如上面两种二进制序列化。   
    java序列化：主要是采用JDK自带的Java序列化实现，性能很不理想。   
    
    Dubbo超时策略：   
    1，服务端提供者设置超时时间，在Dubbo的文档中，推荐如果能在服务端多类端多配置就尽量多配置，因为服务提供者比消费者更清楚自己的消费特性。   
    2，服务消费端设置超时时间，如果服务消费者端设置了超时时间，以消费者端为主，即优先级更高。因为服务调用方设置超时时间控制性更灵活。如果消费方超时，服务端线程不会定制，会产生警告。   
    
    Dubbo在调用不成功时，默认会重试两次。    
    
    Dubbo服务调用**默认**是阻塞的，没有返回值时，可以异步调用。Dubbo 是基于 NIO 的非阻塞实现并行调用，客户端不需要启动多线程即可完成并行调用多个远程服务，相对多线程开销较小，异步调用会返回一个 Future 对象。   
       
       
 
    