package org.example.mebkuch.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.domain.service.ITokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class JwtService implements ITokenProvider {

    @Value("${variables.SECRET_KEY_JWT}")
    private String SECRET_KEY_JWT;

    @Override
    public String generateToken(UserModel userModel) {
        return JWT.create()
                .withClaim("email", userModel.getMail())
                .withClaim("fullname", userModel.getFullname())
                .withClaim("telephone",userModel.getTelephoneNumber())
                .withClaim("role", userModel.getUserRole().name())
                .sign(Algorithm.HMAC256(SECRET_KEY_JWT));
    }

    @Override
    public boolean validateToken(String token) {
        try{
            return JWT.require(Algorithm.HMAC256(SECRET_KEY_JWT))
                    .build()
                    .verify(token) != null;
        }
         catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractEmail(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("email").asString();
    }

    @Override
    public String extractFullname(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("fullname").asString();
    }

    @Override
    public String extractTelephone(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("telephone").asString();
    }

    @Override
    public String extractRoleAsString(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("role").asString();
    }

    @Override
    public List<GrantedAuthority> extractRole(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String role = decodedJWT.getClaim("role").asString();
        GrantedAuthority authority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_" + role;
            }
        };
        return List.of(authority);
    }
}