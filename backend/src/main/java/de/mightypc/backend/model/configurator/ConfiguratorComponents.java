package de.mightypc.backend.model.configurator;

import java.util.LinkedHashMap;
import java.util.List;

public record ConfiguratorComponents(
        List<LinkedHashMap<String, String>> componentIdsAndNames
) {
}
