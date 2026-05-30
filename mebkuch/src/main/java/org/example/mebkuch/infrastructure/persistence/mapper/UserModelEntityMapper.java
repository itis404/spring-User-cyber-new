package org.example.mebkuch.infrastructure.persistence.mapper;

import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.infrastructure.persistence.entities.UserEntity;

public class UserModelEntityMapper {
    public static UserEntity toEntity(UserModel userModel){
        return UserEntity.builder()
                .fullname(userModel.getFullname())
                .mail(userModel.getMail())
                .telephoneNumber(userModel.getTelephoneNumber())
                .password(userModel.getPassword())
                .build();
    }

    public static UserModel toModel(UserEntity userEntity){
        return UserModel.builder()
                .id(userEntity.getId())
                .fullname(userEntity.getFullname())
                .mail(userEntity.getMail())
                .telephoneNumber(userEntity.getTelephoneNumber())
                .password(userEntity.getPassword())
                .userRole(userEntity.getUserRole())
                .userStatus(userEntity.getUserStatus())
                .build();
    }
}
