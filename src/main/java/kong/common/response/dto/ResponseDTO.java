package kong.common.response.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO {

    private String message;
    private Object data;

    @Builder
    public ResponseDTO(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}
