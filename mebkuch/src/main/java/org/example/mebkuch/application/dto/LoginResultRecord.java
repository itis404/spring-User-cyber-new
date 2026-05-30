package org.example.mebkuch.application.dto;

import org.example.mebkuch.api.dto.user.UserDto;

public record LoginResultRecord(UserDto userDto, String jwt) {}
