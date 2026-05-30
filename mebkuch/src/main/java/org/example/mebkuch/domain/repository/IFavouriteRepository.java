package org.example.mebkuch.domain.repository;


import org.example.mebkuch.domain.models.user.FavouriteModel;

import java.util.List;
import java.util.Optional;

public interface IFavouriteRepository {

    FavouriteModel save(FavouriteModel model);

    Optional<FavouriteModel> find(Long productId, Long userId);

    List<FavouriteModel> findByUser(Long userId);

    boolean delete(Long productId, Long userId);

    boolean exists(Long productId, Long userId);
}
