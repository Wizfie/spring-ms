    package com.ms.springms.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.springframework.stereotype.Component;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "ms_user")
    @Entity
    @Component
    public class UserInfo {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String username;
        private String nip;
        private String email;
        private String password;
        private String role;
    }
