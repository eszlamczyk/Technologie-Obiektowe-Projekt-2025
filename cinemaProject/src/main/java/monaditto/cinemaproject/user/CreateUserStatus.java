package monaditto.cinemaproject.user;

import monaditto.cinemaproject.status.Status;

public enum CreateUserStatus implements Status {

    SUCCESS,
    USER_ALREADY_EXISTS,
    INVALID_EMAIL,
    INVALID_PASSWORD,
    MISSING_DATA,
    DATABASE_ERROR;

    @Override
    public String message() {
        return switch(this) {
            case SUCCESS -> "Successfully registered";
            case USER_ALREADY_EXISTS -> "User with given email already exists";
            case INVALID_EMAIL -> "Incorrect email";
            case INVALID_PASSWORD -> "Incorrect password: has to be at least 8 characters long," +
                    " use lowercase and uppercase letters, a number and a special character";
            case MISSING_DATA -> "Please fill up the data correctly";
            case DATABASE_ERROR -> "Something went wrong in our database";
        };
    }

    @Override
    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }
}
