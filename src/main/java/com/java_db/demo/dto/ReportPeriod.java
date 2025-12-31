package com.java_db.demo.dto;

/**
 * 报表时间周期枚举
 */
public enum ReportPeriod {
    DAY("日"),
    WEEK("周"),
    MONTH("月"),
    YEAR("年");

    private final String displayName;

    ReportPeriod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
