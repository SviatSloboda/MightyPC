package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.GPU;
import de.mightypc.backend.repository.pc.hardware.GpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class GpuService extends BaseService<GPU, GpuRepository> {
    public GpuService(GpuRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(GPU entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<GPU> gpu = repository.findById(id);
        if (gpu.isPresent()) {
            GPU currGpu = gpu.get();
            List<String> photos = gpu.get().gpuPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(currGpu.withPhotos(photos));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices(){
        HashMap<String, String> hashMap = new HashMap<>();

        List<GPU> allGpus = repository.findAll();

        for(GPU gpu: allGpus){
            hashMap.put(gpu.id(), gpu.hardwareSpec().name() + " ($" + gpu.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}