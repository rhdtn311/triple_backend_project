package kong.point.util;

import kong.event.dto.EventReqDTO;
import kong.place.domain.Place;
import kong.point.domain.ReviewPoint;
import kong.point.repository.PointRepository;
import kong.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReviewPointCalculator {

    private final PointRepository<ReviewPoint> pointRepository;

    // 리뷰 등록 시 몇 포인트를 얻는지 계산
    public int calculateAddActionPoint(EventReqDTO eventReqDTO, boolean isBonus) {

        int point = 0;
        if (eventReqDTO.hasContent()) {
            point++;
        }

        if (eventReqDTO.hasAttachedPhoto()) {
            point++;
        }

        if (isBonus) {
            point++;
        }

        return point;
    }

    // 리뷰 삭제 시 몇 포인트를 감소해야 하는지 계산
    public int calculateDeleteActionPoint(EventReqDTO eventReqDTO, boolean isBonus) {

        int point = 0;
        if (eventReqDTO.hasContent()) {
            point++;
        }

        if (eventReqDTO.hasAttachedPhoto()) {
            point++;
        }

        if (isBonus) {
            point++;
        }

        return -point;
    }

    // 리뷰 수정 시 몇 포인트를 얻는지 계산
    public int calculateModifyActionPoint(EventReqDTO eventReqDTO, boolean isBonus, User user, Place place) {

        int point = 0;
        if (eventReqDTO.hasContent()) {
            point++;
        }

        if (eventReqDTO.hasAttachedPhoto()) {
            point++;
        }

        if (isBonus) {
            point++;
        }

        return point - sumPointValues(user, place);
    }

    // 리뷰 포인트에 대한 이력을 불러와서 모든 포인트 변동 사항을 더한다.
    private int sumPointValues(User user, Place place) {
        return pointRepository.findReviewPointsByUserAndPlace(user, place).stream()
                .mapToInt(ReviewPoint::getValue)
                .sum();
    }
}
