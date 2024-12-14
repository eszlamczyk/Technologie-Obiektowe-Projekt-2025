package monaditto.cinemafront.databaseMapping;



public class Purchase {

    private Long id;

    private User user;

    private Screening screening;


    private int boughtSeats;

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
