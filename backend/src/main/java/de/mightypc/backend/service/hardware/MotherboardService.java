package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.MotherboardNotFoundException;
import de.mightypc.backend.model.hardware.Motherboard;
import de.mightypc.backend.repository.hardware.MotherboardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
public class MotherboardService extends BaseService<Motherboard, MotherboardRepository, MotherboardNotFoundException> {
    public MotherboardService(MotherboardRepository motherboardRepository) {
        super(motherboardRepository);
    }

    @Override
    protected MotherboardNotFoundException getException(String message) {
        return new MotherboardNotFoundException(message);
    }

    @Override
    protected String getId(Motherboard entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(Motherboard entity) {
        return entity.hardwareSpec().name();
    }

    @Override
    @Transactional
    public Motherboard attachPhoto(String id, String photoUrl) {
        Motherboard currMotherboard = getById(id);

        ArrayList<String> photos = new ArrayList<>(currMotherboard.motherboardPhotos());

        photos.addFirst(photoUrl);
        Motherboard updatedMotherboard = currMotherboard.withPhotos(photos);

        return repository.save(updatedMotherboard);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getMotherboardsBySocket(String motherboardSocket) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        List<Motherboard> motherboards = getAll().stream()
                .filter(motherboard -> motherboard.socket().equalsIgnoreCase(motherboardSocket))
                .toList();

        for (Motherboard motherboard : motherboards) {
            hashMap.put(motherboard.id(), motherboard.hardwareSpec().name() + " ($" + motherboard.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedHashMap<String, String> getAllNamesWithPrices() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        List<Motherboard> allMotherboards = getAllWithSortingOfPriceDesc();

        for (Motherboard motherboard : allMotherboards) {
            hashMap.put(motherboard.id(), motherboard.hardwareSpec().name() + " ($" + motherboard.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Transactional(readOnly = true)
    public Page<Motherboard> getAllWithSortingOfPriceDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<Motherboard> getAllWithSortingOfPriceAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<Motherboard> getAllWithSortingOfRatingDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<Motherboard> getAllWithSortingOfRatingAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<Motherboard> getAllWithFilteringByPriceAsPages(Pageable pageable, int lowestPrice, int highestPrice) {
        return new PageImpl<>(getAllWithFilteringByPrice(lowestPrice, highestPrice), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<Motherboard> getAllWithFilteringByEnergyConsumptionAsPages(Pageable pageable, int lowestEnergyConsumption, int highestEnergyConsumption) {
        return new PageImpl<>(getAllWithFilteringByEnergyConsumption(lowestEnergyConsumption, highestEnergyConsumption), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<Motherboard> getAllWithFilteringBySocketAsPages(Pageable pageable, String socket) {
        return new PageImpl<>(getAllWithFilteringBySocket(socket), pageable, 8);
    }

    private List<Motherboard> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(motherboard -> motherboard.hardwareSpec().price()))
                .toList()
                .reversed();
    }

    private List<Motherboard> getAllWithSortingOfPriceAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(motherboard -> motherboard.hardwareSpec().price()))
                .toList();
    }

    private List<Motherboard> getAllWithSortingOfRatingDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(motherboard -> motherboard.hardwareSpec().rating()))
                .toList();
    }

    private List<Motherboard> getAllWithSortingOfRatingAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(motherboard -> motherboard.hardwareSpec().rating()))
                .toList()
                .reversed();
    }

    private List<Motherboard> getAllWithFilteringByPrice(int lowestPrice, int highestPrice) {
        return getAll().stream()
                .filter(motherboard -> motherboard.hardwareSpec().price().intValue() >= lowestPrice
                                       && motherboard.hardwareSpec().price().intValue() <= highestPrice)
                .toList();
    }

    private List<Motherboard> getAllWithFilteringBySocket(String socket) {
        return getAll().stream()
                .filter(motherboard -> motherboard.socket().equals(socket))
                .toList();
    }

    private List<Motherboard> getAllWithFilteringByEnergyConsumption(int lowestEnergyConsumption, int highestEnergyConsumption) {
        return getAll().stream()
                .filter(motherboard -> motherboard.energyConsumption() >= lowestEnergyConsumption
                                       && motherboard.energyConsumption() <= highestEnergyConsumption)
                .toList();
    }
}
