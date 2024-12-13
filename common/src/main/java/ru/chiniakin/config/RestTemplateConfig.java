package ru.chiniakin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурационный класс для настройки RestTemplate.
 *
 * @author ChiniakinD
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Создает и настраивает экземпляр RestTemplate.
     *
     * @return настроенный экземпляр RestTemplate.
     */

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
