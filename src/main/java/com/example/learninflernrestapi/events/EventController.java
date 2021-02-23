package com.example.learninflernrestapi.events;

import com.example.learninflernrestapi.accounts.Account;
import com.example.learninflernrestapi.accounts.AccountAdapter;
import com.example.learninflernrestapi.accounts.CurrentUser;
import com.example.learninflernrestapi.common.ErrorsResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) { // create()를 보낼 때는, 항상 uri가 필요하다.
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event newEvent = eventRepository.save(modelMapper.map(eventDto, Event.class));
        Integer eventId = newEvent.getId();
        newEvent.update();
        newEvent.setManager(currentUser);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(eventId);
        URI createdUri = selfLinkBuilder.toUri();//uri를 만들 때는 HATEOS(=헤이티오스)에서 제공하는 linkTo(), methodOn() 메소드 사용 하면 편리하게 만들 수 있다.


//        List<Link> links = Arrays.asList(
//                selfLinkBuilder.slash(eventId).withSelfRel(),
//                selfLinkBuilder.withRel("query-events"),
//                selfLinkBuilder.withRel("update-event"), // update를 하는 것도, self랑 링크는 같은데, relation과 메서드만 다르다
//                selfLinkBuilder.withRel("/docs/index.html#resources-events-create").withRel("profile")
//        );
//        EntityModel<Event> eventResource = EntityModel.of(newEvent, links); // new 생성자로 EntityModel 객체 생성하는 방식 deprecated되었고, of 팩토리 메서드 사용 권장!

        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }

    //        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //        User principal = (User) authentication.getPrincipal();  이 두 주석을 애너테이션으로 바로 처리할 수 있다!!
//    @GetMapping
//    public ResponseEntity queryEvents(Pageable pageable,
//                                      PagedResourcesAssembler<Event> assembler,
//                                      @CurrentUser Account account) {
//        Page<Event> page = this.eventRepository.findAll(pageable);
//        PagedModel<EventResource> pagedResources = assembler.toModel(page, EventResource::new);
//        pagedResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
//        if (account != null) {
//            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
//        }
//        return ResponseEntity.ok().body(pagedResources);
//    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler,
                                      @AuthenticationPrincipal User user) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EventResource> pagedResources = assembler.toModel(page, EventResource::new);
        pagedResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        if (user != null) {
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok().body(pagedResources);
    }


    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));

        if (event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }

        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();
        if (!existingEvent.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        this.modelMapper.map(eventDto, existingEvent); // 원천소스(~에서)가 먼저 나오고 타겟 (~로)
        Event savedEvent = eventRepository.save(existingEvent);
        // 기존 이벤트에다가 eventDto의 데이터를 덮어 씌어주는 것이다.
        // 이 값은 Transactional에 들어있는 것이 아니기 때문에 이런 변경사항이 자동으로 dirty checking이 되서
        // Transaction이 commit 될 때, DB에 반영해주는 일이 일어나지 않는다!
        // 그런 일들은 Service 단을 만들어 주면 발생하는 것이다!

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }
}

// 강의 내용에서는 linkTo() 메소드 사용 시에, ControllerLinkBuilder 클래스를 임포트하라고 하는데, 이는 Deprecated 되었다.
// 주석의 설명을 보면, WebMvcLinkBuilder 를 대신 사용하라고 설명되어 있다!
