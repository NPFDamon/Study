## JMM
    JMM为Java Memory Model，即Java内存模型。用来屏蔽各种硬件个操作系统的内存访问差异,以实现让Java在各平台下能够达到一致的内存访问效果。

+ 主内存与工作内存   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/juc/jmm.png)  
    * 主内存：主要存储的是Java实例对象，所有线程创建的实例对象都放在主内存中，不管实例对象是成员变量还是方法中的本地变量(也称局部变量)，也包括共享的类信息，
    常量，静态变量。
    * 工作内存：主要存储当前方法的所有本地变量信息(工作内存中存储这主内存中的变量副本的拷贝)。每个线程只能访问自己的工作内存，线程中的本地变量对其他线程是不可见的。
+ 内存间的交互   
    JMM只是一种抽象的概念，一组规范，并不实际存在。对于真正的计算机硬件来说，计算机内存只有寄存器、缓存内存、主内存的概念。   
    不管是工作内存的数据还是主内存的数据，对于计算机硬件来说都会存储在计算机主内存中，当然也有可能存储到CPU缓存或者寄存器中，因此总体上来说，Java内存模型和计算机硬件内存架构是一个相互交叉的关系；   
    * lock(锁定):_作用于**主内存**的变量_。把一个变量标识为一个线程的独占状态。   
    * unlock(解锁):_作用于**主内存**的变量_。把一个处于锁定状态的变量释放出来。释放出来的变量才能被其他线程锁定。   
    * read(读取):_作用于**主内存**的变量_。把一个变得的值从主内存传输到线程的工作内存，以便后续的load动作使用。   
    * load(载入):_作用于**工作内存**的变量_。把read操作从主内存中获取到的值放入到工作内存的变量中。   
    * use(使用):_作用于**工作内存**的变量_。把工作内存中一个变量的值传递给执行引擎，每当虚拟机遇到一个需要使用到变量的值的字节编码指令时将执行这个操作。   
    * assign(赋值):_作用于**工作内存**的变量_。把一个从执行引擎收到的值赋值给工作变量，每当虚拟机遇到一个给变量赋值的字节码编码指令时执行这个操作。   
    * store(存储):_作用于**工作内存**的变量_。把工作内存中一个变量的值传送给主内存，以便后续write操作使用。   
    * write(写入):_作用于**主内存**的变量_。把store操作从工作内存传送过来的变量的值放入到主内存的变量中。   
    如果把一个变量从主内存复制到工作内存就要顺序的执行read、load操作，同理如果把一个变量从工作内存复制到主内存就要顺序的执行store、write操作。   
    注意：把一个变量从主内存中复制到工作内存中就需要执行read,load操作，将工作内存同步到主内存中就需要执行store,write操作。Java内存模型只要求上述两个操作必须按顺序执行，
    而没有保证必须是连续执行的。也就是说read与load之间、store与write之间是可插入其他指令的，如对主内存中的变量a、b进行访问时，一种可能出现的顺序是read a、read b、load b、load a。
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/juc/jmm_opera.png)  
    + 同步规则分析   
        * 不允许read和load、store和write操作之一单独出现，即不允许一个变量从主内存读取了但工作内存不接受，或者从工作内存发起回写了但主内存不接受的情况出现。   
        * 不允许一个线程丢弃它的最近的assign操作，即变量在工作内存中改变了之后必须把该变化同步回主内存。   
        * 不允许一个线程无原因地（没有发生过任何assign操作）把数据从线程的工作内存同步回主内存。   
        * 一个新的变量只能在主内存中“诞生”，不允许在工作内存中直接使用一个未被初始化（load或assign）的变量。   
        * 一个变量在同一个时刻只允许一条线程对其进行lock操作，但lock操作可以被同一条线程重复执行多次，多次执行lock后，只有执行相同次数的unlock操作，变量才会被解锁。   
        * 如果对一个变量执行lock操作，将会清空工作内存中此变量的值，在执行引擎使用这个变量前，需要重新执行load或assign操作初始化变量的值。   
        * 如果一个变量事先没有被lock操作锁定，则不允许对它执行unlock操作；也不允许去unlock一个被其他线程锁定的变量。
        * 对一个变量执行unlock操作之前，必须把此变量同步回主内存中（执行store和write操作）   
