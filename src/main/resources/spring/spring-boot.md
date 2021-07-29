## Spring Boot 
      SpringBoot是一种全新的框架，目的是为了简化Spring应用的初始搭建以及开发过程。该框架使用特定的方式(集成starter，约定优于配置)来进行配置，
     从而使开发人员不需要再定义样板化的配置。SpringBoot提供了一种新的编程范式，可以更加快速便捷地开发Spring项目，在开发过程当中可以专注于应用程序本身的功能开发，
     而无需在Spring配置上花太大的工夫。   
+ **启动入口**   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/spring-boot.png)        
    SpringBoot的启动类入口：   
    ```java
    @SpringBootApplication
    public class Application {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }
    ```
    Annotation定义（@SpringBootApplication）和类定义（SpringApplication.run）是启动的核心。   
    SpringBootApplication注解：
    ```java
    @Target(ElementType.TYPE)// 注解的适用范围，其中TYPE用于描述类、接口（包括包注解类型）或enum声明
    @Retention(RetentionPolicy.RUNTIME)// 注解的生命周期，保留到class文件中（三个生命周期）
    @Documented // 表明这个注解应该被javadoc记录
    @Inherited // 子类可以继承该注解
    @SpringBootConfiguration // 继承了Configuration，表示当前是注解类
    @EnableAutoConfiguration // 开启springboot的注解功能，springboot的四大神器之一，其借助@import的帮助
    @ComponentScan(excludeFilters = {// 扫描路径设置
    @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
    @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
    public @interface SpringBootApplication {
    ...
    }　
    ```
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/spring-boot-application.png)          
    @SpringBootConfiguration：标注当前类是Java Config类，会被扫描并加载到IOC容器。   
    @ComponentScan：扫描默认包或指定包下面的符合条件的组件并加载，如@Component，@Controller。   
    @EnableAutoConfiguration：可以自动配置，是SpringBoot中最重要的注解。从classpath中搜寻所有的META-INF/spring.factory配置文件，并将其中org.springframework.boot.autoconfigure.EnableAutoConfiguration
    对应的配置项通过反射实例化为对应的标注了@Configuration的Java Config形式的IOC容器配置类，然后汇总为一个，并加载到IOC容器内。  
    在spring框架中就提供了各种以@Enable开头的注解，例如：@EnableScheduling，@EnableCaching、@EnableMBeanExport等。
    @EnableAutoConfiguration实现是通过@Import的支持，将所有符合自动装配条件的Bean定义加载到IOC容器。   
    ```java
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @AutoConfigurationPackage
    @Import({AutoConfigurationImportSelector.class})
    public @interface EnableAutoConfiguration {
        String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
    
        Class<?>[] exclude() default {};
    
        String[] excludeName() default {};
    }
    ```
    @AutoConfigurationPackage：注册当前主程序类的同级以及子级的包中的符合条件(如@Configuration)的Bean定义。   
    @Import({AutoConfigurationImportSelector.class})：扫描各个组件jar下META-INF/spring.factory配置文件，将下面的"包名.类名"中的工厂
    类全部加载到IOC容器内。所有符合条件的Bean定义都加载到Spring的IOC容器里。   
    
    其中最关键的是@Import({AutoConfigurationImportSelector.class})，借助AutoConfigurationImportSelector，EnableAutoConfiguration可以帮助
    springboot应用将所有符合条件(spring.factories)的Bean定义（如Java Config@Configuration配置）都加载到当前springboot的IOC容器内。   
    
    SpringFactoriesLoader：   
    借助Spring原有的一个工具类SpringFactoriesLoader的支持，@EnableAutoConfiguration可以智能的自动配置功能才得以成功。   
    SpringFactoriesLoader属于Spring框架下私有的一种扩展方案，其主要功能就是从指定的配置文件META-INF/spring.factories加载配置，加载工厂类。   
    SpringFactoriesLoader为Spring工厂加载器，该对象提供了loadFactoryNames方法，入参为factoryClass和classLoader，即要传入对应的工厂类名称
    和类加载器，该方法会通过指定的类加载器加载，加载该类加载器下的指定文件，即spring.factories文件。   
    ```java
    public abstract class SpringFactoriesLoader {
    //...
    　　public static <T> List<T> loadFactories(Class<T> factoryClass, ClassLoader classLoader) {
    　　　　...
    　　}
       
       
    　　public static List<String> loadFactoryNames(Class<?> factoryClass, ClassLoader classLoader) {
    　　　　....
    　　}
    }
    ```
    配合@EnableAutoConfiguration使用的话，它更多是提供一种配置查找的功能支持，即根据@EnableAutoConfiguration的完整类名
    org.springframework.boot.autoconfigure.EnableAutoConfiguration作为查找的Key,获取对应的一组@Configuration类。    
    所以，@EnableAutoConfiguration自动装配的过程就是，从classpath中搜寻所有的META-INF/spring.factories配置文件，并将其中
    org.springframework.boot.autoconfigure.EnableAutoConfiguration对应的配置项通过反射实例化对应的标注了@Configuration的Java Config形式
    的IOC配置类，然后汇总为一个并加载到IOC容器内。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/spring-auto.png)        
    **启动类启动流程图**   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/spring-boot-start.png)        
    
