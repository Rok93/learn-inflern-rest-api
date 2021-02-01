package com.example.learninflernrestapi.configs;

import com.example.learninflernrestapi.accounts.Account;
import com.example.learninflernrestapi.accounts.AccountRole;
import com.example.learninflernrestapi.accounts.AccountService;
import com.example.learninflernrestapi.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthServerConfigTest extends BaseControllerTest { // 일종의 ControllerTest이기 때문 ControllerTest를 상속받아야 한다

    @Autowired
    AccountService accountService;

    @DisplayName("인증 토큰을 발급 받는 테스트")
    @Test
    void getAuthToken() throws Exception {
        //given
        String username = "keesun@email.com";
        String password = "keesun";
        Account keesun = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(keesun);

        String clientId = "myApp";
        String clientSecret = "pass";

        //when //then
        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
        ;
    }
}
