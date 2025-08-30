package api.newsletter.web.mapper;

import api.newsletter.model.Subscriber;
import api.newsletter.web.dto.SubscriberRegisterDto;
import api.newsletter.web.dto.SubscriberResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriberMapper {

    SubscriberMapper INSTANCE = Mappers.getMapper(SubscriberMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "subscriptionDate", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    Subscriber toEntity(SubscriberRegisterDto subscriberRegisterDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "subscriptionDate", ignore = true)
    SubscriberResponseDto toDto(Subscriber subscriber);
}