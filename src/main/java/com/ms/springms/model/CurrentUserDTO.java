package com.ms.springms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserDTO {

    private String username;
    private String nip;
    private String email;
    private String role;
}
