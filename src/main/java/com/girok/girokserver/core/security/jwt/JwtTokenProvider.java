package com.girok.girokserver.core.security.jwt;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.core.security.jwt.dto.JwtTokenDto;
import com.girok.girokserver.core.security.jwt.dto.JwtUserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.girok.girokserver.core.exception.ErrorInfo.*;

@Component
public class JwtTokenProvider {

    private final Key key;

    @Value("${jwt.access_token_expiration_ms}")
    private long accessTokenExpirationTimeMs;

    @Value("${jwt.refresh_token_expiration_ms}")
    private long refreshTokenExpirationTimeMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtTokenDto generateToken(Long userId) {
        Date accessTokenExpiration = getTokenExpiration(accessTokenExpirationTimeMs);
        Date refreshTokenExpiration = getTokenExpiration(refreshTokenExpirationTimeMs);

        String accessToken = Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(accessTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Date getTokenExpiration(long expirationMs) {
        long now = (new Date()).getTime();
        return new Date(now + expirationMs);
    }

    public JwtUserInfo validateAndExtractUserInfo(String token) {
        try {
            Jws<Claims> parsedToken = Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            Long userId = Long.parseLong(parsedToken.getBody().getSubject());
            return new JwtUserInfo(userId);
        } catch (SecurityException | MalformedJwtException e) {
            throw new CustomException(INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(INVALID_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(INVALID_JWT_TOKEN);
        }
    }
}
