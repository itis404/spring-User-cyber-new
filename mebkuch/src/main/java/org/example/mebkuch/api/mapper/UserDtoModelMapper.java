package org.example.mebkuch.api.mapper;

import org.example.mebkuch.api.dto.user.UserDto;
import org.example.mebkuch.domain.models.user.UserModel;

public class UserDtoModelMapper {

    public static UserDto toDto(UserModel userModel){
        return UserDto.builder()
                .fullname(userModel.getFullname())
                .email(userModel.getMail())
                .telephoneNumber(userModel.getTelephoneNumber())
                .role(userModel.getUserRole().name())
                .build();
    }

    public static UserModel toModel(UserDto userDto){
        return UserModel.builder()
                .fullname(userDto.getFullname())
                .password(userDto.getPassword())
                .mail(userDto.getEmail())
                .telephoneNumber(userDto.getTelephoneNumber())
                .build();
    }
}
