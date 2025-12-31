package pl.ros.authsvc.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SigninRequest(@NotBlank @Email String username, String password) {
}
