package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.pc.specs.SSD;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SsdService extends BaseService<SSD, SsdRepository> {
    protected SsdService(SsdRepository ssdRepository) {
        super(ssdRepository);
    }

    @Override
    protected String getId(SSD entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<SSD> ssd = repository.findById(id);
        if (ssd.isPresent()) {
            SSD presentWorkout = ssd.get();
            List<String> photos = ssd.get().ssdPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(presentWorkout.withPhotos(photos));
        }
    }
}
