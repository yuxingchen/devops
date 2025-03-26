package com.devops.util;

import com.devops.common.BusinessException;
import com.devops.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户信息
 *
 * @author yux
 */
@Component
public class UserContextUtil {

    private static JwtUtils jwtUtils;

    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        UserContextUtil.jwtUtils = jwtUtils;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        if (!isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
            return jwtUtils.getUserIdFromToken(jwtUtils.getJwtFromRequest(request));
        } catch (Exception e) {
            throw new BusinessException("获取用户ID失败");
        }
    }

    /**
     * 判断用户是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
} 