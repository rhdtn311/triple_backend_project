package kong.user.exception;

import kong.common.exception.BusinessException;
import kong.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND_EXCEPTION);
    }
}
