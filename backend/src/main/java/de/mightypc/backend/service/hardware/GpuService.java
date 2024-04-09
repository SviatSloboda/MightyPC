package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.repository.hardware.GpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class GpuService extends BaseService<GPU, GpuRepository> {
    public GpuService(GpuRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(GPU entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(GPU entity) {
        return entity.hardwareSpec().name();
    }

    public GPU attachPhoto(String id, String photoUrl) {
        GPU currGpu = getById(id);

        ArrayList<String> photos = new ArrayList<>(currGpu.gpuPhotos());

        photos.addFirst(photoUrl);
        GPU updatedGpu = currGpu.withPhotos(photos);

        return repository.save(updatedGpu);
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices() {
        HashMap<String, String> hashMap = new HashMap<>();

        List<GPU> allGpus = repository.findAll();

        for (GPU gpu : allGpus) {
            hashMap.put(gpu.id(), gpu.hardwareSpec().name() + " ($" + gpu.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}
