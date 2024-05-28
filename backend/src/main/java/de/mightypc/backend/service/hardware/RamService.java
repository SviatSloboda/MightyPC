package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.RamNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.repository.hardware.RamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RamService extends BaseService<RAM, RamRepository, RamNotFoundException> {
    public RamService(RamRepository ramRepository) {
        super(ramRepository);
    }

    @Override
    protected RamNotFoundException getException(String message) {
        return new RamNotFoundException(message);
    }

    @Override
    protected String getId(RAM entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(RAM entity) {
        return entity.hardwareSpec().name();
    }

    @Transactional
    public RAM attachPhoto(String id, String photoUrl) {
        RAM currRAM = getById(id);
        ArrayList<String> photos = new ArrayList<>(currRAM.ramPhotos());
        photos.addFirst(photoUrl);
        RAM updatedRAM = currRAM.withPhotos(photos);
        return repository.save(updatedRAM);
    }

    @Transactional(readOnly = true)
    public String getAllNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder("$rams:\n");
        List<RAM> allRams = getAllWithSortingOfPriceDesc();
        for (RAM ram : allRams) {
            String ramAsString = "{" + ram.id() + ":" + ram.hardwareSpec().name() + ":($" + ram.hardwareSpec().price() + ")}\n";
            stringBuilder.append(ramAsString);
        }
        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(RAM::id).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();
        List<RAM> allRams = getAllWithSortingOfPriceDesc();
        for (RAM ram : allRams) {
            String ramPhoto = "";
            if (!ram.ramPhotos().isEmpty()) {
                ramPhoto = ram.ramPhotos().getFirst();
            }
            items.add(new ItemForConfigurator(
                    ram.id(),
                    ram.hardwareSpec().name(),
                    ram.hardwareSpec().price(),
                    ramPhoto,
                    "ram"
            ));
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Page<RAM> getRams(Pageable pageable, String sortType, Integer lowestPrice, Integer highestPrice, Integer minimalMemorySize, Integer maximalMemorySize, String type) {
        List<RAM> rams = getAll();

        if (lowestPrice != null && highestPrice != null) {
            rams = rams.stream()
                    .filter(ram -> ram.hardwareSpec().price().intValue() >= lowestPrice &&
                                   ram.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        if (minimalMemorySize != null && maximalMemorySize != null) {
            rams = rams.stream()
                    .filter(ram -> ram.memorySize() >= minimalMemorySize &&
                                   ram.memorySize() <= maximalMemorySize)
                    .toList();
        }

        if (type != null) {
            rams = rams.stream()
                    .filter(ram -> ram.type().equals(type))
                    .toList();
        }

        switch (sortType) {
            case "price-asc":
                rams = rams.stream()
                        .sorted(Comparator.comparing(ram -> ram.hardwareSpec().price()))
                        .toList();
                break;
            case "price-desc":
                rams = rams.stream()
                        .sorted(Comparator.comparing((RAM ram) -> ram.hardwareSpec().price()).reversed())
                        .toList();
                break;
            case "rating-asc":
                rams = rams.stream()
                        .sorted(Comparator.comparing(ram -> ram.hardwareSpec().rating()))
                        .toList();
                break;
            case "rating-desc":
                rams = rams.stream()
                        .sorted(Comparator.comparing((RAM ram) -> ram.hardwareSpec().rating()).reversed())
                        .toList();
                break;
            default:
                break;
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), rams.size());

        return new PageImpl<>(rams.subList(start, end), pageable, rams.size());
    }


    private List<RAM> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing((RAM ram) -> ram.hardwareSpec().price()).reversed())
                .toList();
    }
}
