package org.example.mebkuch.domain.service.user;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.domain.repository.IUserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGoogleService {

    private final IUserRepository userRepository;

    public void register(UserModel userModel){
        if (!userRepository.isExistByUniqueFields(userModel)){
            userRepository.save(userModel);
        }
    }
}