+ **Spring boot执行流程**   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/spring-boot-app-start.png)        
    
+ **Spring boot自动装配**   
    SpringBoot定义了一套规范，这套规范规定：SpringBoot在启动的时候外部引用jar包中的META-INF/spring.factories文件，将文件中配置的类信息加载到
    spring容器，并执行类中定义的各种操作。对于外部jar来说，只需要按照SpringBoot定义的标准，就能将自己的功能装置进SpringBoot。   
    SpringBoot核心注解SpringBootApplication：   
    ```java
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    <1.>@SpringBootConfiguration
    <2.>@ComponentScan
    <3.>@EnableAutoConfiguration
    public @interface SpringBootApplication {
    
    }
    
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Configuration //实际上它也是一个配置类
    public @interface SpringBootConfiguration {
    }
    ```
    大概可以把SpringBootApplication看作是@Configuration，@EnableAutoConfiguration和@ComponentScan注解的集合。这三个注解的作用为：  
    - EnableAutoConfiguration：启动SpringBoot的自动装配机制。   
    - Configuration：允许在上下文中注册额外的Bean或导入其他配置。   
    - ComponentScan：扫描被@Component (@Service,@Controller)注解的Bean，注解默认会扫描启动类所在的包下的所有的类，可以自定义不扫描某些Bean。
    @EnableAutoConfiguration是实现自动装配的核心类。   
    @EnableAutoConfiguration只是一个简单的注解，@Enablexxx的注解是开启某一项功能的注解，其原理是借助`@Import`实现,将所有符合自动配置条件的bean定义加载到IOC容器。
    自动装配实际是通过@Import加载AutoConfigurationImportSelector类,然后由AutoConfigurationImportSelector实现加载功能。   
    ```java
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @AutoConfigurationPackage //作用：将main包下的所欲组件注册到容器中
    @Import({AutoConfigurationImportSelector.class}) //加载自动装配类 xxxAutoconfiguration
    public @interface EnableAutoConfiguration {
        String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
    
        Class<?>[] exclude() default {}; //排除相关类
    
        String[] excludeName() default {};//排除相关类
    }
    ```
    `AutoConfigurationImportSelector.class`的类的继承关系：   
    ```java
    public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware, ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
    
    }
    
    public interface DeferredImportSelector extends ImportSelector {
    
    }
    
    public interface ImportSelector {
        String[] selectImports(AnnotationMetadata var1);
    }
    ```
    AutoConfigurationImportSelectorl类实现了`ImportSelector`接口，也实现了这个接口中的selectImports方法，该方法主要拥有获取所有符合条件的
    类的全限类名。这些类需要被加载到IOC容器内。   
    ```java
    private static final String[] NO_IMPORTS = new String[0];
    
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
            // <1>.判断自动装配开关是否打开
            if (!this.isEnabled(annotationMetadata)) {
                return NO_IMPORTS;
            } else {
              //<2>.获取所有需要装配的bean
                AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader.loadMetadata(this.beanClassLoader);
                AutoConfigurationImportSelector.AutoConfigurationEntry autoConfigurationEntry = this.getAutoConfigurationEntry(autoConfigurationMetadata, annotationMetadata);
                return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
            }
        }
    ``` 
    getAutoConfigurationEntry()方法是核心，其主要负责加载自动配置类。   
        ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/getAutoConfigurationEntry.png)      
       
    getAutoConfigurationEntry其实现为：   
    ```java
    private static final AutoConfigurationEntry EMPTY_ENTRY = new AutoConfigurationEntry();
    
    AutoConfigurationEntry getAutoConfigurationEntry(AutoConfigurationMetadata autoConfigurationMetadata, AnnotationMetadata annotationMetadata) {
            //<1>.
            if (!this.isEnabled(annotationMetadata)) {
                return EMPTY_ENTRY;
            } else {
                //<2>.
                AnnotationAttributes attributes = this.getAttributes(annotationMetadata);
                //<3>.
                List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);
                //<4>.
                configurations = this.removeDuplicates(configurations);
                Set<String> exclusions = this.getExclusions(annotationMetadata, attributes);
                this.checkExcludedClasses(configurations, exclusions);
                configurations.removeAll(exclusions);
                configurations = this.filter(configurations, autoConfigurationMetadata);
                this.fireAutoConfigurationImportEvents(configurations, exclusions);
                return new AutoConfigurationImportSelector.AutoConfigurationEntry(configurations, exclusions);
            }
        }
    ```
    第一步判断自动装配开关是否打开。默认是spring.boot.enableautoconfiguration=true，可以在 application.properties 或 application.yml 中设置。   
    如果没有禁用则进入else分支，第一步操作首先加载所有Spring预先定义的配置条件信息，这些信息在`org.springframework.boot.autoconfigure`包下
    的`META-INF/spring-autoconfigure-metadata.properties`文件中。   
    这些配置条件的主要含义是：如果需要自动装配某个类，你觉得需要先存放哪些类或者那些配置文件等等条件，这些条件的判断主要是用了`@ConditionalXXX`注解。   
    文件内容大致为：
    `org.springframework.boot.actuate.autoconfigure.web.servlet.WebMvcEndpointChildContextConfiguration.ConditionalOnClass=org.springframework.web.servlet.DispatcherServlet
     org.springframework.boot.actuate.autoconfigure.metrics.jdbc.DataSourcePoolMetricsAutoConfiguration.ConditionalOnClass=javax.sql.DataSource,io.micrometer.core.instrument.MeterRegistry
     org.springframework.boot.actuate.autoconfigure.flyway.FlywayEndpointAutoConfiguration.AutoConfigureAfter=org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
    `
     ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/first.png)      
    第二步用于获取EnableAutoConfiguration注解中的`exclude`和`excludeName`。   
     ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/second.png)      
    第三步获取用于自动装配的所有配置类，读取`META-INF/spring.factories`。 
     ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/thrid.png)      
    `spring-boot/spring-boot-project/spring-boot-autoconfigure/src/main/resources/META-INF/spring.factories`    
    xxxAutoConfiguration的作用是按需加载组件。   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/auto.png)      
    不仅是这个依赖下的META-INF/spring.factories会被读取到，所有Spring Boot Starter下的META-INF/spring.factories都会被读取到。   
    第四步spring.factories中的配置不是每次启动都会全部加载    
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/spring/fourth.png)     
    filter()方法会对其进行筛选，@ConditionalOnXXX注解中所有的条件都满足，该类才会生效。   
    例如：   
    ```java
    @Configuration
    // 检查相关的类：RabbitTemplate 和 Channel是否存在
    // 存在才会加载
    @ConditionalOnClass({ RabbitTemplate.class, Channel.class })
    @EnableConfigurationProperties(RabbitProperties.class)
    @Import(RabbitAnnotationDrivenConfiguration.class)
    public class RabbitAutoConfiguration {
    }
    ```
    Spring Boot提供的条件注解类：   
    @ConditionalOnBean:当容器内有指定Bean的条件下次才会被加载。   
    @ConditionalOnMissingBean：当容器内没有指定Bean的条件下。   
    @ConditionalOnSingleCandidate：当指定Bean在容器中只有一个，或者虽然有多个但是指定首选Bean。   
    @ConditionalOnClass：当路径下有指定Class的条件下。   
    @ConditionalOnMissingClass：当路径下没有指定Class条件下。   
    @ConditionalOnProperty：指定的属性是否有指定的值。   
    @ConditionalOnResource：类路径是否有指定的值。    
    @ConditionalOnExpression：基于SpEL表达式作为判断条件。   
    @ConditionalOnJava：基于Java版本作为判断条件。   
    @ConditionalOnJndi：在JNDI存在的条件下差在指定的位置(??)。   
    @ConditionalOnNotWebApplication：在当前项目不是web的条件下。   
    @ConditionalOnWebApplication：在当前项目是web的条件下。   
    
    Spring Boot通过`@EnableAutoConfiguration`开启自动装配，通过`SpringFactoriesLoader` 最终加载`META-INF/spring.factories`中
    的自动配置类实现自动装配，自动装配类其实是通过`@Conditional`按需加载配置类，想要其生效必须引入spring-boot-starter-xxx包实现起步依赖。
     
    
    
    
    
    