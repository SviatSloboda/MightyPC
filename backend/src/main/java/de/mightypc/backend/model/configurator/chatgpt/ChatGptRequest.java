package de.mightypc.backend.model.configurator.chatgpt;

import java.util.Collections;
import java.util.List;

public record ChatGptRequest(
        String model,
        List<ChatGptMessage> messages
) {
    public ChatGptRequest(String message) {
        this("gpt-3.5-turbo", Collections.singletonList(new ChatGptMessage("user", message)));
    }
}