+ 原子性、可见性与有序性   
    多线程并发下存在：原子性、可见性与有序性三种问题。   
    * 原子性：原子性是一个操作是不可中断的，即一个操作或多个操作要么全部执行并且执行的过程不会被任何外界因素打断，要不就都不执行。由原子性变量操作read,load,use,assign,store,write，
    可以大致认为基本数据类型的访问读写具备原子性（例外就是long和double的非原子性协定）。除了JVM自身提供的对基本数据类型的读写操作是原子性外，可以通过synchronized和Lock实现原子性。
    因为synchronized和Lock能保证同一时刻只有一个线程访问代码块。   
    * 可见性：当一个线程修改了共享变量的值，其他线程能立即获得这个修改。   
    * 有序性：Java程序中天然有序，如果再本线程内观察，所有的指令都是有序的，如果观察另一个线程，所有的操作都是无序的。前半句是指线程内表现为串行，后半句指指令重排序现象和工作内存与主内存同步延迟现象。   
        指令重排序：为了是处理器内部的运算单元被充分利用，处理器可能会对输入的代码进行乱序执行优化，处理器会在处理后将乱序结果重组，并保证这一结果和顺序执行结果是一致的，但是这个过程
        并不能保证语句的计算先后顺序和输入代码的顺序是一致的。Java中指令重排序有两次。第一次发生在字节码编译成机器码的阶段，第二次发生在CPU执行的时候，也会进行适当的指令重排序。     
+ Happens-Before原则   
    如果存在Happens-Before(a,b)，那么操作a及a之前在内存上面所做的操作（如赋值操作等）都对操作b可见，即操作a影响了操作b。
    如果一个操作的结果需要对另一个操作可见，那么这两个操作之间必须存在 Happens-before 关系。也就是说，Happens-before 的前后两个操作不会被重排序且后者对前者的内存可见。
    值得注意的是：这里说的两个操作既可以是在一个线程之内，也可以是在不同线程之间。还有就是这两个操作并不是说一定要一前一后执行，Happens-before 只要求第一个操作的结果对第二个操作可见，并且第一个操作排在第二个操作之前就可以。
    简单点来说就是：Happens-before 就是可见性规则；什么情况下对共享变量的写，可以对共享变量的读是可见的。
    happens-before 规则：   
    * 程序次序法则：线程中的每个动作A都Happens-Before该线程中的没一个动作B。其中，在程序中，所有的动作B都能出现在A之后。   
    * 监视器锁法则：对一个监视器锁的解锁 happens-before于每一个后续对同一监视器锁的加锁。   
    * volatile变量法则：对volatile域的写入操作happens-before于每一个后续对同一个域的读写操作。   
    * 线程启动法则：在一个线程里，对Thread.start的调用会happens-before于每个启动线程的动作。   
    * 线程终结法则：线程中的任何动作都happens-before于其他线程检测到这个线程已经终结、或者从Thread.join调用中成功返回，或Thread.isAlive返回false。   
    * 中断法则：一个线程调用另一个线程的interrupt happens-before于被中断的线程发现中断。   
    * 终结法则：一个对象的构造函数的结束happens-before于这个对象finalizer的开始。   
    * 传递性：如果A happens-before于B，且B happens-before于C，则A happens-before于C   
