package org.example.mebkuch.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.mapper.UserDtoModelMapper;
import org.example.mebkuch.application.dto.LoginResultRecord;
import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.domain.service.user.UserCleanLoginService;
import org.example.mebkuch.infrastructure.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationWithPasswordUseCase {

    private final UserCleanLoginService userCleanLoginService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResultRecord register(UserModel userModel){
        String password = userModel.getPassword();
        userModel.setPassword(passwordEncoder.encode(password));

        UserModel userModelFromMemory = userCleanLoginService.register(userModel);

        String jwt = jwtService.generateToken(userModelFromMemory);

        return new LoginResultRecord(UserDtoModelMapper.toDto(userModelFromMemory), jwt);
    }

}