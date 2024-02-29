package de.mightypc.backend.service.pc.hardware;

import de.mightypc.backend.model.pc.specs.RAM;
import de.mightypc.backend.repository.pc.hardware.RamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RamService extends BaseService<RAM, RamRepository> {
    protected RamService(RamRepository ramRepository) {
        super(ramRepository);
    }

    @Override
    protected String getId(RAM entity) {
        return entity.id();
    }

    public void attachPhoto(String id, String photoUrl) {
        Optional<RAM> ram = repository.findById(id);
        if (ram.isPresent()) {
            RAM currRam = ram.get();
            List<String> photos = ram.get().ramPhotos();

            if (photos == null) {
                photos = new ArrayList<>();
            }

            photos.addFirst(photoUrl);
            repository.save(currRam.withPhotos(photos));
        }
    }
}
