## JVM
    JVM(Java Virtual Machine Java虚拟机),JVM可以理解为是一个虚拟出来的计算机，具备着计算机的基本运算方式，它主要负责把 Java 程序生成的字节码文件，解释成具体系统平台上的机器指令，让其在各个平台运行。
    
+ **类加载**   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/class_loading.png)  
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/class_loading_1.png)  
    JVM类加载分为：加载,链接,初始化,使用,卸载。其中链接又包括：验证,准备,解析。   
    * **加载**：Java虚拟机规范对class文件格式进行了严格的规则，但是对于从哪里加载class文件，却非常自由。Java虚拟机可以从文件系统中读取，可以从
    Jar(或者ZIP)压缩包中提取class。除此之外还可以从网络下载，数据库加载甚至是运行时直接生成class文件。   
    * **链接**：包括了三个阶段：   
        * 验证：确保被加载类的正确性，验证字节流是否符合class文件规范，例魔数 0xCAFEBABE，以及版本号等。   
        * 准备：为类的静态变量分屏内存并设置变量初始值（这个时候内存分配仅包括类变量（被static修饰的变量），而不包括实例变量，实例变量将会在对象实例化时随着对象一起分配在Java堆中。
        这里所设置的初始值通常情况下是数据类型默认的零值(如0、0L、null、false等)，而不是被在Java代码中被显式地赋予的值。比如：假设一个类变量的定义为: public static int value = 3；
        那么变量value在准备阶段过后的初始值为0，而不是3，因为这时候尚未开始执行任何Java方法，而把value赋值为3的put static指令是在程序编译后，存放于类构造器()方法之中的，所以把value赋值为3的动作将在初始化阶段才会执行。
        如果同时被final和static修饰，那么在准备阶段变量value就会被初始化为ConstValue属性所指定的值。）。   
        * 解析：把类中的符号引用变成直接引用。解析就是把常量池中的直接引用替换为直接引用的过程。解析主要针对类或接口，字段，类方法，接口方法，方法类型，方法句柄和调用点限定符7类符号引用进行。符号引用就是一组符号来描述目标，可以是任何字面量。   
    * **初始化**：类加载完成的最后一步就是初始化，目的就是为标记常量值的字段赋值，以及执行<clinit>方法的过程。JVM通过锁的方式保证<clinit>方法只被执行一次。   
    * **使用**：程序代码执行使用阶段。   
    * **卸载**：程序代码退出、异常、结束等。   
