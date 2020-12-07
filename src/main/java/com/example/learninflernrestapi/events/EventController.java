package com.example.learninflernrestapi.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) { // create()를 보낼 때는, 항상 uri가 필요하다.
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();//uri를 만들 때는 HATEOS(=헤이티오스)에서 제공하는 linkTo(), methodOn() 메소드 사용 하면 편리하게 만들 수 있다.
        EventResource eventResource = new EventResource(event); // todo: 생성자로 생성하는 방식은 deprecated되어 있는데, 이를 어떻게 개선할 수 있을까?
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event")); // update를 하는 것도, self랑 링크는 같은데, relation과 메서드만 다르다
        return ResponseEntity.created(createdUri).body(eventResource);
    }
}

// 강의 내용에서는 linkTo() 메소드 사용 시에, ControllerLinkBuilder 클래스를 임포트하라고 하는데, 이는 Deprecated 되었다.
// 주석의 설명을 보면, WebMvcLinkBuilder 를 대신 사용하라고 설명되어 있다!
