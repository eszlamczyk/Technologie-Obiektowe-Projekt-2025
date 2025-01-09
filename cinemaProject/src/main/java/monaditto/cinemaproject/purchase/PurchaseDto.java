package monaditto.cinemaproject.purchase;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PurchaseDto(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Screening ID is required")
        Long screeningId,

        @Min(value = 1, message = "Must buy at least one seat")
        int boughtSeats
) {}
