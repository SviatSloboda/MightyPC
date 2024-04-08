package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.hardware.PowerSupply;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PowerSupplyService extends BaseService<PowerSupply, PowerSupplyRepository> {
    public PowerSupplyService(PowerSupplyRepository powerSupplyRepository) {
        super(powerSupplyRepository);
    }

    @Override
    protected String getId(PowerSupply entity) {
        return entity.id();
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
    public HashMap<String, String> getAllNamesWithPrices() {
        HashMap<String, String> hashMap = new HashMap<>();

        List<PowerSupply> allPowerSupplies = repository.findAll();

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
}
