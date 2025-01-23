package monaditto.cinemaproject.RESTcontrollers;

import jakarta.annotation.security.RolesAllowed;
import monaditto.cinemaproject.purchase.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<PurchaseResponseDto>> getAllPurchases() {
        List<PurchaseResponseDto> purchases = purchaseService.findAll()
                .stream()
                .map(PurchaseResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(purchases);
    }

    @RolesAllowed({"ADMIN","CASHIER"})
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponseDto> getPurchaseById(@PathVariable Long id) {
        Purchase purchase = purchaseService.findById(id);
        return purchase != null ? ResponseEntity.ok(PurchaseResponseDto.fromEntity(purchase))
                : ResponseEntity.notFound().build();
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @PostMapping
    public ResponseEntity<PurchaseResponseDto> createPurchase(@RequestBody PurchaseDto purchaseDto) {
        try {
            Purchase purchase = purchaseService.create(purchaseDto);
            return ResponseEntity.ok(PurchaseResponseDto.fromEntity(purchase));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        }
    }

    @RolesAllowed({"ADMIN","CASHIER"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        try {
            purchaseService.deletePurchase(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseResponseDto>> getPurchasesByUser(@PathVariable Long userId) {
        List<PurchaseResponseDto> purchases = purchaseService.findByUser(userId)
                .stream()
                .map(PurchaseResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(purchases);
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/screening/{screeningId}")
    public ResponseEntity<List<PurchaseResponseDto>> getPurchasesByScreening(@PathVariable Long screeningId) {
        List<PurchaseResponseDto> purchases = purchaseService.findByScreening(screeningId)
                .stream()
                .map(PurchaseResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(purchases);
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseResponseDto>> getPurchasesByStatus(
            @PathVariable ReservationStatus status) {
        List<PurchaseResponseDto> purchases = purchaseService.findByStatus(status)
                .stream()
                .map(PurchaseResponseDto::fromEntity)
                .toList();
        return ResponseEntity.ok(purchases);
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @PostMapping("/{id}/confirm")
    public ResponseEntity<PurchaseResponseDto> confirmPurchase(@PathVariable Long id) {
        try {
            purchaseService.confirmPayment(id);
            Purchase purchase = purchaseService.findById(id);
            return ResponseEntity.ok(PurchaseResponseDto.fromEntity(purchase));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        }
    }

    @RolesAllowed({"ADMIN","CASHIER", "USER"})
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseResponseDto> cancelPurchase(@PathVariable Long id) {
        try {
            purchaseService.cancelPurchase(id);
            Purchase purchase = purchaseService.findById(id);
            return ResponseEntity.ok(PurchaseResponseDto.fromEntity(purchase));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
