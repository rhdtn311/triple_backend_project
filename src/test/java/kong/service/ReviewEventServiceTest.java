package kong.service;

import kong.attached_photo.domain.AttachedPhoto;
import kong.attached_photo.repository.AttachedPhotoRepository;
import kong.common.uuid.UuidManager;
import kong.event.dto.EventReqDTO;
import kong.event.service.ReviewEventService;
import kong.event.util.EventType;
import kong.place.repository.PlaceRepository;
import kong.point.repository.PointRepository;
import kong.review.repository.ReviewRepository;
import kong.review.util.ReviewAction;
import kong.place.domain.Place;
import kong.point.domain.ReviewPoint;
import kong.review.domain.Review;
import kong.user.domain.User;
import kong.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ReviewEventServiceTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachedPhotoRepository attachedPhotoRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PointRepository<ReviewPoint> pointRepository;

    @Autowired
    private ReviewEventService reviewEventService;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    void init() {
            }

    @AfterEach
    void after() {
        attachedPhotoRepository.deleteAll();
        reviewRepository.deleteAll();
        placeRepository.deleteAll();
    }

    @Test
    @DisplayName("content만 작성했을 경우 1포인트 저장 확인 보너스 X")
    void onlyContentPlusOnePoint() {
        // given
        User user = user();
        User tempUser = user();
        userRepository.save(user);
        userRepository.save(tempUser);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        Review tempReview = review(tempUser, place, "temp content");
        reviewRepository.save(tempReview);
        reviewRepository.save(review);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.ADD, user, place, review, List.of());

        // when
        reviewEventService.eventProcess(eventReqDTO);

        // then
        assertThat(user.getTotalPoints()).isEqualTo(11);
    }

    @Test
    @DisplayName("사진만 첨부했을 경우 1포인트 저장 확인 보너스 X")
    void onlyAttachedPhotosPlusOnePoint() {
        // given
        User user = user();
        User tempUser = user();
        userRepository.save(user);
        userRepository.save(tempUser);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, null);
        Review tempReview = review(tempUser, place, "temp content");
        reviewRepository.save(tempReview);
        reviewRepository.save(review);

        AttachedPhoto attachedPhoto = attachedPhoto(review);
        attachedPhotoRepository.save(attachedPhoto);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.ADD, user, place, review, List.of(attachedPhoto.getAttachedPhotoId()));

        // when
        reviewEventService.eventProcess(eventReqDTO);

        // then
        assertThat(user.getTotalPoints()).isEqualTo(11);
    }

    @Test
    @DisplayName("content 작성 사진 첨부 모두 했을 경우 2포인트 저장 확인 보너스 X")
    void contentAndAttachedPhotosPlusTwoPoint() {
        // given
        User user = user();
        User tempUser = user();
        userRepository.save(user);
        userRepository.save(tempUser);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        Review tempReview = review(tempUser, place, "temp content");
        reviewRepository.save(tempReview);
        reviewRepository.save(review);

        AttachedPhoto attachedPhoto = attachedPhoto(review);
        attachedPhotoRepository.save(attachedPhoto);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.ADD, user, place, review, List.of(attachedPhoto.getAttachedPhotoId()));

        // when
        reviewEventService.eventProcess(eventReqDTO);

        // then
        assertThat(user.getTotalPoints()).isEqualTo(12);
    }

    @Test
    @DisplayName("첫 리뷰일 경우 보너스 점수 추가 확인")
    void bonusPointPlusOnePoint() {
        // given
        User user = user();
        userRepository.save(user);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, null);
        reviewRepository.save(review);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.ADD, user, place, review, List.of());

        // when
        reviewEventService.eventProcess(eventReqDTO);

        // then
        assertThat(user.getTotalPoints()).isEqualTo(11);
    }

    @Test
    @DisplayName("첫 리뷰가 아닐 경우 보너스 점수 미적용 확인")
    void notBonusPoint() {
        // given
        User originalUser = user();
        User newUser = user();
        userRepository.save(newUser);
        userRepository.save(originalUser);

        Place place = place();
        placeRepository.save(place);

        Review originalReview = review(originalUser, place, "content");
        Review addReview = review(newUser, place, "content");
        reviewRepository.save(originalReview);
        reviewRepository.save(addReview);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.ADD, newUser, place, addReview, List.of());

        // when
        reviewEventService.eventProcess(eventReqDTO);

        // then
        assertThat(newUser.getTotalPoints()).isEqualTo(11);

    }

    @Test
    @DisplayName("리뷰 수정 시 포인트 증가 확인 - 글만 작성한 리뷰 -> 사진 추가")
    void reviewModifyPlusPoint() {
        // given
        User user = user();
        userRepository.save(user);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        reviewRepository.save(review);

        ReviewPoint reviewPoint = reviewPoint(user, place, 2);
        pointRepository.save(reviewPoint);

        // 사진 id 추가
        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.MOD, user, place, review, List.of(UUID.randomUUID().toString()));

        // when
        reviewEventService.eventProcess(eventReqDTO);

        // then
        assertThat(user.getTotalPoints()).isEqualTo(11);
    }

    @Test
    @DisplayName("리뷰 수정 시 포인트 감소 확인")
    void reviewModifyMinusPoint() {
        // given
        User user = user();
        userRepository.save(user);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        reviewRepository.save(review);

        ReviewPoint reviewPoint = reviewPoint(user, place, 3);
        pointRepository.save(reviewPoint);

        // 리뷰 사진 삭제
        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.MOD, user, place, review, List.of());

        // when
        reviewEventService.eventProcess(eventReqDTO);

        // then
        assertThat(user.getTotalPoints()).isEqualTo(9);
    }

    @Test
    @DisplayName("리뷰 수정 시 회원 id와 장소 id로 리뷰 포인트 변경 이력 조회 확인1 - user가 2명 이상일 경우")
    void reviewPointsByUserIdAndPlaceIdIfUserOver2() {

        // given
        User user1 = user();
        userRepository.save(user1);
        User user2 = user();
        userRepository.save(user2);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user1, place, "content");
        reviewRepository.save(review);

        ReviewPoint reviewPoint1 = reviewPoint(user1, place, 2);
        pointRepository.save(reviewPoint1);
        ReviewPoint reviewPoint2 = reviewPoint(user1, place, 1);
        pointRepository.save(reviewPoint2);
        ReviewPoint reviewPoint3 = reviewPoint(user2, place, 1);
        pointRepository.save(reviewPoint3);

        // when
        List<ReviewPoint> reviewPoints = pointRepository.findReviewPointsByUserAndPlace(user1, place);

        // then
        assertThat(reviewPoints.size()).isEqualTo(2);
        for (int i = 0; i < reviewPoints.size(); i++) {
            assertThat(reviewPoints.get(i).getUser()).isEqualTo(user1);
            if (i == 0) assertThat(reviewPoints.get(i).getValue()).isEqualTo(reviewPoint1.getValue());
            else if (i == 1) assertThat(reviewPoints.get(i).getValue()).isEqualTo(reviewPoint2.getValue());
        }
    }

    @Test
    @DisplayName("리뷰 수정 시 회원 id와 장소 id로 리뷰 포인트 변경 이력 조회 확인1 - place가 2곳 이상일 경우")
    void reviewPointsByUserIdAndPlaceIdIfPlaceOver2() {

        // given
        User user1 = user();
        userRepository.save(user1);

        Place place1 = place();
        placeRepository.save(place1);
        Place place2 = place();
        placeRepository.save(place2);

        Review review = review(user1, place1, "content");
        reviewRepository.save(review);

        ReviewPoint reviewPoint1 = reviewPoint(user1, place1, 2);
        pointRepository.save(reviewPoint1);
        ReviewPoint reviewPoint2 = reviewPoint(user1, place2, 1);
        pointRepository.save(reviewPoint2);
        ReviewPoint reviewPoint3 = reviewPoint(user1, place2, 3);
        pointRepository.save(reviewPoint3);

        // when
        List<ReviewPoint> reviewPoints = pointRepository.findReviewPointsByUserAndPlace(user1, place2);

        // then
        assertThat(reviewPoints.size()).isEqualTo(2);
        for (int i = 0; i < reviewPoints.size(); i++) {
            assertThat(reviewPoints.get(i).getUser()).isEqualTo(user1);
            if (i == 0) assertThat(reviewPoints.get(i).getValue()).isEqualTo(reviewPoint2.getValue());
            else if (i == 1) assertThat(reviewPoints.get(i).getValue()).isEqualTo(reviewPoint3.getValue());
        }
    }

    @Test
    @DisplayName("리뷰 삭제 시 포인트 회수 확인")
    void reviewDeleteMinusPoint() {

        // given
        User user = user();
        userRepository.save(user);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        reviewRepository.save(review);

        ReviewPoint reviewPoint = reviewPoint(user, place, 2);
        pointRepository.save(reviewPoint);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.DELETE, user, place, review, List.of());

        // when
        reviewEventService.eventProcess(eventReqDTO);

        // then
        assertThat(user.getTotalPoints()).isEqualTo(8);
    }

    @Test
    @DisplayName("리뷰 등록 시 포인트 이력 생성 확인")
    void createReviewPointWhenAddReview() {
        // given
        User user = user();
        userRepository.save(user);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        reviewRepository.save(review);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.ADD, user, place, review, List.of());

        // when
        reviewEventService.eventProcess(eventReqDTO);
        List<ReviewPoint> reviewPoints = pointRepository.findReviewPointsByUserAndPlace(user, place);
        ReviewPoint reviewPoint = reviewPoints.get(reviewPoints.size() - 1);

        // then
        assertThat(reviewPoint.getValue()).isEqualTo(2);
        assertThat(reviewPoint.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("리뷰 수정 시 포인트 이력 생성 확인")
    void createReviewPointWhenModifyReview() {
        // given
        User user = user();
        userRepository.save(user);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        reviewRepository.save(review);

        ReviewPoint addReviewPoint1 = reviewPoint(user, place, 2);
        ReviewPoint deleteReviewPoint = reviewPoint(user, place, -2);
        ReviewPoint addReviewPoint2 = reviewPoint(user, place, 3);
        ReviewPoint modifyReviewPoint = reviewPoint(user, place, -1);
        pointRepository.save(addReviewPoint1);
        pointRepository.save(deleteReviewPoint);
        pointRepository.save(addReviewPoint2);
        pointRepository.save(modifyReviewPoint);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.MOD, user, place, review, List.of(UUID.randomUUID().toString()));

        // when
        reviewEventService.eventProcess(eventReqDTO);
        List<ReviewPoint> reviewPoints = pointRepository.findReviewPointsByUserAndPlace(user, place);
        ReviewPoint reviewPoint = reviewPoints.get(reviewPoints.size() - 1);

        // then
        assertThat(reviewPoint.getValue()).isEqualTo(1);
        assertThat(reviewPoint.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("리뷰 수정 시 포인트 변동 없으면 이력 생성 X 확인")
    void notCreateReviewPointWhenModifyReview() {
        // given
        User user = user();
        userRepository.save(user);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        reviewRepository.save(review);

        ReviewPoint addReviewPoint = reviewPoint(user, place, 2);
        pointRepository.save(addReviewPoint);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.MOD, user, place, review, List.of());

        // when
        reviewEventService.eventProcess(eventReqDTO);
        List<ReviewPoint> reviewPoints = pointRepository.findReviewPointsByUserAndPlace(user, place);

        // then
        assertThat(reviewPoints.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("리뷰 삭제 시 이력 생성 확인")
    void createReviewPointWhenDeleteReview() {
        // given
        User user = user();
        userRepository.save(user);

        Place place = place();
        placeRepository.save(place);

        Review review = review(user, place, "content");
        reviewRepository.save(review);

        ReviewPoint addReviewPoint = reviewPoint(user, place, 2);
        pointRepository.save(addReviewPoint);

        EventReqDTO eventReqDTO = eventReqDTO(ReviewAction.DELETE, user, place, review, List.of());

        // when
        reviewEventService.eventProcess(eventReqDTO);
        List<ReviewPoint> reviewPoints = pointRepository.findReviewPointsByUserAndPlace(user, place);
        ReviewPoint reviewPoint = reviewPoints.get(reviewPoints.size() - 1);

        // then
        assertThat(reviewPoint.getValue()).isEqualTo(-2);
        assertThat(reviewPoint.getUser()).isEqualTo(user);
    }


    private ReviewPoint reviewPoint(User user, Place place, int value) {
        return ReviewPoint.builder()
                .user(user)
                .place(place)
                .value(value)
                .build();
    }

    private AttachedPhoto attachedPhoto(Review review) {
        return AttachedPhoto.builder()
                .attachedPhotoId(UuidManager.toIndexingUuid(UUID.randomUUID().toString()))
                .review(review)
                .build();
    }

    private Place place() {
        return Place.builder()
                .placeId(UuidManager.toIndexingUuid(UUID.randomUUID().toString()))
                .build();
    }

    private User user() {
        return User.builder()
                .userId(UuidManager.toIndexingUuid(UUID.randomUUID().toString()))
                .totalPoints(10)
                .build();
    }

    private Review review(User user, Place place, String content) {
        Review review = Review.builder()
                .reviewId(UuidManager.toIndexingUuid(UUID.randomUUID().toString()))
                .content(content)
                .user(user)
                .place(place)
                .build();
        return review;
    }

    private EventReqDTO eventReqDTO(ReviewAction reviewAction, User user, Place place, Review review, List<String> attachedPhotoIds) {
        EventReqDTO eventReqDTO = EventReqDTO.builder()
                .type(EventType.REVIEW)
                .action(reviewAction)
                .reviewId(UuidManager.toUuid(review.getReviewId()))
                .content(review.getContent())
                .attachedPhotoIds(attachedPhotoIds)
                .userId(user.getUserId())
                .placeId(place.getPlaceId())
                .build();
        return eventReqDTO;
    }

//    @Test
//    void test1() {
//        User user = user();
//        userRepository.save(user);
//
//        User user1 = user();
//        userRepository.save(user1);
//
//        Place place = place();
//        placeRepository.save(place);
//
//        Place place1 = place();
//        placeRepository.save(place1);
//
//        Review review = review(user, place, "content");
//        reviewRepository.save(review);
//
//        ReviewPoint addReviewPoint1 = reviewPoint(user, place, 1);
//        pointRepository.save(addReviewPoint1);
//
//        ReviewPoint addReviewPoint2 = reviewPoint(user, place, 2);
//        pointRepository.save(addReviewPoint2);
//
//        ReviewPoint addReviewPoint3 = reviewPoint(user, place1, -1);
//        pointRepository.save(addReviewPoint3);
//
//        System.out.println("----------------");
//        List<ReviewPoint> reviewPointsByUserAndPlace = pointRepository.findReviewPointsByUserAndPlace(user, place);
//        System.out.println("-------------------");
//        for (ReviewPoint reviewPoint : reviewPointsByUserAndPlace) {
//            System.out.println("user = " + reviewPoint.getUser() + "   point : " + reviewPoint.getValue());
//        }
//    }
}