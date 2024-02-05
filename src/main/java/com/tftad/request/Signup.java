package com.tftad.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Signup {

    private String email;
    private String name;
    private String password;
}
