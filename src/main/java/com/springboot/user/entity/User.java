package com.springboot.user.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.like.entity.Like;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String userNickname;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Question> questions = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private UserStatus userStatus;

    public enum UserStatus {
        ACTIVE_USER("활성화 상태"),
        DEACTIVATED_USER("탈퇴 상태"),
        DORMANT_USER("휴면 상태");

        private String message;

        UserStatus(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}
