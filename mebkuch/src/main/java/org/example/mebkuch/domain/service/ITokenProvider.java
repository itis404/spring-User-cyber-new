package org.example.mebkuch.domain.service;

import org.example.mebkuch.domain.models.user.UserModel;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface ITokenProvider {
    String generateToken(UserModel user);
    boolean validateToken(String token);
    String extractEmail(String token);
    String extractFullname(String token);
    String extractTelephone(String token);
    String extractRoleAsString(String token);
    List<GrantedAuthority> extractRole(String token);
}