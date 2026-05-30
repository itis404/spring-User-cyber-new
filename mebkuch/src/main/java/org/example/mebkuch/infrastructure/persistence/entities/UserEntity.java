package org.example.mebkuch.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.mebkuch.domain.models.user.UserRole;
import org.example.mebkuch.domain.models.user.UserStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "fullname")
    String fullname;

    @Column(name = "mail", unique = true)
    String mail;

    @Column(name = "telephone_number", unique = true)
    String telephoneNumber;

    @Column
    private String password;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    UserStatus userStatus = UserStatus.ACTIVE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role_system")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    UserRole userRole = UserRole.CLIENT;

}
