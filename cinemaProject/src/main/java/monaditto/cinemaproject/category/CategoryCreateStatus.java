package monaditto.cinemaproject.category;

import monaditto.cinemaproject.status.Status;

public enum CategoryCreateStatus implements Status {

    SUCCESS,
    INCORRECT_ID,
    CATEGORY_NAME_TAKEN;

    @Override
    public String message() {
        return switch(this) {
            case SUCCESS -> "Successfully created new Category";
            case CATEGORY_NAME_TAKEN -> "Category of this name already exists";
            case INCORRECT_ID -> "There is no category with this ID";
        };
    }

    @Override
    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }

}
