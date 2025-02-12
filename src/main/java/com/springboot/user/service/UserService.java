package com.springboot.user.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.question.entity.Question;
import com.springboot.user.entity.User;
import com.springboot.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User createUser(User user) {
        // 중복된 회원인지 이메일로 검증
        isUserAlreadyRegistered(user);
        // 중복된 닉네임인지 검증
        isNicknameAlreadyUsed(user);

        return userRepository.save(user);
    }

    @PatchMapping
    public User updateUser(User user) {
        // userId 로 존재하는 회원인지 검증
        User findUser = validateExistingUser(user.getUserId().intValue());
        validateUserStatus(findUser);

        findUser.setUserNickname(
                Optional.ofNullable(user.getUserNickname())
                        .orElse(findUser.getUserName()));

        findUser.setUserStatus(
                Optional.ofNullable(user.getUserStatus())
                        .orElse(findUser.getUserStatus()));

        findUser.setPassword(
                Optional.ofNullable(user.getPassword())
                        .orElse(findUser.getPassword()));

        findUser.setPhone(
                Optional.ofNullable(user.getPhone())
                        .orElse(findUser.getPhone()));

        return userRepository.save(findUser);
    }

    @GetMapping
    public User findUser(int userId) {
        // 유저 존재 확인
        User findUser = validateExistingUser(userId);

        return findUser;
    }

    @GetMapping
    public Page<User> findUsers(int page, int size) {
        Page<User> users = userRepository.findAll(PageRequest.of(page, size, Sort.by("userId").ascending()));

        return users;
    }

    @DeleteMapping
    public void deleteUser(int userId) {
        User user = validateExistingUser(userId);
        user.setUserStatus(User.UserStatus.DEACTIVATED_USER);
        user.getQuestions().stream()
                .forEach(question -> question.setQuestionStatus(Question.QuestionStatus.QUESTION_DELETED));

        userRepository.save(user);
    }

    // 이미 가입된 회원인지 중복 가입 예외처리
    public void isUserAlreadyRegistered(User user) {
        Optional<User> findUser = userRepository.findByEmail(user.getEmail());
        if(findUser.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EXISTING_USER);
        }
    }

    // 이미 사용중인 닉네임인지 닉네임 예외처리
    public void isNicknameAlreadyUsed(User user) {
        Optional<User> findUser = userRepository.findByUserNickname(user.getUserNickname());
        if(findUser.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_ALREADY_USED);
        }
    }

    // 가입된 회원인지 검증
    public User validateExistingUser(int userId) {
        Optional<User> findUser = userRepository.findByUserId(userId);
        return findUser.orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    // 회원 상태 검증
    public void validateUserStatus(User user) {
        if (user.getUserStatus() == User.UserStatus.DORMANT_USER) {
            throw new BusinessLogicException(ExceptionCode.USER_DORMANT);
        } else if(user.getUserStatus() == User.UserStatus.DEACTIVATED_USER) {
            throw new BusinessLogicException(ExceptionCode.USER_DEACTIVATED);
        }
    }

}
