##IO
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