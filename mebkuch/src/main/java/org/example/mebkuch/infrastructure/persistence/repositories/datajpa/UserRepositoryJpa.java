package org.example.mebkuch.infrastructure.persistence.repositories.datajpa;

import org.example.mebkuch.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepositoryJpa extends JpaRepository<UserEntity, Long> {
    boolean existsByMailOrTelephoneNumber(String mail, String telephoneNumber);

    @Query("SELECT u FROM UserEntity u WHERE u.mail=:contact")
    Optional<UserEntity> findUserEntity(String contact);

    boolean existsByTelephoneNumber(String telepgone);

    Optional<UserEntity> findByMail(String mail);
}
