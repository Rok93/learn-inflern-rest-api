package com.example.learninflernrestapi.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel<Event> { // bean이 아니다(bean 등록x) . 매번 컨버팅해서 사용해야하는 객체이다!
    public EventResource(Event event, Link... links) {
        super(event, links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel()); //self link로 추가할 때는, withSelfRel() 메서드로 추가하면, SELF가 추가된다.
    }
}
