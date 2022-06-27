package kong.event.service;

import kong.attached_photo.repository.AttachedPhotoRepository;
import kong.common.uuid.UuidManager;
import kong.event.dto.EventReqDTO;
import kong.place.domain.Place;
import kong.point.repository.PointRepository;
import kong.point.util.ReviewPointCalculator;
import kong.review.exception.ReviewActionNotFoundException;
import kong.review.exception.ReviewNotFoundException;
import kong.review.repository.ReviewRepository;
import kong.review.domain.Review;
import kong.point.domain.ReviewPoint;
import kong.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewEventService {

    private final ReviewRepository reviewRepository;
    private final PointRepository<ReviewPoint> pointRepository;

    private final ReviewPointCalculator reviewPointCalculator;

    @Transactional
    public String eventProcess(EventReqDTO eventReqDTO) {

        switch (eventReqDTO.getAction()) {
            case ADD : return addReviewEvent(eventReqDTO);
            case MOD : return modifyReviewEvent(eventReqDTO);
            case DELETE : return deleteReviewEvent(eventReqDTO);
        }

        throw new ReviewActionNotFoundException();
    }

    // 리뷰 등록 시 포인트 증가
    private String addReviewEvent(EventReqDTO eventReqDTO) {

        // Review 테이블에서 Review 불러오기
        Review review = reviewRepository.findReviewByReviewId(UuidManager.toIndexingUuid(eventReqDTO.getReviewId()))
                .orElseThrow(ReviewNotFoundException::new);
        Place place = review.getPlace();
        User user = review.getUser();

        // 회원의 포인트 변경 (update)
        boolean isBonus = isBonus(place, review.getId());
        int userGetPoint = reviewPointCalculator.calculateAddActionPoint(eventReqDTO, isBonus);
        user.changePoint(userGetPoint);

        // 이력 남기기 (insert)
        pointRepository.save(eventReqDTO.createReviewPoint(userGetPoint, user, place));

        return user.getUserId();
    }

    // 리뷰 수정
    private String modifyReviewEvent(EventReqDTO eventReqDTO) {

        Review review = reviewRepository.findReviewByReviewId(UuidManager.toIndexingUuid(eventReqDTO.getReviewId()))
                .orElseThrow(ReviewNotFoundException::new);
        Place place = review.getPlace();
        User user = review.getUser();

        boolean isBonus = isBonus(place, review.getId());
        int point = reviewPointCalculator.calculateModifyActionPoint(eventReqDTO, isBonus, user, place);

        if (point != 0) {
            user.changePoint(point);
            pointRepository.save(eventReqDTO.createReviewPoint(point, user, place));
        }

        return user.getUserId();
    }

    // 리뷰 삭제
    private String deleteReviewEvent(EventReqDTO eventReqDTO) {

        Review review = reviewRepository.findReviewByReviewId(UuidManager.toIndexingUuid(eventReqDTO.getReviewId()))
                .orElseThrow(ReviewNotFoundException::new);
        Place place = review.getPlace();
        User user = review.getUser();

        // 회원의 포인트 차감
        boolean isBonus = isBonus(place, review.getId());
        int minusPoint = reviewPointCalculator.calculateDeleteActionPoint(eventReqDTO, isBonus);
        user.changePoint(minusPoint);

        // 리뷰 포인트 변경 이력
        pointRepository.save(eventReqDTO.createReviewPoint(minusPoint, user, place));

        return user.getUserId();
    }

    // 리뷰 등록 시 보너스 점수를 얻는지 확인
    private boolean isBonus(Place place, Long review_id) {
        return reviewRepository.findFirstByPlaceOrderByRegDateAsc(place)
                .map(review -> review.getId().equals(review_id))
                .orElse(true);
    }
}