+ volatile 关键字   
    * 保证数据的可见性。被volatile关键字修饰的变量对所有的线程都是可见的。被volatile关键字修饰的变量在修改时，内存操作上会强制刷新到主内存。   
        被volatile关键字修饰后，在程序被反编译成汇编指令后会增加lock操作。   
        lock相对应一个内存屏障，指令有以下作用：    
            第一将本处理器的缓存写入内存。   
            第二重排序是不能把后面的指令排序到内存屏障位置之前。   
            第三如果是写入动作会导致其他处理器的结果无效。   
    * 指令重排序。Java编译器，运行时和处理器都会保证单线程下的as-if-serial语义。as-if-serial语义的意思是所有的动作都可以为了优化而被重排序，但必须保证重排序
        后的结果和程序代码本身的结果是一致的。Java使用内存屏障来保证紧张指令的重排序。   
        内存屏障可以分为：   
        * LoadLoad屏障：对于这样的语句Load1; LoadLoad; Load2，在Load2及后续读取操作要读取的数据被访问前，保证Load1要读取的数据被读取完毕。   
        * StoreStore屏障：对于这样的语句Store1; StoreStore; Store2，在Store2及后续写入操作执行前，保证Store1的写入操作对其它处理器可见。   
        * LoadStore屏障：对于这样的语句Load1; LoadStore; Store2，在Store2及后续写入操作被刷出前，保证Load1要读取的数据被读取完毕。   
        * StoreLoad屏障：对于这样的语句Store1; StoreLoad; Load2，在Load2及后续所有读取操作执行前，保证Store1的写入对所有处理器可见。它的开销是四种屏障中最大的。在大多数处理器的实现中，这个屏障是个万能屏障，兼具其它三种内存屏障的功能。   
    * volatile保证指令重排序是：   
        * 在每个volatile写操作的前面插入一个StoreStore屏障。   
        * 在每个volatile写操作的后面插入一个StoreLoad屏障。   
        * 在每个volatile读操作的后面插入一个LoadLoad屏障。   
        * 在每个volatile读操作的后面插入一个LoadStore屏障。    
   * 在汇编角度，CPU是怎么识别到不同的内存屏障：   
        * sfence：实现Store Barrior 会将store buffer中缓存的修改刷入L1 cache中，使得其他cpu核可以观察到这些修改，而且之后的写操作不会被调度到之前，即sfence之前的写操作一定在sfence完成且全局可见。   
        * lfence：实现Load Barrior 会将invalidate queue失效，强制读取入L1 cache中，而且lfence之后的读操作不会被调度到之前，即lfence之前的读操作一定在lfence完成（并未规定全局可见性）。   
        * mfence：实现Full Barrior 同时刷新store buffer和invalidate queue，保证了mfence前后的读写操作的顺序，同时要求mfence之后写操作结果全局可见之前，mfence之前写操作结果全局可见。   
        * lock：用来修饰当前指令操作的内存只能由当前CPU使用，若指令不操作内存仍然由用，因为这个修饰会让指令操作本身原子化，而且自带Full Barrior效果。   
    
    JDK1.8之后Java的Unsafe类中增加了手动加入内存屏障的方法:   
    ```java
    /**
     * Ensures lack of reordering of loads before the fence
     * with loads or stores after the fence.
     * @since 1.8
     */
    public native void loadFence();
     
    /**
     * Ensures lack of reordering of stores before the fence
     * with loads or stores after the fence.
     * @since 1.8
     */
    public native void storeFence();
     
    /**
     * Ensures lack of reordering of loads or stores before the fence
     * with loads or stores after the fence.
     * @since 1.8
     */
    public native void fullFence();
    ```
    * loadFence()该方法之前所有的load操作在内存屏障之前完成。
    * storeFence()该方法之前所有的store操作在内存屏障之前完成。
    * fullFence()该方法之前所有的load，store材质在内存屏障之前完成。   
+ 总结：
    * 被volatile修饰的变量，在修改其值时，在内存操作上会强制把新值刷入到主内存。JMM会把该线程对应的CPU内存设置过期,从主内存读取新值。
    * volatile防止指令重排序是内存屏障，来保证重排序是不把内存屏障之后的指令重新排序到内存屏障之前。   
    * volatile不能保证原子性，要解决原子性的问题需要synchronzied 或者 lock。
