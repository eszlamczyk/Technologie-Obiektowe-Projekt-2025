package monaditto.cinemafront.databaseMapping;

public record PurchaseDto(
        Long id,

        Long userId,

        Long screeningId,


        int boughtSeats,

        ReservationStatus reservationStatus
) {}
