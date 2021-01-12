package com.example.learninflernrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            errors.reject("wrongPrices", "Values of prices are wrong");
        }

        LocalDateTime eventDateTime = eventDto.getEndEventDateTime();
        if (eventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                eventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                eventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong. ");
        }

        // todo: beginEventDateTime
        // todo: CloseEnrollmentDateTime
    }
}
