package kong.event.controller;

import kong.event.dto.EventReqDTO;
import kong.common.response.dto.ResponseDTO;
import kong.event.service.ReviewEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class EventController {

    private final ReviewEventService reviewEventService;

    @PostMapping("/events")
    public ResponseEntity<ResponseDTO> events(@RequestBody EventReqDTO eventReqDTO) {

        return ResponseEntity.ok(ResponseDTO.builder()
                .data(reviewEventService.eventProcess(eventReqDTO))
                .message("회원 포인트 처리 완료")
                .build());
    }
}
