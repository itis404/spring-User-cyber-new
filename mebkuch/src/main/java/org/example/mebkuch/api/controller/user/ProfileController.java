package org.example.mebkuch.api.controller.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.user.UserDto;
import org.example.mebkuch.api.mapper.UserDtoModelMapper;
import org.example.mebkuch.api.validation.UserValidatorDto;
import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.domain.service.ITokenProvider;
import org.example.mebkuch.domain.service.user.UserProfileService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;
    private final ITokenProvider tokenProvider;

    @PatchMapping
    public ResponseEntity<UserDto> addOrUpdateTelephone(@RequestParam String mail, @RequestParam String telephone){

        UserValidatorDto.validateEmail(mail);
        UserValidatorDto.validateTelephone(telephone);

        UserModel userModel = userProfileService.addOrChangeTelephone(mail, telephone);

        String jwt = tokenProvider.generateToken(userModel);

        return ResponseEntity.ok()
                .header("Set-Cookie", generateResponseCookie(jwt).toString())
                .body(UserDtoModelMapper.toDto(userModel));
    }

    @GetMapping
    public ResponseEntity<Long> getUserIdByMail(@RequestParam String mail){
        UserModel userModel = userProfileService.getUserByMail(mail);
        return ResponseEntity.ok().body(userModel.getId());
    }


    private ResponseCookie generateResponseCookie(String jwt){

        // 4 дня поставил - время жизни Cookie
        long masAgeSeconds = 60 * 60 * 24 * 4;

        ResponseCookie responseCookie = ResponseCookie.from("json-web-token", jwt)
                .httpOnly(true)
                .path("/")
                .maxAge(masAgeSeconds)
                .build();

        return responseCookie;
    }

}