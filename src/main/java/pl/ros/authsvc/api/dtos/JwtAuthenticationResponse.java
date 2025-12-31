package pl.ros.authsvc.api.dtos;

import lombok.Builder;

@Builder
public record JwtAuthenticationResponse(String token, String refreshToken) {
}
