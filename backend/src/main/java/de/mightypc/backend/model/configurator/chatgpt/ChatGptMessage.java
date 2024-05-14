package de.mightypc.backend.model.configurator.chatgpt;

public record ChatGptMessage(
        String role,
        String content
) {
}
