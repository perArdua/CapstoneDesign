package com.example.campusin.domain.post.dto.request;

import com.example.campusin.domain.post.ReportType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {
    private ReportType type;
}
