package de.mightypc.backend.model.configurator.chatgpt;

import lombok.With;

import java.util.List;

@With
public record ChatGptResponse(
        List<ChatGptChoice> choices
) {
    public String text() {
        return choices.getFirst().message().content();
    }
}
