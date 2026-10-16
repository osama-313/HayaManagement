package com.haya.service;

import com.haya.dto.ResourceDtos;
import com.haya.entity.Resource;
import com.haya.exception.NotFoundException;
import com.haya.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resources;

    public ResourceService(ResourceRepository resources) {
        this.resources = resources;
    }

    @Transactional(readOnly = true)
    public List<ResourceDtos.Response> findAll() {
        return resources.findAllByOrderByIdDesc().stream().map(ResourceDtos.Response::from).toList();
    }

    @Transactional
    public ResourceDtos.Response create(ResourceDtos.Request request) {
        Resource resource = new Resource(request.url().trim(), blankToNull(request.highlights()));
        return ResourceDtos.Response.from(resources.save(resource));
    }

    @Transactional
    public ResourceDtos.Response update(Long id, ResourceDtos.Request request) {
        Resource resource = require(id);
        resource.setUrl(request.url().trim());
        resource.setHighlights(blankToNull(request.highlights()));
        return ResourceDtos.Response.from(resource);
    }

    @Transactional
    public void delete(Long id) {
        resources.delete(require(id));
    }

    private Resource require(Long id) {
        return resources.findById(id).orElseThrow(() -> new NotFoundException("Resource", id));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
