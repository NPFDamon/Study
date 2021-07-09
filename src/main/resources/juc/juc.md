## Lock
    在并发编程中，为了解决程序中多个进程或线程对资源的抢占问题，引入锁的概念。锁是多线程环境下的一种同步机制，对线程的访问资源权限做控制，实现并发策略。
+ **锁的分类**   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/lock.png)  
    + 偏向锁：一段代码一种被一个线程所访问，那么这个线程会自动获得锁，锁的这段代码一种被一个线程访问。   
    + 轻量级锁：当偏向锁被其他线程访问时升级为轻量级锁，其他线程循环判断是否获能获取到锁，成为自旋。  
    + 重量级锁：当自旋次数达到一定次数之后，进入阻塞状态，轻量级锁升级为重量级锁。    
    + 自旋锁：尝试获取锁的线程通过循环的方式获得锁。好处是减少线程上下文转换的消耗，缺点是占用CPU资源   
    + 公平锁：按照申请锁的顺序获得锁。   
    + 非公平锁：不按照申请锁的顺序，每次尝试获取锁时，所有的线程同时进行尝试，后申请的线程有可能比先申请的线程获得到锁。   
    + 可重入锁：又称递归锁，允许同一个线程多次获取同一把锁，既一个线程在方法外获取获取锁，那么再方法内自动获取该锁。   
    + 独享锁：又称互斥锁，排它锁，每次只能由一个线程持有。   
    + 共享锁：一次可被多个线程持有。   
    + 乐观锁/悲观锁： 乐观锁与悲观锁不是指具体的什么类型的锁，而是指看待并发同步的角度。
        悲观锁认为对于同一个数据的并发操作一定会发生修改，哪怕没有发生修改，也认为发生修改；对于一个数据的并发操作无论读写
        都会进行加锁操作，悲观的认为,不加锁的操作一定会发生错误。乐观锁则认为对一个数据的并发操作总是安全的，是不会发生数据的修改
        在更新操作时，会采用尝试更新，不断重新的方式更新。悲观锁适合写操作多的操作，乐观锁适合读操作多的操作。在Java中乐观锁常采用
        无锁编程的方式，使用CAS算法，典型的例子就是原子类，通过CAS自旋实现原子操作的更新。悲观锁就是利用各种锁。   
    + 分段锁：分段锁其实是一种锁的设计，并不是具体的一种锁。在JDK1.7之前的ConcurrentHashMap通过分段锁的形式来实现高效的并发操作。   
    + 锁消除：虚拟机即时编译器运行时，对一些代码要求同步，但是被检测到不可能存在共享数据竞争的锁进行消除。缩小成的主要判断依据来源于逃逸分析
    的数据支持，如果判断在一段代码中，堆上的所以数据都不会逃逸出去被其他线程所访问到，那就可以把它当做栈上的数据对待，认为他是私有的，同步加锁自然无法进行。   
    + 锁粗化：如果一系列操作对一个对象反复进行加锁和解锁，甚至加锁操作是在循环中操作的，即使没有线程竞争，频发的进行互斥同步操作也会导致不必要的性能损耗，
    如果虚拟机检测到有这样一串零碎的操作都对同一个对象进行加锁,那么会把锁的同步范围扩展（粗化）到整个操作的外部。
    
