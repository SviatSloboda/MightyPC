package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.pc.hardware.CpuNotFoundException;
import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.repository.hardware.CpuRepository;
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
public class CpuService extends BaseService<CPU, CpuRepository, CpuNotFoundException> {
    public CpuService(CpuRepository repository) {
        super(repository);
    }

    @Override
    protected CpuNotFoundException getException(String message) {
        return new CpuNotFoundException(message);
    }

    @Override
    protected String getId(CPU entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(CPU entity) {
        return entity.hardwareSpec().name();
    }

    @Override
    @Transactional
    public CPU attachPhoto(String id, String photoUrl) {
        CPU currCpu = getById(id);

        ArrayList<String> photos = new ArrayList<>(currCpu.cpuPhotos());

        photos.addFirst(photoUrl);
        CPU updatedCpu = currCpu.withPhotos(photos);

        return repository.save(updatedCpu);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedHashMap<String, String> getAllNamesWithPrices() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        List<CPU> allCpus = getAllWithSortingOfPriceDesc();

        for (CPU cpu : allCpus) {
            hashMap.put(cpu.id(), cpu.hardwareSpec().name() + " ($" + cpu.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Transactional(readOnly = true)
    public String getSocketOfCpuById(String cpuId) {
        return getById(cpuId).socket();
    }

    @Transactional(readOnly = true)
    public Page<CPU> getAllWithSortingOfPriceDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<CPU> getAllWithSortingOfPriceAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<CPU> getAllWithSortingOfRatingDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<CPU> getAllWithSortingOfRatingAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<CPU> getAllWithFilteringByPriceAsPages(Pageable pageable, int lowestPrice, int highestPrice) {
        return new PageImpl<>(getAllWithFilteringByPrice(lowestPrice, highestPrice), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<CPU> getAllWithFilteringByEnergyConsumptionAsPages(Pageable pageable, int lowestEnergyConsumption, int highestEnergyConsumption) {
        return new PageImpl<>(getAllWithFilteringByEnergyConsumption(lowestEnergyConsumption, highestEnergyConsumption), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<CPU> getAllWithFilteringBySocketAsPages(Pageable pageable, String socket) {
        return new PageImpl<>(getAllWithFilteringBySocket(socket), pageable, 8);
    }

    private List<CPU> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().price()))
                .toList()
                .reversed();
    }

    private List<CPU> getAllWithSortingOfPriceAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().price()))
                .toList();
    }

    private List<CPU> getAllWithSortingOfRatingDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().rating()))
                .toList();
    }

    private List<CPU> getAllWithSortingOfRatingAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().rating()))
                .toList()
                .reversed();
    }

    private List<CPU> getAllWithFilteringByPrice(int lowestPrice, int highestPrice) {
        return getAll().stream()
                .filter(cpu -> cpu.hardwareSpec().price().intValue() >= lowestPrice
                               && cpu.hardwareSpec().price().intValue() <= highestPrice)
                .toList();
    }

    private List<CPU> getAllWithFilteringBySocket(String socket) {
        return getAll().stream()
                .filter(cpu -> cpu.socket().equals(socket))
                .toList();
    }

    private List<CPU> getAllWithFilteringByEnergyConsumption(int lowestEnergyConsumption, int highestEnergyConsumption) {
        return getAll().stream()
                .filter(cpu -> cpu.energyConsumption() >= lowestEnergyConsumption
                               && cpu.energyConsumption() <= highestEnergyConsumption)
                .toList();
    }
}
