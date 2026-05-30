package org.example.mebkuch.domain.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserModel {
    private Long id;
    private String fullname;
    private String password;
    private String mail;
    private String telephoneNumber;

    @Builder.Default
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Builder.Default
    private UserRole userRole = UserRole.CLIENT;
}
