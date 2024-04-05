package de.mightypc.backend.service.pc;

import de.mightypc.backend.exception.pc.HardwareNotFoundException;
import de.mightypc.backend.model.pc.PC;
import de.mightypc.backend.model.pc.createpc.CreatePC;
import de.mightypc.backend.model.pc.createpc.PcResponse;
import de.mightypc.backend.model.shop.user.User;
import de.mightypc.backend.repository.shop.UserRepository;
import de.mightypc.backend.service.pc.PcService;
import de.mightypc.backend.service.shop.UserService;
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

    private PC getPcOfUserById(String userId, String pcId) {
        User user = userService.getUserById(userId);

        return user.getPcs().stream()
                .filter(pc -> pc.id().equals(pcId))
                .findAny()
                .orElseThrow(() -> new HardwareNotFoundException("There is no such pc!"));
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
                .orElseThrow(() -> new HardwareNotFoundException("There is no such PC!!!"));

        return pcService.createPcResponse(userPc);
    }
}
