package ru.chiniakin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.chiniakin.interceptor.AuthInterceptor;

/**
 * Конфигурационный класс для настройки интерсепторов.
 *
 * @author ChiniakinD
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    /**
     * Добавляет интерсепторы в реестр интерсепторов.
     *
     * @param registry реестр интерсепторов
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/task/**")
                .addPathPatterns("/comment/**")
                .addPathPatterns("/user/**");
    }

}
