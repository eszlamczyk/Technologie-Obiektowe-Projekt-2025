package monaditto.cinemaproject.user;

public enum CreateUserStatus {
    SUCCESS,
    USER_ALREADY_EXISTS,
    INVALID_EMAIL,
    INVALID_PASSWORD,
    DATABASE_ERROR;

    public String message() {
        return switch(this) {
            case SUCCESS -> "Successfully registered";
            case USER_ALREADY_EXISTS -> "User with given email already exists";
            case INVALID_EMAIL -> "Incorrect email";
            case INVALID_PASSWORD -> "Incorrect password: use lowercase and uppercase letters, a number and a special character";
            case DATABASE_ERROR -> "Something went wrong in our database";
        };
    }
}
