## Thread

+ **线程的状态**   
    Java 的线程状态描述在枚举类 java.lang.Thread.State 中，共包括如下五种状态：   
    ```java
    public enum State {
        NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED;
    }
    ```
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/thread/thread.png)  
  * New:新创建一个线程，处于等待状态。   
  * Runnable:可运行状态，并不是已经运行了，具体的线程调度由操作系统决定。在Runnable中包含了两种状态Ready，Running，当线程调用start()方法后，线程
  处于Ready状态，等待CPU分配时间片，分配后进入Running状态。此外当调用yield()方法后，当前线程让出CPU的时间片,使正在运行的线程变成Ready状态，并重新
  争夺CPU的调度权。他可能获得，也可能被其他线程获得。   
  * Timed_waiting:指定时间内让出CPU资源，此时线程不会执行，也不会被调度，直到等待时间结束后才会被执行。
  下列方法可以触发:Thread.sleep、Object.wait、Thread.join、LockSupport.parkNanos、LockSupport.parkUntil。     
  * Waiting:可被唤醒的等待状态，此时线程不会执行也不会被系统调度。此状态可以通过synchronized获得锁，或者调用wait()方法进入等待状态。最后通过
  notify(),notifyAll()唤醒，下列方法可以触发：Object.wait(),Thread.join(),LockSupport.park。   
  * Blocked:当发生锁竞争的状态，没有获得锁的线程会被挂起。例如synchronized锁，先获得先执行，没有获得的进入阻塞状态。   
  * Terminated:终止状态，从new到Terminated时不可逆转的，一般是程序流程正常结束或发生了异常。   
  常用方法：
    yield(): 
    当前线程让出cpu时间片，使正在运行的线程变成Ready状态，并重新争夺CPU的调度权，它可能获得也可能被其他线程获得。   
    wait()和notify()/notifyAll():  
    是一对方法，有一个等待就会有一个叫醒。
    join():   
    主线程等待子线程完成结束。join()是一个synchronized方法，里面调用了wait(),让持有当前同步锁的线程（也就是主线程）进入等待状态，当子线程执行完毕后
    JVM 调用了 lock.notify_all(thread) 唤醒了主线程继续执行。
    在多数情况下，主线程创建并启动子线程，如果子线程要进行大量耗时运算，主线程已经执行完毕，如果主线程需要知道子线程的结果，就需要等待
    子线程执行完毕。主线程可以sleep()但是时间不好确定，join()适合这个场景。
    sleep():   
    讲一个线程睡眠，指定时间内让出CPU资源，此时线程不会被执行。sleep和wait的区别：sleep来自于Thread，wait来自于Object；sleep方法没有释放锁，而wait释放了锁；
    wiat()必须放在synchronized block中，否则会在program runtime时扔出”java.lang.IllegalMonitorStateException“异常，sleep可以再任何地方使用。
  线程启动过程：   
    ```java
    new Thread(() -> {
        // todo
    }).start();
    
    // JDK 源码
    public synchronized void start() {
    
        if (threadStatus != 0)
            throw new IllegalThreadStateException();
    
        group.add(this);
        boolean started = false;
        try {
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {}
        }
    }
    ```
    线程启动方法start(),在它的方法英文注释中已经把核心内容描述出来。Causes this thread to begin execution; the Java Virtual Machine calls the run method of this thread.
    这段话的意思是:由JVM调用此线程的run方法，使线程开始执行。是一个JVN的回调过程。group.add(this)是把当前线程加入线程租ThreadGroup。start0()是一个native方法。
    线程启动的流程为：   
    1、java创建线程和启动；
    2、调用本地方法start0()；
    3、JVM中的JVM_StartThread的创建和启动；
    4、设置线程状态等特被唤醒；
    5、根据不同的OS启动线程并唤醒；
    6、最后回调run方法启动Java线程；
    start()方法和run()方法的区别：   
    start()方法是新创建一个线程，并处于Ready状态，并且在run方法中的代码将在新线程中执行；直接调用run()方法时并不会新创建线程，而是在当前线程执行。   
