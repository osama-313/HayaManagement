package com.haya.controller;

import com.haya.dto.CommitmentDtos;
import com.haya.security.AppUserDetails;
import com.haya.service.CommitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/commitments")
@Tag(name = "Commitments")
public class CommitmentController {

    private final CommitmentService commitments;

    public CommitmentController(CommitmentService commitments) {
        this.commitments = commitments;
    }

    @GetMapping
    @Operation(summary = "List every member's commitments, newest first")
    List<CommitmentDtos.Response> list() {
        return commitments.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record a commitment for the signed-in member")
    CommitmentDtos.Response create(@Valid @RequestBody CommitmentDtos.Request request,
                                   @AuthenticationPrincipal AppUserDetails actor) {
        return commitments.create(request, actor);
    }

    @PutMapping("/{id}")
    CommitmentDtos.Response update(@PathVariable Long id, @Valid @RequestBody CommitmentDtos.Request request,
                                   @AuthenticationPrincipal AppUserDetails actor) {
        return commitments.update(id, request, actor);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id, @AuthenticationPrincipal AppUserDetails actor) {
        commitments.delete(id, actor);
    }
}
