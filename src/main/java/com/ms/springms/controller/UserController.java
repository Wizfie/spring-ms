package com.ms.springms.controller;

import com.ms.springms.entity.UserInfo;
import com.ms.springms.model.AuthRequest;
import com.ms.springms.service.JwtService;
import com.ms.springms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/welcome")
    public String Welcome(){
        return "Start Spring";
    }

    @PostMapping("addUser")
    public String addUser(@RequestBody UserInfo userInfo){
        return userService.addUser(userInfo);
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()){
            return jwtService.generateToken(authRequest.getUsername());
        }else {
            throw  new UsernameNotFoundException("UserInfo not found");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Add the token to the blacklist
        jwtService.addToBlackList(token.substring(7));
        return ResponseEntity.ok("Logged out successfully");
    }


    @GetMapping("/getAll")
    public List<UserInfo> getAllUsers(){
        return userService.getAllUser();
    }


//    @GetMapping("/getUsers/{id}")
//    public UserInfo getById(@PathVariable Integer id){
//        return userService.getAllUser().get();
//    }

}
