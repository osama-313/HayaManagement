package com.haya.dto;

import com.haya.entity.Resource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class ResourceDtos {

    private ResourceDtos() {
    }

    /** Accepts {@code example.com/path} or {@code https://example.com/path}; rejects other URI schemes such as {@code javascript:}. */
    private static final String URL_PATTERN = "(?i)(https?://)?[a-z0-9.-]+(:\\d+)?([/?#]\\S*)?";

    public record Request(
            @NotBlank @Size(max = 2048) @Pattern(regexp = URL_PATTERN, message = "must be a valid http(s) URL") String url,
            @Size(max = 500) String highlights) {
    }

    public record Response(Long id, String url, String highlights) {

        public static Response from(Resource resource) {
            return new Response(resource.getId(), resource.getUrl(), resource.getHighlights());
        }
    }
}
