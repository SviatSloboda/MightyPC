package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.pc.specs.CPU;
import de.mightypc.backend.repository.hardware.CpuRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CpuService extends BaseService<CPU, CpuRepository> {
    protected CpuService(CpuRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(CPU entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<CPU> cpu = repository.findById(id);
        if (cpu.isPresent()) {
            CPU presentWorkout = cpu.get();
            List<String> photos = cpu.get().cpuPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(presentWorkout.withPhotos(photos));
        }
    }
}