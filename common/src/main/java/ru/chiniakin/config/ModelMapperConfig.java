package ru.chiniakin.config;

import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.chiniakin.entity.User;

import java.util.UUID;

/**
 * Конфигурационный класс для настройки ModelMapper.
 *
 * @author ChiniakinD
 */
@Configuration
public class ModelMapperConfig {


    @Bean
    public ModelMapper modelMapper() {
        class UserModelConverter implements Converter<User, UUID> {

            @Override
            public UUID convert(MappingContext<User, UUID> mappingContext) {
                return mappingContext.getSource().getId();
            }
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new UserModelConverter());

        return modelMapper;
    }

    /**
     * Создает modelMapper для слияния объектов, который соединяет только не null поля.
     *
     * @return modelMapper.
     */
    @Bean
    public ModelMapper mergeMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

}
