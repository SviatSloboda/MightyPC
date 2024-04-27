package de.mightypc.backend.controller.configurator;

import de.mightypc.backend.model.pc.createpc.CreatePC;
import de.mightypc.backend.model.pc.createpc.PcResponse;
import de.mightypc.backend.service.configurator.UserPcsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user-pcs/{userId}")
public class UserPcsController {
    private final UserPcsService userPcsService;

    public UserPcsController(UserPcsService userPcsService) {
        this.userPcsService = userPcsService;
    }

    @GetMapping("/page")
    public Page<PcResponse> getAllByPage(@PathVariable String userId, Pageable pageable) {
        return userPcsService.getAllByPage(userId, pageable);
    }

    @GetMapping("/{pcId}")
    public PcResponse getPcById(@PathVariable String userId, @PathVariable String pcId) {
        return userPcsService.getPcById(userId, pcId);
    }

    @DeleteMapping("/{pcId}")
    public void deletePcById(@PathVariable String userId, @PathVariable String pcId) {
        userPcsService.deletePc(userId, pcId);
    }

    @PutMapping
    public void update(@PathVariable String userId, @RequestBody PcResponse pcResponse) {
        userPcsService.update(userId, pcResponse);
    }

    @GetMapping("/{pcId}/promote")
    public void addPrivateUserPcToAllPcs(@PathVariable String userId, @PathVariable String pcId) {
        userPcsService.addPrivateUserPcToAllPcs(userId, pcId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUserPc(@PathVariable String userId, @RequestBody CreatePC createPC) {
        userPcsService.savePc(userId, createPC);
    }

    @DeleteMapping("/all")
    public void clearBasket(@PathVariable String userId) {
        userPcsService.deleteAll(userId);
    }
}
