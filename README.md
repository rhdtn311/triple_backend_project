# triple_backend_project

# 사용 기술
- Java 11
- Spring MVC 5.3.20
- Spring Boot 2.7.0
- Spring Data JPA 2.7.0
- Lombok
- Gradle
- Junit5
- Mysql

&nbsp;

# 폴더 구조
```bash
├─main
│  ├─java
│  │  └─kong
│  │      ├─attached_photo
│  │      │  ├─domain
│  │      │  └─repository
│  │      ├─common
│  │      │  ├─exception
│  │      │  ├─response
│  │      │  │  └─dto
│  │      │  └─uuid
│  │      ├─event
│  │      │  ├─controller
│  │      │  ├─dto
│  │      │  ├─service
│  │      │  └─util
│  │      ├─place
│  │      │  ├─domain
│  │      │  ├─exception
│  │      │  └─repository
│  │      ├─point
│  │      │  ├─controller
│  │      │  ├─domain
│  │      │  ├─repository
│  │      │  ├─service
│  │      │  └─util
│  │      ├─review
│  │      │  ├─domain
│  │      │  ├─exception
│  │      │  ├─repository
│  │      │  └─util
│  │      └─user
│  │          ├─domain
│  │          ├─exception
│  │          └─repository
│  └─resources
│      ├─static
│      └─templates
└─test
    └─java
        └─kong
            ├─point
            │  └─service
            ├─service
            └─triple_api
```
- 도메인 별로 폴더를 분류했습니다. 
- 하위 폴더
    - domain : 엔티티 클래스
    - service : 서비스 클래스
    - controller : 컨트롤러 클래스
    - repository : repository 클래스
    - dto : dto 클래스
    - util : enum 타입, 특정 기능 수행을 위한 클래스
    - exception : 비즈니스 예외 클래스
- test 
    - point.service : 회원 포인트 조회 API에 대한 테스트 코드
    - kong.service : 리뷰 포인트 적립 API에 대한 테스트 코드
    - 테스트 코드는 Junit5를 이용한 통합테스트 코드를 작성하였습니다.

