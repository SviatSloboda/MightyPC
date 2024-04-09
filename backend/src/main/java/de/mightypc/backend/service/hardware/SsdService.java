package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SsdService extends BaseService<SSD, SsdRepository> {
    public SsdService(SsdRepository ssdRepository) {
        super(ssdRepository);
    }

    @Override
    protected String getId(SSD entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(SSD entity) {
        return entity.hardwareSpec().name();
    }

    @Override
    @Transactional
    public SSD attachPhoto(String id, String photoUrl) {
        SSD currSSD = getById(id);

        ArrayList<String> photos = new ArrayList<>(currSSD.ssdPhotos());

        photos.addFirst(photoUrl);
        SSD updatedSSD = currSSD.withPhotos(photos);

        return repository.save(updatedSSD);
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices() {
        HashMap<String, String> hashMap = new HashMap<>();

        List<SSD> allSsds = repository.findAll();

        for (SSD ssd : allSsds) {
            hashMap.put(ssd.id(), ssd.hardwareSpec().name() + " ($" + ssd.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}
