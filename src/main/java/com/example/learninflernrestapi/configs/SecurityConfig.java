package com.example.learninflernrestapi.configs;

import com.example.learninflernrestapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // @EnableWebSecurity & WebSecurityConfigurerAdapter 적용하는 순간 더 이상, 스프링 부트가 적용해주는스프링 시큐리티 설정이 적용되지 않는다!

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    // authenticationManager를 Bean으로 노출시켜 줘야하기 때문에
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    // 스프링 시큐리티 폼 인증 설정 방법
    // 아래의 두 configure는 필터링을 하는 로직이다. 기능은 같으나 Spring Security안으로 들어갔냐 안들어갔냐의 차이가 발생한다!
    @Override
    public void configure(WebSecurity web) throws Exception { // security filter를 적용할지 말지를 결정하는 곳!(Web에서 설정한다)
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적인 리소스들 무시하려면! servlet 꺼를 사용!
    }

    // form 설정은 해도되고 안해도 되지만... 이 강의에서는 안하는 방향으로 진행!
//    @Override
//    protected void configure(HttpSecurity http) throws Exception { //HTTP로 거를 수도 있다. 일단 Spring Security 안으로 들어온 것이다!
//        http
//                .anonymous()
//                .and()
//                .formLogin()
//                .and()
//                .authorizeRequests()
//                .mvcMatchers(HttpMethod.GET, "/api/**").authenticated()
//                .anyRequest().authenticated();
//    }
}
