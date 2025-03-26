package com.devops.aspect;

import com.devops.util.MDCUtil;
import com.devops.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 日志追踪切面
 *
 * @author yux
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class TraceIdAspect {

    @Pointcut("execution(* com.devops.controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            // 生成traceId
            String traceId = TraceIdUtil.generateTraceId();
            // 设置到ThreadLocal
            TraceIdUtil.setTraceId(traceId);
            // 设置到MDC
            MDCUtil.setTraceId(traceId);

            // 记录请求日志
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

            log.info("请求开始 - URL: {}, Method: {}, IP: {}, Class Method: {}.{}",
                    request.getRequestURL().toString(),
                    request.getMethod(),
                    request.getRemoteAddr(),
                    point.getSignature().getDeclaringTypeName(),
                    point.getSignature().getName());

            // 执行目标方法
            Object result = point.proceed();

            log.info("请求结束 - 耗时: {}ms", System.currentTimeMillis() - startTime);
            return result;
        } finally {
            // 清除traceId
            TraceIdUtil.clear();
            MDCUtil.clear();
        }
    }
} 