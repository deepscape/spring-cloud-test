package com.optimagrowth.license.model.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper {

    private Object data;
    private Object metadata;
    private List<ErrorMessage> errors;

    /**
     * @param data
     * @param metadata
     * @param errors
     */
    public ResponseWrapper(Object data, Object metadata, List<ErrorMessage> errors) {
        super();
        this.data = data;
        this.metadata = metadata;
        this.errors = errors;
    }

}