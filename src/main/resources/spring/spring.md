## Spring 
   `自己实现的Spring：https://github.com/NPFDamon/small-spring` 
+ IOC   
    IOC(Inversion of Control)控制反转，资源的使用不由使用者各自管理，而是交给不使用资源的第三方进行管理。这样做的好处是资源是集中管理的，
    可配置、易维护，同时也降低了双方的依赖度做到了低耦合。   
    Spring的IOC容器主要基于BeanFactory和ApplicationContext两个接口。BeanFactory是Spring IOC的最底层接口，ApplicationContext是其最高级
    接口之一,并对BeanFactory做了很多扩展功能，所以在绝大部分的工作场景下，都会使用 ApplicationContext 作为 Spring IoC 容器。   
    * BeanFactory   
    最基础的IOC容器，提供完整的IOC服务，如果没有特殊指定，默认采用延迟初始化策略(lazy-load)。只有当客户端需要访问某个受管理的对象的时候，才会对
    该受管理的对象进行初始化已经依赖注入操作。所以，相对来说启动较快，所需要的资源有限。对于资源有限，并且功能要求不是很严格的场景，BeanFactory是比较合适的IoC容器选择。  
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/bean-factory.png)     
    * ApplicationContext   
    ApplicationContext是在BeanFactory基础上构建的，是相对比较高级的实现。除了拥有BeanFactory的所有功能以为，ApplicationContext还提供了其他
    高级特性，比如事件发布、国际化信息支持等。ApplicationContext所管理的对象，在该类型容器启动之后，默认全部初始化并绑定完成。所以，相对BeanFactory，
    ApplicationContext要求更多的系统资源，同时，容器启动的时间也会比BeanFactory要长，在那些系统资源充足，并且要求更多功能的场景中，ApplicationContext类型的容器是比较合适的选择。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/application-context.png)    
    >[https://blog.csdn.net/f641385712/article/details/88578656]   
    
    Spring Bean生命周期：   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/spring-bean.png)    
    一个Bean的构造函数初始化时是最先执行的，这个时候，bean属性还没有被注入；   
    @PostConstruct注解的方法优先于InitializingBean的afterPropertiesSet执行，这时Bean的属性竟然被注入了；   
    spring很多组件的初始化都放在afterPropertiesSet做,想和spring一起启动，可以放在这里启动;   
    spring为bean提供了两种初始化bean的方式，实现InitializingBean接口，实现afterPropertiesSet方法，或者在配置文件中同过init-method指定，两种方式可以同时使用；   
    实现InitializingBean接口是直接调用afterPropertiesSet方法，比通过反射调用init-method指定的方法效率相对来说要高点；但是init-method方式消除了对spring的依赖；   
    如果调用afterPropertiesSet方法时出错，则不调用init-method指定的方法。   
    Bean在实例化的过程中：   
    Constructor > @PostConstruct > InitializingBean > init-method   
    BeanFactory和FactoryBean区别：   
    1，两者都是接口。   
    2，BeanFactory主要是用来创建Bean和获取Bean的。   
    3，FactoryBean也是一个Bean，由BeanFactory创建，与普通Bean不同，其返回的对象不是指定类的一个实例，而是该FactoryBean的getObject()方法返回的对象。   
    4，通过beanName从BeanFactory中获取对象时，如果beanName不加'&'则获取到对应的Bean的实例；如果加'&'则获取到FactoryBean实例。   
    5，FactoryBean通常是用来创建比较复杂的Bean(如创建mybatis的SqlSessionFactory)，一般的bean直接用xml配置即可。但是如果创建一个Bean的过程中
    涉及到很多其他的Bean和复杂逻辑用xml配置比较困难，这个时候可以考虑用FactoryBean。   
    Bean的循环依赖：   
    Bean在创建的时候需要先调用其构造函数进行实例化，然后再进行属性填充,再接着进行附加操作和初始化，正是这样的生命周期，才有了Spring的解决循环依赖，
    这样的解决机制是根据Spring框架内定义的三级缓存来实现的，也就是说：三级缓存解决了Bean之间的循环依赖。我们从源码中来说明。   
    ```java
        /** Cache of singleton objects: bean name to bean instance. */
    	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    
    	/** Cache of singleton factories: bean name to ObjectFactory. */
    	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    
    	/** Cache of early singleton objects: bean name to bean instance. */
    	private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    ```
    singletonObjects第一级缓存，存放已经实例化好的单例bean。   
    earlySingletonObjects第二级缓存，存放已经曝光的单例对象。已经实例化好，但是还没有进行属性赋值。      
    singletonFactories第三级缓存，存放的是将要被实例化的对象的对象工厂。   
+ AOP   
    在软件业，AOP为Aspect Oriented Programming的缩写，意为：面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术。
    AOP是OOP的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程的一种衍生范型。利用AOP可以对业务逻辑
    的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。     
    Spring是基于动态代理实现的，有CGlib和JDK动态代理两个实现。   
