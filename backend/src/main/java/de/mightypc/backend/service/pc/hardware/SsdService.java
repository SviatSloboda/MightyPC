package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.SSD;
import de.mightypc.backend.repository.pc.hardware.SsdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class SsdService extends BaseService<SSD, SsdRepository> {
    public SsdService(SsdRepository ssdRepository) {
        super(ssdRepository);
    }

    @Override
    protected String getId(SSD entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<SSD> ssd = repository.findById(id);
        if (ssd.isPresent()) {
            SSD currSsd = ssd.get();
            List<String> photos = ssd.get().ssdPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(currSsd.withPhotos(photos));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNames() {
        HashMap<String, String> hashMap = new HashMap<>();

        List<SSD> allSsds = repository.findAll();

        for (SSD ssd : allSsds) {
            hashMap.put(ssd.id(), ssd.hardwareSpec().name());
        }

        return hashMap;
    }
}
