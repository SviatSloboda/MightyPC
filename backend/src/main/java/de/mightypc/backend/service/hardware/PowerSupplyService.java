package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.hardware.PowerSupplyNotFoundException;
import de.mightypc.backend.model.hardware.PowerSupply;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    @Override
    @Transactional
    public PowerSupply attachPhoto(String id, String photoUrl) {
        PowerSupply currPowerSupply = getById(id);

        ArrayList<String> photos = new ArrayList<>(currPowerSupply.powerSupplyPhotos());

        photos.addFirst(photoUrl);
        PowerSupply updatedPowerSupply = currPowerSupply.withPhotos(photos);

        return repository.save(updatedPowerSupply);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedHashMap<String, String> getAllNamesWithPrices() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        List<PowerSupply> allPowerSupplies = getAllWithSortingOfPriceDesc();

        for (PowerSupply powerSupply : allPowerSupplies) {
            hashMap.put(powerSupply.id(), powerSupply.hardwareSpec().name() + " ($" + powerSupply.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Transactional(readOnly = true)
    public Map<String, String> getAllPowerSuppliesByEnergyConsumption(int energyConsumption) {
        HashMap<String, String> powerSuppliesWithIdsAndNames = new HashMap<>();
        List<PowerSupply> allPowerSupplies = getAll();

        List<PowerSupply> suitablePowerSupplies = allPowerSupplies.stream()
                .filter(powerSupply -> powerSupply.power() >= energyConsumption)
                .toList();

        for (PowerSupply powerSupply : suitablePowerSupplies) {
            powerSuppliesWithIdsAndNames.put(powerSupply.id(), powerSupply.hardwareSpec().name() + " ($" + powerSupply.hardwareSpec().price() + ")");
        }

        return powerSuppliesWithIdsAndNames;
    }

    @Transactional(readOnly = true)
    public Page<PowerSupply> getAllWithSortingOfPriceDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PowerSupply> getAllWithSortingOfPriceAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PowerSupply> getAllWithSortingOfRatingDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PowerSupply> getAllWithSortingOfRatingAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PowerSupply> getAllWithFilteringByPriceAsPages(Pageable pageable, int lowestPrice, int highestPrice) {
        return new PageImpl<>(getAllWithFilteringByPrice(lowestPrice, highestPrice), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PowerSupply> getAllWithFilteringByPowerAsPages(Pageable pageable, int minimalPower, int maximalPower) {
        return new PageImpl<>(getAllWithFilteringByPower(minimalPower, maximalPower), pageable, 8);
    }

    private List<PowerSupply> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(powerSupply -> powerSupply.hardwareSpec().price()))
                .toList()
                .reversed();
    }

    private List<PowerSupply> getAllWithSortingOfPriceAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(powerSupply -> powerSupply.hardwareSpec().price()))
                .toList();
    }

    private List<PowerSupply> getAllWithSortingOfRatingDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(powerSupply -> powerSupply.hardwareSpec().rating()))
                .toList();
    }

    private List<PowerSupply> getAllWithSortingOfRatingAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(powerSupply -> powerSupply.hardwareSpec().rating()))
                .toList()
                .reversed();
    }

    private List<PowerSupply> getAllWithFilteringByPrice(int lowestPrice, int highestPrice) {
        return getAll().stream()
                .filter(powerSupply -> powerSupply.hardwareSpec().price().intValue() >= lowestPrice
                                       && powerSupply.hardwareSpec().price().intValue() <= highestPrice)
                .toList();
    }

    private List<PowerSupply> getAllWithFilteringByPower(int minimalPower, int maximalPower) {
        return getAll().stream()
                .filter(powerSupply -> powerSupply.power() >= minimalPower && powerSupply.power() <= maximalPower)
                .toList();
    }
}
