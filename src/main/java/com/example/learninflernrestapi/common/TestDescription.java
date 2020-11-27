package com.example.learninflernrestapi.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface TestDescription { // JUnit4의 경우 JUnit5의 @DisplayName 과 같은 애노테이션이 존재하지 않는다.

    String value();
}
