package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.PcCase;
import de.mightypc.backend.repository.pc.hardware.PcCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class PcCaseService extends BaseService<PcCase, PcCaseRepository> {
    public PcCaseService(PcCaseRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(PcCase entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<PcCase> pcCase = repository.findById(id);
        if (pcCase.isPresent()) {
            PcCase currMotherboard = pcCase.get();
            List<String> photos = pcCase.get().pcCasePhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(currMotherboard.withPhotos(photos));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<String, String> getAllNamesWithPrices(){
        HashMap<String, String> hashMap = new HashMap<>();

        List<PcCase> allPcCases = repository.findAll();

        for(PcCase pcCase: allPcCases){
            hashMap.put(pcCase.id(), pcCase.hardwareSpec().name() + " ($" + pcCase.hardwareSpec().price() + ")");
        }

        return hashMap;
    }
}