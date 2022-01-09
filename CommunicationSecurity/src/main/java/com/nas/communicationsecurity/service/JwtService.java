package com.nas.communicationsecurity.service;

import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.UserView;
import com.nas.persistence.repository.UserViewRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {
    @Autowired
    private UserViewRepository userViewRepository;

    private final String SECRET_KEY = "secret";

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Object extractClaim(String token, String claim) {
        return extractAllClaims(token).get(claim);
    }

    public String generateToken(UserDetailsDTO userDetails) {
        return generateTokenWithFields(userDetails, new HashMap<>(), 10L);
    }

    public String generateToken(UserDetailsDTO userDetails, String[] pathFiles, String parentFolder) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("pathFiles", pathFiles);
        claims.put("parentFolder", parentFolder);
        return generateTokenWithFields(userDetails, claims, 24L * 7L);
    }

    private String generateTokenWithFields(UserDetailsDTO userDetails, Map<String, Object> claims, Long duration) {
        claims.put("username", userDetails.getUsername());
        claims.put("name", userDetails.getName());
        claims.put("active", userDetails.isEnabled());
        claims.put("authorities", userDetails.getAuthorities());
        return createToken(claims, Long.toString(userDetails.getId()), duration);
    }

    public UserView validateToken(String token) throws ExpiredTokenException, InvalidCredentialsException {
        String id = extractUserId(token);
        Optional<UserView> optionalUserView = userViewRepository.findById(Long.parseLong(id));

        if (optionalUserView.isPresent()) {
            if (!validateToken(token, new UserDetailsDTO(optionalUserView.get()))) {
                throw new ExpiredTokenException("Expired jwt");
            } else {
                return optionalUserView.get();
            }
        } else {
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    public Boolean validateToken(String token, UserDetailsDTO userDetailsDTO) {
        final String userId = extractUserId(token);
        return (userId.equals(Long.toString(userDetailsDTO.getId())) && !isTokenExpired(token));
    }

    private String createToken(Map<String, Object> claims, String subject, Long hoursDuration) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * hoursDuration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}