package org.example.mebkuch.domain.repository;

import org.example.mebkuch.domain.models.user.UserModel;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository {
    UserModel save(UserModel userModel);
    boolean isExistByUniqueFields(UserModel userModel);
    UserModel findUser(UserModel userModel);
    UserModel addOrChangeTelephone(String mail , String telephone);
    UserModel getUserByMail(String mail);
}
