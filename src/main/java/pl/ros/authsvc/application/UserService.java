package pl.ros.authsvc.application;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pl.ros.authsvc.api.dtos.AppUserDto;
import pl.ros.authsvc.core.AppUser;
import pl.ros.authsvc.core.UserRepository;
import pl.ros.commons.enums.CrudOperation;
import pl.ros.commons.enums.EntityStatus;
import pl.ros.commons.exceptions.ConflictException;
import pl.ros.commons.services.crud.AbstractCrudService;
import pl.ros.commons.utils.NpeUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractCrudService<AppUserDto, AppUser> {
    private final UserRepository userRepository;
    @Value("${system.user.email}")
    private String systemUserEmail;
    public UserDetailsService userDetailsService() {
        return this::findEntityByEmail;
    }

    @Override
    @Transactional
    public void delete(@NonNull Long id) {
        AppUser user = findById(id);
        user.setEnabled(false);
        repository.save(user);
        super.delete(id);
    }

    public AppUserDto findByEmail(@NonNull String email) {
        return converter.toDto(findEntityByEmail(email));
    }

    public AppUser findEntityByEmail(@NonNull String email) {
        return userRepository.findActualByEmail(email).orElseThrow(() -> new IllegalArgumentException(String.format("User with email %s not found", email)));
    }

    @Override
    protected void setEntityFields(AppUser entity, AppUserDto dto) {
        entity.setType(NpeUtils.getName(dto.getType()));
        entity.setLocked(dto.getLocked() != null && dto.getLocked());
    }

    @Override
    protected void validate(AppUserDto dto, @NonNull CrudOperation mode) {
        super.validate(dto, mode);
        List<AppUser> optUsers = userRepository.findActualByEmailOrUsername(dto.getEmail(), dto.getUsername());
        if(optUsers.stream().anyMatch(a -> !a.getId().equals(dto.getId()))) {
            throw new ConflictException("User with email already exists");
        }

        if (mode == CrudOperation.CREATE) {
            Assert.notNull(dto.getPasswordHash(), "Password cannot be null");
        }
    }

    public List<AppUserDto> findAllActive() {
        return converter.toDtoList(userRepository.findAllByStatusIn(Collections.singletonList(EntityStatus.CURRENT.getCode())))
                .stream().filter(userDto -> !Arrays.asList(systemUserEmail, contextService.getCurrentUser().email()).contains(userDto.getEmail())).collect(Collectors.toList());
    }

}