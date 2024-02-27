package com.haya.service;

import com.haya.modles.Resources;
import com.haya.repository.ResourcesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourcesServices {

    private ResourcesRepository repo;

    public ResourcesServices(ResourcesRepository repo) {
        this.repo = repo;
    }


    // find all notes
    public List<Resources> findAllResources(){
        return repo.findAll();
    }

    // create/insert/add Resource
    public Resources insertResource(Resources resources){
        return repo.save(resources);
    }

    //edit/update Resource
    public Resources updateResource(int id, Resources newResource){


        Resources updatedResource = repo.findById(id).orElse(null);

        updatedResource.setUrl(newResource.getUrl());
        updatedResource.setHighlights(newResource.getHighlights());

        return repo.save(updatedResource);
    }

    // delete Resource
    public void deleteNote(int id){
        repo.deleteById(id);
    }




}
