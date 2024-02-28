package de.mightypc.backend.model.shop;

public record UserResponse(
        String id,
        String email,
        String role,
        String dateOfAccountCreation,
        String photo
) {
    public UserResponse(User user) {
        this(user.getId(), user.getEmail(), user.getRole(), user.getDateOfAccountCreation(), user.getUserPhoto());
    }
}