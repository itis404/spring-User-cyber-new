package org.example.mebkuch.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.FavouriteDto;
import org.example.mebkuch.application.usecase.FavouriteCommandUseCase;
import org.example.mebkuch.application.usecase.FavouriteQueryUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
@Slf4j
public class FavouriteController {

    private final FavouriteQueryUseCase queryUseCase;
    private final FavouriteCommandUseCase commandUseCase;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavouriteDto>> getUserFavourites(@PathVariable Long userId) {
        return ResponseEntity.ok(queryUseCase.getUserFavourites(userId));
    }

    @GetMapping
    public ResponseEntity<FavouriteDto> getOne(
            @RequestParam Long productId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(queryUseCase.getOne(productId, userId));
    }

    @PostMapping
    public ResponseEntity<FavouriteDto> add(@RequestBody FavouriteDto dto) {
        return ResponseEntity.ok(commandUseCase.add(dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(
            @RequestParam Long productId,
            @RequestParam Long userId
    ) {
        commandUseCase.remove(productId, userId);
        return ResponseEntity.noContent().build();
    }
}