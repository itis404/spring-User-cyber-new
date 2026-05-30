package org.example.mebkuch.infrastructure.persistence.repositories;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.models.user.FavouriteModel;
import org.example.mebkuch.domain.repository.IFavouriteRepository;
import org.example.mebkuch.infrastructure.persistence.entities.*;
import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.FavouriteRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.UserRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product.ProductRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class FavouriteRepositoryImpl implements IFavouriteRepository {

    private final FavouriteRepositoryJpa favouriteRepositoryJpa;
    private final ProductRepositoryJpa productRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;

    @Override
    @Transactional
    public FavouriteModel save(FavouriteModel model) {

        ProductEntity product = productRepositoryJpa.findById(model.getProductId())
                .orElseThrow();

        UserEntity user = userRepositoryJpa.findById(model.getUserId())
                .orElseThrow();

        FavouriteEntity entity = FavouriteEntity.builder()
                .id(new FavouriteId(model.getProductId(), model.getUserId()))
                .product(product)
                .user(user)
                .addedDate(LocalDateTime.now())
                .build();

        FavouriteEntity saved = favouriteRepositoryJpa.save(entity);

        return FavouriteModel.builder()
                .productId(saved.getId().getProductId())
                .userId(saved.getId().getUserId())
                .addedDate(saved.getAddedDate())
                .build();
    }

    @Override
    public Optional<FavouriteModel> find(Long productId, Long userId) {

        return favouriteRepositoryJpa.findById(new FavouriteId(productId, userId))
                .map(entity -> FavouriteModel.builder()
                        .productId(entity.getId().getProductId())
                        .userId(entity.getId().getUserId())
                        .addedDate(entity.getAddedDate())
                        .build());
    }

    @Override
    public List<FavouriteModel> findByUser(Long userId) {
        return favouriteRepositoryJpa.findByUserId(userId)
                .stream()
                .map(entity -> FavouriteModel.builder()
                        .productId(entity.getId().getProductId())
                        .userId(entity.getId().getUserId())
                        .addedDate(entity.getAddedDate())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public boolean delete(Long productId, Long userId) {
        FavouriteId id = new FavouriteId(productId, userId);

        if (!favouriteRepositoryJpa.existsById(id)) {
            return false;
        }

        favouriteRepositoryJpa.deleteById(id);
        return true;
    }

    @Override
    public boolean exists(Long productId, Long userId) {
        return favouriteRepositoryJpa.existsById(new FavouriteId(productId, userId));
    }
}
