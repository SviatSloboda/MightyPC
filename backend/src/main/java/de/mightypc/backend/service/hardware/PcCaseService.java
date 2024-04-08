package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class PcCaseService extends BaseService<PcCase, PcCaseRepository> {
    public PcCaseService(PcCaseRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(PcCase entity) {
        return entity.id();
    }

    @Override
    @Transactional
    public PcCase attachPhoto(String id, String photoUrl) {
        PcCase currPcCase = getById(id);

        ArrayList<String> photos = new ArrayList<>(currPcCase.pcCasePhotos());

        photos.addFirst(photoUrl);
        PcCase updatedPcCase = currPcCase.withPhotos(photos);

        return repository.save(updatedPcCase);
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices() {
        HashMap<String, String> hashMap = new HashMap<>();

        List<PcCase> allPcCases = repository.findAll();

        for (PcCase pcCase : allPcCases) {
            hashMap.put(pcCase.id(), pcCase.hardwareSpec().name() + " ($" + pcCase.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}
