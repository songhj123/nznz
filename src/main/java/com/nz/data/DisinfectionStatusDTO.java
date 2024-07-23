package com.nz.data;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisinfectionStatusDTO {
    private Long levelId;
    private Integer level;
    private String reasonTitle;
    private String reasonDetails;
    private String username;
    private Timestamp updatedAt;
}
