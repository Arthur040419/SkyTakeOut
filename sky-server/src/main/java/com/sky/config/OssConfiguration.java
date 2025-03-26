package com.sky.config;


import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


//oss配置类，用于生成一个阿里云oss工具类到IOC容器中
@Configuration
@Slf4j
public class OssConfiguration{ //extends WebMvcConfigurationSupport {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云工具类：{}",aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getBucketName(),
                aliOssProperties.getRegion(),
                aliOssProperties.getFilePath());

    }

}
