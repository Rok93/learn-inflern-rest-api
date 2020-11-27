package com.example.learninflernrestapi.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
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
            return ResponseEntity.badRequest().build();
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();//uri를 만들 때는 HATEOS(=헤이티오스)에서 제공하는 linkTo(), methodOn() 메소드 사용 하면 편리하게 만들 수 있다.
        return ResponseEntity.created(createdUri).body(event);
    }
}

// 강의 내용에서는 linkTo() 메소드 사용 시에, ControllerLinkBuilder 클래스를 임포트하라고 하는데, 이는 Deprecated 되었다.
// 주석의 설명을 보면, WebMvcLinkBuilder 를 대신 사용하라고 설명되어 있다!
