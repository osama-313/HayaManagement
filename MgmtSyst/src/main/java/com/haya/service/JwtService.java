package com.haya.service;

import com.haya.modles.User;
import com.haya.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRITE_KEY = "98747f3a2b14eb887288bf3365bcf5b1c7fe4eb4e79db14c7ee23bde6d607610";
    private final TokenRepository tokenRepository;

    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String extractUsername(String token){

        return extractClaim(token,Claims::getSubject);
    }

    public boolean isValid(String token, UserDetails user){
        String username = extractUsername(token);

        boolean validToken = tokenRepository
                .findByToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        return (username.equals(user.getUsername())) && !isTokenExpired(token) && validToken;
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){

        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims,T> resolver){

        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);

    }


    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSinginKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

     public String generateToken(User user){
         String token = Jwts
                 .builder()
                 .subject(user.getUserName())
                 .issuedAt(new Date(System.currentTimeMillis()))
                 .expiration(new Date(System.currentTimeMillis() + 86400000 )) // 1 day in milliseconds
                 .signWith(getSinginKey())
                 .compact();
         return token;

     }

     private SecretKey getSinginKey(){
         byte[] keyBytes = Decoders.BASE64.decode(SECRITE_KEY);
         return Keys.hmacShaKeyFor(keyBytes);
     }

}
