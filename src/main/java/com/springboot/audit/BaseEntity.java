package com.springboot.audit;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

// 이 클래스를 상속 받은 엔티티는 createdAt, modifiedAt 필드를 공통으로 가진다.
@MappedSuperclass
// JPA Auditing 활성화
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    // 생성 날짜 자동 저장
    @CreatedDate
    // 생성 날짜는 변경되면 안됨
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 마지막 수정 날짜 자동 저장
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
