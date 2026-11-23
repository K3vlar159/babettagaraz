package sk.babettagaraz.domain.user.dto;

import sk.babettagaraz.domain.user.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        String bio,
        LocalDateTime createdAt
) {}