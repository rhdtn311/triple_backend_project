package kong.point.service;

import kong.user.domain.User;
import kong.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class PointCheckServiceTest {

    @Autowired
    PointCheckService pointCheckService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원 포인트 조회 확인")
    void userTotalPoint() {

        // given
        User user = user();
        userRepository.save(user);

        // when
        int totalPoints = pointCheckService.getTotalPoints(user.getUserId());

        // then
        assertThat(totalPoints).isEqualTo(user.getTotalPoints());
    }

    private User user() {
        return User.builder()
                .userId(UUID.randomUUID().toString())
                .totalPoints(10)
                .build();
    }

}