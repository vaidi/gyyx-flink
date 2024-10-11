package cn.gyyx.flink;

import cn.gyyx.flink.task.KafkaToEsTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: yel
 * @Date: 2024/10/8 11:07
 */
@SpringBootApplication
public class KafkaToEsFlinkTaskApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(KafkaToEsFlinkTaskApplication.class, args);
        KafkaToEsTask.main(new String[]{});
    }

}
