package com.ms.springms.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.json.JSONObject;


import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class JwtService {

    @Autowired
    private UserDetails userDetails;

    private Set<String> tokenBlackList = ConcurrentHashMap.newKeySet();


    private static final String SECRET = "23131313dwdddddddddddwa222222222222232131231dedDADWW21";

    public String generateToken(String username){

        System.out.println("Generated token for user: " + username);
        Map<String , Objects> claims = new HashMap<>();
      String token =  Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 ))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

      tokenBlackList.add(token);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("token", token);

        return jsonResponse.toString();
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token , Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaim(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaim(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private  Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return !isTokenBlackListed(token) && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }


    public String extractTokenId(String token){
        Claims claims = extractAllClaim(token);
        return claims.getId();
    }

    public void addToBlackList(String token) {
        tokenBlackList.add(token);
    }

    public Boolean isTokenBlackListed(String token) {
        return tokenBlackList.contains(token);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Menjalankan setiap hari pada pukul 00:00
    public void cleanExpiredTokens() {
        Iterator<String> iterator = tokenBlackList.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            if (isTokenExpired(token)) {
                iterator.remove(); // Hapus token yang sudah expired
            }
        }
    }



}
