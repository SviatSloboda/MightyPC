package de.mightypc.backend.model.configurator;

import java.util.List;
import java.util.Map;

public record ConfiguratorComponents(
        List<Map<String, String>> componentIdsAndNames
) {
}
