package de.mightypc.backend.model.shop.chatgpt;

public record ChatGptMessage(
        String role,
        String content
) {
}
