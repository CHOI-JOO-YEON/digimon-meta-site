package com.joo.digimon.card.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadWebpResponseDto {
    Integer uploadCount;
    Integer pendingUploadCount;
    Long uploadDurationSeconds;
}
