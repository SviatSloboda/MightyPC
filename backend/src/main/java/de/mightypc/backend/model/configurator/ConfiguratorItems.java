package de.mightypc.backend.model.configurator;

import java.util.List;

public record ConfiguratorItems(
        List<List<ItemForConfigurator>> itemsForConfigurator
) {
}
