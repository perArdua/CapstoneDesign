package com.example.campusin.infra.badge;

import com.example.campusin.domain.badge.Badge;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BadgeRepository")
class BadgeRepositoryTest extends DataJpaTestSupport {

    @Autowired
    BadgeRepository badgeRepository;

    @Test
    @DisplayName("뱃지를 저장하고 조회한다")
    void saveAndFind() {
        // given
        User user = TestEntityFactory.persistUser(em, "badge-user");
        Badge badge = badgeRepository.save(Badge.builder().name("helper").user(user).build());

        // when // then
        assertThat(badgeRepository.findById(badge.getId()))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getName()).isEqualTo("helper");
                    assertThat(found.getUser().getId()).isEqualTo(user.getId());
                });
    }

    @Test
    @DisplayName("사용자별 뱃지를 페이지로 조회한다")
    void findAllByUserId() {
        // given
        User owner = TestEntityFactory.persistUser(em, "badge-owner");
        User other = TestEntityFactory.persistUser(em, "badge-other");
        badgeRepository.save(Badge.builder().name("one").user(owner).build());
        badgeRepository.save(Badge.builder().name("two").user(owner).build());
        badgeRepository.save(Badge.builder().name("alien").user(other).build());

        // when
        var page = badgeRepository.findAllByUserId(owner.getId(), PageRequest.of(0, 10));

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).allMatch(badge -> badge.getUser().getId().equals(owner.getId()));
    }
}
