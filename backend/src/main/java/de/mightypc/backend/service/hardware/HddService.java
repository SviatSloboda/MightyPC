package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.HddNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.repository.hardware.HddRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HddService extends BaseService<HDD, HddRepository, HddNotFoundException> {
    public HddService(HddRepository hddRepository) {
        super(hddRepository);
    }

    @Override
    protected HddNotFoundException getException(String message) {
        return new HddNotFoundException(message);
    }

    @Override
    protected String getId(HDD entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(HDD entity) {
        return entity.hardwareSpec().name();
    }

    @Transactional
    public HDD attachPhoto(String id, String photoUrl) {
        HDD currHdd = getById(id);
        ArrayList<String> photos = new ArrayList<>(currHdd.hddPhotos());
        photos.addFirst(photoUrl);
        HDD updatedHdd = currHdd.withPhotos(photos);
        return repository.save(updatedHdd);
    }

    @Transactional(readOnly = true)
    public String getAllNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder("$hdds:\n");
        List<HDD> allHdds = getAllWithSortingOfPriceDesc();
        for (HDD hdd : allHdds) {
            String hddAsString = "{" + hdd.id() + ":" + hdd.hardwareSpec().name() + ":($" + hdd.hardwareSpec().price() + ")}\n";
            stringBuilder.append(hddAsString);
        }
        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(HDD::id).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();
        List<HDD> allHdds = getAllWithSortingOfPriceDesc();
        for (HDD hdd : allHdds) {
            String hddPhoto = "";
            if (!hdd.hddPhotos().isEmpty()) {
                hddPhoto = hdd.hddPhotos().getFirst();
            }
            items.add(new ItemForConfigurator(
                    hdd.id(),
                    hdd.hardwareSpec().name(),
                    hdd.hardwareSpec().price(),
                    hddPhoto,
                    "hdd"
            ));
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Page<HDD> getHdds(Pageable pageable, String sortType, Integer lowestPrice, Integer highestPrice, Integer minimalCapacity, Integer maximalCapacity, Integer lowestEnergyConsumption, Integer highestEnergyConsumption) {
        List<HDD> hdds = getAll();

        if (lowestPrice != null && highestPrice != null) {
            hdds = hdds.stream()
                    .filter(hdd -> hdd.hardwareSpec().price().intValue() >= lowestPrice &&
                                   hdd.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        if (minimalCapacity != null && maximalCapacity != null) {
            hdds = hdds.stream()
                    .filter(hdd -> hdd.capacity() >= minimalCapacity && hdd.capacity() <= maximalCapacity)
                    .toList();
        }

        if (lowestEnergyConsumption != null && highestEnergyConsumption != null) {
            hdds = hdds.stream()
                    .filter(hdd -> hdd.energyConsumption() >= lowestEnergyConsumption &&
                                   hdd.energyConsumption() <= highestEnergyConsumption)
                    .toList();
        }

        if (sortType != null) {
            switch (sortType) {
                case "price-asc":
                    hdds = hdds.stream()
                            .sorted(Comparator.comparing(hdd -> hdd.hardwareSpec().price()))
                            .toList();
                    break;
                case "price-desc":
                    hdds = hdds.stream()
                            .sorted(Comparator.comparing((HDD hdd) -> hdd.hardwareSpec().price()).reversed())
                            .toList();
                    break;
                case "rating-asc":
                    hdds = hdds.stream()
                            .sorted(Comparator.comparing(hdd -> hdd.hardwareSpec().rating()))
                            .toList();
                    break;
                case "rating-desc":
                    hdds = hdds.stream()
                            .sorted(Comparator.comparing((HDD hdd) -> hdd.hardwareSpec().rating()).reversed())
                            .toList();
                    break;
                default:
                    break;
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), hdds.size());
        return new PageImpl<>(hdds.subList(start, end), pageable, hdds.size());
    }


    private List<HDD> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing((HDD hdd) -> hdd.hardwareSpec().price()).reversed())
                .toList();
    }
}
