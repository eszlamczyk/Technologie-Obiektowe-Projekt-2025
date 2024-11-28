package monaditto.cinemaproject.user;

import jakarta.persistence.*;
import monaditto.cinemaproject.jobTitle.JobTitle;
import monaditto.cinemaproject.opinion.Opinion;
import monaditto.cinemaproject.purchase.Purchase;

import java.util.Set;

@Entity
@Table(name = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME = "users";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_category",
            joinColumns = @JoinColumn(name = "\"user\""),
            inverseJoinColumns = @JoinColumn(name = "title")
    )
    private Set<JobTitle> jobTitles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Purchase> purchases;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Opinion> opinions;

    public User() {}

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName =lastName;
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }
}
