package com.assignment.ecommerce_rookie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private List<String> roles;
    private String accessToken;
}