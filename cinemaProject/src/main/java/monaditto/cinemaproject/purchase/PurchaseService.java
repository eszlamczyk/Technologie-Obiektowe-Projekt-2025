package monaditto.cinemaproject.purchase;

import monaditto.cinemaproject.screening.Screening;
import monaditto.cinemaproject.screening.ScreeningRepository;
import monaditto.cinemaproject.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@Transactional
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    public Purchase findById(Long id) {
        return purchaseRepository.findById(id).orElse(null);
    }

    public List<Purchase> findByUser(Long userId) {
        return purchaseRepository.findByUserId(userId);
    }

    public List<Purchase> findByScreening(Long screeningId) {
        return purchaseRepository.findByScreeningId(screeningId);
    }

    public List<Purchase> findByStatus(ReservationStatus status) {
        return purchaseRepository.findByReservationStatus(status);
    }

    public Purchase create(PurchaseDto purchaseDto) {
        var user = userRepository.findById(purchaseDto.userId());
        if(user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        var screening = screeningRepository.findById(purchaseDto.screeningId());
        if(screening.isEmpty()) {
            throw new IllegalArgumentException("Screening not found");
        }

        if(purchaseDto.boughtSeats() <= 0) {
            throw new IllegalArgumentException("Bought seats must be greater than 0");
        }

        validateSeatAvailability(screening.get(), purchaseDto.boughtSeats());
        validatePurchaseTimeWindow(screening.get());

        var purchase = new Purchase(user.get(), screening.get(), purchaseDto.boughtSeats(), ReservationStatus.UNPAID);
        purchaseRepository.save(purchase);
        return purchase;
    }

    public void deletePurchase(Long purchaseId) {
        var purchaseResult = purchaseRepository.findById(purchaseId);
        if(purchaseResult.isEmpty()) {
            throw new IllegalArgumentException("Purchase not found");
        }

        var purchase = purchaseResult.get();
        purchaseRepository.delete(purchase);
    }

    public void confirmPayment(Long purchaseId) {
        updateStatusIfValid(
                purchaseId,
                ReservationStatus.PAID,
                status -> status == ReservationStatus.PAID,
                "Purchase is already paid"
        );
    }

    public void cancelPurchase(Long purchaseId) {
        updateStatusIfValid(
                purchaseId,
                ReservationStatus.CANCELLED,
                status -> status == ReservationStatus.CANCELLED,
                "Purchase is already cancelled"
        );
    }

    private void updateStatusIfValid(
            Long purchaseId,
            ReservationStatus newStatus,
            Predicate<ReservationStatus> invalidCondition,
            String errorMessage
    ) {
        var purchase = findById(purchaseId);
        if (purchase == null) {
            throw new IllegalArgumentException("Purchase not found");
        }
        if (invalidCondition.test(purchase.getReservationStatus())) {
            throw new IllegalStateException(errorMessage);
        }
        purchase.setReservationStatus(newStatus);
        purchaseRepository.save(purchase);
    }

    private void validateSeatAvailability(Screening screening, int requestedSeats) {
        int availableSeats = screening.getRoom().getMaxSeats() - purchaseRepository
                .findByScreeningId(screening.getScreeningId())
                .stream()
                .mapToInt(Purchase::getBoughtSeats)
                .sum();

        if (requestedSeats > availableSeats) {
            throw new IllegalStateException("Not enough seats available");
        }
    }

    private void validatePurchaseTimeWindow(Screening screening) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime screeningTime = screening.getStart();

        if (now.isAfter(screeningTime)) {
            throw new IllegalStateException("Cannot purchase tickets for past screenings");
        }
    }
}
