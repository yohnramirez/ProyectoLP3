package com.backend.project.util.enums;

import lombok.Getter;

@Getter
public enum ReportStatus {
    PENDIENTE("pendiente"),
    EN_REVISION("en_revision"),
    RESUELTO("resuelto"),
    DESCARTADO("descartado");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }

    public static ReportStatus fromString(String value) {
        for (ReportStatus status : ReportStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
