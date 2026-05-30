package org.example.mebkuch.api.validation;

import org.example.mebkuch.api.dto.user.UserDto;
import org.example.mebkuch.domain.exception.UserException;

public class UserValidatorDto {

    public static void validate(UserDto user) {
        if (user == null) {
            throw new UserException("User не может быть null");
        }

        validateEmail(user.getEmail());
        validateTelephone(user.getTelephoneNumber());
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new UserException("Email не может быть пустым");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!email.matches(emailRegex)) {
            throw new UserException("Некорректный email");
        }
    }

    public static void validateTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            throw new UserException("Телефон не может быть пустым");
        }

        String phoneRegex = "^\\+?[0-9]{10,15}$";

        if (!telephone.matches(phoneRegex)) {
            throw new UserException("Некорректный номер телефона");
        }
    }

    public static void validatePassword(String password) {
        if (password.length() < 7) {
            throw new UserException("пароль должен быть не меньше 7 символов");
        }

    }
}