package com.buddy.studybuddy.exceptions;

import com.buddy.studybuddy.dtos.ErrorDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


public class BusinessException extends RuntimeException {

    private List<ErrorDTO> errors;

    public BusinessException(List<ErrorDTO> errors){
        this.errors = errors;
    }

    public List<ErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDTO> errors) {
        this.errors = errors;
    }
}
