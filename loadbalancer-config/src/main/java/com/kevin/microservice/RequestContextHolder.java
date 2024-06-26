package com.kevin.microservice;

/**
 * kevin<br/>
 * 2024/6/19 21:18<br/>
 */
public class RequestContextHolder {

    private static final ThreadLocal<String> envTag = new ThreadLocal<>();

    public static void setEnvTag(String env) {
        envTag.set(env);
    }

    public static String getEnvTag() {
        return envTag.get();
    }

    public static void clear() {
        envTag.remove();
    }
}
