package org.example.mebkuch.application.usecase;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.user.UserDto;
import org.example.mebkuch.api.mapper.UserDtoModelMapper;
import org.example.mebkuch.application.dto.LoginResultRecord;
import org.example.mebkuch.application.dto.TokenResponseDto;
import org.example.mebkuch.domain.exception.UserException;
import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.domain.service.ITokenProvider;
import org.example.mebkuch.domain.service.user.UserGoogleService;
import org.example.mebkuch.domain.service.user.UserCleanLoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

//Документация по Google OAuth 2.0 для веб-приложений
//https://developers.google.com/identity/protocols/oauth2/web-server?hl=ru#httprest_3

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthUseCase {

    // вынести потом в .env какой-нибудь
    @Value("${variables.GOOGLE_CLIENT_ID}")
    private String GOOGLE_CLIENT_ID;

    @Value("${variables.GOOGLE_CLIENT_SECRET}")
    private String GOOGLE_CLIENT_SECRET;

    private final RestClient restClient;
    private final UserGoogleService userGoogleService;
    private final UserCleanLoginService userCleanLoginService;
    private final ITokenProvider jwtTokenService;

    public LoginResultRecord loginWithGoogle(String code) {

//        Content-Type: application/x-www-form-urlencoded
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", GOOGLE_CLIENT_ID);
        body.add("client_secret", GOOGLE_CLIENT_SECRET);
        body.add("code", code);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", "http://localhost:8080/api/auth/callback");

        TokenResponseDto response = restClient
                .post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(TokenResponseDto.class);

        // здесь просто происходит раскодировка с base64
        log.info("id from google: " + response.getIdToken());
        UserDto userDto = userDtoFromJwt(response.getIdToken());

        userGoogleService.register(UserDtoModelMapper.toModel(userDto));

        String jwt = jwtTokenService.generateToken(UserDtoModelMapper.toModel(userDto));

        log.info("JWT of user is saved: " + jwt);

        return new LoginResultRecord(userDto, jwt);
    }

    public String buildGoogleOAuthUrl() {
        return UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", GOOGLE_CLIENT_ID)
                .queryParam("redirect_uri", "http://localhost:8080/api/auth/callback")
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .build()
                .toUriString();
    }

    public LoginResultRecord loginWithUserFields(UserModel userModel){
        UserModel userModelFromMemory = userCleanLoginService
                .loginInPasswordAndContactData(userModel)
                .orElseThrow(() -> new UserException("Пользователь ввел неправильно данные"));

        String jwt = jwtTokenService.generateToken(userModelFromMemory);

        return new LoginResultRecord(UserDtoModelMapper.toDto(userModelFromMemory), jwt);
    }

    // здесь просто происходит раскодировка с base64
    private UserDto userDtoFromJwt(String idToken){
        DecodedJWT decodedJWT = JWT.decode(idToken);

        log.info(decodedJWT.getClaim("role").asString());
        return UserDto.builder()
                .email(decodedJWT.getClaim("email").asString())
                .fullname(decodedJWT.getClaim("name").asString())
                .role("CLIENT")
                .build();
    }
}
