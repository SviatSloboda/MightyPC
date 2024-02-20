package de.mightypc.backend.model.shop;

public record UserResponse(
        String id,
        String email
) {
    public UserResponse(User user) {
        this(user.id(), user.email());
    }
}