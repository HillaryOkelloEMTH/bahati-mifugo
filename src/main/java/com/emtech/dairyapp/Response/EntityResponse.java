package com.emtech.dairyapp.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EntityResponse <T>{


    private String message;
    private Integer statusCode;
    private T entity;
}
