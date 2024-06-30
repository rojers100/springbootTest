package com.luojie.postprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DecryptEnvironmentPostProcessor implements EnvironmentPostProcessor {

    // 可只对需要解密的配置进行解密
    private String pre;

    /**
     * 对全部配置文件进行加解密
     *
     * @param environment
     * @param application
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> decryptedProperties = new HashMap<>();
        pre = environment.getProperty("pre.fix");
        environment.getPropertySources().forEach(propertySource -> {
            if (propertySource instanceof OriginTrackedMapPropertySource) {
                OriginTrackedMapPropertySource source = (OriginTrackedMapPropertySource) propertySource;
                source.getSource().forEach((key, value) -> {
                    String strV = String.valueOf(value);
                    if ((strV.startsWith(pre)) && strV.length() > pre.length()) {
                        String decryptedValue = decrypt(strV);
                        decryptedProperties.put(key, decryptedValue);
                    } else {
                        decryptedProperties.put(key, value);
                    }
                });
                // 去掉原来的property
                environment.getPropertySources().remove(propertySource.getName());
                // 增加为新的property
                environment.getPropertySources().addLast(new OriginTrackedMapPropertySource(propertySource.getName(), decryptedProperties));
            }
        });
    }


    private String decrypt(String encryptedValue) {
        // 还原本来应该的属性值
        encryptedValue = encryptedValue.substring(pre.length());
        // 在这里编写你的解密逻辑，这里假设是简单的Base64解码
        return new String(Base64.getDecoder().decode(encryptedValue));
    }

    private String encrypt(String value) {
        return new String(Base64.getEncoder().encode(value.getBytes()));
    }

//    public static void main(String[] args) {
//        DecryptEnvironmentPostProcessor processor = new DecryptEnvironmentPostProcessor();
//        String s = processor.encrypt("lj1004018277");
//        System.out.println(s);
//    }
}