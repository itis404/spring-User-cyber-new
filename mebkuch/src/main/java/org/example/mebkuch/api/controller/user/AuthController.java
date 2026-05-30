package org.example.mebkuch.api.controller.user;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.user.UserDto;
import org.example.mebkuch.api.mapper.UserDtoModelMapper;
import org.example.mebkuch.api.validation.UserValidatorDto;
import org.example.mebkuch.application.dto.LoginResultRecord;
import org.example.mebkuch.application.usecase.UserAuthUseCase;
import org.example.mebkuch.domain.service.ITokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserAuthUseCase userAuthUseCase;
    private final ITokenProvider jwtTokenService;

    @GetMapping("/google")
    public void redirect(HttpServletResponse response) throws IOException {
        log.info("началась регистрация через google");
        response.sendRedirect(userAuthUseCase.buildGoogleOAuthUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        log.info("сохраняю пользователя в бд");
        LoginResultRecord loginResultRecord = userAuthUseCase.loginWithGoogle(code);

        ResponseCookie responseCookie = generateResponseCookie(loginResultRecord);

        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .header("Set-Cookie", responseCookie.toString())
                .header("Location", "/")
                .build();
    }

    // Вход с паролем
    @PostMapping("/user/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto){

        UserValidatorDto.validateEmail(userDto.getEmail());

        LoginResultRecord loginResultRecord = userAuthUseCase.loginWithUserFields(UserDtoModelMapper.toModel(userDto));

        ResponseCookie responseCookie = generateResponseCookie(loginResultRecord);

        log.info(responseCookie.toString());

        return ResponseEntity.ok()
                .header("Set-Cookie", responseCookie.toString())
                .body(loginResultRecord.userDto());
    }

    @PostMapping("/user/logout")
    public ResponseEntity<Void> logout(){
        return ResponseEntity.ok()
                .header("Set-Cookie", "json-web-token=; HttpOnly; Path=/; Max-Age=0")
                .build();
    }


    @GetMapping("/user/about-me")
    public ResponseEntity<UserDto> getUser(@CookieValue("json-web-token") String jwt){
        String email = jwtTokenService.extractEmail(jwt);
        String fullname = jwtTokenService.extractFullname(jwt);
        String telephone = jwtTokenService.extractTelephone(jwt);
        String role = jwtTokenService.extractRoleAsString(jwt);

        UserDto userDto = UserDto.builder()
                .fullname(fullname)
                .email(email)
                .telephoneNumber(telephone)
                .role(role)
                .build();

        return ResponseEntity.ok().body(userDto);
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

