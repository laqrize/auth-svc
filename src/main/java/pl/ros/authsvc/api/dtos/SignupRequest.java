package pl.ros.authsvc.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignupRequest(@NotBlank @Size(min = 3) String username, @NotBlank @Email @Size(min = 3) String email,
                            @NotBlank @Size(min = 3) String password) {



}
