package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.domain.models.LoginRequest;
import com.infoworks.lab.domain.models.NewAccountRequest;
import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTPayload;
import com.infoworks.lab.jwtoken.definition.TokenProvider;
import com.infoworks.lab.jwtoken.services.JWTokenProvider;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.services.UserService;
import com.infoworks.lab.webapp.config.AuthorizationFilter;
import com.infoworks.lab.webapp.config.JWTokenValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/v1")
public class AuthController {

    private static Logger LOG = LoggerFactory.getLogger("AuthController");
    private UserService service;
    private PasswordEncoder passwordEncoder;

    public AuthController(UserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/isAccountExist")
    public ResponseEntity<String> isExist(@RequestParam("username") String username){
        //
        Response response = new Response()
                .setStatus(HttpStatus.NOT_FOUND.value())
                .setMessage("User Not Found");
        //
        if (service.read(username) != null){
            response.setStatus(HttpStatus.OK.value())
                    .setMessage("User exist: " + username);
        }
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ADMIN')")
    public ResponseEntity<String> newAccount(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token
            , @ApiIgnore @AuthenticationPrincipal UserDetails principal
            , @Valid @RequestBody NewAccountRequest account){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello NewAccount");
        //TODO:
        //Check is already exist:
        User exist = service.read(account.getUsername());
        if (exist == null){
            //Do Create New User Account using username and password.
            String securePass = passwordEncoder.encode(account.getPassword());
            User user = new User(account.getUsername(), securePass, Arrays.asList("USER"));
            service.put(account.getUsername(), user);
            response.setMessage("Successfully Created: " + user.getUsername());
        } else{
            response.setMessage("User exist: " + exist.getUsername());
        }
        //..
        return ResponseEntity.ok(response.toString());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request){
        Response response = new Response().setStatus(HttpStatus.OK.value())
                .setMessage("Hello Login");
        //TODO:
        String kid = JWTokenValidator.getRandomSecretKey();
        String secret = JWTokenValidator.getSecretKeyMap().get(kid);
        //Check is already exist:
        User exist = service.read(request.getUsername());
        String userRole = request.getUsername().startsWith("ADMIN")
                ? "ROLE_ADMIN, ADMIN"
                : (exist != null ? String.join(",", exist.getRoles()) : "ROLE_USER");
        //
        JWTHeader header = new JWTHeader().setTyp("round").setKid(kid);
        JWTPayload payload = new JWTPayload().setSub(request.getUsername())
                .setIss(request.getUsername())
                .setIat(new Date().getTime())
                .setExp(TokenProvider.defaultTokenTimeToLive().getTimeInMillis())
                .addData(AuthorizationFilter.AUTHORITIES_KEY, userRole)
                .addData("/new/account","false")
                .addData("/isValidToken","true");
        //
        TokenProvider token = new JWTokenProvider(secret)
                .setHeader(header)
                .setPayload(payload);
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