+ Spring事务   
    Spring事务的本质其实就是数据库对事务的支持,没有数据库的支持，spring是无法完成事务的。   
    那么，我们一般使用JDBC操作事务的时候，代码如下：   
    (1)获取连接 Connection con = DriverManager.getConnection();   
    (2)开启事务con.setAutoCommit(true/false);   
    (3)执行CRUD   
    (4)提交事务/回滚事务 con.commit() / con.rollback();   
    (5)关闭连接 conn.close();   
    使用Spring管理事务后，可以忽略步骤2和4，就是让AOP去完成这个工作，关键类在TransactionAspectSupport这个切面里。   
    Spring回滚机制：当所拦截的方法有指定异常抛出,事务才会自动回滚。默认配置下，事务只会对Error和RuntimeException及其子类异常进行回滚。
    一般的Exception这些Checked异常不会发生回滚（如果一般Exception想回滚要做出配置`@Transactional(rollbackFor = Exception.class)`）   
    * Spring支持两种方式的事务管理   
    通过`TransactionTemplate`或者`TransactionManager`手动管理事务，实际应用的很少。   
    使用`TransactionTemplate`：
    ```java
    @Autowired
    private TransactionTemplate transactionTemplate;
    public void testTransaction() {
    
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
    
                    try {
    
                        // ....  业务代码
                    } catch (Exception e){
                        //回滚
                        transactionStatus.setRollbackOnly();
                    }
    
                }
            });
    }
    ```
    使用`TransactionManager`：
    ```java
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    public void testTransaction() {
    
      TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
              try {
                   // ....  业务代码
                  transactionManager.commit(status);
              } catch (Exception e) {
                  transactionManager.rollback(status);
              }
    }
    ```
    声明式事务：   
    通过 AOP 实现（基于@Transactional 的全注解方式使用最多）   
    
    * Spring事务传播行为：    
    1.TransactionDefinition.PROPAGATION_REQUIRED。使用最多的一个事务传播行为，@Transactional默认传播行为，如果当前有事务，就加入该事务；
    如果当前没有事务，就新建一个事务。   
    2.TransactionDefinition.PROPAGATION_REQUIRES_NEW。创建一个新事物，如果当我线程存在事务，则把当前事务挂起。也就是说不管外部方法是否开启事务，
    Propagation.REQUIRES_NEW修饰的方法内部都会开启自己的新事务，且开启的事务相互独立，互不干涉。   
    3.TransactionDefinition.PROPAGATION_NESTED。如果当前存在事务，则创建一个事务做完当前事务的嵌套事务来运行；如果当前没有事务，
    则该取值等价于TransactionDefinition.PROPAGATION_REQUIRED。也就是说：   
        1.在外部方法未开启事务的情况下Propagation.NESTED和Propagation.REQUIRED作用相同，修饰的内部方法都会新开启自己的事务，且开启的事务相互独立，互不干扰。   
        2.如果外部方法开启事务的话，Propagation.NESTED修饰的内部方法属于外部事务的子事务，外部主事务回滚的话，子事务也会回滚，而内部子事务可以单独回滚而不影响外部主事务和其他子事务。   
    4.TransactionDefinition.PROPAGATION_MANDATORY。如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。   
    5.TransactionDefinition.PROPAGATION_SUPPORTS。如果当前有事务，则加入该事务；如果当前没有事务，则以非事务的方式运行。   
    6.TransactionDefinition.PROPAGATION_NOT_SUPPORTED。以非事务的方式运行，如果当前有事务，则把当前事务挂起。   
    7.TransactionDefinition.PROPAGATION_NEVER。以非事务的方式运行，如果当前有事务则抛出异常。       
    
    * Spring事务失效：   
    1.发送自我调用的时候。   
    ```java
    @Service
    public class UserService{
      public void update(User user){
           updateUser(user);       
      }     
      @Transactional
      public void updateUser(User user){
      xxxDao.update(user);
       }
    }
    ```
    此时事务是无效的。上面代码等效于：   
    ```java
        @Service
        public class UserService{
          public void update(User user){
               this.updateUser(user);       
          }     
          @Transactional
          public void updateUser(User user){
          xxxDao.update(user);
           }
        }
     ```
    此时这个this对象不是代理类，而是UserService本身。解决办法就是让this变成UserService的代理类。   
    2.方法不是公关(public)的   
    @Transactional注解的方法都是被外部其他类调用才有效，如果方法修饰符是private的，这个方法就不能被调用，既然调不到，事务生效就没意义。   
    @Transactional 注解只能应用到 public 可见度的方法上。如果你在 protected、private 或者 package-visible 的方法上使用 @Transactional 注解，
    它也不会报错， 但是这个被注解的方法将不会有事务行为。    
    3.发生了错误异常   
    因为默认回滚的是：RuntimeException。如果是其他异常想要回滚，需要在@Transactional注解上加rollbackFor属性。   
    4.数据库不支持事务   
    毕竟spring事务用的是数据库的事务，如果数据库不支持事务，那spring事务肯定是无法生效！   
    5.异常被"吃"调场景   
    ```java
     @Transactional
        private Integer A() throws Exception {
            int insert = 0;
            try {
                CityInfoDict cityInfoDict = new CityInfoDict();
                cityInfoDict.setCityName("2");
                cityInfoDict.setParentCityId(2);
                /**
                 * A 插入字段为 2的数据
                 */
                insert = cityInfoDictMapper.insert(cityInfoDict);
                /**
                 * B 插入字段为 3的数据
                 */
                b.insertB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    ```
  如果B方法内部抛了异常，而A方法此时try catch了B方法的异常并没有抛出，此时事务会失效。   
  当ServiceB中抛出了一个异常后，ServiceB标识当前事务需要rollback。但是由于在ServiceA中由于手动捕获异常并进行处理，ServiceA任务当前事务应该正常
  commit。此时就出现了前后不一致，也就是因为这样，抛出了前面的UnexpectedRollbackException异常。   
  
    
                                                                          
