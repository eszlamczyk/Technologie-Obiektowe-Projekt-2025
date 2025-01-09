package monaditto.cinemaproject.purchase;

import java.time.LocalDateTime;

public record PurchaseResponseDto(
        Long id,
        Long userId,
        Long screeningId,
        String userName,
        String movieTitle,
        LocalDateTime screeningTime,
        int boughtSeats,
        ReservationStatus status
) {
    public static PurchaseResponseDto fromEntity(Purchase purchase) {
        return new PurchaseResponseDto(
                purchase.getId(),
                purchase.getUser().getId(),
                purchase.getScreening().getScreeningId(),
                purchase.getUser().getFirstName() + " " + purchase.getUser().getLastName(),
                purchase.getScreening().getMovie().getTitle(),
                purchase.getScreening().getStart(),
                purchase.getBoughtSeats(),
                purchase.getReservationStatus()
        );
    }
}
