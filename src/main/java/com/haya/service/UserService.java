package com.haya.service;

import com.haya.dto.UserDtos;
import com.haya.entity.Role;
import com.haya.entity.User;
import com.haya.exception.ConflictException;
import com.haya.exception.NotFoundException;
import com.haya.repository.CommitmentRepository;
import com.haya.repository.TaskRepository;
import com.haya.repository.TokenRepository;
import com.haya.repository.UserRepository;
import com.haya.security.AppUserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository users;
    private final TaskRepository tasks;
    private final CommitmentRepository commitments;
    private final TokenRepository tokens;
    private final RoleService roles;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, TaskRepository tasks, CommitmentRepository commitments,
                       TokenRepository tokens, RoleService roles, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.tasks = tasks;
        this.commitments = commitments;
        this.tokens = tokens;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserDtos.Response> findAll() {
        return users.findAll().stream().map(UserDtos.Response::from).toList();
    }

    @Transactional(readOnly = true)
    public UserDtos.Response findById(Long id) {
        return UserDtos.Response.from(require(id));
    }

    @Transactional
    public UserDtos.Response create(UserDtos.CreateRequest request) {
        String userName = request.userName().trim();
        String email = request.email().trim();
        if (users.existsByUserNameIgnoreCase(userName)) {
            throw new ConflictException("Username '" + userName + "' is already taken");
        }
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email '" + email + "' is already registered");
        }

        User user = new User(request.firstName().trim(), request.lastName().trim(), userName, email,
                passwordEncoder.encode(request.password()), request.age(), blankToNull(request.proficiency()),
                roles.require(request.role()));
        return UserDtos.Response.from(users.save(user));
    }

    @Transactional
    public UserDtos.Response update(Long id, UserDtos.UpdateRequest request, AppUserDetails actor) {
        User user = require(id);
        String userName = request.userName().trim();
        String email = request.email().trim();
        if (users.existsByUserNameIgnoreCaseAndIdNot(userName, id)) {
            throw new ConflictException("Username '" + userName + "' is already taken");
        }
        if (users.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ConflictException("Email '" + email + "' is already registered");
        }

        Role role = roles.require(request.role());
        if (actor.getId().equals(id) && !Role.ADMIN.equals(role.getNormalizedName())) {
            throw new ConflictException("You cannot remove your own administrator role");
        }

        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setUserName(userName);
        user.setEmail(email);
        user.setAge(request.age());
        user.setProficiency(blankToNull(request.proficiency()));
        user.setRole(role);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return UserDtos.Response.from(user);
    }

    @Transactional
    public void delete(Long id, AppUserDetails actor) {
        if (actor.getId().equals(id)) {
            throw new ConflictException("You cannot delete your own account");
        }
        User user = require(id);
        tokens.deleteByUserId(id);
        commitments.deleteByUserId(id);
        tasks.deleteByAssigneeId(id);
        users.delete(user);
    }

    private User require(Long id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
