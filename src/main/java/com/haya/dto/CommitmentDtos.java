package com.haya.dto;

import com.haya.entity.Commitment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class CommitmentDtos {

    private CommitmentDtos() {
    }

    private static final String TIME_PATTERN = "([01]\\d|2[0-3]):[0-5]\\d";

    public record Request(
            @NotBlank @Pattern(regexp = TIME_PATTERN, message = "must be a time formatted as HH:mm") String fromWhen,
            @NotBlank @Pattern(regexp = TIME_PATTERN, message = "must be a time formatted as HH:mm") String toWhen,
            @Min(1) @Max(24) int hours,
            @Size(max = 500) String note) {
    }

    public record Response(
            Long id,
            String fromWhen,
            String toWhen,
            int hours,
            String note,
            UserDtos.Summary user) {

        public static Response from(Commitment commitment) {
            return new Response(commitment.getId(), commitment.getFromWhen(), commitment.getToWhen(),
                    commitment.getHours(), commitment.getNote(), UserDtos.Summary.from(commitment.getUser()));
        }
    }
}
