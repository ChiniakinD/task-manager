package ru.chiniakin.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.chiniakin.entity.Role;
import ru.chiniakin.entity.User;
import ru.chiniakin.model.SecurityUser;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.service.interfaces.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Реализация сервиса {@link JwtService} для создания, валидации и извлечения информации из JWT токенов.
 *
 * @author ChiniakinD
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${token.signing.key}")
    private String secretKey;

    @Value("${token.expiration}")
    private Integer expiration;

    private final UserRepository userRepository;

    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * {@inheritDoc}
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * {@inheritDoc}
     */
    public String generateToken(SecurityUser user) {
        Map<String, Object> claimsForUser = createClaimsForUser(user);
        return createToken(claimsForUser, user);
    }

    /**
     * {@inheritDoc}
     */
    public String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)
                ? authHeader.substring(BEARER_PREFIX.length())
                : null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * {@inheritDoc}
     */
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = getByUsername(username);
            return SecurityUser.build(user);
        };
    }

    private User getByUsername(String username) {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    /**
     * Получает данный параметр из токена
     *
     * @param token           JWT токен
     * @param claimsResolvers функция для получения.
     * @return требование из токена.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Создает токен с дополнительными данными.
     */
    private String createToken(Map<String, Object> claims, SecurityUser user) {
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 60 * 1000L))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Создает мапу с параметрами для токена.
     */
    private Map<String, Object> createClaimsForUser(SecurityUser user) {
        Set<Role> roles = userRepository.findUserByLoginOrThrow(user.getUsername()).getRoles();
        user.setRoles(roles);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", roles.stream()
                .map(role -> role.getRole().name())
                .collect(Collectors.toList()));

        return claims;
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Получает все параметры из токена.
     *
     * @param token JWT токен.
     * @return все параметры из токена.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Получает ключ для подписания JWT токена.
     *
     * @return ключ.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
