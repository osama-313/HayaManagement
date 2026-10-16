package com.haya.dto;

import com.haya.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class UserDtos {

    private UserDtos() {
    }

    private static final String ROLE_PATTERN = "(?i)ADMIN|USER";

    public record CreateRequest(
            @NotBlank @Size(max = 60) String firstName,
            @NotBlank @Size(max = 60) String lastName,
            @NotBlank @Size(min = 3, max = 40) @Pattern(regexp = "[A-Za-z0-9._-]+", message = "may only contain letters, digits, '.', '_' and '-'") String userName,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotNull @Min(16) @Max(100) Integer age,
            @Size(max = 80) String proficiency,
            @NotBlank @Pattern(regexp = ROLE_PATTERN, message = "must be ADMIN or USER") String role) {
    }

    /** Same as {@link CreateRequest}, but the password is optional and only changed when supplied. */
    public record UpdateRequest(
            @NotBlank @Size(max = 60) String firstName,
            @NotBlank @Size(max = 60) String lastName,
            @NotBlank @Size(min = 3, max = 40) @Pattern(regexp = "[A-Za-z0-9._-]+", message = "may only contain letters, digits, '.', '_' and '-'") String userName,
            @NotBlank @Email @Size(max = 120) String email,
            @Size(min = 8, max = 72) String password,
            @NotNull @Min(16) @Max(100) Integer age,
            @Size(max = 80) String proficiency,
            @NotBlank @Pattern(regexp = ROLE_PATTERN, message = "must be ADMIN or USER") String role) {
    }

    /** Public view of a user. Never contains credentials. */
    public record Response(
            Long id,
            String firstName,
            String lastName,
            String userName,
            String email,
            int age,
            String proficiency,
            String role) {

        public static Response from(User user) {
            return new Response(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName(),
                    user.getEmail(), user.getAge(), user.getProficiency(), user.getRole().getNormalizedName());
        }
    }

    /** Minimal user reference embedded in tasks and commitments. */
    public record Summary(Long id, String firstName, String lastName) {

        public static Summary from(User user) {
            return new Summary(user.getId(), user.getFirstName(), user.getLastName());
        }
    }
}
