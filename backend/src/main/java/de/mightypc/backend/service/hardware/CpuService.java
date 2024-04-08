package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.repository.hardware.CpuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
public class CpuService extends BaseService<CPU, CpuRepository> {
    public CpuService(CpuRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(CPU entity) {
        return entity.id();
    }

    @Override
    @Transactional
    public CPU attachPhoto(String id, String photoUrl) {
        CPU currCpu = getById(id);

        ArrayList<String> photos = new ArrayList<>(currCpu.cpuPhotos());

        photos.addFirst(photoUrl);
        CPU updatedCpu = currCpu.withPhotos(photos);

        return repository.save(updatedCpu);
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices() {
        HashMap<String, String> hashMap = new HashMap<>();

        List<CPU> allCpus = repository.findAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().price()))
                .toList();

        for (CPU cpu : allCpus) {
            hashMap.put(cpu.id(), cpu.hardwareSpec().name() + " ($" + cpu.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Transactional(readOnly = true)
    public String getSocketOfCpuById(String cpuId) {
        return getById(cpuId).socket();
    }
}
