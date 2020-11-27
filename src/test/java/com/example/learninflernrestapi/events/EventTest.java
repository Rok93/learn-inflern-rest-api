package com.example.learninflernrestapi.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @DisplayName("빌더 테스트 ")
    @Test
    void builder() {
        //given //when
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API development with Spring")
                .build();

        //then
        assertThat(event).isNotNull();
    }

    @DisplayName("기본 생성자 생성 테스트 ")
    @Test
    void name() {
        //given
        String name = "Event";
        String description = "Spring";

        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}
