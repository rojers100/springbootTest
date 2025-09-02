package com.luojie.config.filter;

import com.luojie.context.UserContext;
import com.luojie.moudle.UserModel;
import com.luojie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class UserAuthFilter implements Filter {

    private final UserService userService;

    // 通过构造函数注入
    public UserAuthFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("执行了过滤器！");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 获取token
            String token = httpRequest.getHeader("token");
            if (!StringUtils.hasText(token)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("text/plain;charset=UTF-8");
                httpResponse.getWriter().write("Missing token");
                return;
            }

            // 获取用户信息
            log.info("------请求开始----");
            UserModel user = userService.getUserByToken(token);
            log.info("------请求结束----");
            if (user == null) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("text/plain;charset=UTF-8");
                httpResponse.getWriter().write("Invalid token");
                return;
            }

            // 设置用户上下文
            UserContext.setCurrentUser(user);

            // 继续处理请求
            chain.doFilter(request, response);
        } finally {
            // 清理用户上下文
            UserContext.clear();
            log.info("------filter清理用户上下文----");
        }
    }
}
