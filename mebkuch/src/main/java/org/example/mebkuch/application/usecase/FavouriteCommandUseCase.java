package org.example.mebkuch.application.usecase;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.FavouriteDto;
import org.example.mebkuch.application.service.FavouriteRedisService;
import org.example.mebkuch.domain.models.user.FavouriteModel;
import org.example.mebkuch.domain.service.FavouriteService;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class FavouriteCommandUseCase {

    private final FavouriteService favouriteService;
    private final FavouriteRedisService favouriteRedisService;

    public FavouriteDto add(FavouriteDto dto) {

        FavouriteModel model = FavouriteModel.builder()
                .productId(dto.getProductId())
                .userId(dto.getUserId())
                .addedDate(dto.getAddedDate())
                .build();

        FavouriteModel saved = favouriteService.add(model);

        favouriteRedisService.add(dto.getUserId(), dto.getProductId());

        return mapToDto(saved);
    }

    public void remove(Long productId, Long userId) {

        favouriteService.remove(productId, userId);

        favouriteRedisService.remove(userId, productId);
    }

    private FavouriteDto mapToDto(FavouriteModel model) {
        return FavouriteDto.builder()
                .productId(model.getProductId())
                .userId(model.getUserId())
                .addedDate(model.getAddedDate())
                .build();
    }
}
