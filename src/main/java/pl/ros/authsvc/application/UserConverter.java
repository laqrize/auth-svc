package pl.ros.authsvc.application;

import org.springframework.stereotype.Component;
import pl.ros.authsvc.api.dtos.AppUserDto;
import pl.ros.authsvc.api.enums.UserType;
import pl.ros.authsvc.core.AppUser;
import pl.ros.commons.converters.IStandardRecordConverter;
import pl.ros.commons.utils.NpeUtils;

@Component
public class UserConverter implements IStandardRecordConverter<AppUserDto, AppUser> {
    @Override
    public AppUser toEntity(AppUserDto dto) {
        AppUser user = AppUser.builder()
                .id(dto.getId())
                .type(NpeUtils.getName(dto.getType()))
                .locked(dto.getLocked())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .passwordHash(dto.getPasswordHash())
                .enabled(dto.getEnabled())
                .build();
        setCommonFieldsToEntity(dto, user);
        return user;
    }

    @Override
    public AppUserDto toDto(AppUser entity) {
        AppUserDto dto =  AppUserDto.builder()
                .id(entity.getId())
                .type(NpeUtils.getValue(entity.getType(), UserType.class))
                .locked(entity.getLocked())
                .enabled(entity.getEnabled())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .passwordHash(null) // password is not needed in dto
                .build();
        setCommonFieldsToDTO(dto, entity);
        return dto;
    }

}
