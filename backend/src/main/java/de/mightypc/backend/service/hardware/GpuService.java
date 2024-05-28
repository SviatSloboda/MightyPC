package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.GpuNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.GPU;
import de.mightypc.backend.repository.hardware.GpuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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

    @Transactional
    public GPU attachPhoto(String id, String photoUrl) {
        GPU currGpu = getById(id);
        ArrayList<String> photos = new ArrayList<>(currGpu.gpuPhotos());
        photos.addFirst(photoUrl);
        GPU updatedGpu = currGpu.withPhotos(photos);
        return repository.save(updatedGpu);
    }

    @Transactional(readOnly = true)
    public String getAllNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder("$gpus:\n");
        List<GPU> allGpus = getAllWithSortingOfPriceDesc();
        for (GPU gpu : allGpus) {
            String gpuAsString = "{" + gpu.id() + ":" + gpu.hardwareSpec().name() + ":($" + gpu.hardwareSpec().price() + ")}\n";
            stringBuilder.append(gpuAsString);
        }
        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(GPU::id).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();
        List<GPU> allGpus = getAllWithSortingOfPriceDesc();
        for (GPU gpu : allGpus) {
            String gpuPhoto = "";
            if (!gpu.gpuPhotos().isEmpty()) {
                gpuPhoto = gpu.gpuPhotos().getFirst();
            }
            items.add(new ItemForConfigurator(
                    gpu.id(),
                    gpu.hardwareSpec().name(),
                    gpu.hardwareSpec().price(),
                    gpuPhoto,
                    "gpu"
            ));
        }
        return items;
    }

    @Transactional(readOnly = true)
    public Page<GPU> getGpus(Pageable pageable, String sortType, Integer lowestPrice, Integer highestPrice, Integer lowestEnergyConsumption, Integer highestEnergyConsumption) {
        List<GPU> gpus = getAll();

        if (lowestPrice != null && highestPrice != null) {
            gpus = gpus.stream()
                    .filter(gpu -> gpu.hardwareSpec().price().intValue() >= lowestPrice &&
                                   gpu.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        if (lowestEnergyConsumption != null && highestEnergyConsumption != null) {
            gpus = gpus.stream()
                    .filter(gpu -> gpu.energyConsumption() >= lowestEnergyConsumption &&
                                   gpu.energyConsumption() <= highestEnergyConsumption)
                    .toList();
        }

        switch (sortType) {
            case "price-asc":
                gpus = gpus.stream()
                        .sorted(Comparator.comparing(gpu -> gpu.hardwareSpec().price()))
                        .toList();
                break;
            case "price-desc":
                gpus = gpus.stream()
                        .sorted(Comparator.comparing((GPU gpu) -> gpu.hardwareSpec().price()).reversed())
                        .toList();
                break;
            case "rating-asc":
                gpus = gpus.stream()
                        .sorted(Comparator.comparing(gpu -> gpu.hardwareSpec().rating()))
                        .toList();
                break;
            case "rating-desc":
                gpus = gpus.stream()
                        .sorted(Comparator.comparing((GPU gpu) -> gpu.hardwareSpec().rating()).reversed())
                        .toList();
                break;
            default:
                break;
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), gpus.size());
        return new PageImpl<>(gpus.subList(start, end), pageable, gpus.size());
    }

    private List<GPU> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing((GPU gpu) -> gpu.hardwareSpec().price()).reversed())
                .toList();
    }
}

