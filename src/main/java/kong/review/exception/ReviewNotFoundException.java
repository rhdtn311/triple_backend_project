package kong.review.exception;

import kong.common.exception.BusinessException;
import kong.common.exception.ErrorCode;

public class ReviewNotFoundException extends BusinessException {
    public ReviewNotFoundException() {
        super(ErrorCode.REVIEW_NOT_FOUND_EXCEPTION);
    }
}
