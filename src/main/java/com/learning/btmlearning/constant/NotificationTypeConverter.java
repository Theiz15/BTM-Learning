package com.learning.btmlearning.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class NotificationTypeConverter implements AttributeConverter<NotificationType, String> {

    @Override
    public String convertToDatabaseColumn(NotificationType attribute) {
        if (attribute == null) {
            return null;
        }

        return switch (attribute) {
            case CERTIFICATE_ISSUED -> "CERTIFICATE";
            case REVIEW_RECEIVED -> "REVIEW";
            case SYSTEM -> "SYSTEM";
        };
    }

    @Override
    public NotificationType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return switch (dbData) {
            case "CERTIFICATE", "CERTIFICATE_ISSUED" -> NotificationType.CERTIFICATE_ISSUED;
            case "REVIEW", "REVIEW_RECEIVED" -> NotificationType.REVIEW_RECEIVED;
            case "SYSTEM" -> NotificationType.SYSTEM;
            default -> throw new IllegalArgumentException("Unsupported notification type: " + dbData);
        };
    }
}
