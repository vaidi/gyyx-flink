package cn.gyyx.flink.operator;

import cn.gyyx.flink.context.ApplicationContextHolder;
import cn.gyyx.flink.service.BizService;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * @Author: yel
 * @Date: 2024/10/8 13:34
 */
@Slf4j
@Service
public class ProcessionWindowDemo extends ProcessAllWindowFunction<Tuple2<Integer,String>,String, GlobalWindow>{
    @Override
    public void process(Context context, Iterable<Tuple2<Integer, String>> elements, Collector<String> out) throws Exception {
        long maxTimestamp = context.window().maxTimestamp();
        BizService bean = ApplicationContextHolder.getBean(BizService.class);
        bean.testBiz();
        log.info("process maxTimestamp:{},elements size:{}",maxTimestamp,elements.spliterator().estimateSize());
        Iterator<Tuple2<Integer, String>> iterator = elements.iterator();
        StringBuilder sb = new StringBuilder();
        int i =0;
        while (iterator.hasNext()){
            Tuple2<Integer, String> next = iterator.next();
            sb.append("排位：").append(i++).append("，tuple:").append(next.toString());
        }
        out.collect(sb.toString());

    }
}
