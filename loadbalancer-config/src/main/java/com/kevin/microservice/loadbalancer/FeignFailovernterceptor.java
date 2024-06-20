package com.kevin.microservice.loadbalancer;

import com.kevin.microservice.common.BizConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * kevin<br/>
 * 2024/6/19 14:28<br/>
 */
public class FeignFailovernterceptor implements RequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void apply(RequestTemplate template) {
        logger.info("thread: {}", Thread.currentThread().getName());
        String envTag = FailoverRequestContextHolder.getEnvTag();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String value = request.getHeader(name);

                    if (name.equals(BizConstant.ENV_HEADER) && StringUtils.hasLength(envTag)) {
                        logger.info("自动故障转移, old-request-env: {}, new-request-env: {}", value, envTag);
                        value = envTag;
                        // 清除线程局部变量标记
                        FailoverRequestContextHolder.clear();
                    }

                    template.header(name, value);
                }
            }
        }
    }
}
