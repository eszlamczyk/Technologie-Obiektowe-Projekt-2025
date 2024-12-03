package monaditto.cinemaproject.purchase;

import jakarta.persistence.*;
import monaditto.cinemaproject.screening.Screening;
import monaditto.cinemaproject.user.User;

@Entity
@Table(name = Purchase.TABLE_NAME)
public class Purchase {

    public static final String TABLE_NAME = "purchases";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Column(name = "bought_seats", nullable = false)
    private int boughtSeats;

    @Column(name = "reservation_status", nullable = false)
    private String reservationStatus;

    public Purchase() {}

    public Purchase(User user, Screening screening, int boughtSeats, String reservationStatus) {
        this.user = user;
        this.screening = screening;
        this.boughtSeats = boughtSeats;
        this.reservationStatus = reservationStatus;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setScreening(Screening screening) {
        this.screening = screening;
    }

    public Screening getScreening() {
        return screening;
    }

    public void setBoughtSeats(int boughtSeats) {
        this.boughtSeats = boughtSeats;
    }

    public int getBoughtSeats() {
        return boughtSeats;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public Long getId() {
        return id;
    }
}
