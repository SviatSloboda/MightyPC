package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.CPU;
import de.mightypc.backend.repository.pc.hardware.CpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class CpuService extends BaseService<CPU, CpuRepository> {
    public CpuService(CpuRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(CPU entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<CPU> cpu = repository.findById(id);
        if (cpu.isPresent()) {
            CPU currCpu = cpu.get();
            List<String> photos = cpu.get().cpuPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(currCpu.withPhotos(photos));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNames(){
        HashMap<String, String> hashMap = new HashMap<>();

        List<CPU> allCpus = repository.findAll();

        for(CPU cpu: allCpus){
            hashMap.put(cpu.id(), cpu.hardwareSpec().name());
        }

        return hashMap;
    }
}