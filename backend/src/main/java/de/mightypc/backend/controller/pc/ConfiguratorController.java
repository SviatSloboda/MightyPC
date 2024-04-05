package de.mightypc.backend.controller.pc;

import de.mightypc.backend.model.pc.ConfiguratorComponents;
import de.mightypc.backend.model.pc.specs.SpecsIds;
import de.mightypc.backend.service.pc.configurator.ChatGptService;
import de.mightypc.backend.service.pc.configurator.ConfiguratorService;
import de.mightypc.backend.service.pc.hardware.MotherboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/configurator")
public class ConfiguratorController {
    private final ConfiguratorService configuratorService;
    private final MotherboardService motherboardService;
    private final ChatGptService chatGptService;

    public ConfiguratorController(ConfiguratorService configuratorService, ChatGptService chatGptService, MotherboardService motherboardService) {
        this.configuratorService = configuratorService;
        this.chatGptService = chatGptService;
        this.motherboardService = motherboardService;
    }

    @GetMapping
    public ConfiguratorComponents getAllConfiguratorComponents() {
        return configuratorService.getAllComponentsIdsAndNamesWithPrices();
    }

    @GetMapping("/motherboard/socket/{cpuSocket}")
    public Map<String, String> getAllMotherboardIdsAndNamesWithPrices(@PathVariable String cpuSocket) {
        return motherboardService.getMotherboardsBySocket(cpuSocket);
    }

    @PostMapping("/gpt")
    public SpecsIds createPcWithChatGpt(@RequestBody String[] userPreferences) {
        return chatGptService.createChatGptRecommendation(userPreferences[0], userPreferences[1]);
    }
}
