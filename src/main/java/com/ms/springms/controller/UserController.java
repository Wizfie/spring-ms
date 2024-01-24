package com.ms.springms.controller;

import com.ms.springms.entity.UserInfo;
import com.ms.springms.model.AuthRequest;
import com.ms.springms.model.CurrentUserDTO;
import com.ms.springms.service.JwtService;
import com.ms.springms.service.MyUserDetails;
import com.ms.springms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/addUser")
    public String addUser(@RequestBody UserInfo userInfo){
        return userService.addUser(userInfo);
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){
        System.out.println("Logged in user: " + authRequest.getUsername());

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authenticate.isAuthenticated()){
            SecurityContextHolder.getContext().setAuthentication(authenticate); // Set authentication
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("UserInfo not found");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        if (token != null) {
            // Add the token to the blacklist
            jwtService.addToBlackList(token);

            System.out.println("Token added to blacklist: " + token);
            System.out.println("LOGOUT");

            SecurityContextHolder.clearContext();

            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.badRequest().body("Token not found in the request");
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }




    @GetMapping("/getAll")
    public List<UserInfo> getAllUsers(){
        return userService.getAllUser();
    }


    @GetMapping("/getUsers/{id}")
    public UserInfo getById(@RequestHeader("Authorization") String token, @PathVariable Long id){
        return userService.getById(id);
    }

    @Secured("ROLE_USER")
    @GetMapping("/currentUser")
    public CurrentUserDTO getCurrentUser(Authentication authentication) {
        System.out.println("Authentication from parameter: " + authentication);

        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("User is authenticated");

            Object principal = authentication.getPrincipal();
            if (principal instanceof MyUserDetails) {
                MyUserDetails userDetails = (MyUserDetails) principal;
                String username = userDetails.getUsername();
                String nip = userDetails.getNip();
                String email = userDetails.getEmail();

                String role = userDetails.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .collect(Collectors.joining(","));

                System.out.println("Username: " + username);
                System.out.println("NIP: " + nip);
                System.out.println("Email: " + email);
                System.out.println("Roles: " + role);

                return new CurrentUserDTO(username, nip, email, role);
            } else {
                System.out.println("Authentication principal is not an instance of MyUserDetails");
            }
        } else {
            System.out.println("User is not authenticated");
        }

        return null; // or handle appropriately if no user is found
    }



}
