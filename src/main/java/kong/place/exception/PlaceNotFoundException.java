package kong.place.exception;

import kong.common.exception.BusinessException;
import kong.common.exception.ErrorCode;
import kong.place.domain.Place;

public class PlaceNotFoundException extends BusinessException {
    public PlaceNotFoundException() {
        super(ErrorCode.PLACE_NOT_FOUND_EXCEPTION);
    }
}
