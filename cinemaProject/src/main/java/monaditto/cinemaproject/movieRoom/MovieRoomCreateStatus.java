package monaditto.cinemaproject.movieRoom;

import monaditto.cinemaproject.status.Status;

public enum MovieRoomCreateStatus implements Status {
    SUCCESS,
    INCORRECT_ID,
    MOVIE_ROOM_NAME_TAKEN;

    @Override
    public String message() {
        return switch(this) {
            case SUCCESS -> "Successfully created new Category";
            case MOVIE_ROOM_NAME_TAKEN -> "Room of this name already exists";
            case INCORRECT_ID -> "There is no category with this ID";
        };
    }

    @Override
    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }
}
