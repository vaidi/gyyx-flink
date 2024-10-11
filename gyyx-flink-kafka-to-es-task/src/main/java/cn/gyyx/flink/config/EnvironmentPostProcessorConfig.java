package cn.gyyx.flink.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * 为了读取k8s的secret 创建的变量
 * @Author: yel
 * @Date: 2024/10/8 11:04
 */
@Slf4j
public class EnvironmentPostProcessorConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            ApiClient client = Config.defaultClient();
            io.kubernetes.client.openapi.Configuration.setDefaultApiClient(client);
            CoreV1Api coreV1Api = new CoreV1Api();
            String SECRET_NAME = "jiasj-secret";
            V1Secret secret = coreV1Api.readNamespacedSecret(SECRET_NAME, "growth-local", null, null, null);
            // 创建一个包含要覆盖的属性的Map
            Map<String, Object> customProperties = new HashMap<>();
            Map<String, byte[]> secretData = secret.getData();
            if (MapUtils.isNotEmpty(secretData)) {
                for (String secretKey : secretData.keySet()) {
                    customProperties.put(secretKey, new String(secretData.get(secretKey)));
                }
            }
            log.info("postProcessEnvironment :{}",customProperties);
            // 将这些属性作为一个新的PropertySource添加到环境中
            MutablePropertySources propertySources = environment.getPropertySources();
            MapPropertySource customPropertySource = new MapPropertySource("customProperties", customProperties);
            propertySources.addFirst(customPropertySource); // 确保它优先*/
        } catch (Exception e) {
            log.error("postProcessEnvironment", e);
        }
    }

}
