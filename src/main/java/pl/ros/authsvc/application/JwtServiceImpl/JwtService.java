package pl.ros.authsvc.application.JwtServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.ros.authsvc.application.IJwtService;
import pl.ros.authsvc.core.AppUser;
import pl.ros.commons.enums.JwtClaims;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService implements IJwtService {

    @Value("${jwt.expiration.minutes:15}")
    private int expirationMinutes;

    @Value("${jwt.refresh.token.expiration.multiplier:7}")
    private int jwtRefreshTokenExpirationMultiplier;

    @Value("${jwt.signing.key:}")
    private String jwtSignKey;

    private static final String AUTHORITIES = "authorities";

//    previous
//    public String generateToken(UserDetails userDetails) {
//        return Jwts.builder().setSubject(userDetails.getUsername())
//                .claim(AUTHORITIES, userDetails.getAuthorities())
//                .setIssuedAt(new Date())
//                .setExpiration(Date.from(LocalDateTime.now()
//                        .plusMinutes(expirationMinutes).toInstant(ZoneOffset.UTC)))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//
//    }

    public String generateToken(UserDetails user) {
        if(!(user instanceof AppUser)) {
            throw new IllegalArgumentException("User must be of type AppUser");
        }
        AppUser appUser = (AppUser) user;
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaims.USER_ID.getName(), appUser.getId());
        claims.put(JwtClaims.EMAIL.getName(), appUser.getEmail());
        claims.put(JwtClaims.ROLES.getName(), List.of(appUser.getType()));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now()
                        .plusMinutes(expirationMinutes).toInstant(ZoneOffset.UTC)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim(AUTHORITIES, userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(expirationMinutes * jwtRefreshTokenExpirationMultiplier)
                        .toInstant(ZoneOffset.UTC)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    private SecretKey getSigningKey() {
        byte[] key = Decoders.BASE64.decode(jwtSignKey);
        return Keys.hmacShaKeyFor(key);
    }

}
