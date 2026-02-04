package com.epam.gymcrm.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IdGenerator {

    /**
     * Generates the next available ID for a given entity map.
     * Finds the maximum existing ID and returns maxId + 1.
     * If map is empty, returns 1.
     *
     * @param entityMap Map containing entities with Long keys
     * @return Next available ID
     */
    public Long generateNextId(Map<Long, ?> entityMap) {
        if (entityMap.isEmpty()) {
            return 1L;
        }
        Long maxId = entityMap.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        return maxId + 1;
    }
}
