package com.springboot.member.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Question> questions = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private MemberStatus memberStatus;


    public enum MemberStatus {
        ACTIVE_MEMBER("활성화 상태"),
        DEACTIVATED_MEMBER("탈퇴 상태"),
        DORMANT_MEMBER("휴면 상태");

        private String message;

        MemberStatus(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}
