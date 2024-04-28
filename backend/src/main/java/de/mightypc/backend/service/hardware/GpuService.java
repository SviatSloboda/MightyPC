package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.hardware.GpuNotFoundException;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.repository.hardware.GpuRepository;
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
public class GpuService extends BaseService<GPU, GpuRepository, GpuNotFoundException> {
    public GpuService(GpuRepository repository) {
        super(repository);
    }

    @Override
    protected GpuNotFoundException getException(String message) {
        return new GpuNotFoundException(message);
    }

    @Override
    protected String getId(GPU entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(GPU entity) {
        return entity.hardwareSpec().name();
    }

    public GPU attachPhoto(String id, String photoUrl) {
        GPU currGpu = getById(id);

        ArrayList<String> photos = new ArrayList<>(currGpu.gpuPhotos());

        photos.addFirst(photoUrl);
        GPU updatedGpu = currGpu.withPhotos(photos);

        return repository.save(updatedGpu);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedHashMap<String, String> getAllNamesWithPrices() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        List<GPU> allGpus = getAllWithSortingOfPriceDesc();

        for (GPU gpu : allGpus) {
            hashMap.put(gpu.id(), gpu.hardwareSpec().name() + " ($" + gpu.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Transactional(readOnly = true)
    public Page<GPU> getAllWithSortingOfPriceDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<GPU> getAllWithSortingOfPriceAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<GPU> getAllWithSortingOfRatingDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<GPU> getAllWithSortingOfRatingAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<GPU> getAllWithFilteringByPriceAsPages(Pageable pageable, int lowestPrice, int highestPrice) {
        return new PageImpl<>(getAllWithFilteringByPrice(lowestPrice, highestPrice), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<GPU> getAllWithFilteringByEnergyConsumptionAsPages(Pageable pageable, int lowestEnergyConsumption, int highestEnergyConsumption) {
        return new PageImpl<>(getAllWithFilteringByEnergyConsumption(lowestEnergyConsumption, highestEnergyConsumption), pageable, 8);
    }

    private List<GPU> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(gpu -> gpu.hardwareSpec().price()))
                .toList()
                .reversed();
    }

    private List<GPU> getAllWithSortingOfPriceAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(gpu -> gpu.hardwareSpec().price()))
                .toList();
    }

    private List<GPU> getAllWithSortingOfRatingDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(gpu -> gpu.hardwareSpec().rating()))
                .toList();
    }

    private List<GPU> getAllWithSortingOfRatingAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(gpu -> gpu.hardwareSpec().rating()))
                .toList()
                .reversed();
    }

    private List<GPU> getAllWithFilteringByPrice(int lowestPrice, int highestPrice) {
        return getAll().stream()
                .filter(gpu -> gpu.hardwareSpec().price().intValue() >= lowestPrice
                               && gpu.hardwareSpec().price().intValue() <= highestPrice)
                .toList();
    }

    private List<GPU> getAllWithFilteringByEnergyConsumption(int lowestEnergyConsumption, int highestEnergyConsumption) {
        return getAll().stream()
                .filter(gpu -> gpu.energyConsumption() >= lowestEnergyConsumption
                               && gpu.energyConsumption() <= highestEnergyConsumption)
                .toList();
    }
}
