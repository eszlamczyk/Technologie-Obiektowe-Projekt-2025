package monaditto.cinemafront.session;

import org.springframework.stereotype.Component;



@Component
public class SessionContext {
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

