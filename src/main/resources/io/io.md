## IO
+ **BIO**
    Blocking IO 同步阻塞IO。数据的读取和写入必须在一个线程内等待其完成。
    例子：假设一个烧开水的场景，有一排水壶在烧开水，BIO的工作模式就是， 叫一个线程停留在一个水壶那，直到这个水壶烧开，才去处理下一个水壶。
    但是实际上线程在等待水壶烧开的时间段什么都没有做.

+ **NIO**
    No-Blocking IO(New IO)。IO多路复用的基础。NIO多路复用：IO指我们的网络IO，多路指多个TCP连接(或者多个Channel)，复用是指用一个或少量线程。
    串起来就是很多个网络请求用一个或者少量线程来处理这些连接。
    NIO由原来的阻塞读写变成了单线程轮询模式。除了事件轮询是线程阻塞的(没有可干的事情必须要阻塞）其余的I/O操作都是
    CPU操作，没有必要开启多线程，如果还拿烧开水来说，NIO的做法是叫一个线程不断的轮询每个水壶的状态，
    看看是否有水壶的状态发生了改变，从而进行下一步的操作。
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/nio)   
    NIO一些基本概念：   
    Buffer：缓存区。Buffer用于和NIO通道进行交互。数据从通道进入缓冲器，从缓冲器写入通道，他的主要作用是和Channel进行交互。   
    Channel：通道。Channel是一个通道可以通过它进行数据的读取和写入，通道是双向的，通道可以用于读，写或同时读写。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/channel.png)   
    Selector：选择器。Channel向Selector注册自己。Selector会不断轮询注册在它上面的Channel,如果有新的连接读写事件的时候就会被轮询出来，
    一个Selector可以注册多个Channel，只需要一个线程负责Selector的轮询，就可以支持成千上万的连接。
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/selector.png)    
+ **AIO**
    Asynchronous IO。异步非阻塞I/O。又称Java NIO2。AIO相对应NIO的区别在于，NIO需要使用者不停的轮询IO对象，来确定底层是否有数据可以读取，
    而AIO则是在数据准备好之后，才会通知数据使用者，这样使用者就不用不停轮询。在Unix系统下，采用了epoll IO模型，而windows便是使用了IOCP模型
    
