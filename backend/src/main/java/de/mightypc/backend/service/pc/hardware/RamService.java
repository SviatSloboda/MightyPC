package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.RAM;
import de.mightypc.backend.repository.pc.hardware.RamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class RamService extends BaseService<RAM, RamRepository> {
    public RamService(RamRepository ramRepository) {
        super(ramRepository);
    }

    @Override
    protected String getId(RAM entity) {
        return entity.id();
    }

    @Override
    @Transactional
    public RAM attachPhoto(String id, String photoUrl) {
        RAM currRAM = getById(id);

        ArrayList<String> photos = new ArrayList<>(currRAM.ramPhotos());

        photos.addFirst(photoUrl);
        RAM updatedRAM = currRAM.withPhotos(photos);

        return repository.save(updatedRAM);
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices(){
        HashMap<String, String> hashMap = new HashMap<>();

        List<RAM> allRams = repository.findAll();

        for(RAM ram: allRams){
            hashMap.put(ram.id(), ram.hardwareSpec().name() + " ($" + ram.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}
