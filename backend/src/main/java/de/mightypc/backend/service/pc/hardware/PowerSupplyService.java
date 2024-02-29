package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.PowerSupply;
import de.mightypc.backend.model.pc.specs.PowerSupply;
import de.mightypc.backend.repository.pc.hardware.PowerSupplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class PowerSupplyService extends BaseService<PowerSupply, PowerSupplyRepository> {
    public PowerSupplyService(PowerSupplyRepository powerSupplyRepository) {
        super(powerSupplyRepository);
    }

    @Override
    protected String getId(PowerSupply entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<PowerSupply> powerSupply = repository.findById(id);
        if (powerSupply.isPresent()) {
            PowerSupply currPsu = powerSupply.get();
            List<String> photos = powerSupply.get().powerSupplyPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(currPsu.withPhotos(photos));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNames(){
        HashMap<String, String> hashMap = new HashMap<>();

        List<PowerSupply> allPowerSupplies = repository.findAll();

        for(PowerSupply powerSupply: allPowerSupplies){
            hashMap.put(powerSupply.id(), powerSupply.hardwareSpec().name());
        }

        return hashMap;
    }
}
