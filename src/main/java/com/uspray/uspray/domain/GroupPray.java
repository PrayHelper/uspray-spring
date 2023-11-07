package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPray extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grouppray_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @Builder
    public GroupPray(String content, Group group, Member author) {
        this.content = content;
        setGroup(group);
        setAuthor(author);
    }

    private void setGroup(Group group) {
        this.group = group;
        group.getGroupPrayList().add(this);
    }

    private void setAuthor(Member author) {
        this.author = author;
        author.getGroupPrayList().add(this);
    }

    public void changeContent(String content) {
        this.content = content;
    }
}