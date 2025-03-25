package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类，用于拦截方法，实现公共字段自动填充
 */
@Component
@Aspect
@Slf4j
public class AutoFillAspect {

    /**
     *  切入点表达式
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    private void pt(){}

    /**
     * 在前置通知中实现自动公共字段填充
     */
    @Before("pt()")
    public void autoFill(JoinPoint joinPoint){
        //获取连接点方法的数据库操作类型，根据操作类型来判断要如何操作
        //数据库操作类型在自定义注解AutoFill的属性里面，所以要先获取方法的注解对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法上的注解
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        //获取注解对象中的数据库操作类型
        OperationType operationType = annotation.value();

        //获取连接点方法的实体类对象，我们约定将实体类对象放在方法形参列表的第一个
        Object[] args = joinPoint.getArgs();
        //处理异常情况，如果方法没有参数
        if(args==null&&args.length==0){
            return;
        }
        //因为不同表的实体类不一样，所以这里统一用Object来接收
        Object entity = args[0];

        //准备好要传入的数据
        LocalDateTime now = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();

        //获取实体类对象的set方法来为对象赋值，根据数据库操作类型的不同，要获取的set方法也不同
        if(operationType==OperationType.INSERT){
            //如果是插入操作，就要获取四个set方法
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //执行set方法来为实体类对象赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,id);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,id);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(operationType==OperationType.UPDATE){
            //如果是更新方法，就只需要获取两个set方法
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //执行set方法来为实体类对象赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
