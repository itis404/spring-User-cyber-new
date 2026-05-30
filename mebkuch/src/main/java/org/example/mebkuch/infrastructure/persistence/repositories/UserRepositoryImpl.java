package org.example.mebkuch.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.UserException;
import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.domain.repository.IUserRepository;
import org.example.mebkuch.infrastructure.persistence.entities.UserEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.UserModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.UserRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Primary
public class UserRepositoryImpl implements IUserRepository {

    private final UserRepositoryJpa userRepositoryJpa;

    @Override
    public UserModel save(UserModel userModel) {
        userRepositoryJpa.save(UserModelEntityMapper.toEntity(userModel));
        return userModel;
    }

    @Override
    public boolean isExistByUniqueFields(UserModel userModel) {
        return userRepositoryJpa.existsByMailOrTelephoneNumber(userModel.getMail(), userModel.getTelephoneNumber());
    }

    @Override
    public UserModel findUser(UserModel userModel) {
        UserEntity userEntity = userRepositoryJpa.findUserEntity(
                userModel.getMail()).orElseThrow(() -> new UserException("пользователь не найден"));

        return UserModelEntityMapper.toModel(userEntity);
    }

    @Override
    @Transactional
    public UserModel addOrChangeTelephone(String mail, String telephone) {
        UserEntity userEntity = userRepositoryJpa.findUserEntity(mail)
                .orElseThrow(()-> new UserException("пользователь не найден по данному email"));

        if (userRepositoryJpa.existsByTelephoneNumber(telephone) && !telephone.equals(userEntity.getTelephoneNumber())){
            throw new UserException("этот номер телефона уже занят");
        }

        userEntity.setTelephoneNumber(telephone);

        return UserModelEntityMapper.toModel(userEntity);
    }

    @Override
    public UserModel getUserByMail(String mail) {
        return UserModelEntityMapper.toModel(userRepositoryJpa.findByMail(mail).
                orElseThrow(() -> new UserException("не существует пользователя по такому mail")));
    }
}
