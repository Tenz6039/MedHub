package com.medhub.aspect;

import com.medhub.annotation.AutoFill;
import com.medhub.constant.AutoFillConstant;
import com.medhub.context.BaseContext;
import com.medhub.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 切面类，用于处理自动填充注解
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.medhub.mapper.*.*(..)) && @annotation(com.medhub.annotation.AutoFill)")
    public void autoFillPointcut() {

    }
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("自动填充注解触发");

        // 获得被拦截方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 从连接点获取方法参数
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || args[0] == null) {
            return;
        }
        Object entity = args[0];

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        if (operationType == OperationType.INSERT){
            try {
                Method setCreateTimeMethod = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUserMethod = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTimeMethod = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUserMethod = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 为插入操作赋值
                setCreateTimeMethod.invoke(entity, now);
                setCreateUserMethod.invoke(entity, currentId);
                setUpdateTimeMethod.invoke(entity, now);
                setUpdateUserMethod.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("自动填充插入操作失败", e);
            }
        }else if (operationType == OperationType.UPDATE){
            try {
                Method setUpdateTimeMethod = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUserMethod = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 为更新操作赋值
                setUpdateTimeMethod.invoke(entity, now);
                setUpdateUserMethod.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("自动填充更新操作失败", e);
            }
        }

    }
}
