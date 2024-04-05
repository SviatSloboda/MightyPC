package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.CPU;
import de.mightypc.backend.model.pc.specs.Motherboard;
import de.mightypc.backend.repository.pc.hardware.MotherboardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MotherboardService extends BaseService<Motherboard, MotherboardRepository> {
    public MotherboardService(MotherboardRepository motherboardRepository) {
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

    @Transactional(readOnly = true)
    public Map<String, String> getMotherboardsBySocket(String cpuSocket) {
        HashMap<String, String> hashMap = new HashMap<>();

        List<Motherboard> motherboards = getAll().stream()
                .filter(motherboard -> motherboard.socket().equalsIgnoreCase(cpuSocket))
                .toList();

        for(Motherboard motherboard: motherboards){
            hashMap.put(motherboard.id(), motherboard.hardwareSpec().name() + " ($" + motherboard.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices(){
        HashMap<String, String> hashMap = new HashMap<>();

        List<Motherboard> allMotherboards = repository.findAll()
                .stream()
                .sorted(Comparator.comparing(motherboard -> motherboard.hardwareSpec().price()))
                .toList();

        for(Motherboard motherboard: allMotherboards){
            hashMap.put(motherboard.id(), motherboard.hardwareSpec().name() + " ($" + motherboard.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}