package cn.gyyx.flink.context;

import cn.gyyx.flink.KafkaToEsFlinkTaskApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring bean 上下文类的获取
 * @Author: yel
 * @Date: 2024/10/8 11:18
 */
@Slf4j
@Component
public class ApplicationContextHolder  implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("初始化applicationContext{}", applicationContext);
        ApplicationContextHolder.applicationContext = applicationContext;
    }


    public static <T> T getBean(Class<T> requiredType) {
        if (applicationContext == null) {
            System.setProperty("spring.config.location",BeanProvider.CONFIG_FILE_URL);
            //KafkaToEsFlinkTaskApplication 这里 要写启动类
            SpringApplication application = new SpringApplication(KafkaToEsFlinkTaskApplication.class);
            application.setBannerMode(Banner.Mode.OFF);
            applicationContext = application.run("context");
        }
        return applicationContext.getBean(requiredType);
    }

}
