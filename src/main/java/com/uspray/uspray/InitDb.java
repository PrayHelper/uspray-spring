package com.uspray.uspray;

import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.Enums.CategoryType;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.GroupMember;
import com.uspray.uspray.domain.GroupPray;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.ScrapAndHeart;
import java.time.LocalDate;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public void dbInit() {
            Member member = Member.builder()
                .userId("test")
                .password(passwordEncoder.encode("test"))
                .name("홍길동")
                .phone("01012345678")
                .birth("2002-02-01")
                .gender("female")
                .authority(Authority.ROLE_USER)
                .build();
            em.persist(member);

            Member member2 = Member.builder()
                .userId("test2")
                .password(passwordEncoder.encode("test2"))
                .name("김길동")
                .phone("01012345670")
                .birth("2002-02-24")
                .gender("male")
                .authority(Authority.ROLE_USER)
                .build();
            em.persist(member2);

            Member member_mook = Member.builder()
                .userId("wjdanr0869")
                .password(passwordEncoder.encode("wjdanr0869"))
                .name("김정묵")
                .phone("01057770869")
                .birth("2001-11-07")
                .gender("male")
                .authority(Authority.ROLE_USER)
                .build();
            em.persist(member_mook);

            Member member3 = Member.builder()
                .userId("test3")
                .password(passwordEncoder.encode("test3"))
                .name("이수빈")
                .phone("01022223333")
                .birth("2001-11-07")
                .gender("female")
                .authority(Authority.ROLE_USER)
                .build();
            em.persist(member3);

            Member member4 = Member.builder()
                .userId("test4")
                .password(passwordEncoder.encode("test4"))
                .name("권은혜")
                .phone("01022223331")
                .birth("2001-11-07")
                .gender("female")
                .authority(Authority.ROLE_USER)
                .build();
            em.persist(member4);

            Member member5 = Member.builder()
                .userId("test5")
                .password(passwordEncoder.encode("test5"))
                .name("배서현")
                .phone("01022223931")
                .birth("2001-11-07")
                .gender("female")
                .authority(Authority.ROLE_USER)
                .build();
            em.persist(member5);

            Group group = Group.builder()
                .name("테스트 모임")
                .leader(member)
                .build();
            em.persist(group);

            GroupMember groupMember = GroupMember.builder()
                .group(group)
                .member(member)
                .build();
            em.persist(groupMember);

            Category category = Category.builder()
                .name("기타 카테고리")
                .color("#75BD62")
                .member(member)
                .order(1024)
                .categoryType(CategoryType.PERSONAL)
                .build();
            em.persist(category);

            Category category1_by_member = Category.builder()
                .name("가족")
                .color("#75BD62")
                .member(member)
                .order(2048)
                .categoryType(CategoryType.PERSONAL)
                .build();
            em.persist(category1_by_member);

            Category category2_by_member2 = Category.builder()
                .name("친구")
                .color("#75BD62")
                .member(member2)
                .categoryType(CategoryType.PERSONAL)
                .order(1024)
                .build();
            em.persist(category2_by_member2);

            Category category3_by_member = Category.builder()
                .name("공유 카테고리")
                .color("#75BD62")
                .member(member)
                .categoryType(CategoryType.SHARED)
                .order(1024)
                .build();
            em.persist(category3_by_member);

            Category category4_by_member2 = Category.builder()
                .name("공유 카테고리")
                .color("#75BD62")
                .member(member2)
                .categoryType(CategoryType.SHARED)
                .order(1024)
                .build();
            em.persist(category4_by_member2);

            Pray pray = Pray.builder()
                .content("테스트 기도")
                .deadline(LocalDate.parse("2025-01-01"))
                .member(member)
                .category(category)
                .prayType(PrayType.PERSONAL)
                .build();
            em.persist(pray);

            Pray pray_1 = Pray.builder()
                .content("공유 테스트 기도")
                .deadline(LocalDate.parse("2025-01-01"))
                .member(member2)
                .category(category2_by_member2)
                .prayType(PrayType.PERSONAL)
                .build();
            em.persist(pray_1);

            Pray pray1 = Pray.builder()
                .content("공유 테스트 기도")
                .deadline(LocalDate.parse("2025-02-24"))
                .member(member)
                .category(category3_by_member)
                .prayType(PrayType.SHARED)
                .originPrayId(pray_1.getId())
                .build();
            em.persist(pray1);

            Pray pray2 = Pray.builder()
                .content("테스트 기도")
                .deadline(LocalDate.parse("2024-02-24"))
                .member(member2)
                .category(category4_by_member2)
                .prayType(PrayType.SHARED)
                .originPrayId(pray.getId())
                .build();
            em.persist(pray2);

            Pray pray3 = Pray.builder()
                .content("기도할게요")
                .deadline(LocalDate.parse("2024-02-02"))
                .member(member)
                .category(category1_by_member)
                .prayType(PrayType.PERSONAL)
                .build();
            em.persist(pray3);

            History history = History.builder()
                .pray(pray)
                .build();
            em.persist(history);

            GroupPray groupPray = GroupPray.builder()
                .group(group)
                .author(member)
                .content("테스트기도하고 아니기도하고")
                .deadline(LocalDate.parse("2025-01-01"))
                .build();
            em.persist(groupPray);

            ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .build();
            em.persist(scrapAndHeart);
        }

    }
}
