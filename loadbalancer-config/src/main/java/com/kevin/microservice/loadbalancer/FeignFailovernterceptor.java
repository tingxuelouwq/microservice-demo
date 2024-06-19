package com.kevin.microservice.loadbalancer;

import com.kevin.microservice.common.BizConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * kevin<br/>
 * 2024/6/19 14:28<br/>
 */
public class FeignFailovernterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String value = request.getHeader(name);
                    template.header(name, value);

                    if (name.equals(BizConstant.ENV_HEADER)) {
                        if (value.equals(BizConstant.ZS_ENV_VALUE)) {
                            FailoverRequestContextHolder.setEnvTag(BizConstant.ZS_ENV_VALUE);
                        } else if (value.equals(BizConstant.DB_ENV_VALUE)) {
                            FailoverRequestContextHolder.setEnvTag(BizConstant.DB_ENV_VALUE);
                        }
                    }
                }
            }
        }
    }
}
