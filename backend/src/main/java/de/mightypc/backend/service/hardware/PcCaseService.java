package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.PcCaseNotFoundException;
import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
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
public class PcCaseService extends BaseService<PcCase, PcCaseRepository, PcCaseNotFoundException> {
    public PcCaseService(PcCaseRepository repository) {
        super(repository);
    }

    @Override
    protected PcCaseNotFoundException getException(String message) {
        return new PcCaseNotFoundException(message);
    }

    @Override
    protected String getId(PcCase entity) {
        return entity.id();
    }

    @Override
    protected String getNameOfEntity(PcCase entity) {
        return entity.hardwareSpec().name();
    }

    @Override
    @Transactional
    public PcCase attachPhoto(String id, String photoUrl) {
        PcCase currPcCase = getById(id);

        ArrayList<String> photos = new ArrayList<>(currPcCase.pcCasePhotos());

        photos.addFirst(photoUrl);
        PcCase updatedPcCase = currPcCase.withPhotos(photos);

        return repository.save(updatedPcCase);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedHashMap<String, String> getAllNamesWithPrices() {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();

        List<PcCase> allPcCases = getAllWithSortingOfPriceDesc();

        for (PcCase pcCase : allPcCases) {
            hashMap.put(pcCase.id(), pcCase.hardwareSpec().name() + " ($" + pcCase.hardwareSpec().price() + ")");
        }

        return hashMap;
    }

    @Transactional(readOnly = true)
    public Page<PcCase> getAllWithSortingOfPriceDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PcCase> getAllWithSortingOfPriceAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfPriceAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PcCase> getAllWithSortingOfRatingDescAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingDesc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PcCase> getAllWithSortingOfRatingAscAsPages(Pageable pageable) {
        return new PageImpl<>(getAllWithSortingOfRatingAsc(), pageable, 8);
    }

    @Transactional(readOnly = true)
    public Page<PcCase> getAllWithFilteringByPriceAsPages(Pageable pageable, int lowestPrice, int highestPrice) {
        return new PageImpl<>(getAllWithFilteringByPrice(lowestPrice, highestPrice), pageable, 8);
    }

    private List<PcCase> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().price()))
                .toList()
                .reversed();
    }

    private List<PcCase> getAllWithSortingOfPriceAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().price()))
                .toList();
    }

    private List<PcCase> getAllWithSortingOfRatingDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().rating()))
                .toList();
    }

    private List<PcCase> getAllWithSortingOfRatingAsc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().rating()))
                .toList()
                .reversed();
    }

    private List<PcCase> getAllWithFilteringByPrice(int lowestPrice, int highestPrice) {
        return getAll().stream()
                .filter(cpu -> cpu.hardwareSpec().price().intValue() >= lowestPrice
                               && cpu.hardwareSpec().price().intValue() <= highestPrice)
                .toList();
    }
}
