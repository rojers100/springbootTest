package com.luojie.config.trace;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * TraceId拦截器
 * 用于在请求入口处生成traceId并放入MDC中，便于链路追踪
 */
@Slf4j
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    /**
     * TraceId的键名
     */
    public static final String TRACE_ID = "traceId";
    
    /**
     * 请求头中的TraceId键名
     */
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 尝试从请求头中获取traceId，用于跨服务调用时保持traceId一致
        String traceId = request.getHeader(TRACE_ID_HEADER);
        
        // 如果请求头中没有traceId，则生成一个新的
        if (StringUtils.isBlank(traceId)) {
            traceId = generateTraceId();
        }
        
        // 将traceId放入MDC中，使得日志输出中包含traceId
        MDC.put(TRACE_ID, traceId);
        
        // 将traceId放入响应头中，便于跨服务调用
        response.setHeader(TRACE_ID_HEADER, traceId);
        
        log.info("请求开始处理 - URL: {}, Method: {}, TraceId: {}", 
                request.getRequestURI(), request.getMethod(), traceId);
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        // 请求处理完成后的操作
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // 请求完全结束后，清除MDC中的traceId，防止内存泄漏
        log.info("请求处理完成 - URL: {}, TraceId: {}", 
                request.getRequestURI(), MDC.get(TRACE_ID));
        MDC.remove(TRACE_ID);
    }
    
    /**
     * 生成唯一的traceId
     * @return traceId字符串
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}