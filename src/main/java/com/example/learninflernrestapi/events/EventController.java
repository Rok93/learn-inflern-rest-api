package com.example.learninflernrestapi.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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
import java.util.Arrays;
import java.util.List;

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

        Event newEvent = eventRepository.save(modelMapper.map(eventDto, Event.class));
        Integer eventId = newEvent.getId();
        newEvent.update();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(eventId);
        URI createdUri = selfLinkBuilder.toUri();//uri를 만들 때는 HATEOS(=헤이티오스)에서 제공하는 linkTo(), methodOn() 메소드 사용 하면 편리하게 만들 수 있다.
        List<Link> links = Arrays.asList(
                selfLinkBuilder.slash(eventId).withSelfRel(),
                selfLinkBuilder.withRel("query-events"),
                selfLinkBuilder.withRel("update-event") // update를 하는 것도, self랑 링크는 같은데, relation과 메서드만 다르다
        );

        EntityModel<Event> eventResource = EntityModel.of(newEvent, links); // new 생성자로 EntityModel 객체 생성하는 방식 deprecated되었고, of 팩토리 메서드 사용 권장!
        return ResponseEntity.created(createdUri).body(eventResource);
    }
}

// 강의 내용에서는 linkTo() 메소드 사용 시에, ControllerLinkBuilder 클래스를 임포트하라고 하는데, 이는 Deprecated 되었다.
// 주석의 설명을 보면, WebMvcLinkBuilder 를 대신 사용하라고 설명되어 있다!
