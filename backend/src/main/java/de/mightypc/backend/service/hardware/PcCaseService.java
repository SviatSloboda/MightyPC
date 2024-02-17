package de.mightypc.backend.service.hardware;

import de.mightypc.backend.model.specs.PcCase;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PcCaseService extends BaseService<PcCase, PcCaseRepository> {
    protected PcCaseService(PcCaseRepository repository) {
        super(repository);
    }

    @Override
    protected String getId(PcCase entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<PcCase> pcCase = repository.findById(id);
        if (pcCase.isPresent()) {
            PcCase presentWorkout = pcCase.get();
            List<String> photos = pcCase.get().pcCasePhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(presentWorkout.withPhotos(photos));
        }
    }
}