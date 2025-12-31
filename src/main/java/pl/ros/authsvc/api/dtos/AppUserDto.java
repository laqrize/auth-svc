package pl.ros.authsvc.api.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.ros.authsvc.api.enums.UserType;
import pl.ros.commons.dtos.AbstractDto;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto extends AbstractDto {

    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    @Email
    @Size(min = 3, max = 255)
    private String email;

    @NotNull
    private UserType type;

    private Boolean enabled;

    private Boolean locked;

    @JsonIgnore
    private String passwordHash;
}
