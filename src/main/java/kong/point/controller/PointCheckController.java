package kong.point.controller;

import kong.common.response.dto.ResponseDTO;
import kong.point.service.PointCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PointCheckController {

    private final PointCheckService pointCheckService;

    @GetMapping("/points/{userId}")
    public ResponseEntity<ResponseDTO> points(@PathVariable String userId) {

        return ResponseEntity.ok(ResponseDTO.builder()
                .data(pointCheckService.getTotalPoints(userId))
                .message("회원의 총 포인트 조회 완료")
                .build());
    }
}