+ **类加载器**   
    虚拟机设计团队把类加载阶段中的“通过一个类的全限定名来获取描述此类的二进制字节流”这个动作放到Java虚拟机外部去实现，以便让应用程序自己决定如何去获取所需要的类。 实现这个动作的代码模块称为“类加载器”。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/classloader.jpeg)  
    Java中的类加载器可以分为两种类型；一种是启动类加载器，这个是用C++实现的,是虚拟机自身的一部分；另一种是则是由Java应用开发人员编写的，独立于虚拟机外部，并且全都继承自抽象类java.lang.ClassLoader。
    * 启动类加载器(Bootstrap ClassLoader):负责将在<JAVA_HOME>/lib目录中的文件，或者被-Xbootclasspath参数所指定的并且是虚拟机识别的类库加载到虚拟机内存中。启动类加载去无法被
    Java程序直接引用。    
    * 扩展类加载器(Extension ClassLoader):这个加载器由sun.misc.Launcher$ExtClassLoader实现，它负责加载<JAVA_HOME>/lib/ext目录下的文件，
    或者被java.ext.dirs系统变量所指定路径中的所有类库，开发者可以直接使用扩展类加载器。   
    * 应用程序类加载器(Application ClassLoader):这个类加载器由sun.misc.Launcher$AppClassLoader实现。负责加载用户路径(classpath)下所有的类库。开发者
    可以在代码中使用ClassLoader#getSystemClassLoader()获取这个类加载器。   
    * 自定义类加载器:用户可以自定义类加载器。继承自java.lang.ClassLoader重写findClass()方法。   
    获取ClassLoader的几种方式：
    ```java
    // 方式一：获取当前类的 ClassLoader
    clazz.getClassLoader()
    // 方式二：获取当前线程上下文的 ClassLoader
    Thread.currentThread().getContextClassLoader()
    // 方式三：获取系统的 ClassLoader
    ClassLoader.getSystemClassLoader()
    // 方式四：获取调用者的 ClassLoader
    DriverManager.getCallerClassLoader()
    ```
    * 双亲委派模式：   
    如果一个类加载器收到了类加载请求，它自己不会先加载这个类，而是委托给其父类加载器去加载，如果父类加载器还有父类加载器,则一直向上委托，一直到启动类加载器(Bootstrap ClassLoader)
    当父类加载器返回无法加载这个类（它的搜索范围内没找到这个类）时，自己才会尝试加载。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/classloader.png)  
    双亲委派模式是为了保证java核心类库的安全，所有的java类都至少引用java.lang.Object类，也就是说，在运行的时候，java.lang.Object类会被加载到Java虚拟机中，
    如果这个类由应用自己的类加载器加载的话，就会有多个版本的java.lang.Object,而且这些类之间都是不兼容的。通过双亲委派模式，对于java核心类库的加载工作由启动类加载器统一完成，保证了 Java 应用所使用的都是同一个版本的 Java 核心库的类，是互相兼容的。
    * 双亲委派模式的破坏：   
    在Java应用中存在着很多服务提供者接口（Service Provider Interface，SPI），这些接口允许第三方为它们提供实现，如常见的 SPI 有 JDBC、JNDI等，这些 SPI 的接口属于 Java 核心库，
    一般存在rt.jar包中，由Bootstrap类加载器加载。而Bootstrap类加载器无法直接加载SPI的实现类，同时由于双亲委派模式的存在，Bootstrap类加载器也无法反向委托AppClassLoader加载器SPI的实现类。
    在这种情况下，我们就需要一种特殊的类加载器来加载第三方的类库，而线线程上下文类加载器（context class loader）（双亲委派模型的破坏者）就是很好的选择。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/thread_classloader.png)    
+ **JVM内存模型**   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/jvm_memory.png)   
    * jdk1.6:有永久代，静态变量存储在永久代上。   
    * jdk1.7:有永久代，但是已经把字符串常量值，静态变量存放在堆上，逐渐的减少永久代的使用。   
    * jdk1.8:无永久代，运行时常量池，类常量池，都保存在元数据区，也就是常称的元空间。但是字符串常量仍存放在堆上。   
    
    * **程序计数器**   
    较小的内存空间，线程私有，记录当前线程所执行的字节码行号，也就是说记录的是当前线程正在执行的那一条字节码指令的地址。(如果当前线程执行的是本地方法,那么程序计数器为空)。
    这是唯一不会发生OutOfMemoryError的区域。   
    ```java
    public static float circumference(float r){
            float pi = 3.14f;
            float area = 2 * pi * r;
            return area;
    }
    ```
    此段代码的在虚拟机的执行过程，左侧为其程序计数器：
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/cxjsq.png)   
    * 每个行号都会对应一条需要执行的字节码指令，是压栈还是弹出或是执行计算。   
    * 之所以说是线程私有的，因为如果不是私有的，那么整个计算过程最终的结果也将错误。   
    * **Java虚拟机栈**   
    每一个方法执行的时候，都会创建一个栈帧，用于存放局部变量表，操作数栈，动态链接，方法出口，线程信息等。方法从调用到执行完成，都对应着栈帧从虚拟机中入栈和出栈的过程。
    最终，栈帧会随着方法的创建到结束而销毁。java虚拟机栈也是线程私有的。
    * **本地方法栈**   
    本地方法栈和java虚拟机栈类似，唯一不同的是本地方法栈执行的是native方法，而java虚拟机栈是为jvm执行java方法服务的。，与 Java 虚拟机栈一样，本地方法栈也会抛出 StackOverflowError 和 OutOfMemoryError 异常。
    * **堆和元空间**   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/heap.png)   
    JDK1.8之后JVM主要有三大块组成：堆内存，元空间和栈。Java堆是内存空间占用最大的区域。   
    堆由年轻代和老年的组成,其中年轻代占内存区域的1/3，老年的占内存区域的2/3。   
    年轻代又分为Eden,From Survivor,To Survivor，占据比例为8：1：1(大小可调节)。
    元空间也就是直接存储区域，在jdk1.8之后就不在堆上分配方法区了。
    元空间从虚拟机Java堆中移到了本地内存，默认情况下，元空间的大小仅受本地内存大小限制。如果不指定大小，默认情况下，虚拟机会耗尽所有可用的系统内存，
    如果元数据发生溢出，虚拟机一样会报出：OutOfMemoryError:Metaspace。
