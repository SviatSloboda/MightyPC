package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.MotherboardNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
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
                .sorted(Comparator.comparingDouble(motherboard -> motherboard.hardwareSpec().price().doubleValue()))
                .toList();
        for (Motherboard motherboard : motherboards) {
            hashMap.put(motherboard.id(), motherboard.hardwareSpec().name() + " ($" + motherboard.hardwareSpec().price() + ")");
        }
        return hashMap;
    }

    @Transactional(readOnly = true)
    public String getAllNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder("$motherboards:\n");
        List<Motherboard> allMotherboards = getAllWithSortingOfPriceDesc();
        for (Motherboard motherboard : allMotherboards) {
            String motherboardAsString = "{" + motherboard.id() + ":" + motherboard.hardwareSpec().name() + ":($" + motherboard.hardwareSpec().price() + ")}\n";
            stringBuilder.append(motherboardAsString);
        }
        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(Motherboard::id).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();
        List<Motherboard> allMotherboards = getAllWithSortingOfPriceDesc();
        for (Motherboard motherboard : allMotherboards) {
            String motherboardPhoto = "";
            if (!motherboard.motherboardPhotos().isEmpty()) {
                motherboardPhoto = motherboard.motherboardPhotos().getFirst();
            }
            items.add(new ItemForConfigurator(
                    motherboard.id(),
                    motherboard.hardwareSpec().name(),
                    motherboard.hardwareSpec().price(),
                    motherboardPhoto,
                    "motherboard"
            ));
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Page<Motherboard> getMotherboards(Pageable pageable, String sortType, Integer lowestPrice, Integer highestPrice, String socket, Integer lowestEnergyConsumption, Integer highestEnergyConsumption) {
        List<Motherboard> motherboards = getAll();

        if (lowestPrice != null && highestPrice != null) {
            motherboards = motherboards.stream()
                    .filter(motherboard -> motherboard.hardwareSpec().price().intValue() >= lowestPrice &&
                                           motherboard.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        if (socket != null && !socket.isEmpty()) {
            motherboards = motherboards.stream()
                    .filter(motherboard -> motherboard.socket().equals(socket))
                    .toList();
        }

        if (lowestEnergyConsumption != null && highestEnergyConsumption != null) {
            motherboards = motherboards.stream()
                    .filter(motherboard -> motherboard.energyConsumption() >= lowestEnergyConsumption &&
                                           motherboard.energyConsumption() <= highestEnergyConsumption)
                    .toList();
        }

        switch (sortType) {
            case "price-asc":
                motherboards = motherboards.stream()
                        .sorted(Comparator.comparing(motherboard -> motherboard.hardwareSpec().price()))
                        .toList();
                break;
            case "price-desc":
                motherboards = motherboards.stream()
                        .sorted(Comparator.comparing((Motherboard motherboard) -> motherboard.hardwareSpec().price()).reversed())
                        .toList();
                break;
            case "rating-asc":
                motherboards = motherboards.stream()
                        .sorted(Comparator.comparing(motherboard -> motherboard.hardwareSpec().rating()))
                        .toList();
                break;
            case "rating-desc":
                motherboards = motherboards.stream()
                        .sorted(Comparator.comparing((Motherboard motherboard) -> motherboard.hardwareSpec().rating()).reversed())
                        .toList();
                break;
            default:
                break;
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), motherboards.size());
        return new PageImpl<>(motherboards.subList(start, end), pageable, motherboards.size());
    }


    private List<Motherboard> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing((Motherboard motherboard) -> motherboard.hardwareSpec().price()).reversed())
                .toList();
    }
}
