package org.example.mebkuch.domain.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.UserException;
import org.example.mebkuch.domain.models.user.UserModel;
import org.example.mebkuch.domain.repository.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCleanLoginService {

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserModel register(UserModel userModel){
        if (userRepository.isExistByUniqueFields(userModel)){
            log.error("SO USER IS EXISTS");
            throw new UserException("SO USER IS EXISTS");
        }
        return userRepository.save(userModel);
    }

    public Optional<UserModel> loginInPasswordAndContactData(UserModel userModel){
        UserModel userModelFromBd = userRepository.findUser(userModel);

        if (passwordEncoder.matches(userModel.getPassword(), userModelFromBd.getPassword())){
            return Optional.of(userModelFromBd);
        }
        return Optional.empty();
    }

}
