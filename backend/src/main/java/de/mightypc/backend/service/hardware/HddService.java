package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.hardware.HddNotFoundException;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.repository.hardware.HddRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class HddService extends BaseService<HDD, HddRepository, HddNotFoundException> {
    public HddService(HddRepository hddRepository) {
        super(hddRepository);
    }

    @Override
    protected HddNotFoundException getException(String message) {
        return new HddNotFoundException(message);
    }

    @Override
    protected String getId(HDD entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(HDD entity) {
        return entity.hardwareSpec().name();
    }

    @Override
    @Transactional
    public HDD attachPhoto(String id, String photoUrl) {
        HDD currHdd = getById(id);

        ArrayList<String> photos = new ArrayList<>(currHdd.hddPhotos());

        photos.addFirst(photoUrl);
        HDD updatedHdd = currHdd.withPhotos(photos);

        return repository.save(updatedHdd);
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
