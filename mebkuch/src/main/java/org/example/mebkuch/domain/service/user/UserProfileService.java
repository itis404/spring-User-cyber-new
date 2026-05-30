package org.example.mebkuch.domain.service.user;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.UserException;
import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.domain.repository.IUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final IUserRepository userRepository;

    public UserModel addOrChangeTelephone(String mail, String telephone){
        return userRepository.addOrChangeTelephone(mail, telephone);
    }

    public UserModel getUserByMail(String mail){
        return userRepository.getUserByMail(mail);
    }
}
