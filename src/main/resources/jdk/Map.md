## Map
### [HashMap](https://bugstack.cn/interview/2020/08/07/%E9%9D%A2%E7%BB%8F%E6%89%8B%E5%86%8C-%E7%AC%AC3%E7%AF%87-HashMap%E6%A0%B8%E5%BF%83%E7%9F%A5%E8%AF%86-%E6%89%B0%E5%8A%A8%E5%87%BD%E6%95%B0-%E8%B4%9F%E8%BD%BD%E5%9B%A0%E5%AD%90-%E6%89%A9%E5%AE%B9%E9%93%BE%E8%A1%A8%E6%8B%86%E5%88%86-%E6%B7%B1%E5%BA%A6%E5%AD%A6%E4%B9%A0.html)
### [ConcurrentHashMap](https://crossoverjie.top/2018/07/23/java-senior/ConcurrentHashMap/)   
    
+ HashMap    
    HashMap求key的hash散列。Java8的散列值扰动函数，用于优化散列效果。   
    ```java
     static final int hash(Object key) {
            int h;
            return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        }
    ```
  理论上来说字符串的hashCode是一个int类型值，那可以直接作为数组下标了，且不会出现碰撞。但是这个hashCode的取值范围是[-2147483648, 2147483647]，、
  有将近40亿的长度，谁也不能把数组初始化的这么大，内存也是放不下的。    
  默认的初始化hashmap大小是**16**个长度为`DEFAULT_INITIAL_CAPACITY  = 1 >> 4`,所有hash值并不能作为下标使用，需要与数组长度进行一个取模运算
  得到一个下标值。    
  hashmap并不是直接获取hash值之后就进行下标取模运算，而是进行了一次扰动运算，`(h = key.hashCode()) ^ (h >>> 16)`。把哈希值**右移16位**，也就
  正好是自己长度的一半(int值的二进制为32位)，之后与原哈希值进行**异或运算**，这样就混合了原始哈希值的高位和低位，增大了随机性。   
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jdk/hash.png)    
  **hashmap在put元素时会根据求的哈希值与数组长度-1进行&(按位与)运算。**
  按位与的计算方法为；只有当前对应位置的数据都是1时，运算结果才是1。当hashmap为2的n次幂的时候，（n-1)的2进制就是00000000***111这样形式的，
  这样与添加元素的hash值进行位运算时，能够充分的散列，使得添加的元素均匀分布在HashMap的每个位置上，减少hash碰撞。    
  
  **使用扰动函数就是为了增加随机性，让数据元素更加均匀的散列，减少碰撞。**   
  
  初始化容量和负载因子   
  Hashmap数组默认大小位16，为2的4次方，在扩容时每次都是当前数组的2倍，散列数组是一个2的倍数的长度，因为**这样(数组长度-1)正好相当于一个"底位掩码"。
  **"与"操作的结果就是散列值的高位全部归0，只保留低位，用来做数组下标访问**。散列数组需要一个2的倍数的长度，因为只有2的倍数在减1的时候，才会出现000...1111这样的值。   
  如果传入初始容量不是2的倍数，会 **寻找2的倍数的最小的值**（临近当前值的最小的2二的倍数的值）。    
  ```
  public HashMap(int initialCapacity, float loadFactor) {
      ...
      this.loadFactor = loadFactor;
      this.threshold = tableSizeFor(initialCapacity);
  }
  ```
  阀值threshold，通过方法tableSizeFor进行计算，是根据初始化来计算的。   
  这个方法也就是要寻找比初始值大的，最小的那个2进制数值。比如传了17，我应该找到的是32。    
  
  数组扩容时1.7之前时通过尾插发进行，多线程环境下会出现链表环。1.8之后扩容为：   
  原哈希值与新扩容出的数组长度进行&运算，结果等于0则下标位置不变；如果不为0，新位置就是原来位置加上16(原来的位置加原数组的大小)，这样就不需要重新计算
  每一个数组的哈希值了。   
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jdk/hash-resize.png)    
  （a）表示扩容前的key1和key2两种key确定索引位置的示例，图（b）表示扩容后key1和key2两种key确定索引位置的示例，n代表length。    
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jdk/resize-cap.jpg)    
  元素在重新计算哈希之后，因为数组为原来的2倍大小，那么n-1的mask大小范围在高位多1bit，一次新的index为**原位置+原数组大小**。    
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jdk/hash-resize.png)    
  resize不再像1.7中那样每个都重新计算哈希值，现在只需要看原来的hash值新增的bit位是1还是0就行了，是0的话索引没有变，是1就变成原来的位置加上原来的数组大小。    
  ![avatar](https://github.com/NPFDamon/Study/blob/main/src/main/resources/jdk/hash-resize.jpg)    
 
  
  再扩容时如果链表长度大于8且数组大小大于64则把链表转位[红黑树](https://bugstack.cn/interview/2020/08/16/%E9%9D%A2%E7%BB%8F%E6%89%8B%E5%86%8C-%E7%AC%AC5%E7%AF%87-%E7%9C%8B%E5%9B%BE%E8%AF%B4%E8%AF%9D-%E8%AE%B2%E8%A7%A32-3%E5%B9%B3%E8%A1%A1%E6%A0%91-%E7%BA%A2%E9%BB%91%E6%A0%91%E7%9A%84%E5%89%8D%E8%BA%AB.html)。   
  
  线程安全问题：   
  1.8之后在扩容时不会在出现链表环的问题，但是会出现数据丢失的问题，和数组size大小问题。   
  单线程环境下:    
  我们先查 a 准备写入的位置，查到是 0 号桶的 c 后面的位置，则我们写入 a，即 c.next = a。
  然后查 b 准备写入的位置，这次查到是 0号桶的 a 后面的位置，写入 b，即 a.next = b。此情景下 a, b 两个元素都能正常被写入。    
  
  多线程环境下：   
  a 的查询可能和 b 是同步进行的。
  比如， a 在线程 t1 里，b 在线程 t2 里     
  线程 t1 的查询里，a 的写入位置是 c 后面，c.next=a     
  线程 t2 的查询里，b 的写入位置也是 c 后面（因为此刻 a 尚未插入）, c.next=b。    
  最终先插入 c 后面的会被后插入的覆盖，a 和 b 两个元素只有一个被实际成功写入，另一个丢了。    
  ```java
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                       boolean evict) {
            Node<K,V>[] tab; Node<K,V> p; int n, i;
            if ((tab = table) == null || (n = tab.length) == 0)
                n = (tab = resize()).length;
            if ((p = tab[i = (n - 1) & hash]) == null) // 如果没有hash碰撞则直接插入元素
                tab[i] = newNode(hash, key, value, null);
            else {
                Node<K,V> e; K k;
                if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                    e = p;
                else if (p instanceof TreeNode)
                    e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
                else {
                    for (int binCount = 0; ; ++binCount) {
                        if ((e = p.next) == null) {
                            p.next = newNode(hash, key, value, null);
                            if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                                treeifyBin(tab, hash);
                            break;
                        }
                        if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                            break;
                        p = e;
                    }
                }
                if (e != null) { // existing mapping for key
                    V oldValue = e.value;
                    if (!onlyIfAbsent || oldValue == null)
                        e.value = value;
                    afterNodeAccess(e);
                    return oldValue;
                }
            }
            ++modCount;
            if (++size > threshold)
                resize();
            afterNodeInsertion(evict);
            return null;
        }
  ```
  ++size不是原子操作，A和B线程同时拿到size=10,A线程操作完成之后执行+1操作为11，B线程在A之后完成也执行+1操作为11，B线程可能获取不到A的最新值，导致size出差。     
  
+ ConcurrentHashMap    
    ConcurrentHashMap是线程安全的。   
    ConcurrentHashMap在写入时会对 **0号桶加锁**，加锁期间别的线程需要等到锁释放之后才能写入这个桶。   
    ConcurrentHashMap的key和value时不能为空的。   
    在存储对象时，将key和value传入put方法：   
    - 如果没有初始化，就调用initTable()方法对数组进行初始化。   
    - 如果没有hash冲突则直接通过CAS进行无锁插入。   
    - 如果需要扩容就先进行扩容，扩容为原来大小的2倍。   
    - 如果出现hash冲突则通过枷锁的方式进行插入，从而保证线程安全。（如果是链表就按照尾插法插入，如果是红黑树就按照红黑树的数据结构进行插入）。    
    - 如果达到链表转红黑树条件，就将链表转为红黑树；    
    - 如果插入成功就调用addCount()方法计数，并检查是否需要扩容。    
    
    
 
  

  