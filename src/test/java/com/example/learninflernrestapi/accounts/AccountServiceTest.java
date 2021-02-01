package com.example.learninflernrestapi.accounts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void findByUsername() {
        //given
        String password = "keesun";
        String username = "keesun@email.com";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        accountService.saveAccount(account);

        //when
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //then
        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    @Test
    void findByUsernameFail() {
        //given
        String username = "random@emial.com";
//
//        //when //then
//        assertThatThrownBy(() -> accountService.loadUserByUsername(username))
//                .isExactlyInstanceOf(UsernameNotFoundException.class);

        // 이런 방법도 있다! (강의에서 언급), 코드가 조금 장황해진다는 단점이 있지만 더 많은 테스트를 할 수 있기 때문에 나쁘지 않은 방법이다!
        try {
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        } catch (UsernameNotFoundException e) {
            assertThat(e.getMessage()).containsSequence(username); // 에러메시지가 username을 포함하고 있는지?
        }

    }
}
