package com.dailymission.api.springboot.web.repository.post;

import com.dailymission.api.springboot.web.dto.post.PostHistoryDto;
import com.dailymission.api.springboot.web.repository.common.StringToDateTime;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.dailymission.api.springboot.web.repository.post.QPost.post;


@RequiredArgsConstructor
public class PostRepositoryCustomImpl  implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findAllDescAndDeletedIsFalse() {

        return queryFactory
                .select(post)
                .from(post)
                .where(post.deleted.isFalse())
                .orderBy(post.id.desc()).fetch();
    }

    @Override
    public List<PostHistoryDto> findSchedule(Long id, String startDate, String endDate) {
        LocalDateTime start =  StringToDateTime.builder().date(startDate).build().get();
        LocalDateTime end =  StringToDateTime.builder().date(endDate).build().get();

        return queryFactory
                .select(Projections.constructor(PostHistoryDto.class, post.createdDate.as("date"), post.user.id.as("userId"), post.user.name.as("userName")) )
                .from(post)
                .where(post.mission.id.in(id).and(post.createdDate.after(start).and(post.createdDate.before(end))))
                .fetch();

    }
}
