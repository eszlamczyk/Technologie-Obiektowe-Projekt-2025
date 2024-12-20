package monaditto.cinemaproject.movie;

import monaditto.cinemaproject.status.Status;

public enum CreateMovieStatus implements Status {

    SUCCESS,
    INVALID_URL,
    MOVIE_DOESNT_EXIST,
    CATEGORY_DOESNT_EXIST,
    MISSING_DATA,
    DATABASE_ERROR;

    @Override
    public String message() {
        return switch(this) {
            case SUCCESS -> "Successfully created the movie";
            case INVALID_URL -> "Given image url is invalid";
            case MOVIE_DOESNT_EXIST -> "No such movie";
            case CATEGORY_DOESNT_EXIST -> "No such category";
            case MISSING_DATA -> "Please fill up the data correctly";
            case DATABASE_ERROR -> "Something went wrong in our database";
        };
    }

    @Override
    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }
}
