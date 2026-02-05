package com.securetransfer.SFTG.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShareLinkRequest {

    @NotBlank(message = "Stored filename must not be empty")
    @Size(min = 36, max = 255, message = "Invalid stored filename length")
    @Pattern(
            regexp = "^[a-fA-F0-9\\-]{36}_.+$",
            message = "Invalid stored filename format"
    )
    private String storedFilename;

}
