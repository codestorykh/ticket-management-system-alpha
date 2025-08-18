package com.codestorykh.event.controller;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.event.dto.EventRequest;
import com.codestorykh.event.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping()
    public ResponseEntity<ResponseErrorTemplate> create(@RequestBody EventRequest eventRequest) {
        log.info("Intercept create new event with req: {}", eventRequest);
        return ResponseEntity.ok(eventService.create(eventRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> update(@PathVariable Long id, @RequestBody EventRequest eventRequest) {
        log.info("Intercept update event with id: {} and req: {}", id, eventRequest);
        return ResponseEntity.ok(eventService.update(id, eventRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> getById(@PathVariable Long id) {
        log.info("Intercept get event by id: {}", id);
        return ResponseEntity.ok(eventService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseErrorTemplate> delete(@PathVariable Long id) {
        log.info("Intercept delete event with id: {}", id);
        eventService.delete(id);
        return ResponseEntity.ok(new ResponseErrorTemplate(
                "Event deleted successfully",
                "EVENT_DELETED",
                null,
                false));
    }

}
