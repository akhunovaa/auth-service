package com.botmasterzzz.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

public abstract class AbstractDto implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}