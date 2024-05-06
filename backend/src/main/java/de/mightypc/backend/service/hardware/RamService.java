package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.RamNotFoundException;
import de.mightypc.backend.model.hardware.RAM;
import de.mightypc.backend.repository.hardware.RamRepository;
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

    @Override
    @Transactional
    public RAM attachPhoto(String id, String photoUrl) {
        RAM currRAM = getById(id);

        ArrayList<String> photos = new ArrayList<>(currRAM.ramPhotos());

        photos.addFirst(photoUrl);
        RAM updatedRAM = currRAM.withPhotos(photos);

        return repository.save(updatedRAM);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedHashMap<String, String> getAllNamesWithPrices() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        List<RAM> allRams = getAllWithSortingOfPriceDesc();

        for (RAM ram : allRams) {
            hashMap.put(ram.id(), ram.hardwareSpec().name() + " ($" + ram.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Transactional(readOnly = true)
    public Page<RAM> getAllWithSortingOfPriceDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<RAM> getAllWithSortingOfPriceAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<RAM> getAllWithSortingOfRatingDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<RAM> getAllWithSortingOfRatingAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<RAM> getAllWithFilteringByPriceAsPages(Pageable pageable, int lowestPrice, int highestPrice) {
        return new PageImpl<>(getAllWithFilteringByPrice(lowestPrice, highestPrice), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<RAM> getAllWithFilteringByEnergyConsumptionAsPages(Pageable pageable, int lowestEnergyConsumption, int highestEnergyConsumption) {
        return new PageImpl<>(getAllWithFilteringByEnergyConsumption(lowestEnergyConsumption, highestEnergyConsumption), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<RAM> getAllWithFilteringByMemorySizeAsPages(Pageable pageable, int minimalMemorySize, int maximalMemorySize) {
        return new PageImpl<>(getAllWithFilteringByMemorySize(minimalMemorySize, maximalMemorySize), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<RAM> getAllWithFilteringByTypeAsPages(Pageable pageable, String type) {
        return new PageImpl<>(getAllWithFilteringByType(type), pageable, 8);
    }

    private List<RAM> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().price()))
                .toList()
                .reversed();
    }

    private List<RAM> getAllWithSortingOfPriceAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().price()))
                .toList();
    }

    private List<RAM> getAllWithSortingOfRatingDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().rating()))
                .toList();
    }

    private List<RAM> getAllWithSortingOfRatingAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().rating()))
                .toList()
                .reversed();
    }

    private List<RAM> getAllWithFilteringByPrice(int lowestPrice, int highestPrice) {
        return getAll().stream()
                .filter(cpu -> cpu.hardwareSpec().price().intValue() >= lowestPrice
                               && cpu.hardwareSpec().price().intValue() <= highestPrice)
                .toList();
    }

    private List<RAM> getAllWithFilteringByType(String type) {
        return getAll().stream()
                .filter(cpu -> cpu.type().equals(type))
                .toList();
    }

    private List<RAM> getAllWithFilteringByEnergyConsumption(int lowestEnergyConsumption, int highestEnergyConsumption) {
        return getAll().stream()
                .filter(cpu -> cpu.energyConsumption() >= lowestEnergyConsumption
                               && cpu.energyConsumption() <= highestEnergyConsumption)
                .toList();
    }

    private List<RAM> getAllWithFilteringByMemorySize(int minimalMemorySize, int maximalMemorySize) {
        return getAll().stream()
                .filter(cpu -> cpu.memorySize() >= minimalMemorySize
                               && cpu.memorySize() <= maximalMemorySize)
                .toList();
    }
}
