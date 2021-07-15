# 代理
    代理是一种设计模式。提供了对目标对象的额外访问方式，即通过代理对象访问目标对象，这样可以再不修改原目标对象的前提下，提供额外的功能，扩展目标对象。
![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/rp/proxy.png)

+ **静态代理**  
    这种代理需要代理对象和目标对象实现一样的接口。   
    优点：可以在不修改目标对象的前提下扩展目标对象的功能。   
    缺点：冗余。由于代理对象要实现与目标对象一致的接口，会产生过多的代理类。不易维护。一旦接口增加方法，目标对象与代理对象都要进行修改。   
    目标接口：   
    ```java
    public interface Internet {
        void internet();
    }
    ```
   被代理对象：   
   ```java
    public class CHNInternet implements Internet{
        @Override
        public void internet() {
            System.out.println("You can visit CHN Internet");
        }
    }
    ```
  代理对象：   
  ```java
    public class ProxyInternet implements Internet {
        private Internet internet = null;
    
        @Override
        public void internet() {
            if (internet == null) {
                internet = new CHNInternet();
            }
            linkToAllInternet();
    
            System.out.println("You have get proxy internet,you can visit all network!");
        }
    
        //代理增强功能
        private void linkToAllInternet() {
            System.out.println("User Static Method link to all network please wait .....");
            try {
                Thread.sleep(600);
            } catch (Exception e) {
                System.out.println("Fail,please try again");
            }
            System.out.println("Successful, you can visit all network .....");
        }
    }
    ```
  测试类：   
  ```java
    public static void main(String[] args){
                 //无代理
                 CHNInternet chnInternet = new CHNInternet();
                 chnInternet.internet();
                 //静态代理
                 ProxyInternet proxyInternet = new ProxyInternet();
                 proxyInternet.internet();
      }
  ```
  输出：
  ```java
    You can visit CHN Internet
    User Static Method link to all network please wait .....
    Successful, you can visit all network .....
    You have get proxy internet,you can visit all network!
  ```
