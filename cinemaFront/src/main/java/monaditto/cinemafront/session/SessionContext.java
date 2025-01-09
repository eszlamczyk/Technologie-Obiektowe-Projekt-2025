package monaditto.cinemafront.session;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SessionContext {
    private Long userId;

    private String jwtToken;

    public String getJwtToken(){
        return this.jwtToken;
    }

    public void setJwtToken(String jwtToken){
        this.jwtToken = jwtToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

