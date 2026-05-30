package org.example.mebkuch.domain.models.eav;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeModel {

    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    private AttributeType type;
}