package com.lab.infoworks.controllers.rest;

import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTPayload;
import com.infoworks.lab.jwtoken.definition.TokenProvider;
import com.infoworks.lab.jwtoken.services.JWTokenProvider;
import com.infoworks.lab.rest.models.Response;
import com.lab.infoworks.domain.models.LoginRequest;
import com.lab.infoworks.domain.models.NewAccountRequest;
import com.lab.infoworks.webapp.config.AuthorizationFilter;
import com.lab.infoworks.webapp.config.JWTokenValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static Logger LOG = LoggerFactory.getLogger("AuthController");

    @GetMapping("/isAccountExist")
    public ResponseEntity<String> isExist(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token
            , @ApiIgnore @AuthenticationPrincipal UserDetails principal){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello isExist");
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/isValidToken")
    public ResponseEntity<String> isValid(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token
                    , @ApiIgnore @AuthenticationPrincipal UserDetails principal){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello IsValidToken");
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token
            , @ApiIgnore @AuthenticationPrincipal UserDetails principal){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello RefreshToken");
        return ResponseEntity.ok(response.toString());
    }

    @PostMapping("/new/account")
    public ResponseEntity<String> newAccount(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token
            , @ApiIgnore @AuthenticationPrincipal UserDetails principal
            , @Valid @RequestBody NewAccountRequest account){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello NewAccount");
        //Check is already exist.
        //Do Create New User Account using username and password.
        String securePass = passwordEncoder.encode(account.getPassword());
        //Save salted password into Persistence Layer.
        //..
        return ResponseEntity.ok(response.toString());
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello Login");
        //TODO:
        String kid = JWTokenValidator.getRandomSecretKey();
        String secret = JWTokenValidator.getSecretKeyMap().get(kid);
        //TODO:
        String userRole = request.getUsername().startsWith("ADMIN") ? "ROLE_ADMIN" : "ROLE_USER";
        //
        JWTPayload payload = new JWTPayload().setSub(request.getUsername())
                .setIss(request.getUsername())
                .setIat(new Date().getTime())
                .setExp(TokenProvider.defaultTokenTimeToLive().getTimeInMillis())
                .addData(AuthorizationFilter.AUTHORITIES_KEY, userRole)
                .addData("/new/account","false")
                .addData("/isValidToken","true");
        //
        TokenProvider token = new JWTokenProvider(secret)
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("round").setKid(kid));
        //
        String tokenKey = token.generateToken(TokenProvider.defaultTokenTimeToLive());
        LOG.info(tokenKey);
        response.setMessage(tokenKey);
        return ResponseEntity.ok(response.toString());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token
            , @ApiIgnore @AuthenticationPrincipal UserDetails principal){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello Logout");
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/forget")
    public ResponseEntity<String> forget(@RequestParam String email){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello Forget");
        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/reset")
    public ResponseEntity<String> reset(@RequestParam String resetToken){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello Reset");
        return ResponseEntity.ok(response.toString());
    }

}