&nbsp;
# ERD
![erd](https://user-images.githubusercontent.com/68289543/175872564-c7ed8377-d5a7-4dfc-9dd1-12619b8be66c.png)

- user
  - 회원 테이블로 review와 1:N 관계를 갖습니다.
  - id는 auto_increment 값을 갖습니다. uuid는 user_id라는 이름의 컬럼으로 저장되며 "-"를 제외한 varchar 형식으로 저장됩니다. 이때 생성된 uuid를 하이픈을 기준으로 앞에서부터 1-2-3-4-5라는 조각으로 나뉜다고 가졍했을 때 32145로 붙인 값을 저장합니다. (해당 컬럼으로 인덱스를 생성할 경우 성능읖 높이기 위함입니다.)
  - total_points는 회원의 총 포인트를 저장합니다.
  - reg_date는 컬럼의 저장 시간을, mod_date는 컬럼의 수정 시간을 datetime으로 저장합니다.
  - 인덱스는 기본키인 id를 통해 생성된 클러스터형 인덱스와 uuid id인 user_id 컬럼으로 생성한 보조 인덱스를 갖습니다.
- place
    - 장소 테이블로 review와 1:N 관계를 갖습니다.
    - id는 auto_increment 값을 갖습니다. uuid는 place_id라는 이름의 컬럼으로 저장되며 "-"를 제외한 varchar 형식으로 저장됩니다. user_id와 마찬가지로 생성된 uuid를 하이픈을 기준으로 나눠서 다시 붙인 값을 저장합니다.
    - 인덱스는 기본키인 id를 통해 생성된 클러스터형 인덱스를 가집니다.
- review
    - 리뷰 테이블로 user와 N:1, place와 N:1, attached_photo와 1:N, review_point와 1:N 관계를 갖습니다.
    - reg_date, mod_date는 각각 컬럼이 생성된 시간, 수정된 시간을 저장합니다.
    - uuid값인 review_id는 user, place의 uuid 값을 저장하는 것과 마찬가지로 저장합니다. uuid값을 하이픈을 기준으로 나누고 순서룰 바꾼 후 이어붙인 값을 저장하여 인덱스 성능을 높입니다.
    - 인덱스는 기본키인 id로 생성한 클러스터형 인덱스, review_id 컬럼으로 생성된 보조 인덱스를 가집니다. 
- attached_photo 
    - 리뷰 사진 테이블 review와 1:N 관계를 갖습니다.
    - id는 auto_increment 값을 가집니다. 마찬가지로 attached_photo_id 값은 uuid입니다. 
- point
    - 포인트 이력 테이블로 user와 N:1, review_point와 1 : 0,1 관계를 갖습니다
    - type은 포인트가 어떤 방식으로 생성됐는지에 대한 값입니다. 리뷰를 통해 포인트 이력이 생성되었다면 REVIEW라는 값을 저장합니다. 
    - 포인트 테이블은 회원의 포인트의 변동이 있을 때만 값이 저장됩니다.
- review_point
  - 포인트 이력 중 리뷰와 관련된 이력을 저장하는 테이블로 point와 N:1 관계를 갖습니다. 
  - point의 자식 테이블이라고 할 수 있습니다. point의 id와 review_point의 id로 조인하며 리뷰와 관련된 이력을 저장하는 테이블이기 때문에 review_id 값을 가집니다. 해당 컬럼에는 place 테이블의 id(auto increment) 값이 저장되어 있습니다.
  - 즉, point 테이블과 review_point 테이블에서 포인트 변경 이력을 관리합니다. 리뷰 포인트 변경 이력에는 이력 ID, 회원 ID, 생성 날짜, 수정 날짜, 포인트 변경 타입(REVIEW, etc ...), 포인트 변경 값(value), 장소 Id(palce_id) 컬럼이 존재합니다.
  - 인덱스로는 기본키인 id로 생성한 클러스터형 인덱스와 place_id 컬럼으로 생성된 보조 인덱스를 가집니다.
  
&nbsp;

# DDL
#### 테이블 생성 DDL
```sql
create table user (  
id int auto_increment primary key,  user_id varchar(36) not null,     
total_points int default 0,     
reg_date datetime(6),     
mod_date datetime(6));

create table place (  
id int auto_increment primary key,  
place_id varchar(36) not null);

create table review (  
id int auto_increment primary key,  
review_id varchar(36) not null,     
content mediumtext,     
user_id int not null,   
place_id int not null,  
reg_date datetime(6),     
mod_date datetime(6),     
foreign key(user_id) references user(id),     
foreign key(place_id) references place(id));

create table attached_photo (
id int auto_increment primary key,  
attached_photo_id varchar(36) not null,     
review_id int not null,    
foreign key(review_id) references review(id));

create table point (  
id int auto_increment primary key,    
value int default 0,     
user_id int not null,  
reg_date datetime(6),     
mod_date datetime(6),     
type varchar(10) not null,     
foreign key(user_id) references user(id));

create table review_point (  
id int primary key,
place_id int not null,
foreign key(place_id) references place(id));
```

&nbsp;
### 인덱스 생성 DDL
```SQL
alter table `triple`.`review`
add unique index `review_id_UUID` (`review_id` ASC);

alter table `triple`.`review`
add index `place_id` (`place_id` ASC);

alter table `triple`.`user` 
add unique index `user_id_UUID` (`user_id` ASC);

alter table `triple`.`review_point`
add index `place_id` (`place_id` ASC);
```

&nbsp;
# 실행 방법
## ReviewEventService
1. kong.event.controller.ReviewEventController에서 리뷰 작성 시 포인트 적립 API 요청을 받습니다.
    - events 메소드가 실행됩니다.
2. kong.event.service.EventService의 eventProcess(EventReqDTO eventReqDTO) 메소드를 실행합니다.
    - EventReqDTO로 사용자의 요청이 매핑됩니다.
3. EventReqDTO의 action을 확인하여 ADD면 addReviewEvent 메소드를, MOD면 modifyReviewEvent 메소드를, DELETE면 deleteReviewEvent 메소드를 실행합니다.
&nbsp;
### addReviewEvent
```java
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
```
리뷰 작성 시 실행되는 메소드입니다. 리뷰를 통해 얻을 수 있는 총 포인트를 계산하고 user 테이블의 totalPoints의 값을 증가시키고 포인트 이력을 저장합니다.
1. 요청으로 들어온 UUID의 reviewId를 UuidManager 클래스를 통해 값을 변경하여 DB에 저장된 review를 user와 place를 조인하여 함께 불러옵니다.
    - reviewId의 uuid 형태의 id 값은 uuid를 "-"으로 나누고 1-2-3-4-5 형태로 uuid 값이 나뉘어 있을 때 이 값을 32145로 붙여 DB에 저장되어 있다고 가정합니다. 이는 인덱스 성능을 높이기 위함입니다.
    - 위와 같은 이유로 입력으로 들어온 reviewId 값을 UuidManager를 통해 32145 형태로 만들고 DB에서 해당 값을 조회합니다. 이 때 db의 Review 테이블의 reviewId 컬럼은 인덱스가 생성되어 있습니다.
2. 해당 리뷰로 얻을 수 있는 포인트 값을 검사하기 위해 isBonus 메소드를 실행합니다. 현재 리뷰를 작성한 Place의 id를 외래키로 가지는 review들을 생성 날짜(reg_date)로 정렬하여 가장 앞에 있는 하나의 review만 가져오고 그 review의 id와 현재 사용자의 요청으로 넘어온 review의 id 값을 비교하여 같으면 보너스 점수를 얻고 아니면 얻지 못합니다.
3. ReviewPointCalculator의 calculateAddActionPoint 메소드를 실행하여 요청으로 넘어온 리뷰로 얻을 수 있는 총 값을 얻어냅니다.
    - 요청의 content가 있으면 +1, attachedPhotoIds가 있으면 +1, 보너스 검사 값이 true이면 +1
4. User의 totalPoints 값에 계산한 포인트 값을 더하고 저장합니다.
5. PointRepository에 해당 회원이 리뷰를 통해 얻은 포인트 값을 저장하여 이력을 관리합니다.  

&nbsp;
### modifyReviewEvent
```java
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
```
리뷰 수정 시 실행되는 메소드입니다. 리뷰를 통해 얻을 수 있는 총 포인트를 계산하고 포인트가 0점이 아니라면 user 테이블의 totalPoints 값을 수정하고 포인트 이력을 저장합니다.
1. 요청으로 들어온 UUID의 reviewId로 reivew, user, place를 조인하여 함께 조회합니다.
    - addReviewEvent와 동일한 방식입니다.
2. 마찬가지로 보너스 점수를 계산합니다.
3. ReviewCalculator의 calculateModifyActionPoint 메소드를 통해 리뷰 수정 시 변동된 포인트 값을 계산합니다.
    - point, review_point를 조인하여 현재 검사하는 회원(User)과 장소(Place)에대한 모든 포인트 변경 사항 정보를 조회하여 값을 모두 더해줍니다. 
    - 더해준 모든 값들의 합은 해당 회원이 해당 장소에대한 리뷰를 작성, 수정, 삭제함으로써 얻은 총 포인트입니다. 
    - 현재 입력으로 들어온 리뷰를 통해 얻을 수 있는 포인트에서 그 총 포인트를 뺀 값이 사용자가 현재 리뷰를 수정함으로써 얻거나 잃을 수 있는 포인트입니다.
4. 계산한 포인트 값이 0이 아니라면 리뷰 수정을 통해 회원의 포인트가 변동되었음을 의미하기 때문에 User의 totalPoints 값을 수정하고 리뷰 포인트 변경 이력을 저장합니다.  

&nbsp;
### deleteReviewEvent
```java
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
```
리뷰 삭제 시 실행되는 메소드입니다. 요청된 리뷰를 삭제함으로써 잃는 포인트를 계산하고 회원의 총 포인트 값을 수정하고 포인트 이력을 저장합니다.
1. 요청으로 들어온 UUID의 reviewId로 reivew, user, place를 조인하여 함께 조회합니다.
    - addReviewEvent와 동일한 방식입니다.
2. 보너스 점수를 계산합니다.
3. ReviewCalculator의 calculateDeleteActionPoint 메소드를 통해 삭제하려는 리뷰가 가지는 총 포인트를 계산합니다.
4. 계산한 포인트만큼 사용자의 totalPoints 값을 감소시킵니다.
5. 리뷰 포인트 변경 이력을 저장합니다.

&nbsp;
### isBonus
```java
    // 리뷰 등록 시 보너스 점수를 얻는지 확인
    private boolean isBonus(Place place, Long review_id) {
        return reviewRepository.findFirstByPlaceOrderByRegDateAsc(place)
                .map(review -> review.getId().equals(review_id))
                .orElse(true);
    }
```
요청으로 들어온 리뷰가 보너스 점수를 얻을 수 있는지 확인하는 메소드입니다. 요청으로 들어온 Place의 모든 리뷰를 날짜순으로 정렬하여 가장 먼저 저장된 리뷰를 찾아 요청으로 들어온 review_id와 비교합니다. 같으면 보너스 점수를 얻고 다르면 얻지 못합니다.

&nbsp;

### calculateAddActionPoint
```java
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
```
리뷰 등록 시 요청된 리뷰로 얻을 수 있는 총 포인트를 계산하는 메소드입니다.

&nbsp;

### calculateDeleteActionPoint
```java
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
```
리뷰 삭제 시 요청된 리뷰로 잃어야 하는 총 포인트를 계산하는 메소드입니다.

&nbsp;

### calculateModifyActionPoint
```java
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
```
리뷰 수정 시 요청된 리뷰를 통해 얻을 수 있는 포인트와 이미 회원이 이 전에 해당 장소에대한 리뷰를 작성, 수정, 삭제함으로써 얻은 총 포인트의 차이를 통해 결과적으로 회원이 요청된 장소에 리뷰를 수정함으로써 얻거나 잃는 총 포인트를 구하는 메소드입니다.

&nbsp;
### sumPointsValues
```java
    // 리뷰 포인트에 대한 이력을 불러와서 모든 포인트 변동 사항을 더한다.
    private int sumPointValues(User user, Place place) {
        return pointRepository.findReviewPointsByUserAndPlace(user, place).stream()
                .mapToInt(ReviewPoint::getValue)
                .sum();
    }
```
이 전에 회원이 해당 장소에 대한 리뷰로 얻은 모든 포인트의 합을 구합니다.

&nbsp;
## PointCheckService  
```java
    @Transactional(readOnly = true)
    public int getTotalPoints(String userId) {

        User user = userRepository.findUserByUserId(userId).orElseThrow(UserNotFoundException::new);

        return user.getTotalPoints();
    }
```
1. kong.point.controller.PointCheckController에서 포인트 조회 API 요청을 받습니다.
    - points 메소드가 실행됩니다.
2. kong.point.service.PointCheckService의 getTotalPoints(String userId) 메소드를 실행합니다.
3. 요청으로 들어온 회원(User)의 id 값으로 회원을 조회하여 totalPoints 값을 반환합니다.
   - UUID로 요청이 들어온다고 가정했을 때 User 테이블의 user_id(UUID) 값은 인덱스가 생성되어 있습니다. (마찬가지로 UUID의 순서를 변경하여 인덱싱 성능을 높이도록 하였습니다.)