+ **线程池**        
    线程池是一种池化思想的线程管理工具。其核心目的是资源利用，避免重复创建线程带来的资源消耗。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/thread/thread-pool.png)  
    线程池的实现：   
    ```java
    ThreadPoolExecutor threadPoolExecutor = 
                                      hreadPoolExecutor(int corePoolSize,
                                                        int maximumPoolSize,
                                                        long keepAliveTime,
                                                        TimeUnit unit,
                                                        BlockingQueue<Runnable> workQueue,
                                                        ThreadFactory threadFactory,
                                                        RejectedExecutionHandler handler);
    threadPoolExecutor.execute(() -> {
        System.out.println("Hi 线程池！");
    });
    threadPoolExecutor.shutdown();    
    Executors.newFixedThreadPool(10);
    Executors.newCachedThreadPool();
    Executors.newScheduledThreadPool(10);
    Executors.newSingleThreadExecutor();
    ```
  * corePoolSize：核心线程数量，allowCoreThreadTimeOut控制核心线程数量在空闲时是否会被销毁，默认为false不会被销毁，如果设置为true线程会受到keepAliveTime控制而销毁；   
  * maximumPoolSize：最大线程数；   
  * keepAliveTime：多余的空闲线程的存活时间，当线程数量的大小超过corePoolSize，当线程空闲时间超过keepAliveTime时，多余的线程会被销毁，直到corePoolSize个线程为止。
  默认情况下，只有当线程池中的数量大于corePoolSize时，keepAliveTime才会起作用，直到线程池中的线程不大于corePoolSize。   
  * unit：keepAliveTime的单位；   
  * workQueue：任务阻塞队列；    
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/thread/queue.png)   
  * threadFactory：线程工厂；生成线程池中线程的工厂，用于创建线程的一般默认认可；   
  * handler：拒绝策略；当队列满了且工作线程大于等于maximumPoolSize时，会执行相关拒绝策略；   
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/thread/policy.png)   
  线程池执行流程：   
  1、检查线程池状态，如果不是Running直接拒绝，线程池必须保证running状态下才能进行；   
  2、如果工作线程小于corePoolSize，直接创建线程来执行任务；   
  3、当工作线程大于corePoolSize且任务阻塞队列未满，将任务添加到阻塞队列；   
  4、当阻塞队列已满，并且工作线程大于corePoolSize，且工作线程小于maximumPoolSize，则创建一个新的线程执行任务；   
  5、当工作线程大于等于maximumPoolSize且任务的阻塞队列已满，则执行handler相关的拒绝策略。   
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/thread/thread_pool_flow.png)    
  线程池的状态：   
  RUNNING：运行状态，接受新的任务并且处理队列中的任务；   
  SHUTDOWN：关闭状态(调用了shutdown方法)。不接受新的任务，但需要处理队列中的任务；   
  STOP：停止状态(调用了shutdownNow方法)。不接受新的任务，不处理队列中的任务，并且要中断正在处理的任务；   
  TIDYING：所以任务都已经终止，工作线程为0，线程进入该状态后会调用terminated()方法进入terminated状态；   
  TERMINATED：终止状态，terminated()方法调用后的终止状态；   
  JDK提供的默认线程池：   
  * newFixedThreadPool:   
  代码：new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())   
  创建一个固定大小可重复使用的线程池，以LinkedBlockingQueue为无界阻塞队列存放等待线程。随着线程不能被执行的无限堆积，可能造成OOM。
  * newSingleThreadExecutor:   
  代码：new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())   
  只创建一个执行任务的线程的线程池，如果出现意外终止再创建一个。同样是LinkedBlockingQueue无界阻塞队列，可能造成OOM。
  * newCachedThreadPool:   
  代码：new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>())   
  SynchronousQueue是一个生产消费模式的阻塞队列，只要有任务就需要有线程执行，线程池中的线程可重复使用，如果线程比较耗时，会大量创建线程，会导致OOM。
  * newScheduledThreadPool:   
  代码：public ScheduledThreadPoolExecutor(int corePoolSize) { super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS, new ScheduledThreadPoolExecutor.DelayedWorkQueue()); }   
  可以延迟定时执行，有点像定时任务。同样是一个无限大小（Integer.MAX_VALUE）的线程池，可能造成OOM。   
  **线程池参数配置**   
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/thread/thread_pool_params.png)   
  IO密集型：
  CPU密集型：核心线程数为：cpu核心数+1。为什么加一：即使当计算（CPU）密集型的线程偶尔由于页缺失故障或者其他原因而暂停时，这个“额外”的线程也能确保 CPU 的时钟周期不会被浪费。
  [**线程参数动态化**](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html)   
  JDK中setCorePoolSize方法：   
   ```java
    /**
         * Sets the core number of threads.  This overrides any value set
         * in the constructor.  If the new value is smaller than the
         * current value, excess existing threads will be terminated when
         * they next become idle.  If larger, new threads will, if needed,
         * be started to execute any queued tasks.
         *
         * @param corePoolSize the new core size
         * @throws IllegalArgumentException if {@code corePoolSize < 0}
         * @see #getCorePoolSize
         */
        public void setCorePoolSize(int corePoolSize) {
            if (corePoolSize < 0)
                throw new IllegalArgumentException();
            int delta = corePoolSize - this.corePoolSize;
            this.corePoolSize = corePoolSize;
            if (workerCountOf(ctl.get()) > corePoolSize)
                interruptIdleWorkers();
            else if (delta > 0) {
                // We don't really know how many new threads are "needed".
                // As a heuristic, prestart enough new workers (up to new
                // core size) to handle the current number of tasks in
                // queue, but stop if queue becomes empty while doing so.
                int k = Math.min(delta, workQueue.size());
                while (k-- > 0 && addWorker(null, true)) {
                    if (workQueue.isEmpty())
                        break;
                }
            }
        }
    ```
  在运行期间调用此方法设置corePoolSize之后，线程池会覆盖原来的corePoolSize值，并且根据当前值和原始值的比较结果，采取不同的策略处理。   
  当覆盖之后的corePoolSize小于工作线程，说明有多余的worker线程，此时会向当前空闲的worker线程发起中断请求以实现回收，多余的worker在下次空闲时也会回收。   
  对于当前值大于原始值，且队列中有待执行的任务，则线程池会新建worker线程执行任务。    
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/thread/thread_pool_setCore.png)    
  JDK中setMaximumPoolSize方法：   
  ```java
     /**
         * Sets the maximum allowed number of threads. This overrides any
         * value set in the constructor. If the new value is smaller than
         * the current value, excess existing threads will be
         * terminated when they next become idle.
         *
         * @param maximumPoolSize the new maximum
         * @throws IllegalArgumentException if the new maximum is
         *         less than or equal to zero, or
         *         less than the {@linkplain #getCorePoolSize core pool size}
         * @see #getMaximumPoolSize
         */
        public void setMaximumPoolSize(int maximumPoolSize) {
            if (maximumPoolSize <= 0 || maximumPoolSize < corePoolSize)
                throw new IllegalArgumentException();
            this.maximumPoolSize = maximumPoolSize;
            if (workerCountOf(ctl.get()) > maximumPoolSize)
                interruptIdleWorkers();
        }
    ```
  首先校验参数合法性；然后用传递过来的值覆盖原来的值，判断工作线程是否大于最大线程数，如果大于则对其进行中断操作。
  线程池预热：   
  全部启动：   
  ```java
     /**
         * Starts all core threads, causing them to idly wait for work. This
         * overrides the default policy of starting core threads only when
         * new tasks are executed.
         *
         * @return the number of threads started
         */
        public int prestartAllCoreThreads() {
            int n = 0;
            while (addWorker(null, true))
                ++n;
            return n;
        }
    ```
  只启动一个：   
  ```java
     /**
         * Starts a core thread, causing it to idly wait for work. This
         * overrides the default policy of starting core threads only when
         * new tasks are executed. This method will return {@code false}
         * if all core threads have already been started.
         *
         * @return {@code true} if a thread was started
         */
        public boolean prestartCoreThread() {
            return workerCountOf(ctl.get()) < corePoolSize &&
                addWorker(null, true);
        }
    ```
  设置核心线程被回收：   
  ```java
    /**
         * Sets the policy governing whether core threads may time out and
         * terminate if no tasks arrive within the keep-alive time, being
         * replaced if needed when new tasks arrive. When false, core
         * threads are never terminated due to lack of incoming
         * tasks. When true, the same keep-alive policy applying to
         * non-core threads applies also to core threads. To avoid
         * continual thread replacement, the keep-alive time must be
         * greater than zero when setting {@code true}. This method
         * should in general be called before the pool is actively used.
         *
         * @param value {@code true} if should time out, else {@code false}
         * @throws IllegalArgumentException if value is {@code true}
         *         and the current keep-alive time is not greater than zero
         *
         * @since 1.6
         */
        public void allowCoreThreadTimeOut(boolean value) {
            if (value && keepAliveTime <= 0)
                throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
            if (value != allowCoreThreadTimeOut) {
                allowCoreThreadTimeOut = value;
                if (value)
                    interruptIdleWorkers();
            }
        }
    ```