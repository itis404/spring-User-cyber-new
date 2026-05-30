package org.example.mebkuch.api.dto.eav;

import lombok.*;
import org.example.mebkuch.domain.models.eav.AttributeType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeDto {

    private Long id;

    private String name;

    private AttributeType type;
}