+ **动态代理**  
    * JDK动态代理   
    动态代理的字节码在程序运行时由java反射机制动态生成，动态的在内存中创建代理对象，从而实现对目标的增强功能。java.lang.reflect 包中的 Proxy 
    类和InvocationHandler 接口提供了生成动态代理类的能力。    
    ```java
    /**
     * 动态代理
     * JDK动态代理尽支持实现接口的类
     * final Class<?>[] intfs = interfaces.clone();
     */
    public class InternetHandler implements InvocationHandler {
        //目标对象
        private Object target;
    
        public Object newProxyInstance(Object target) {
            this.target = target;
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
        }
    
        //invoke 调用被代理接口的方法时需要调用的，返回的值是被代理【接口】的一个实现类
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Object res = null;
            try {
                res = method.invoke(target, args);
                //增强方法
                linkToAllInternet();
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            return res;
        }
    
        //代理增强功能
        private void linkToAllInternet() {
            System.out.println("Use Internet Handler link to all network please wait .....");
            try {
                Thread.sleep(600);
            }catch (Exception e){
                System.out.println("Fail,please try again");
            }
            System.out.println("Successful, you can visit all network .....");
        }
    }
    ```
  测试：   
  ```java
     public static void main(String[] args){
            //无代理
            CHNInternet chnInternet = new CHNInternet();
            chnInternet.internet();
            //JDK代理
            InternetHandler handler = new InternetHandler();
            Internet net = (Internet) handler.newProxyInstance(new CHNInternet());
            net.internet();
        }
    ```
  输出：   
  ```java
    You can visit CHN Internet
    You can visit CHN Internet
    Use Internet Handler link to all network please wait .....
    Successful, you can visit all network .....
  ```
  * cglib动态代理   
  cglib (Code Generation Library )是一个第三方代码生成类库，运行时在内存中动态生成一个子类对象从而实现对目标对象功能的扩展。   
  cglib包的底层是通过使用一个小而快的字节码处理框架ASM，来转换字节码生成新类。用cglib代理的是目标类的子类，cglib代理类不需要接口，cglib生成的代理类重新写了
  父类的各个方法。cglib采用的是继承的方式，所以不能对final类进行处理。   
  ```java
    /**
     * cglib动态代理
     * 字节码增强，利用asm开源包，对对象的class文件加载过来，通过修改其字节码生成子类来处理
     * 采用的是继承的方式所以不能对final类进行代理
     */
    public class InternetFactory implements MethodInterceptor {
        private Object target;
    
        public InternetFactory(Object target) {
            this.target = target;
        }
    
        public Object getInstance() {
            //工具类
            Enhancer enhancer = new Enhancer();
            //设置父类
            enhancer.setSuperclass(target.getClass());
            //设置回调函数
            enhancer.setCallback(this);
            //创建子类（代理对象）
            return enhancer.create();
        }
    
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            Object res = method.invoke(target, objects);
            linkToAllInternet();
            return res;
        }
    
        //代理增强功能
        private void linkToAllInternet() {
            System.out.println("Use Internet Factory link to all network please wait .....");
            try {
                Thread.sleep(600);
            } catch (Exception e) {
                System.out.println("Fail,please try again");
            }
            System.out.println("Successful, you can visit all network .....");
        }
    }
    ```
  [除此之外还有ASM代理， Byte-Buddy代理， Javassist代理。](https://bugstack.cn/interview/2020/10/14/%E9%9D%A2%E7%BB%8F%E6%89%8B%E5%86%8C-%E7%AC%AC13%E7%AF%87-%E9%99%A4%E4%BA%86JDK-CGLIB-%E8%BF%98%E6%9C%893%E7%A7%8D%E7%B1%BB%E4%BB%A3%E7%90%86%E6%96%B9%E5%BC%8F-%E9%9D%A2%E8%AF%95%E5%8F%88%E5%8D%A1%E4%BD%8F.html)   
## 反射
    反射值程序可以访问，检测和修改它本身状态或行为的一种能力。
+ **反射**   
    * 反射可以让开发人员通过外部类的全路径名称创建对象,并使用这些类，实现一些扩展功能。   
    * 反射让开发人员枚举出类的全部成员变量，包括构造函数，属性，方法。   
    * 反射是可以访问类的私有成员。   
    反射API：   
    ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/rp/reflection.jpeg)   
    * Field:提供类的属性信息，以及对它的动态访问权限。它是一个封装反射类的属性的类。   
    * Constructor:提供类的构造方法信息，以及他的动态访问权限，它是一个封装类的构造方法的类。   
    * Method:提供类的方法信息，包括抽象方法，它是一个封装反射类方法的一个类。   
    * Class:表示正在运行的java应用程序中类的实例。   
    * Object:Object 是所有 Java 类的父类。所有对象都默认实现了 Object 类的方法。   
    ```java
    public class Student {
    
        private String studentName;
        public int studentAge;
    
        public Student() {
        }
    
        private Student(String studentName) {
            this.studentName = studentName;
        }
    
        public void setStudentAge(int studentAge) {
            this.studentAge = studentAge;
        }
    
        private String show(String message) {
            System.out.println("show: " + studentName + "," + studentAge + "," + message);
            return "testReturnValue";
        }
    }
    ```
  获取class对象：   
    ```java
     // 1.通过字符串获取Class对象，这个字符串必须带上完整路径名
     Class studentClass = Class.forName("com.test.reflection.Student");
     // 2.通过类的class属性
     Class studentClass2 = Student.class;
     // 3.通过对象的getClass()函数
     Student studentObject = new Student();
     Class studentClass3 = studentObject.getClass();
    ```
  获取成员变量：   
    ```java
    // 1.获取所有声明的字段
    Field[] declaredFieldList = studentClass.getDeclaredFields();
    for (Field declaredField : declaredFieldList) {
        System.out.println("declared Field: " + declaredField);
    }
    // 2.获取所有公有的字段
    Field[] fieldList = studentClass.getFields();
    for (Field field : fieldList) {
        System.out.println("field: " + field);
    }
    ```
  获取构造方法：   
    ```java
    // 1.获取所有声明的构造方法
    Constructor[] declaredConstructorList = studentClass.getDeclaredConstructors();
    for (Constructor declaredConstructor : declaredConstructorList) {
        System.out.println("declared Constructor: " + declaredConstructor);
    }
    // 2.获取所有公有的构造方法
    Constructor[] constructorList = studentClass.getConstructors();
    for (Constructor constructor : constructorList) {
        System.out.println("constructor: " + constructor);
    }
    ```
  获取非构造方法：   
    ```java
     // 1.获取所有声明的函数
     Method[] declaredMethodList = studentClass.getDeclaredMethods();
     for (Method declaredMethod : declaredMethodList) {
         System.out.println("declared Method: " + declaredMethod);
     }
     // 2.获取所有公有的函数
     Method[] methodList = studentClass.getMethods();
     for (Method method : methodList) {
         System.out.println("method: " + method);
     }
    ```
  实践：   
    ```java
    // 1.通过字符串获取Class对象，这个字符串必须带上完整路径名
    Class studentClass = Class.forName("com.test.reflection.Student");
    // 2.获取声明的构造方法，传入所需参数的类名，如果有多个参数，用','连接即可
    Constructor studentConstructor = studentClass.getDeclaredConstructor(String.class);
    // 如果是私有的构造方法，需要调用下面这一行代码使其可使用，公有的构造方法则不需要下面这一行代码
    studentConstructor.setAccessible(true);
    // 使用构造方法的newInstance方法创建对象，传入构造方法所需参数，如果有多个参数，用','连接即可
    Object student = studentConstructor.newInstance("NameA");
    // 3.获取声明的字段，传入字段名
    Field studentAgeField = studentClass.getDeclaredField("studentAge");
    // 如果是私有的字段，需要调用下面这一行代码使其可使用，公有的字段则不需要下面这一行代码
    // studentAgeField.setAccessible(true);
    // 使用字段的set方法设置字段值，传入此对象以及参数值
    studentAgeField.set(student,10);
    // 4.获取声明的函数，传入所需参数的类名，如果有多个参数，用','连接即可
    Method studentShowMethod = studentClass.getDeclaredMethod("show",String.class);
    // 如果是私有的函数，需要调用下面这一行代码使其可使用，公有的函数则不需要下面这一行代码
    studentShowMethod.setAccessible(true);
    // 使用函数的invoke方法调用此函数，传入此对象以及函数所需参数，如果有多个参数，用','连接即可。函数会返回一个Object对象，使用强制类型转换转成实际类型即可
    Object result = studentShowMethod.invoke(student,"message");
    System.out.println("result: " + result);
    ```
  > [https://zhuanlan.zhihu.com/p/86293659]