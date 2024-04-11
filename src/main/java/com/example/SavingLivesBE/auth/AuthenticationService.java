package com.example.SavingLivesBE.auth;

import com.example.SavingLivesBE.config.JwtService;
import com.example.SavingLivesBE.users.data.constants.UserRole;
import com.example.SavingLivesBE.users.data.entities.UserEntity;
import com.example.SavingLivesBE.users.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = UserEntity.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.CLIENT)
                .build();

        java.util.Map<String, Object> extraRoleClaim = new HashMap<>();
        extraRoleClaim.put("role", user.getRole());
        extraRoleClaim.put("name", user.getName());
        extraRoleClaim.put("id", user.getId());

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(extraRoleClaim, user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findFirstByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("User not found in db"));

        java.util.Map<String, Object> extraRoleClaim = new HashMap<>();
        extraRoleClaim.put("role", user.getRole());
        extraRoleClaim.put("name", user.getName());
        extraRoleClaim.put("id", user.getId());

        var jwtToken = jwtService.generateToken(extraRoleClaim, user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}

