package cn.gyyx.flink.task;

import cn.gyyx.flink.context.ApplicationContextHolder;
import cn.gyyx.flink.operator.MapOperatorDemo;
import cn.gyyx.flink.operator.ProcessionWindowDemo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.evictors.CountEvictor;
import org.apache.flink.streaming.api.windowing.evictors.Evictor;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;

/**
 * @Author: yel
 * @Date: 2024/10/8 11:13
 */

@Slf4j
public class KafkaToEsTask {

    public static void main(String[] args) throws Exception {
        log.info("init start flink task");
        MapOperatorDemo mapOperator = ApplicationContextHolder.getBean(MapOperatorDemo.class);
        ProcessionWindowDemo windowDemo = ApplicationContextHolder.getBean(ProcessionWindowDemo.class);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //总体并行度不要在这里指定 在部署文件中去指定并行度
        env.setParallelism(1);
        env.enableCheckpointing(10000);
       // env.setRestartStrategy();
        DataStreamSource<String> dataStreamSource = env.socketTextStream("10.12.54.64", 7777);
        SingleOutputStreamOperator<Tuple2<Integer, String>> streamOperator = dataStreamSource.map(mapOperator);
        AllWindowedStream<Tuple2<Integer, String>, GlobalWindow> tuple2GlobalWindowAllWindowedStream = streamOperator.countWindowAll(3);
        // 设置基于计数的驱逐策略
        CountEvictor<Window> countEvictor = CountEvictor.of(3);// 设置最大计数值为 3
        AllWindowedStream<Tuple2<Integer, String>, GlobalWindow> evictor = tuple2GlobalWindowAllWindowedStream.evictor(countEvictor);
        SingleOutputStreamOperator<String> process = evictor.process(windowDemo);
        process.print();
        env.execute();
    }

}
