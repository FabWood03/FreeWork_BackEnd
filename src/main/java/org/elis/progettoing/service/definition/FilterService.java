package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.FilterRequest;
import org.elis.progettoing.dto.response.FilteredEntitiesResponse;

public interface FilterService {
    FilteredEntitiesResponse getFilteredEntities(FilterRequest filterRequest);
}