+ **垃圾回收**   
    如何判断对象是否存活：
    * 引用计数器：为每一个对象添加一个引用计数器，统计指向该对象的引用次数。当一个对象有相应的引用更新操作时，则对目标的引用计数器进行增减；一旦当一个对象的
    计数器为0时,标识该对象已经死亡，可以被销毁。引用计数器在处理相互依赖，循环依赖时，可能会存在不在使用但又不能被回收的对象，造成内存泄露(A引用B,B引用A)。
    * 可达性分析：通过定义一系列GC-Roots根节点作为起始节点，从此节点出发，穷举该节点引用到的全部对象填充到集合中。当所有节点被标记完成之后，没有被标记的对象就是可以被回收的对象。
        GC-Roots包括：
        * 全局性引用，对静态变量常量的引用
        * 执行上下文，对java方法帧栈中的局部对象的引用；对JNI handlers（Native方法）对象的引用
        * 已启用且未停用的线程。   
    引用：   
    **强引用**：如果一个对象被强引用,垃圾回收器不会回收它，即使抛出OutOfMemoryError，使程序异常终止，也不会靠随意回收具有强引用的对象来解决内存不足问题。   
    **软引用**：可以通过java.lang.ref.SoftReference使用软引用。在内存足够时，不会被回收,只有在内存不足时，才会回收软引用对象。如果回收了之后仍没有足够内存才会抛出内存溢出异常。   
    **弱引用**：无论内存是否足够,只要JVM进行垃圾回收，就会对弱引用的对象进行回收.可以用java.lang.ref.WeakReference使用弱引用(ThreadLocal的Key使用的就是弱引用)。   
    **虚引用**：虚引用是最弱的一种关系，虚引用并不会觉得对象的生命周期。如果一个对象仅持有虚引用，那么它就和没有引用一样,在任何时候都可能被回收。可以用java.lang.ref.PhantomReference使用，它只有一个构造函数和一个 get() 方法，而且它的 get() 方法仅仅是返回一个null，也就是说将永远无法通过虚引用来获取对象，虚引用必须要和 ReferenceQueue 引用队列一起使用，它的作用在于跟踪垃圾回收过程。      
