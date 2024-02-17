package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.GPU;
import de.mightypc.backend.repository.hardware.GpuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GpuService extends BaseService<GPU, GpuRepository> {
    protected GpuService(GpuRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(GPU entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<GPU> gpu = repository.findById(id);
        if (gpu.isPresent()) {
            GPU presentWorkout = gpu.get();
            List<String> photos = gpu.get().gpuPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(presentWorkout.withPhotos(photos));
        }
    }
}