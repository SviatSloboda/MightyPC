package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.HDD;
import de.mightypc.backend.repository.pc.hardware.HddRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class HddService extends BaseService<HDD, HddRepository> {
    public HddService(HddRepository hddRepository) {
        super(hddRepository);
    }

    @Override
    protected String getId(HDD entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<HDD> hdd = repository.findById(id);
        if (hdd.isPresent()) {
            HDD currHdd = hdd.get();
            List<String> photos = hdd.get().hddPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(currHdd.withPhotos(photos));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices() {
        HashMap<String, String> hashMap = new HashMap<>();

        List<HDD> allHdds = repository.findAll();

        for (HDD hdd : allHdds) {
            hashMap.put(hdd.id(), hdd.hardwareSpec().name() + " ($" + hdd.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}
