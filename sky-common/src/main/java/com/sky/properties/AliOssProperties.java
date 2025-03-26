package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    //桶地址
    private String endpoint;
    //桶名称
    private String bucketName;
    //桶所在区域
    private String region;
    //文件存储路径
    private String filePath;

}
