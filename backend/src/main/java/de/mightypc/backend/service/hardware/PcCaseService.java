package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.PcCaseNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.PcCase;
import de.mightypc.backend.repository.hardware.PcCaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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

    @Transactional
    public PcCase attachPhoto(String id, String photoUrl) {
        PcCase currPcCase = getById(id);
        ArrayList<String> photos = new ArrayList<>(currPcCase.pcCasePhotos());
        photos.addFirst(photoUrl);
        PcCase updatedPcCase = currPcCase.withPhotos(photos);
        return repository.save(updatedPcCase);
    }

    @Transactional(readOnly = true)
    public String getAllNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder("$pcCases:\n");
        List<PcCase> allPcCases = getAllWithSortingOfPriceDesc();
        for (PcCase pcCase : allPcCases) {
            String pcCaseAsString = "{" + pcCase.id() + ":" + pcCase.hardwareSpec().name() + ":($" + pcCase.hardwareSpec().price() + ")}\n";
            stringBuilder.append(pcCaseAsString);
        }
        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(PcCase::id).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();
        List<PcCase> allPcCases = getAllWithSortingOfPriceDesc();
        for (PcCase pcCase : allPcCases) {
            String pcCasePhoto = "";
            if (!pcCase.pcCasePhotos().isEmpty()) {
                pcCasePhoto = pcCase.pcCasePhotos().getFirst();
            }
            items.add(new ItemForConfigurator(
                    pcCase.id(),
                    pcCase.hardwareSpec().name(),
                    pcCase.hardwareSpec().price(),
                    pcCasePhoto,
                    "pc-case"
            ));
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Page<PcCase> getPcCases(Pageable pageable, String sortType, Integer lowestPrice, Integer highestPrice) {
        List<PcCase> pcCases = getAll();

        if (lowestPrice != null && highestPrice != null) {
            pcCases = pcCases.stream()
                    .filter(pcCase -> pcCase.hardwareSpec().price().intValue() >= lowestPrice &&
                                      pcCase.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        switch (sortType) {
            case "price-asc":
                pcCases = pcCases.stream()
                        .sorted(Comparator.comparing(pcCase -> pcCase.hardwareSpec().price()))
                        .toList();
                break;
            case "price-desc":
                pcCases = pcCases.stream()
                        .sorted(Comparator.comparing((PcCase pcCase) -> pcCase.hardwareSpec().price()).reversed())
                        .toList();
                break;
            case "rating-asc":
                pcCases = pcCases.stream()
                        .sorted(Comparator.comparing(pcCase -> pcCase.hardwareSpec().rating()))
                        .toList();
                break;
            case "rating-desc":
                pcCases = pcCases.stream()
                        .sorted(Comparator.comparing((PcCase pcCase) -> pcCase.hardwareSpec().rating()).reversed())
                        .toList();
                break;
            default:
                break;
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), pcCases.size());
        return new PageImpl<>(pcCases.subList(start, end), pageable, pcCases.size());
    }


    private List<PcCase> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing((PcCase pcCase) -> pcCase.hardwareSpec().price()).reversed())
                .toList();
    }
}
