package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.PowerSupplyNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.PowerSupply;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PowerSupplyService extends BaseService<PowerSupply, PowerSupplyRepository, PowerSupplyNotFoundException> {
    public PowerSupplyService(PowerSupplyRepository powerSupplyRepository) {
        super(powerSupplyRepository);
    }

    @Override
    protected PowerSupplyNotFoundException getException(String message) {
        return new PowerSupplyNotFoundException(message);
    }

    @Override
    protected String getId(PowerSupply entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(PowerSupply entity) {
        return entity.hardwareSpec().name();
    }

    @Transactional
    public PowerSupply attachPhoto(String id, String photoUrl) {
        PowerSupply currPowerSupply = getById(id);
        ArrayList<String> photos = new ArrayList<>(currPowerSupply.powerSupplyPhotos());
        photos.addFirst(photoUrl);
        PowerSupply updatedPowerSupply = currPowerSupply.withPhotos(photos);
        return repository.save(updatedPowerSupply);
    }

    @Transactional(readOnly = true)
    public String getAllNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder("power-supplies:\n");
        List<PowerSupply> allPowerSupplies = getAllWithSortingOfPriceDesc();
        for (PowerSupply psu : allPowerSupplies) {
            String psuAsString = "{" + psu.id() + ":" + psu.hardwareSpec().name() + ":($" + psu.hardwareSpec().price() + ")}\n";
            stringBuilder.append(psuAsString);
        }
        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(PowerSupply::id).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, String> getAllPowerSuppliesByEnergyConsumption(int energyConsumption) {
        LinkedHashMap<String, String> powerSuppliesWithIdsAndNames = new LinkedHashMap<>();
        List<PowerSupply> allPowerSupplies = getAll();

        List<PowerSupply> suitablePowerSupplies = allPowerSupplies.stream()
                .filter(powerSupply -> powerSupply.power() >= energyConsumption)
                .sorted(Comparator.comparingDouble(powerSupply -> powerSupply.hardwareSpec().price().doubleValue()))
                .toList();

        for (PowerSupply powerSupply : suitablePowerSupplies) {
            powerSuppliesWithIdsAndNames.put(powerSupply.id(), powerSupply.hardwareSpec().name() + " ($" + powerSupply.hardwareSpec().price() + ")");
        }

        return powerSuppliesWithIdsAndNames;
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();
        List<PowerSupply> allPowerSupplies = getAllWithSortingOfPriceDesc();
        for (PowerSupply powerSupply : allPowerSupplies) {
            String powerSupplyPhoto = "";
            if (!powerSupply.powerSupplyPhotos().isEmpty()) {
                powerSupplyPhoto = powerSupply.powerSupplyPhotos().getFirst();
            }
            items.add(new ItemForConfigurator(
                    powerSupply.id(),
                    powerSupply.hardwareSpec().name(),
                    powerSupply.hardwareSpec().price(),
                    powerSupplyPhoto,
                    "psu"
            ));
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Page<PowerSupply> getPowerSupplies(Pageable pageable, String sortType, Integer lowestPrice, Integer highestPrice, Integer minimalPower, Integer maximalPower) {
        List<PowerSupply> powerSupplies = getAll();

        if (lowestPrice != null && highestPrice != null) {
            powerSupplies = powerSupplies.stream()
                    .filter(powerSupply -> powerSupply.hardwareSpec().price().intValue() >= lowestPrice &&
                                           powerSupply.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        if (minimalPower != null && maximalPower != null) {
            powerSupplies = powerSupplies.stream()
                    .filter(powerSupply -> powerSupply.power() >= minimalPower &&
                                           powerSupply.power() <= maximalPower)
                    .toList();
        }

        if (sortType != null) {
            switch (sortType) {
                case "price-asc":
                    powerSupplies = powerSupplies.stream()
                            .sorted(Comparator.comparing(powerSupply -> powerSupply.hardwareSpec().price()))
                            .toList();
                    break;
                case "price-desc":
                    powerSupplies = powerSupplies.stream()
                            .sorted(Comparator.comparing((PowerSupply powerSupply) -> powerSupply.hardwareSpec().price()).reversed())
                            .toList();
                    break;
                case "rating-asc":
                    powerSupplies = powerSupplies.stream()
                            .sorted(Comparator.comparing(powerSupply -> powerSupply.hardwareSpec().rating()))
                            .toList();
                    break;
                case "rating-desc":
                    powerSupplies = powerSupplies.stream()
                            .sorted(Comparator.comparing((PowerSupply powerSupply) -> powerSupply.hardwareSpec().rating()).reversed())
                            .toList();
                    break;
                default:
                    break;
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), powerSupplies.size());
        return new PageImpl<>(powerSupplies.subList(start, end), pageable, powerSupplies.size());
    }


    private List<PowerSupply> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing((PowerSupply powerSupply) -> powerSupply.hardwareSpec().price()).reversed())
                .toList();
    }
}
