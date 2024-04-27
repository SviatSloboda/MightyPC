package de.mightypc.backend.service.configurator;

import de.mightypc.backend.exception.pc.UserPcNotFoundException;
import de.mightypc.backend.model.configurator.SpecsForEnergyConsumption;
import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.Specs;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.pc.createpc.CreatePC;
import de.mightypc.backend.model.pc.createpc.PcResponse;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.service.pc.PcService;
import de.mightypc.backend.service.shop.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserPcsService {
    private final UserService userService;
    private final PcService pcService;

    private final UserRepository userRepository;

    public UserPcsService(UserService userService, PcService pcService, UserRepository userRepository) {
        this.userService = userService;
        this.pcService = pcService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void savePc(String userId, CreatePC createPC) {
        User user = userService.getUserById(userId);

        PC pcToSave = pcService.createPc(createPC);

        if (user.getPcs() == null) {
            user.setPcs(new ArrayList<>(Collections.singletonList(pcToSave)));
        } else {
            List<PC> userPcs = user.getPcs();
            userPcs.add(pcToSave);
        }

        userRepository.save(user);
    }

    @Transactional
    public void addPrivateUserPcToAllPcs(String userId, String userPcId) {
        User user = userService.getUserById(userId);

        PC pcToSave = user.getPcs().stream()
                .filter(pc -> pc.id().equals(userPcId))
                .findAny()
                .orElseThrow(() -> new UserPcNotFoundException("There is no such user PC with id: " + userPcId));

        pcService.save(pcToSave);
    }

    private PC getPcOfUserById(String userId, String pcId) {
        User user = userService.getUserById(userId);

        return user.getPcs().stream()
                .filter(pc -> pc.id().equals(pcId))
                .findAny()
                .orElseThrow(() -> new UserPcNotFoundException("There is no such pc!"));
    }

    @Transactional
    public void deletePc(String userId, String pcId) {
        User user = userService.getUserById(userId);

        PC pc = getPcOfUserById(userId, pcId);

        user.getPcs().remove(pc);

        userRepository.save(user);
    }

    @Transactional
    public void deleteAll(String userId) {
        User user = userService.getUserById(userId);

        user.setPcs(new ArrayList<>());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<PcResponse> getAllByPage(String userId, Pageable pageable) {
        List<PC> userPCs = userService.getUserById(userId).getPcs();

        List<PcResponse> responses = userPCs.stream()
                .map(pcService::createPcResponse)
                .toList();

        return new PageImpl<>(responses, pageable, userPCs.size());
    }

    public PcResponse getPcById(String userId, String pcId) {
        PC userPc = userService.getUserById(userId).getPcs().stream()
                .filter(pc -> pc.id().equals(pcId))
                .findAny()
                .orElseThrow(() -> new UserPcNotFoundException("There is no such PC!!!"));

        return pcService.createPcResponse(userPc);
    }

    @Transactional
    public void update(String userId, PcResponse pcResponse) {
        User user = userService.getUserById(userId);

        Specs specs = pcService.getSpecs(pcResponse.specsIds());

        HardwareSpec hardwareSpec = new HardwareSpec(
                pcResponse.hardwareSpec().name(),
                pcResponse.hardwareSpec().description(),
                pcService.getTotalPrice(specs),
                pcResponse.hardwareSpec().rating()
        );

        PC userPcToSave = new PC(pcResponse.id(), hardwareSpec, specs,
                pcService.calculateEnergyConsumptionOfPc(
                        new SpecsForEnergyConsumption(
                                specs.cpu(),
                                specs.gpu(),
                                specs.motherboard(),
                                specs.ram(),
                                specs.ssd(),
                                specs.hdd()
                        )
                ), pcResponse.photos());

        PC pcToDelete = user.getPcs().stream()
                .filter(pc -> pc.id().equals(pcResponse.id()))
                .findAny()
                .orElseThrow(() -> new UserPcNotFoundException("There is no such user's pc for deletion with id:" + pcResponse.id()));

        user.getPcs().remove(pcToDelete);
        userRepository.save(user);

        user.getPcs().add(userPcToSave);
        userRepository.save(user);
    }
}
