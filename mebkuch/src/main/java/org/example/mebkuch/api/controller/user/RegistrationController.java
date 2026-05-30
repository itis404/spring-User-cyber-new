package org.example.mebkuch.api.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.user.UserDto;
import org.example.mebkuch.api.mapper.UserDtoModelMapper;
import org.example.mebkuch.api.validation.UserValidatorDto;
import org.example.mebkuch.application.dto.LoginResultRecord;
import org.example.mebkuch.application.usecase.UserRegistrationWithPasswordUseCase;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/user/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRegistrationWithPasswordUseCase userRegistrationWithPasswordUseCase;

    @PostMapping("/register-with-password")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto){

        UserValidatorDto.validate(userDto);

        LoginResultRecord loginResultRecord = userRegistrationWithPasswordUseCase.register(UserDtoModelMapper.toModel(userDto));

        ResponseCookie responseCookie = generateResponseCookie(loginResultRecord);

        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body(loginResultRecord.userDto());
    }

    private ResponseCookie generateResponseCookie(LoginResultRecord loginResultRecord){

        // 4 дня поставил - время жизни Cookie
        long masAgeSeconds = 60 * 60 * 24 * 4;

        ResponseCookie responseCookie = ResponseCookie.from("json-web-token", loginResultRecord.jwt())
                .httpOnly(true)
                .path("/")
                .maxAge(masAgeSeconds )
                .build();

        return responseCookie;
    }
}