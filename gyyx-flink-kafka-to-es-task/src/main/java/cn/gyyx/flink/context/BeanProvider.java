package cn.gyyx.flink.context;


/**
 * @Author: yel
 * @Date: 2024/10/8 11:19
 */
public class BeanProvider {
    private BeanProvider() {
    }

    /**
     * 远端配置文件地址
     */
    //public static final  String CONFIG_FILE_URL = "/data/conf/application.yml";
    public static final String CONFIG_FILE_URL = "classpath:/application.yml";
}
