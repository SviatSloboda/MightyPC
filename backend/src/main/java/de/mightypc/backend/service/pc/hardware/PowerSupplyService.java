package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.pc.specs.PowerSupply;
import de.mightypc.backend.repository.hardware.PowerSupplyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PowerSupplyService extends BaseService<PowerSupply, PowerSupplyRepository> {
    protected PowerSupplyService(PowerSupplyRepository powerSupplyRepository) {
        super(powerSupplyRepository);
    }

    @Override
    protected String getId(PowerSupply entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<PowerSupply> powerSupply = repository.findById(id);
        if (powerSupply.isPresent()) {
            PowerSupply presentWorkout = powerSupply.get();
            List<String> photos = powerSupply.get().powerSupplyPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(presentWorkout.withPhotos(photos));
        }
    }
}
