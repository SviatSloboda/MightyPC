package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.HDD;
import de.mightypc.backend.repository.pc.hardware.HddRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HddService extends BaseService<HDD, HddRepository> {
    protected HddService(HddRepository hddRepository) {
        super(hddRepository);
    }

    @Override
    protected String getId(HDD entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<HDD> hdd = repository.findById(id);
        if (hdd.isPresent()) {
            HDD presentWorkout = hdd.get();
            List<String> photos = hdd.get().hddPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(presentWorkout.withPhotos(photos));
        }
    }
}
