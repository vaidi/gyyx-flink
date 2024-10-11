package cn.gyyx.flink.operator;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.springframework.stereotype.Component;

/**
 * @Author: yel
 * @Date: 2024/10/8 11:25
 */
@Slf4j
@Component
public class MapOperatorDemo extends RichMapFunction<String, Tuple2<Integer, String>> {

    @Override
    public Tuple2<Integer, String> map(String value) throws Exception {
        String[] split = value.split(",");
        return Tuple2.of(Integer.parseInt(split[0]), split[1]);
    }
}
