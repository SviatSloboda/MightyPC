package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.Motherboard;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MotherboardService extends BaseService<Motherboard, MotherboardRepository> {
    protected MotherboardService(MotherboardRepository motherboardRepository) {
        super(motherboardRepository);
    }

    @Override
    protected String getId(Motherboard entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<Motherboard> motherboard = repository.findById(id);
        if (motherboard.isPresent()) {
            Motherboard presentWorkout = motherboard.get();
            List<String> photos = motherboard.get().motherboardPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(presentWorkout.withPhotos(photos));
        }
    }
}
