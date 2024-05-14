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
    private final RestClient restClient;
    private final ConfiguratorService configuratorService;

    public ChatGptService(@Value("${app.chatgpt.api.url}") String url,
                          @Value("${app.chatgpt.api.key}") String apiKey,
                          @Value("${app.chatgpt.api.org}") String org,
                          ConfiguratorService configuratorService
    ) {

        this.configuratorService = configuratorService;

        restClient = RestClient.builder()
                .baseUrl(url)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("OpenAI-Organization", org)
                .build();
    }

    private String[] getIdsFromChatGpt(String computerType, String price) {
        ChatGptResponse response = restClient.post()
                .uri("/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ChatGptRequest(
                        ("Role: Professional PC Configurator.\n" +
                         "Task: Configure the best PC in this price category:" + price +
                         "\nComputer type: " + computerType +
                         "\nFor configuration you can use only these pc components:" +
                         configuratorService.getAllComponentsIdsAndNamesWithPrices() + " " +
                         "\nAs answer give me only ids of components, each separated by comma. Without whitespaces at all!" +
                         "\nIn your configuration you must use all 8 components!!! Do not jump over any!" +
                         "\nOrder of ids must be the same, as in object, that i have gave you higher" +
                         "\nDO NOT ADD ANY COMMENTS OR EXPLANATIONS AT ALL!!!!!!!!!!" +
                         "\nAnalyze really profoundly, which configuration would match for most for provided values" +
                         "\n Be careful by choosing Motherboards, they must match socket of cpu." +
                         "\n Be careful by choosing PowerSupplies, they must match energy consumption of all entities"
                        )
                ))
                .retrieve()
                .body(ChatGptResponse.class);

        if (response == null || response.text() == null || response.text().isEmpty())
            throw new IllegalStateException("String from chatGpt can't be empty of null");

        String[] res = response.text().trim().replace(",", "").split(" ");

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
