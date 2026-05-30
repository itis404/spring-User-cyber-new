package org.example.mebkuch.domain.service;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.UserException;
import org.example.mebkuch.domain.models.user.FavouriteModel;
import org.example.mebkuch.domain.repository.IFavouriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavouriteService {

    private final IFavouriteRepository favouriteRepository;

    public FavouriteModel add(FavouriteModel model) {
        return favouriteRepository.save(model);
    }

    public List<FavouriteModel> getUserFavourites(Long userId) {
        return favouriteRepository.findByUser(userId);
    }

    public void remove(Long productId, Long userId) {
        if (!favouriteRepository.delete(productId, userId)) {
            throw new UserException("Favourite not found");
        }
    }

    public boolean exists(Long productId, Long userId) {
        return favouriteRepository.exists(productId, userId);
    }
}