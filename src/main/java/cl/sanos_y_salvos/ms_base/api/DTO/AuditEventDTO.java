package cl.sanos_y_salvos.ms_base.api.DTO;

import java.time.Instant;

public record AuditEventDTO(
        String eventId,
        String eventType,   // Ej: "USER_CREATED", "USER_UPDATED", "USER_DELETED"
        Instant eventTimestamp,
        Long userId,
        String fullName,
        String email,
        String city,
        String country,
        String role
) {}