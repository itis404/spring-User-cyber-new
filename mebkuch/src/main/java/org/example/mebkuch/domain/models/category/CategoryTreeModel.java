package org.example.mebkuch.domain.models.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeModel {
    private Long ancestorId;
    private Long descendantId;
    private Integer depth;
}