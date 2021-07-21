## MySQL MVVC
    多版本并发控制。

+ **MVVC**   
    在InnoDB中，会在每行数据后增加两个额外的隐藏字段来实现MVVC，这两个值一个记录这行数据是何时被创建的，一个记录这行数据何时过期(或者被删除)。
    在时间存储中存储的并不是时间，而是版本号，没开启一个事务，版本号都会递增。   
    在RR隔离级别下：   
    + Select时，读取创建版本号<=当前事务版本号，删除版本号为空或大于当前事务版本号。   
    + INSERT时，保存当前创建版本号为当前事务版本号。   
    + DELETE时，保存当前创建版本号为行删除版本号。   
    + UPDATE时，插入一条新纪录，保存当时事务版本号为行创建版本号，同事保存当前事务版本号到之前删除的行。   
    通过MVCC，虽然每行记录都需要额外的存储空间，更多的行检查工作以及一些额外的维护工作，但可以减少锁的使用，大多数读操作都不用加锁，读数据操作很简单，
    性能很好，并且也能保证只会读取到符合标准的行，也只锁住必要行。   
+ **快照读和当前读**    
    + 快照读(snapshot read)：读取记录可见版本的数据(有可能是历史数据)。在InnoDB下：简单的Select操作,属于快照读 `select * from table ….`;    
        快照读的实现方式为MVVC+undo log 
    + 当前读(current read)：读取当前最新版本数据。特殊的操作，插入/更新/删除操作，属于当前读，需要加锁(使用next-key(行锁+间隙锁)算法实现)   
    `select * from table where ? lock in share mode;
     select * from table where ? for update;
     insert into table values (…);
     update table set ? where ?;
     delete from table where ?;`   
     事务的隔离级别实际上都是定义了当前读的级别，MySQL为了减少锁处理（包括等待其它锁）的时间，提升并发能力，引入了快照读的概念，使得select不用加锁。
     而update、insert这些“当前读”，就需要另外的模块来解决了。        
       