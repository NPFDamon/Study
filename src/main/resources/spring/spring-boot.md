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
    
    
    
    
    
    
    
    