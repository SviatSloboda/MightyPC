package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.hardware.HddNotFoundException;
import de.mightypc.backend.model.hardware.HDD;
import de.mightypc.backend.repository.hardware.HddRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
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

    @Override
    @Transactional
    public HDD attachPhoto(String id, String photoUrl) {
        HDD currHdd = getById(id);

        ArrayList<String> photos = new ArrayList<>(currHdd.hddPhotos());

        photos.addFirst(photoUrl);
        HDD updatedHdd = currHdd.withPhotos(photos);

        return repository.save(updatedHdd);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedHashMap<String, String> getAllNamesWithPrices() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        List<HDD> allHdds = getAllWithSortingOfPriceDesc();

        for (HDD hdd : allHdds) {
            hashMap.put(hdd.id(), hdd.hardwareSpec().name() + " ($" + hdd.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Transactional(readOnly = true)
    public Page<HDD> getAllWithSortingOfPriceDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<HDD> getAllWithSortingOfPriceAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<HDD> getAllWithSortingOfRatingDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<HDD> getAllWithSortingOfRatingAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<HDD> getAllWithFilteringByPriceAsPages(Pageable pageable, int lowestPrice, int highestPrice) {
        return new PageImpl<>(getAllWithFilteringByPrice(lowestPrice, highestPrice), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<HDD> getAllWithFilteringByEnergyConsumptionAsPages(Pageable pageable, int lowestEnergyConsumption, int highestEnergyConsumption) {
        return new PageImpl<>(getAllWithFilteringByEnergyConsumption(lowestEnergyConsumption, highestEnergyConsumption), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<HDD> getAllWithFilteringByCapacityAsPages(Pageable pageable, int minimalCapacity, int maximalCapacity) {
        return new PageImpl<>(getAllWithFilteringByCapasity(minimalCapacity, maximalCapacity), pageable, 8);
    }

    private List<HDD> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(hdd -> hdd.hardwareSpec().price()))
                .toList()
                .reversed();
    }

    private List<HDD> getAllWithSortingOfPriceAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(hdd -> hdd.hardwareSpec().price()))
                .toList();
    }

    private List<HDD> getAllWithSortingOfRatingDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(hdd -> hdd.hardwareSpec().rating()))
                .toList();
    }

    private List<HDD> getAllWithSortingOfRatingAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(hdd -> hdd.hardwareSpec().rating()))
                .toList()
                .reversed();
    }

    private List<HDD> getAllWithFilteringByPrice(int lowestPrice, int highestPrice) {
        return getAll().stream()
                .filter(hdd -> hdd.hardwareSpec().price().intValue() >= lowestPrice
                               && hdd.hardwareSpec().price().intValue() <= highestPrice)
                .toList();
    }

    private List<HDD> getAllWithFilteringByCapasity(int minimumCapacity, int maximalCapacity) {
        return getAll().stream()
                .filter(hdd -> hdd.capacity() >= minimumCapacity && hdd.capacity() <= maximalCapacity)
                .toList();
    }

    private List<HDD> getAllWithFilteringByEnergyConsumption(int lowestEnergyConsumption, int highestEnergyConsumption) {
        return getAll().stream()
                .filter(hdd -> hdd.energyConsumption() >= lowestEnergyConsumption
                               && hdd.energyConsumption() <= highestEnergyConsumption)
                .toList();
    }
}
