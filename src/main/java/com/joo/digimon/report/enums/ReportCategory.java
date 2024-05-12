package com.joo.digimon.report.enums;

public enum ReportCategory {
    BUG("오류 제보"),
    IMPROVEMENT("개선 제안"),
    NEW_FEATURE("새로운 기능 제안"),;

    private final String description;

    ReportCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
