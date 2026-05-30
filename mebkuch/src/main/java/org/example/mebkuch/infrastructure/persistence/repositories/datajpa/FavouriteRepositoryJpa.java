package org.example.mebkuch.infrastructure.persistence.repositories.datajpa;

import org.example.mebkuch.infrastructure.persistence.entities.FavouriteEntity;
import org.example.mebkuch.infrastructure.persistence.entities.FavouriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepositoryJpa extends JpaRepository<FavouriteEntity, FavouriteId> {

    List<FavouriteEntity> findByUserId(Long userId);

    boolean existsById(FavouriteId id);

    void deleteById(FavouriteId id);
}