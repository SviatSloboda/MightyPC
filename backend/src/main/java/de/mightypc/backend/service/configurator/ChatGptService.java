package de.mightypc.backend.service.configurator;

import de.mightypc.backend.model.hardware.SpecsIds;
import de.mightypc.backend.model.configurator.chatgpt.ChatGptRequest;
import de.mightypc.backend.model.configurator.chatgpt.ChatGptResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

@Service
public class ChatGptService {
    private RestClient restClient;
    private final ConfiguratorService configuratorService;

    public ChatGptService(@Value("${app.chatgpt.api.url}") String url,
                          @Value("${app.chatgpt.api.key}") String apiKey,
                          @Value("${app.chatgpt.api.org}") String org,
                          ConfiguratorService configuratorService) {

        this.configuratorService = configuratorService;
        setRestClient(url, apiKey, org);
    }

    public void setRestClient(String url, String apiKey, String org) {
        restClient = RestClient.builder()
                .baseUrl(url)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("OpenAI-Organization", org)
                .build();
    }

    private String[] getIdsFromChatGpt(String computerType, String price) {
        String componentsList = configuratorService.getAllComponentsIdsAndNamesWithPricesForChatGpt();

        String prompt = """
                Role: Professional PC Configurator.
                Task: Configure the best PC within this price category: %s.
                Computer type: %s.

                Instructions:
                1. Use only the provided list of PC components.
                2. Ensure exactly 8 components are used: 1 CPU, 1 GPU, 1 Motherboard, 1 RAM, 1 SSD, 1 HDD, 1 Power Supply, and 1 PC Case.
                3. Return only the component IDs separated by commas, without spaces or additional characters.
                4. The order of IDs should follow: CPU, GPU, Motherboard, RAM, SSD, HDD, Power Supply, PC Case.
                5. Ensure all IDs exist in the provided list.
                6. Verify compatibility:
                   - The motherboard must match the CPU socket type.
                   - The power supply must support the energy consumption of all selected components.
                7. Use the format:
                   "$componentType:\\n{componentId:componentName:(componentPrice)}\\n".
                8. Do not add any comments or explanations.
                9. If an error is detected, retry until the output meets the requirements.

                Provided components:
                %s

                Example of valid response format:
                066f2ab9-314b-4428-8f5a-9a44e14c5b2c,d4e5c0e6-a8c6-4f95-9c58-1b13a8ba7a6b,42fa1b8e-4cbd-4e86-852b-2771ef267865,75394d25-2ae2-479b-b32b-7bc442208d1f,720c9f40-4446-4899-8036-5930c63d3310,39932c24-d430-4265-856a-2a3bedf212ad,299a1d37-8c70-46aa-88b8-9df81eb0de39,4a90e0fe-f8fe-4858-9632-08c72232aab6
                """.formatted(price, computerType, componentsList);

        ChatGptResponse response = restClient.post()
                .uri("/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ChatGptRequest(prompt))
                .retrieve()
                .body(ChatGptResponse.class);

        if (response == null || response.text() == null || response.text().isEmpty())
            throw new IllegalStateException("String from chatGpt can't be empty of null");

        System.out.println(response.text());
        String[] res = response.text().trim().replace(" ", "").split(",");

        if (res.length != 8)
            throw new IllegalStateException("Wrong mapping or answer from chatGpt:\n" + Arrays.toString(res));

        return res;
    }


    public SpecsIds createChatGptRecommendation(String computerType, String price) {
        String[] componentIds = getIdsFromChatGpt(computerType, price);

        return new SpecsIds(
                componentIds[0],
                componentIds[1],
                componentIds[2],
                componentIds[3],
                componentIds[4],
                componentIds[5],
                componentIds[6],
                componentIds[7]
        );
    }
}