+ **Reactor模式**
``The reactor design pattern is an event handling pattern for handling service requests delivered concurrently to a service handler by one or more inputs. 
  The service handler then demultiplexes the incoming requests and dispatches them synchronously to the associated request handlers``
  Reactor模式是一种事件驱动(回调)机制:事件不主动处理API,而是相应事情发生,Reactor主动调用应用程序注册的接口进行处理。
  核心为:将所有IO请求注册到一个IO多路复用器上，同时线程或进程阻塞在多路复用器上.一旦有IO事件到来或者准备就绪,多路复用器返回或调用相关事件处理函数。
  `>` ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/reactor.jpeg)   https://zhuanlan.zhihu.com/p/357433452
  + Reactor单线程模型（Single threaded version）
      ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/basic-reactor-design.jpeg)    
      多路复用器(Reactor): 负责监听注册的IO事件（对应图中Reactor）。
      事件分离器（Event Demultiplexer）：将多路复用器中返回的就绪事件分到对应的处理函数中。（对应图中dispatch）。
      事件处理器（Event Handler）：负责处理对应事件的处理函数。（对应上图read,decode,compute,encode,send等）。
      Reactor通过epol监听事件，收到时间后通过dispatch进行转发，Handel完成相应的read,decode,compute,encode,send等操作。
      Reactor只试用于小容量应用场景。对于高并发，高负载并不试用。
      1.无法充分利用CPU（一个线程处理），无法满足大数据量。
      2.高并发时Reactor线程过载之后会变得非常慢，会导致大量客户端连接超时。
      3.一旦Reactor线程意外中断或者进入死循环，会导致整个系统通信模块不可用。
  + Reactor多线程模型
      ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/worker-thread-pools-reactor-design.jpeg.jpeg)  
      相比较单线程模型，多线程模型获取到IO的读写事件之后，对应的业务逻辑处理由线程池来处理。handle收到响应后通过send将结果发送给客户端。这样可以降低
      Reactor的性能开销，使其专注于处理事件分发操作，从而提升吐吞量。
      主要流程为：
      Reactor通过epoll监听事件，通过dispatch进行转发。
      如果是建立连接事件由acceptor进行处理，然后创建一个handle进行连接完成后续各种事件。
      如果不是连接事件，Reactor会分发调用连接对应的handle进行处理。
      Handler只负责响应事件，不做具体业务处理，通过Read读取数据后，会分发给后面的Worker线程池进行业务处理。
      Worker线程池会分配独立的线程完成真正的业务处理，如何将响应结果发给Handler进行处理。
      Handler收到响应结果后通过send将响应结果返回给Client。
      但是这个模型存在的问题：
      多线程数据共享和访问比较复杂。如果子线程完成业务处理后，把结果传递给主线程Reactor进行发送，就会涉及共享数据的互斥和保护机制。
      Reactor承担所有事件的监听和响应，只在主线程中运行，可能会存在性能问题。例如并发百万客户端连接，或者服务端需要对客户端握手进行安全认证，但是认证本身非常损耗性能。
  + 主从Reactor多线程模型
      ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/multiple-reactors-design.jpeg)  
      将Reactor分成两部分，mainReactor负责监听server socket,用来处理网络IO连接建立操作，并将建立的socketChannel指定，注册给subReactor
      subReactor主要做和建立起来的socket做数据交互和事件业务处理。通常subReactor的个数可以和CPU核心数相同。
      Nginx、Swoole、Memcached和Netty都是采用这种实现。
      主要流程为：
      从主线程中随机选择一个Reactor作为acceptor线程，用于绑定监听端口，接收客户端连接。
      acceptor线程接收客户端连接请求之后,创建新的SocketChannel,将其注册到主线程池的其他Reactor线程上，由其负责接入认证、IP黑白名单过滤、握手等操作。
      上述步骤完成之后，业务链路建立完成，将SocketChannel从主线程池的多路复用器Reactor线程上摘除，重新注册到subReactor线程上，并创建一个handle处理各种连接事件。
      当有新的事件发生时subReactor会调用handle进行事件处理。
      handle通过read读取数据后，会分发给后面的work线程池进行业务处理。
      work线程池会分配对应的线程进行相关的业务处理，并把处理结果返回给handle进行处理。
      handle收到返回结果之后，通过send返回给客户端。
  总结：
    响应快，不必为单个同步时间阻塞，虽然Reactor本身依然是同步的；
    编程相对简单，可以最大程度的避免复杂的多线程及同步问题，并且避免了多线程/进程的切换开销；
    可扩展性，可以方便地通过增加Reactor实例个数来充分利用CPU资源；
    可复用性，Reactor模型本身与具体事件处理逻辑无关，具有很高的复用性。
+ **Proactor模式**
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/Proactor.png)  
  Procator Initiator负责创建Procator和Handler，并将Procator和Handler都通过Asynchronous operation processor注册到内核。
  Asynchronous operation processor负责处理注册请求，并完成IO操作。完成IO操作后会通知procator。
  procator根据不同的事件类型回调不同的handler进行业务处理。handler完成业务处理，handler也可以注册新的handler到内核进程。
  
  
+ **Netty线程模型**
    ```java
    public class Server {
        public static void main(String[] args) throws Exception {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childAttr(AttributeKey.newInstance("childAttr"), "childAttrValue")
                        .handler(new ServerHandler())
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
                            }
                        });
                ChannelFuture f = b.bind(8888).sync();
                f.channel().closeFuture().sync();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }
    ```
    .Netty定义了两个EventLoopGroup，其中bossGroup对应的是主线程池(mainReactor)，主要负责socket的监听和注册。具体的业务工作由workerGroup进行。
    .客户端和服务端进行链接完成之后，NIO回在两者进行建立Channel，所以启动类里调用channel是为了确定建立哪个类型的Channel，这里是NioServerSocketChannel。
    .启动类还调用了handler()和childHandler(),这俩个handle是一个处理类的概念,负责处理链接后的一个个通道的相应处理，其中handle()处理主线程对应的处理类,childHandler()对应的是子线程对应的处理类。
    .执行ServerBootstrap的bind方法进行绑定端口的同时也执行了sync()方法进行同步阻塞调用。
    .关闭通道采用Channel的closeFuture()方法关闭。
    .最终优雅地关闭两个线程组，执行shutdownGracefully()方法完成关闭线程组。
    Boss线程池的作用：
    .接收客户端连接，初始化Channel参数。
    .将链路状态变更时间通知给ChannelPipeline。
    Worker线程池作用：
    .异步读取发送端的数据报文，发送读事件到ChannelPipeline。
    .异步发送消息到通讯对端，调用ChannelPipeline的消息发送接口。
    .执行系统调用task
    .执行定时任务task
    通过配置boss和worker线程池的线程个数以及是否共享线程池等方式，Netty的线程模型可以在以上三种Reactor模型之间进行切换。
    