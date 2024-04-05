package de.mightypc.backend.model.shop.chatgpt;

import java.util.List;

public record ChatGptResponse(
        List<ChatGptChoice> choices
) {
    public String text() {
        return choices.get(0).message().content();
    }
}