+ **Java中的锁**   
    + synchronized关键字   
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/synchronized.png)   
        对象头：   
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/header.png) 
        HotSpot虚拟机中，对象头在内存中的布局可分为三个区域：对象头（Header），实例数据（Instance Data)和对齐填充（Padding）   
        * mark-word：对象标记占4个字节,用于存储一些标记位，比如：哈希值，轻量级锁的标记位，偏向锁标记位，分代年龄等。
        在mark-word锁类型标记中，无锁，偏向锁，轻量锁，重量锁，以及GC标记，5种类中没法用2比特标记（2比特最终有4种组合00、01、10、11），所以无锁、偏向锁，前又占了一位偏向锁标记。
        最终：101为无锁、001为偏向锁。   
        * Klass Pointer：Class对象的指针类型，JDK1.8默认开启指针压缩，压缩后为4个字节，关闭指针压缩（-XX:-UseCompressedOops）后，长度为8字节。
        其指向的位置为对象对应的class对象（其对应的元数据对象）的内存地址。   
        * 对象实际数据：包括对象的所有成员变量,大小由各个成员变量决定。比如：byte占1个字节8比特位、int占4个字节32比特位。   
        * 对齐：：最后这段空间补全并非必须，仅仅为了起到占位符的作用。   
        Monitor对象：
        在HotSpot虚拟机中，monitor是由C++中ObjectMonitor实现。   
        synchronized的运行机制，就是JVM监测对象在不同的竞争状态时，会自动切换到适合的锁实现，这种切换就是锁的升级降级。   
        执行锁的过程：
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/synchronized_lock.png)    
        每个Java对象头中都包含Monitor对象（存储的指针的指向），synchronized也就是通过这一种方式获取锁，也就解释了为什么synchronized()括号里的任何对象都加了锁。
        同步方法时是通过ACC_SYNCHRONIZED标记符合指定该方法是一个同步方法，从而执行相关的同步调用。线程进入这个方法时，都会判断是否有此标识，然后开始竞争 Monitor 对象。   
        monitorenter，在判断拥有同步标识ACC_SYNCHRONIZED抢先进入此方法的线程会拥有Monitor的owner次数计数器加一。
        monitorexit，当执行完退出之后，计数器减一，归零后其他线程可获得。 
    + ReentrantLock   
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/reentrantLock_plus.png)      
        ReentrantLock是基于Lock实现的可重入锁，所有的Lock都是基于AQS实现的，AQS和Condition各自维护不同的对象，在使用Lock和Condition时其实就是两个队列的相互移动。
        它所提供的共享锁，互斥锁都是基于对state的操作。可重入性是因为实现了同步器Sync，在Sync的两个实现类中，包括了公平锁和非公平锁。Sync继承了AbstractQueuedSynchronizer。
    + AQS   
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/aqs.png)
        AQS 是 AbstractQueuedSynchronizer(抽象队列同步器：同步状态(state) + FIFO线程等待队列)的缩写，几乎所有的Lock都是基于AQS实现，其底层采用了大量的CAS操作提供乐观锁服务，在冲突的时候进行自旋进行重试，以实现轻量和高效的获取锁。
        独享锁中state=0代表为获取锁，state=1代表获取到锁。   
        共享锁中state的值代表锁的数量。   
        可重入锁的state代表重入的次数。   
        读写锁比较特殊，因 state 是 int 类型的变量，为 32 位，所以采取了中间切割的方式，高 16 位标识读锁的数量 ，低 16 位标识写锁的数量。    
        AQS的实现为CLH:   
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/io/clh.png) 
        CLH是一种基于链表，可扩展，高性能，公平的自旋锁。代码中相当于虚构了一个链表结构，由AtomicReference的getAndSet进行链接，getAndSet获取到当前元素，设置新元素。     
        ```java
            public class CLHLock implements Lock {
                private final ThreadLocal<CLHLock.Node> prev;
                private final ThreadLocal<CLHLock.Node> node;
                private final AtomicReference<CLHLock.Node> tail = new AtomicReference<>(new CLHLock.Node());
            
                private static class Node {
                    private volatile boolean locked;
                }
            
                public CLHLock() {
                    this.prev = ThreadLocal.withInitial(() -> null);
                    this.node = ThreadLocal.withInitial(CLHLock.Node::new);
                }
            
                @Override
                public void lock() {
                    final Node node = this.node.get();
                    node.locked = true;
                    Node pred_node = this.tail.getAndSet(node);
                    this.prev.set(pred_node);
                    // 自旋
                    while (pred_node.locked);
                }
            
                @Override
                public void unlock() {
                    final Node node = this.node.get();
                    node.locked = false;
                    this.node.set(this.prev.get());
                }
            
            }
        ```
        * lock()
        1.通过 this.node.get() 获取当前节点，并设置 locked 为 true。
        2.接着调用 this.tail.getAndSet(node)，获取当前尾部节点 pred_node，同时把新加入的节点设置成尾部节点。
        3.之后就是把 this.prev 设置为之前的尾部节点，也就相当于链路的指向。
        4.最后就是自旋 while (pred_node.locked)，直至程序释放。
        * unlock()
        1.释放锁的过程就是拆链，把释放锁的节点设置为false node.locked = false。
        2.之后最重要的是把当前节点设置为上一个节点，这样就相当于把自己的节点拆下来了，等着垃圾回收。   
        AQS同步类：
        ```java
        public class SyncLock {
        
            private final Sync sync;
        
            public SyncLock() {
                sync = new Sync();
            }
        
            public void lock() {
                sync.acquire(1);
            }
        
            public void unlock() {
                sync.release(1);
            }
        
            private static class Sync extends AbstractQueuedSynchronizer {
                @Override
                protected boolean tryAcquire(int arg) {
                    return compareAndSetState(0, 1);
                }
        
                @Override
                protected boolean tryRelease(int arg) {
                    setState(0);
                    return true;
                }
        
                // 该线程是否正在独占资源，只有用到 Condition 才需要去实现
                @Override
                protected boolean isHeldExclusively() {
                    return getState() == 1;
                }
            }
        
        }
        ```
      该实现为ReentrantLock的简版,主要包括：
      1.Sync继承AbstractQueuedSynchronizer并实现tryAcquire，tryRelease和isHeldExclusively方法，这三个方法必须重新，否则会报出UnsupportedOperationException异常。   
      2.主要使用AQS提供的CAS方法。以预期的值0，写入更新值1，写入成功则获取锁成功。这个过程就是对state使用Unsafe本地方法，传递偏移量stateOffset等参数进行交互操作，Unsafe.compareAndSwapInt(this, stateOffset, expect, update)、
      3.最后提供lock(),unlock()方法，实际的类中会实现 Lock 接口中的相应方法，这里为了简化直接自定义这样两个方法。
      CAS是compareAndSet的缩写，应用为对一个值进行更新时，会传入两个值，一个是预期值，一个是需要更新的值，当预期值和被更新的值相同时，进行更新操作。
      CAS操作使用到了Unsafe类，调用了本地方法Unsafe.compareAndSwapInt 比较交换方法。（CAS有可能出现ABA问题）。
      

    