package com.example.learninflernrestapi.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter { // 이벤트 발생 서버는 리소스 서버와 같이 있는게 맞는 반면에 인증 서버는 사실 따로 분리하는 것이 더 좋다! (꼭 분리할 필요는 없지만..)

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**")
                .permitAll()
//                .anonymous() 착각하지말자! 인증받지 않은 경우에만 접근할 수 있다는 뜻! 인증을 받는순간 바로 접근할 수 없다!! (permitAll로 설정해야한다!)
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());
        // 간략히 설명하면 get 요청에 대해서는 모든 유저를 허용해줬고, 그 외의 요청에 대해서는 인증을 필요로하게 하였음! (인증없이는 접근 못함!)
    }
}
