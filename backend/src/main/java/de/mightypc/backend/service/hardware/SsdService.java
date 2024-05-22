package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.SsdNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.model.hardware.SSD;
import de.mightypc.backend.repository.hardware.SsdRepository;
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

    @Override
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

    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(SSD::id).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();

        List<SSD> allSsds = getAllWithSortingOfPriceDesc();

        for (SSD ssd : allSsds) {
            String ssdPhoto = "";

            if(!ssd.ssdPhotos().isEmpty()){
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
    public Page<SSD> getAllWithSortingOfPriceDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<SSD> getAllWithSortingOfPriceAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<SSD> getAllWithSortingOfRatingDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<SSD> getAllWithSortingOfRatingAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<SSD> getAllWithFilteringByPriceAsPages(Pageable pageable, int lowestPrice, int highestPrice) {
        return new PageImpl<>(getAllWithFilteringByPrice(lowestPrice, highestPrice), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<SSD> getAllWithFilteringByEnergyConsumptionAsPages(Pageable pageable, int lowestEnergyConsumption, int highestEnergyConsumption) {
        return new PageImpl<>(getAllWithFilteringByEnergyConsumption(lowestEnergyConsumption, highestEnergyConsumption), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<SSD> getAllWithFilteringByCapacityAsPages(Pageable pageable, int minimalCapacity, int maximalCapacity) {
        return new PageImpl<>(getAllWithFilteringByCapacity(minimalCapacity, maximalCapacity), pageable, 8);
    }

    private List<SSD> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(ssd -> ssd.hardwareSpec().price()))
                .toList()
                .reversed();
    }

    private List<SSD> getAllWithSortingOfPriceAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(ssd -> ssd.hardwareSpec().price()))
                .toList();
    }

    private List<SSD> getAllWithSortingOfRatingDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(ssd -> ssd.hardwareSpec().rating()))
                .toList()
                .reversed();
    }

    private List<SSD> getAllWithSortingOfRatingAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(ssd -> ssd.hardwareSpec().rating()))
                .toList();
    }

    private List<SSD> getAllWithFilteringByPrice(int lowestPrice, int highestPrice) {
        return getAll().stream()
                .filter(ssd -> ssd.hardwareSpec().price().intValue() >= lowestPrice
                               && ssd.hardwareSpec().price().intValue() <= highestPrice)
                .toList();
    }

    private List<SSD> getAllWithFilteringByCapacity(int minimalCapacity, int maximalCapacity) {
        return getAll().stream()
                .filter(ssd -> ssd.capacity() >= minimalCapacity
                               && ssd.capacity() <= maximalCapacity)
                .toList();
    }

    private List<SSD> getAllWithFilteringByEnergyConsumption(int lowestEnergyConsumption, int highestEnergyConsumption) {
        return getAll().stream()
                .filter(ssd -> ssd.energyConsumption() >= lowestEnergyConsumption
                               && ssd.energyConsumption() <= highestEnergyConsumption)
                .toList();
    }
}
