package org.example.mebkuch.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDto {
    private String fullname;
    private String password;
    private String email;
    private String telephoneNumber;
    private String role;
}
