package monaditto.cinemaproject.jobTitle;

import jakarta.persistence.*;
import monaditto.cinemaproject.user.User;

import java.util.Set;

@Entity
@Table(name = JobTitle.TABLE_NAME)
public class JobTitle {

    public static final String TABLE_NAME = "job_titles";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "title_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "jobTitles")
    private Set<User> users;

    public JobTitle() {}

    public JobTitle(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        users.add(user);
    }
}
