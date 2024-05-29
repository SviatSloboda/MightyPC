package de.mightypc.backend.service.hardware;

import de.mightypc.backend.exception.hardware.CpuNotFoundException;
import de.mightypc.backend.model.configurator.ItemForConfigurator;
import de.mightypc.backend.model.hardware.CPU;
import de.mightypc.backend.repository.hardware.CpuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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

    @Transactional(readOnly = true)
    public String getAllNamesWithPrices() {
        StringBuilder stringBuilder = new StringBuilder("$cpus:\n");

        List<CPU> allCpus = getAllWithSortingOfPriceDesc();

        for (CPU cpu : allCpus) {
            String cpuAsString = "{" + cpu.id() + ":" + cpu.hardwareSpec().name() + ":($" + cpu.hardwareSpec().price() + ")}\n";
            stringBuilder.append(cpuAsString);
        }

        return stringBuilder.toString();
    }

    @Transactional(readOnly = true)
    public List<String> getAllIds() {
        return getAllWithSortingOfPriceDesc().stream().map(CPU::id).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemForConfigurator> getAllHardwareInfoForConfiguration() {
        List<ItemForConfigurator> items = new ArrayList<>();
        List<CPU> allCpus = getAllWithSortingOfPriceDesc();

        for (CPU cpu : allCpus) {
            String cpuPhoto = "";

            if (!cpu.cpuPhotos().isEmpty()) {
                cpuPhoto = cpu.cpuPhotos().getFirst();
            }

            items.add(new ItemForConfigurator(
                    cpu.id(),
                    cpu.hardwareSpec().name(),
                    cpu.hardwareSpec().price(),
                    cpuPhoto,
                    "cpu"
            ));
        }

        return items;
    }

    @Transactional(readOnly = true)
    public String getSocketOfCpuById(String cpuId) {
        return getById(cpuId).socket();
    }

    @Transactional(readOnly = true)
    public Page<CPU> getCpus(Pageable pageable, String sortType, String socket, Integer lowestPrice, Integer highestPrice) {
        List<CPU> cpus = getAll();

        if (socket != null && !socket.isEmpty()) {
            cpus = cpus.stream()
                    .filter(cpu -> cpu.socket().equals(socket))
                    .toList();
        }

        if (lowestPrice != null && highestPrice != null) {
            cpus = cpus.stream()
                    .filter(cpu -> cpu.hardwareSpec().price().intValue() >= lowestPrice &&
                                   cpu.hardwareSpec().price().intValue() <= highestPrice)
                    .toList();
        }

        if (sortType != null) {
            switch (sortType) {
                case "price-asc":
                    cpus = cpus.stream()
                            .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().price()))
                            .toList();
                    break;
                case "price-desc":
                    cpus = cpus.stream()
                            .sorted(Comparator.comparing((CPU cpu) -> cpu.hardwareSpec().price()).reversed())
                            .toList();
                    break;
                case "rating-asc":
                    cpus = cpus.stream()
                            .sorted(Comparator.comparing(cpu -> cpu.hardwareSpec().rating()))
                            .toList();
                    break;
                case "rating-desc":
                    cpus = cpus.stream()
                            .sorted(Comparator.comparing((CPU cpu) -> cpu.hardwareSpec().rating()).reversed())
                            .toList();
                    break;
                default:
                    break;
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), cpus.size());
        return new PageImpl<>(cpus.subList(start, end), pageable, cpus.size());
    }

    private List<CPU> getAllWithSortingOfPriceDesc() {
        return getAll()
                .stream()
                .sorted(Comparator.comparing((CPU cpu) -> cpu.hardwareSpec().price()).reversed())
                .toList();
    }
}
