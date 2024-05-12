package com.joo.digimon.report.enums;

public enum ReportStatus {
    SUBMITTED("제출됨"),
    IN_PROGRESS("처리 중"),
    RESOLVED("해결됨"),
    CLOSED("종료됨");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
