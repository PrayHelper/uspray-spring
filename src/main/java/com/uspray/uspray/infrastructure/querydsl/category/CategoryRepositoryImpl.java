package com.uspray.uspray.infrastructure.querydsl.category;

import static com.uspray.uspray.domain.QCategory.category;
import static com.uspray.uspray.domain.QMember.member;
import static com.uspray.uspray.domain.QPray.pray;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.response.PrayResponseDto;
import com.uspray.uspray.DTO.pray.response.QPrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType) {
        // 카테고리 목록 가져오기
        List<Category> categories = queryFactory
            .selectFrom(category)
            .where(category.member.userId.eq(username))
            .where(category.categoryType.stringValue().likeIgnoreCase(prayType))
            .orderBy(category.order.asc())
            .fetch();

        if (categories.isEmpty()) {
            return new ArrayList<>();
        }

        // 각 카테고리 별로 PrayResponseDto 목록 가져오기
        List<PrayListResponseDto> prayListResponseDtos = new ArrayList<>();
        for (Category cat : categories) {
            List<Pray> prays = queryFactory
                .selectFrom(pray)
                .where(pray.category.id.eq(cat.getId())
                    .and(pray.member.userId.eq(username))
                    .and(pray.prayType.stringValue().likeIgnoreCase(prayType)))
                .orderBy(pray.createdAt.asc())
                .fetch();

            List<PrayResponseDto> prayResponseDtos = prays.stream()
                .map(pray_iter -> {
                    if (pray_iter.getPrayType().equals(PrayType.SHARED)) {
                        Member originMember = queryFactory
                            .selectFrom(member)
                            .where(member.id.eq(pray_iter.getOriginMemberId()))
                            .fetchOne();
                        return PrayResponseDto.shared(pray_iter, originMember);
                    } else {
                        return PrayResponseDto.of(pray_iter);
                    }
                })
                .collect(Collectors.toList());

            prayListResponseDtos.add(
                new PrayListResponseDto(cat.getId(), cat.getName(), cat.getColor(),
                    prayResponseDtos));
        }
        return prayListResponseDtos;
    }

    @Override
    public List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType,
        List<Long> prayIds) {
        List<Category> categories = queryFactory
            .selectFrom(category)
            .where(category.member.userId.eq(username))
            .where(category.categoryType.stringValue().likeIgnoreCase(prayType))
            .orderBy(category.order.asc())
            .fetch();

        // 각 카테고리 별로 PrayResponseDto 목록 가져오기
        List<PrayListResponseDto> prayListResponseDtos = new ArrayList<>();
        for (Category cat : categories) {
            List<PrayResponseDto> prayResponseDtos = queryFactory
                .select(new QPrayResponseDto(
                    pray.id,
                    pray.content,
                    pray.member.name,
                    pray.deadline,
                    pray.category.id,
                    pray.category.name,
                    pray.lastPrayedAt,
                    pray.isShared
                ))
                .from(pray)
                .where(pray.category.id.eq(cat.getId())
                    .and(pray.member.userId.eq(username))
                    .and(pray.prayType.stringValue().likeIgnoreCase(prayType)))
                .fetch();
            prayResponseDtos.removeIf(p -> !prayIds.contains(p.getPrayId()));
            prayResponseDtos.forEach(p -> p.setInGroup(true));

            prayListResponseDtos.add(
                new PrayListResponseDto(cat.getId(), cat.getName(), cat.getColor(),
                    prayResponseDtos));
        }
        return prayListResponseDtos;
    }
}
