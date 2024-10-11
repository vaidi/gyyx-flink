# Flink 实践

官方文档1.17

[概览 | Apache Flink](https://nightlies.apache.org/flink/flink-docs-release-1.17/zh/docs/dev/datastream/overview/)

项目组织架构

## maven依赖

见项目

## 重点

###  jobmanger和taskmanger和 task 和subtask 关系

![image-20241011103027746](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20241011103027746.png)

```
Source -> Map (parallelism = 4) -> Sink
```

#### 分解后的任务

- **Map Task**：包含 4 个 Subtask。
- **Source Task**：可能只有一个 Subtask（取决于并行度）。
- **Sink Task**：可能只有一个 Subtask（取决于并行度）。

#### 执行过程

1. **JobManager 分解任务**：
   - JobManager 将应用程序分解成 Source Task、Map Task 和 Sink Task。
2. **任务分配**：
   - JobManager 将 Source Task 分配给一个 TaskManager。
   - JobManager 将 Map Task 分配给多个 TaskManager（假设每个 TaskManager 有一个 TaskSlot，如集群中只有一个TaskManager或Task Slot资源紧张时，也可能多对1）。
   - JobManager 将 Sink Task 分配给另一个 TaskManager。
3. **TaskManager 执行任务**：
   - TaskManager 在各自的 TaskSlots 中执行分配给它们的 Task。

### 算子

> 用户通过算子能将一个或多个 DataStream 转换成新的 DataStream，在应用程序中可以将多个数据转换算子合并成一个复杂的数据流拓扑。

#### 1、数据转换流：

Map ，FlatMap ，Filter ，KeyBy ，Reduce ，Window ，WindowAll ，Window Apply，WindowReduce ，Union ，Window Join

Interval Join，

窗口函数有三种：`ReduceFunction`、`AggregateFunction` 或 `ProcessWindowFunction`。 前两者执行起来更高效（详见 [State Size](https://nightlies.apache.org/flink/flink-docs-release-1.17/zh/docs/dev/datastream/operators/windows/#关于状态大小的考量)）因为 Flink 可以在每条数据到达窗口后 进行增量聚合（incrementally aggregate）。 而 `ProcessWindowFunction` 会得到能够遍历当前窗口内所有数据的 `Iterable`，以及关于这个窗口的 meta-information。

1、增量聚合算子

#### ReduceFunction，AggregateFunction

2、全量聚合算子

#### ProcessWindowFunction

#### 2、窗口

1、滚动窗口Tumbling开头的（偏移量）

2、滑动窗口 （可以有重叠）

3、会话窗口

4、全局窗口

### 传输策略：

1. **ForwardPartitioner**：
   - 前提条件是上下游并行度相同。
   - 会将数据发到下游的对应分区（在Pointwise模式下下游的0号分区也对应着上游相关分区）。
2. **RebalancePartitioner**：
   - 当上下游算子不符合ForwardPartitioner使用条件时，Flink会默认选择此策略。
   - 会先随机选一个下游分区，之后轮询（round-robin）遍历下游所有分区进行数据传输。
3. **RescalePartitioner**：
   - 在Pointwise模式下会先根据上下游并行度进行匹配。
   - 再从匹配后的下游中从0号分区轮询传输数据。
4. **ShufflePartitioner**：
   - 会随机选取下游分区进行数据传输。
   - 由于Random生成的随机数符合均匀分布，因此能够大致保证下发的平均效果，类似于RebalancePartitioner。
5. **KeyGroupStreamPartitioner**：
   - 分区是根据消息的key值经过两层hash处理后获得的。
6. **GlobalPartitioner**：
   - 基于All-to-all的分发模式。
   - 能获得下游算子的全局分区号，保证只下发给下游算子的第一个分区。
7. **BroadcastPartitioner**：
   - 会下发给下游的每个分区，不需要选择。
8. **CustomPartitionerWrapper**：
   - 需要用户指定为消息的每个key设置下游分区的选择规则。

#### 故障恢复，jvm参数

​	部署脚本中实现，代码中不做操作



### 已发现的问题

#### 1、 匿名类的时候指定输出类型

```
DataStream<Tuple2<String, Long>> timestampedStream = textStream
        .map(new MapFunction<String, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(String value) throws Exception {
                System.out.println("value" + value);
                // 假设输入数据格式为 "key,timestamp"
                String[] parts = value.split(",");
                return new Tuple2<>(parts[0], Long.parseLong(parts[1]));
            }
        })
        .returns(Types.TUPLE(Types.STRING, Types.LONG)); // 指定输出类型
```

#### 2、算子之间传递对象（对象属性不能为空）

#### 3、日志问题（已解决）

#### 4、k8s的密钥问题读取不到（已解决）

#### 5、版本依赖关系

​		脚手架中已进行规范，如果本地idea执行没问题，发现传到flink任务上去，结果报classnotFund或者类未定义 都是jar包冲突导致的，把一些flink本身lib中存在的包，在pom中score改成provided

#### 4、nfs 进行挂载保存checkpoint

#### 5、rocksdb 内嵌，进行内存不够的时候磁盘落

#### 6、注意算子之间的传输策略防止重复消费

#### 7、与springboot结合，读取k8s secret的关系

​	 使用ApplicationContextHolder来获取类

