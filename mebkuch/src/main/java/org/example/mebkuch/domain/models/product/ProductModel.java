package org.example.mebkuch.domain.models.product;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"sectionIds"})
public class ProductModel {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private String description;

    private BigDecimal minPrice;

    private BigDecimal discount;

    private LocalDate createdAt;

    // связи
    private Long categoryId;

    private Long productTypeId;

    private Long productStatusId;

    private Long productStyleId;

    @Builder.Default
    private Set<Long> sectionIds = new HashSet<>();

    @Builder.Default
    private List<Long> images = new ArrayList<>();

    @Builder.Default
    private List<Long> components = new ArrayList<>();

    @Builder.Default
    private List<Long> subProducts = new ArrayList<>();


    @Builder.Default
    private List<Long> atributeValues = new ArrayList<>();
}