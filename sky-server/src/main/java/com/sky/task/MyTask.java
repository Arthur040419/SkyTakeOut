package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class MyTask {


    //定义任务触发时间
    //@Scheduled(cron = "0/5 * * * * ? ")
    //定时任务的返回值只能是void
    public void myTask(){
        log.info("定时任务触发了：{}", LocalDateTime.now());
    }
}
