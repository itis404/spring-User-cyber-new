package org.example.mebkuch.domain.repository.eav;

import org.example.mebkuch.domain.models.filter.AttributeFilter;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IProductAttributeFilterRepository {

    // Возвращает ID продуктов, удовлетворяющих EAV фильтру

    Set<Long> findProductIdsByAttributes(List<AttributeFilter> filters);
}
