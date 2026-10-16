package com.haya.controller;

import com.haya.dto.UserDtos;
import com.haya.security.AppUserDetails;
import com.haya.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserController {

    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping
    @Operation(summary = "List all team members")
    List<UserDtos.Response> list() {
        return users.findAll();
    }

    @GetMapping("/{id}")
    UserDtos.Response get(@PathVariable Long id) {
        return users.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a team member (administrators only)")
    UserDtos.Response create(@Valid @RequestBody UserDtos.CreateRequest request) {
        return users.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a team member (administrators only)")
    UserDtos.Response update(@PathVariable Long id, @Valid @RequestBody UserDtos.UpdateRequest request,
                             @AuthenticationPrincipal AppUserDetails actor) {
        return users.update(id, request, actor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a team member and everything they own (administrators only)")
    void delete(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails actor) {
        users.delete(id, actor);
    }
}
