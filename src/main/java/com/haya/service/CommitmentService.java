package com.haya.service;

import com.haya.dto.CommitmentDtos;
import com.haya.entity.Commitment;
import com.haya.exception.NotFoundException;
import com.haya.repository.CommitmentRepository;
import com.haya.repository.UserRepository;
import com.haya.security.AppUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Commitments are visible to the whole team; only their owner (or an administrator) may change or remove them. */
@Service
public class CommitmentService {

    private final CommitmentRepository commitments;
    private final UserRepository users;

    public CommitmentService(CommitmentRepository commitments, UserRepository users) {
        this.commitments = commitments;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<CommitmentDtos.Response> findAll() {
        return commitments.findAllByOrderByIdDesc().stream().map(CommitmentDtos.Response::from).toList();
    }

    @Transactional
    public CommitmentDtos.Response create(CommitmentDtos.Request request, AppUserDetails actor) {
        Commitment commitment = new Commitment(request.fromWhen(), request.toWhen(), request.hours(),
                blankToNull(request.note()),
                users.findById(actor.getId()).orElseThrow(() -> new NotFoundException("User", actor.getId())));
        return CommitmentDtos.Response.from(commitments.save(commitment));
    }

    @Transactional
    public CommitmentDtos.Response update(Long id, CommitmentDtos.Request request, AppUserDetails actor) {
        Commitment commitment = requireManageable(id, actor);
        commitment.setFromWhen(request.fromWhen());
        commitment.setToWhen(request.toWhen());
        commitment.setHours(request.hours());
        commitment.setNote(blankToNull(request.note()));
        return CommitmentDtos.Response.from(commitment);
    }

    @Transactional
    public void delete(Long id, AppUserDetails actor) {
        commitments.delete(requireManageable(id, actor));
    }

    private Commitment requireManageable(Long id, AppUserDetails actor) {
        Commitment commitment = commitments.findById(id).orElseThrow(() -> new NotFoundException("Commitment", id));
        if (!actor.isAdmin() && !commitment.getUser().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Not your commitment");
        }
        return commitment;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
