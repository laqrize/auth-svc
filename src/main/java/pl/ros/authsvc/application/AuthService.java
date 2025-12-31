package pl.ros.authsvc.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.ros.authsvc.api.dtos.*;
import pl.ros.authsvc.api.enums.UserType;
import pl.ros.authsvc.core.AppUser;
import pl.ros.commons.services.context.ContextService;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final ContextService contextService;
    private final UserConverter userConverter; //nw czy to nie antypattern

    public AuthResponse signup(SignupRequest request) {
        AppUserDto appUserDto = addUser(request);
        return signin(SigninRequest.builder()
                .username(request.username())
                .password(request.password())
                .build());
    }

    private AppUserDto addUser(SignupRequest request) {
        AppUserDto appUserDto = AppUserDto.builder()
                .email(request.username())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .type(UserType.USER)
                .enabled(true)
                .locked(false)
                .build();
        if (!contextService.isAuthenticated()) {
            contextService.setSystemUserInContext();
        }

        return userService.create(appUserDto);
    }

    public AuthResponse signin(SigninRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        AppUser userDetails = userService.findEntityByEmail(request.username());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(userConverter.toDto(userDetails), token);

    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.getUsernameFromToken(refreshTokenRequest.token());
        UserDetails userDetails = userService.findEntityByEmail(userEmail);
        if (!jwtService.isTokenValid(refreshTokenRequest.token(), userDetails)) {
            throw new IllegalArgumentException("Invalid token");
        }
        String token = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateToken(userDetails);
        return JwtAuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

}
