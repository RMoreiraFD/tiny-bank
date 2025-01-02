package com.tiny.bank.api.controller;

import com.tiny.bank.api.model.request.UserCreationRequest;
import com.tiny.bank.domain.usecase.user.UserCreator;
import com.tiny.bank.domain.usecase.user.UserDeactivationProcessor;
import com.tiny.bank.domain.user.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserCreator userCreator;
    private final UserDeactivationProcessor userDeactivationProcessor;

    public UserController(final UserCreator userCreator, final UserDeactivationProcessor userDeactivationProcessor) {
        this.userCreator = userCreator;
        this.userDeactivationProcessor = userDeactivationProcessor;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserCreationRequest body) {
        final User user = userCreator.create(User.createUser(body.name(), body.ccNumber(), body.birthdate()));

        return ResponseEntity.ok().body(user);
    }

    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable String userId) {
        final User user = userDeactivationProcessor.process(userId);

        return ResponseEntity.ok().body(user);
    }

}
