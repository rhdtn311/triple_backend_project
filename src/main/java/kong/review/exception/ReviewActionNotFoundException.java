package kong.review.exception;

import kong.common.exception.BusinessException;
import kong.common.exception.ErrorCode;

public class ReviewActionNotFoundException extends BusinessException {

    public ReviewActionNotFoundException() {
        super(ErrorCode.REVIEW_ACTION_NOT_FOUND_EXCEPTION);
    }
}
