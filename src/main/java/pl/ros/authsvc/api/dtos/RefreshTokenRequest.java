package pl.ros.authsvc.api.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest (@NotBlank String token){

}
