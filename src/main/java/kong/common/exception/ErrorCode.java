package kong.common.exception;

public enum ErrorCode {

    USER_NOT_FOUND_EXCEPTION(1L, "존재하지 않는 사용자입니다.", 404),
    PLACE_NOT_FOUND_EXCEPTION(2L, "존재하지 않는 장소입니다.", 404),
    REVIEW_NOT_FOUND_EXCEPTION(3L, "존재하지 않는 리뷰입니다.", 404),
    REVIEW_ACTION_NOT_FOUND_EXCEPTION(4L, "존재하지 않는 리뷰 동작입니다.", 404);

    private final Long code;
    private final String message;
    private final int status;

    ErrorCode(final Long code, final String message, final int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public Long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
