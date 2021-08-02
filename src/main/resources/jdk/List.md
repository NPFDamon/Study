## List
### [ArrayList](https://bugstack.cn/interview/2020/08/27/%E9%9D%A2%E7%BB%8F%E6%89%8B%E5%86%8C-%E7%AC%AC7%E7%AF%87-ArrayList%E4%B9%9F%E8%BF%99%E4%B9%88%E5%A4%9A%E7%9F%A5%E8%AF%86-%E4%B8%80%E4%B8%AA%E6%8C%87%E5%AE%9A%E4%BD%8D%E7%BD%AE%E6%8F%92%E5%85%A5%E5%B0%B1%E6%8A%8A%E8%B0%A2%E9%A3%9E%E6%9C%BA%E9%9D%A2%E6%99%95%E4%BA%86.html)
### [LinkedList](https://bugstack.cn/interview/2020/08/30/%E9%9D%A2%E7%BB%8F%E6%89%8B%E5%86%8C-%E7%AC%AC8%E7%AF%87-LinkedList%E6%8F%92%E5%85%A5%E9%80%9F%E5%BA%A6%E6%AF%94ArrayList%E5%BF%AB-%E4%BD%A0%E7%A1%AE%E5%AE%9A%E5%90%97.html)

+ ArrayList，LinkedList   
    ArrayList：基于数组，初始容量为10，扩容时扩容为newCapacity = oldCapacity + (oldCapacity >> 1);即旧容量 + 旧容量右移一位，相当于扩容了原来容量的（int）3/2。   
    LinkedList:基于双向链表。   
    
    * 插入：   
    头插：   
    ArrayList头插时需要把元素通过Arrays.copyOf的方式把数组元素移位，如果容量不做还需要扩容。   
    LinkedLIst头插时不允许要考虑扩容和位移的问题，直接把元素定位到首位，接点链条链接上即可。   
    因此，头插时LinkedList速度会比较快。   
    尾插：   
    ArrayList尾插不需要位移数据，比较耗时的是扩容操作，需要拷贝迁移数据。   
    LinkedList尾插时，与头插相比耗时点会在对象的实例化上。    
    中间插：   
    ArrayList 中间插入，首先我们知道他的定位时间复杂度是O(1)，比较耗时的点在于数据迁移和容量不足的时候扩容。    
    LinkedList 中间插入，链表的数据实际插入时候并不会怎么耗时，但是它定位的元素的时间复杂度是O(n)，所以这部分以及元素的实例化比较耗时。     
    
    