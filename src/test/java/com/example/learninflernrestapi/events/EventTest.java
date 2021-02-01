package com.example.learninflernrestapi.events;

import org.hibernate.annotations.Parameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest { // 사실 이 테스트는 롬복의 학습용 테스트이지, 굳이 필요한 테스트가 아니다!!

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
    void javaBean() {
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

    @ParameterizedTest
//    @CsvSource(value = {
//            "0, 0, true",
//            "100, 0, false",
//            "0, 100, false"
//    },delimiter = ',')
    @MethodSource
    void testFree(int basePrice, int maxPrice, boolean isFree) {
        //given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    // MethodSource로 쓰일 method name이 test 메서드명과 동일하면
    // (value = "com.example.learninflernrestapi.events.EventTest#paramsForTestFree") 와 같이 설정해줄 필요 없이 사용할 수 있다
    static Object[] testFree() {
        return new Object[]{
                new Object[]{0, 0, true},
                new Object[]{100, 0, false},
                new Object[]{0, 100, false},
                new Object[]{100, 200, false}
        };
    }

    @Test
    void testNotFree() {
        //given
        Event event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
    }

    @ParameterizedTest
    @MethodSource
    void testOffline(String location, boolean isOffline) {
        //given
        Event event = Event.builder()
                .location(location)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    static Object[] testOffline() {
        return new Object[]{
                new Object[]{"강남", true},
                new Object[]{null, false},
                new Object[]{"  ", false}
        };
    }

    @Test
    void testOnline() {
        //given
        Event event = Event.builder()
                .build();

        //when
        event.update();

        //then
        assertThat(event.isOffline()).isFalse();
    }
}
