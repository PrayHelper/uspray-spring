package com.uspray.uspray.infrastructure.querydsl.pray;

import static com.uspray.uspray.domain.QCategory.category;
import static com.uspray.uspray.domain.QPray.pray;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.domain.Pray;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PrayRepositoryImpl implements PrayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Pray> findAllWithOrderAndType(String username, String prayType) {
        return queryFactory
            .select(pray)
            .from(pray)
            .join(pray.category, category)
            .where(category.member.userId.eq(username))
            .where(pray.prayType.stringValue().likeIgnoreCase(prayType))
            .orderBy(pray.createdAt.asc())
            .orderBy(pray.category.order.asc())
            .fetch();
    }

    @Override
    // prayId로 originPrayId를 넘겨줌
    public Integer getSharedCountByOriginPrayId(Long prayId) {
        Integer result = queryFactory
            .select(pray.count.sum())
            .from(pray)
            .where(pray.originPrayId.eq(prayId))
            .groupBy(pray.id)
            .fetchOne();
        Integer result_for_owner = queryFactory
            .select(pray.count.sum())
            .from(pray)
            .where(pray.id.eq(prayId))
            .fetchOne();
        return result != null ? result + result_for_owner : result_for_owner;
    }
}