+ **垃圾回收算法**   
    * 标记清除算法   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/mark-sweep.png)   
    标记无引用的死亡对象锁占据的空闲内存，并记录到空闲列表中。   
    当创建新对象时，内存管理模块会从空闲列表中寻找空闲内存，分配给新建对象。   
    这种清理方式非常高效，但是问题内存碎片化太严重了。Java 虚拟机的堆中对象，必须是连续分布的，所以极端的情况下可能即使总剩余内存充足，但寻找连续内存分配效率低，或者严重到无法分配内存。重启汤姆猫！
    在CMS有此算法的使用，GC时间短，但是算法有缺陷。
    * 标记复制算法   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/mark-copy.png)   
    把内存区域分成两部分，分别用两个指针from，to维护，并且只是用from指针指向的内存区域分配内存。   
    当发生垃圾回收时，把存活的对象复制到to区域，并交换指针。
    解决了碎片化的问题，但是把内存区域分成两部分，内存空间浪费了一半。
    * 标记压缩算法   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jvm/mark-compact.png)   
    标记过程与标记清除算法一样，但是在后续的对象清理步骤，先把存活对象向一侧移动，然后清理掉其他内存空间。
    这种算法能够解决内存碎片化问题，但压缩算法的性能开销也不小。   
    * 新生代的回收算法：   
    新生代分为Eden，survivor0，survivor2(8:1:1)。一半来说，大部分对象先在Eden区域生成(当然也有特殊情况，大对象(大对象指需要大量(例如很长的数组)连续内存空间地址的对象)直接进入老年代)，
    回收时先将Eden区域存活的对象复制到survivor0区域，然后清空
    Eden区域，当survivor也满了时，将Eden区域和survivor0的存活的对象数据复制到survivor1，然后清空Eden和这个survivor0，此时survivor0是空的，然后
    将survivor0和survivor1互换，即保持survivor1位空（为什么保持survivor1为空？为了让Eden区和survivor0交互存活对象），如此往复，如果Eden没有足够的空间，
    则会触发一次Minor GC。  
    Minor GC在Eden区域满的时候和新创建的对象大小大于Eden区域所剩空间时会触发Minor GC。
    当survivor1不足以存放Eden和survivor0的存活对象时，就将存活的对象放入老年代，若是老年的也满了，则触发一次full GC，也就是新生代、老年代都进行回收。   
    新生代发生的GC为Minor GC，Minor GC发生的频率比较高（不一定等Eden满了才触发）。
    Full GC触发条件：1、老年的空间不足时，2、晋升到老年代的对象的平均大小大于老年代剩余空间，3、MinorGC后需要存放的对象大于老年代的剩余空间大小。
    * 老年的回收算法：   
    回收主要以Mark-Compact为主。    
    在年前代经历了N次（默认为15次）垃圾回收后仍存活的对象，就会放到老年的中，因此，可以认为年老代中存放的都是一些生命周期较长的对象。
    * System.gc()   
    此方法是建议JVM进行Full GC（并不一定执行），虽然只是建议，但是很多情况下还是会触发Full GC，从而增加Full GC的频率，即增加了间歇性停顿的次数。
    新生代进入老年代的条件：   
    1、对象经过多次(默认为15次)Minor GC依然存活，就会晋升到老年代。
    2、大对象(大对象指需要大量(例如很长的数组)连续内存空间地址的对象)超出了jvm设置的限定值直接进入老年代。
    3、在一次Minor GC后，survivor区域中的几个年龄对象加起来超过survivor区域内存的一半，根据动态分配原则，从最小的年龄加起，比如年龄1+年龄2+年龄3的对象大小总和
    超过了survivor内存的一半，则年龄3以上的对象进入老年代。
    4、Minor GC后存活的对象太多，survivor存不下，此时对象直接进入老年代。
+ **垃圾回收器**   
    新生代：   
    1，Serial：标记-复制算法。简单高效的单核机器，Client模式下默认新生代收集器；   
    2，Parallel ParNew：标记-复制算法。GC线程并行版本，在单CPU场景效果不突出。常用于Client模式下的JVM ；  
    3，Parallel Scavenge：标记-复制算法。目标在于达到可控吞吐量（吞吐量=用户代码运行时间/(用户代码运行时间+垃圾回收时间)）；   
    老年代：   
    1，Serial Old：标记-压缩算法。性能一般，单线程版本。1.5之前与Parallel Scavenge配合使用；作为CMS的后备预案。   
    2，Parallel Old：标记-压缩算法。GC多线程并行，为了替代Serial Old与Parallel Scavenge配合使用。   
    3，CMS：标记-清除算法。对CPU资源敏感、停顿时间长。标记-清除算法，会产生内存碎片，可以通过参数开启碎片的合并整理。基本已被G1取代。   
    G1:标记-压缩算法,适用于多核大内存机器、GC多线程并行执行，低停顿、高回收效率。   
    

    

    