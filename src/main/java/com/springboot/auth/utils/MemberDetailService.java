package com.springboot.auth.utils;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class MemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils authorityUtils;


    public MemberDetailService(MemberRepository memberRepository, CustomAuthorityUtils authorityUtils) {
        this.memberRepository = memberRepository;
        this.authorityUtils = authorityUtils;
    }



    @Override
    // 유저 정보 생성
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // member 레포에서 email 로 찾아서 optionalMember 에 할당
        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        // 값이 제대로 있는지 확인
        Member findMember = optionalMember.orElseThrow(() ->
                // null 이라면 예외 터뜨리고
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        // 값이 있다면 MemberDetails 생성
        return new MemberDetails(findMember);
    }

    private final class MemberDetails extends Member implements UserDetails {
        MemberDetails(Member member) {
            setMemberId(member.getMemberId());
            setEmail(member.getEmail());
            setPassword(member.getPassword());
            setRoles(member.getRoles());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.createAuthorities(this.getRoles());
        }

        @Override
        // 이메일을 username 으로 변경
        public String getUsername() {
            return getEmail();
        }

        @Override
        // 계정 만료되었는지 확인
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        // 계정이 잠겨있지 않은지 확인
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        // 비밀번호와 같은 민감한 정보가 만료되었는지 확인
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        // 사용자 계정이 활성화 상태인지 확인
        public boolean isEnabled() {
            return true;
        }
    }
}
