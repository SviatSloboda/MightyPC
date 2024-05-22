package de.mightypc.backend.controller.configurator;

import de.mightypc.backend.model.configurator.ConfiguratorItems;
import de.mightypc.backend.model.hardware.SpecsIds;
import de.mightypc.backend.service.configurator.ChatGptService;
import de.mightypc.backend.service.configurator.ConfiguratorService;
import de.mightypc.backend.service.hardware.MotherboardService;
import de.mightypc.backend.service.hardware.PowerSupplyService;
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
    private final PowerSupplyService powerSupplyService;
    private final ChatGptService chatGptService;

    public ConfiguratorController(ConfiguratorService configuratorService, ChatGptService chatGptService, MotherboardService motherboardService, PowerSupplyService powerSupplyService) {
        this.configuratorService = configuratorService;
        this.chatGptService = chatGptService;
        this.motherboardService = motherboardService;
        this.powerSupplyService = powerSupplyService;
    }

    @GetMapping
    public String getAllConfiguratorComponents() {
        return configuratorService.getAllComponentsIdsAndNamesWithPrices();
    }

    @GetMapping("/items")
    public ConfiguratorItems getAllConfiguratorItems() {
        return configuratorService.getAllItems();
    }

    @GetMapping("/motherboard/socket/{cpuSocket}")
    public Map<String, String> getAllMotherboardIdsAndNamesWithPrices(@PathVariable String cpuSocket) {
        return motherboardService.getMotherboardsBySocket(cpuSocket);
    }

    @PostMapping("/gpt")
    public SpecsIds createPcWithChatGpt(@RequestBody String[] userPreferences) {
        return chatGptService.createChatGptRecommendation(userPreferences[0], userPreferences[1]);
    }

    @PostMapping("/power-supply/energyConsumption/{energyConsumption}")
    public Map<String, String> getAllPowerSuppliesByEnergyConsumption(@PathVariable int energyConsumption) {
        return powerSupplyService.getAllPowerSuppliesByEnergyConsumption(energyConsumption);
    }
}
