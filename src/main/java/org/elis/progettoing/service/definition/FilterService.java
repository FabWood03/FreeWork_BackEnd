package org.elis.progettoing.service.definition;

import org.elis.progettoing.dto.request.FilterRequest;
import org.elis.progettoing.dto.response.FilteredEntitiesResponse;

/**
 * Interface for the FilterService class. Provides methods for filtering entities.
 */
public interface FilterService {
    FilteredEntitiesResponse getFilteredEntities(FilterRequest filterRequest);
}
