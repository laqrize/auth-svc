package pl.ros.authsvc.api.dtos;


public record AuthResponse(AppUserDto user, String token) {
}
