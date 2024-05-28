package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.SsdNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.repository.hardware.SsdRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SsdService extends BaseService<SSD, SsdRepository, SsdNotFoundException> {
    public SsdService(SsdRepository ssdRepository) {
        super(ssdRepository);
    }

    @Override
    protected SsdNotFoundException getException(String message) {
        return new SsdNotFoundException(message);
    }

    @Override
    protected String getId(SSD entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(SSD entity) {
        return entity.hardwareSpec().name();
    }

    @Transactional
    public SSD attachPhoto(String id, String photoUrl) {
        SSD currSSD = getById(id);
        ArrayList<String> photos = new ArrayList<>(currSSD.ssdPhotos());
        photos.addFirst(photoUrl);
        SSD updatedSSD = currSSD.withPhotos(photos);
        return repository.save(updatedSSD);
    }

    @Transactional(readOnly = true)
    public String getAllNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder("$ssds:\n");
        List<SSD> allSsds = getAllWithSortingOfPriceDesc();
        for (SSD ssd : allSsds) {
            String ssdAsString = "{" + ssd.id() + ":" + ssd.hardwareSpec().name() + ":($" + ssd.hardwareSpec().price() + ")}\n";
            stringBuilder.append(ssdAsString);
        }
        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(SSD::id).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();
        List<SSD> allSsds = getAllWithSortingOfPriceDesc();
        for (SSD ssd : allSsds) {
            String ssdPhoto = "";
            if (!ssd.ssdPhotos().isEmpty()) {
                ssdPhoto = ssd.ssdPhotos().getFirst();
            }
            items.add(new ItemForConfigurator(
                    ssd.id(),
                    ssd.hardwareSpec().name(),
                    ssd.hardwareSpec().price(),
                    ssdPhoto,
                    "ssd"
            ));
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Page<SSD> getSsds(Pageable pageable, String sortType, Integer lowestPrice, Integer highestPrice, Integer minimalCapacity, Integer maximalCapacity) {
        List<SSD> ssds = getAll();

        if (lowestPrice != null && highestPrice != null) {
            ssds = ssds.stream()
                    .filter(ssd -> ssd.hardwareSpec().price().intValue() >= lowestPrice &&
                                   ssd.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        if (minimalCapacity != null && maximalCapacity != null) {
            ssds = ssds.stream()
                    .filter(ssd -> ssd.capacity() >= minimalCapacity &&
                                   ssd.capacity() <= maximalCapacity)
                    .toList();
        }

        switch (sortType) {
            case "price-asc":
                ssds = ssds.stream()
                        .sorted(Comparator.comparing(ssd -> ssd.hardwareSpec().price()))
                        .toList();
                break;
            case "price-desc":
                ssds = ssds.stream()
                        .sorted(Comparator.comparing((SSD ssd) -> ssd.hardwareSpec().price()).reversed())
                        .toList();
                break;
            case "rating-asc":
                ssds = ssds.stream()
                        .sorted(Comparator.comparing(ssd -> ssd.hardwareSpec().rating()))
                        .toList();
                break;
            case "rating-desc":
                ssds = ssds.stream()
                        .sorted(Comparator.comparing((SSD ssd) -> ssd.hardwareSpec().rating()).reversed())
                        .toList();
                break;
            default:
                break;
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), ssds.size());
        return new PageImpl<>(ssds.subList(start, end), pageable, ssds.size());
    }


    private List<SSD> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing((SSD ssd) -> ssd.hardwareSpec().price()).reversed())
                .toList();
    }
}
