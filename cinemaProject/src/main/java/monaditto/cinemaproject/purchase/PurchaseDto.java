package monaditto.cinemaproject.purchase;

public record PurchaseDto(
        Long userId,
        Long screeningId,
        int boughtSeats
) {
}
