package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.hardware.MotherboardNotFoundException;
import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MotherboardService extends BaseService<Motherboard, MotherboardRepository, MotherboardNotFoundException> {
    public MotherboardService(MotherboardRepository motherboardRepository) {
        super(motherboardRepository);
    }

    @Override
    protected MotherboardNotFoundException getException(String message) {
        return new MotherboardNotFoundException(message);
    }

    @Override
    protected String getId(Motherboard entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(Motherboard entity) {
        return entity.hardwareSpec().name();
    }

    @Override
    @Transactional
    public Motherboard attachPhoto(String id, String photoUrl) {
        Motherboard currMotherboard = getById(id);

        ArrayList<String> photos = new ArrayList<>(currMotherboard.motherboardPhotos());

        photos.addFirst(photoUrl);
        Motherboard updatedMotherboard = currMotherboard.withPhotos(photos);

        return repository.save(updatedMotherboard);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getMotherboardsBySocket(String cpuSocket) {
        HashMap<String, String> hashMap = new HashMap<>();

        List<Motherboard> motherboards = getAll().stream()
                .filter(motherboard -> motherboard.socket().equalsIgnoreCase(cpuSocket))
                .toList();

        for (Motherboard motherboard : motherboards) {
            hashMap.put(motherboard.id(), motherboard.hardwareSpec().name() + " ($" + motherboard.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices() {
        HashMap<String, String> hashMap = new HashMap<>();

        List<Motherboard> allMotherboards = repository.findAll()
                .stream()
                .sorted(Comparator.comparing(motherboard -> motherboard.hardwareSpec().price()))
                .toList();

        for (Motherboard motherboard : allMotherboards) {
            hashMap.put(motherboard.id(), motherboard.hardwareSpec().name() + " ($" + motherboard.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}
