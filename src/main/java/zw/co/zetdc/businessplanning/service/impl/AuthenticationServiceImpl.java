package zw.co.zetdc.businessplanning.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.zetdc.businessplanning.entities.User;
import zw.co.zetdc.businessplanning.enums.TokenType;
import zw.co.zetdc.businessplanning.exception.AuthenticationException;
import zw.co.zetdc.businessplanning.exception.UserNotFoundException;
import zw.co.zetdc.businessplanning.payload.request.AuthenticationRequest;
import zw.co.zetdc.businessplanning.payload.request.MailBody;
import zw.co.zetdc.businessplanning.payload.request.RegisterRequest;
import zw.co.zetdc.businessplanning.payload.request.UserUpdateRequest;
import zw.co.zetdc.businessplanning.payload.response.AuthenticationResponse;
import zw.co.zetdc.businessplanning.repository.UserRepository;
import zw.co.zetdc.businessplanning.service.AuthenticationService;
import zw.co.zetdc.businessplanning.service.EmailService;
import zw.co.zetdc.businessplanning.service.JwtService;
import zw.co.zetdc.businessplanning.service.RefreshTokenService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service @Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    private final EmailService emailService;
    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        // Generate a random password
        String generatedPassword = generateRandomPassword(12); // Adjust length as needed

        // Create a new User object based on the RegisterRequest with the generated password
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .cell(request.getCell())
                .password(passwordEncoder.encode(generatedPassword)) // Save the encoded generated password
                .role(request.getRole())
                .sectionId(request.getSectionId()) // Set the sectionId from the request
                .departmentId(request.getDepartmentId())
                .divisionId(request.getDivisionId())
                .temporaryPassword(true) // Set the temporary password flag
                .build();

        // Save the user to the repository
        user = userRepository.save(user);

        // Generate and set OTP
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes
        userRepository.save(user); // Save OTP details

        // Prepare OTP email content
        String otpSubject = "Your OTP Code";
        String otpBody = "Your account has been successfully created. Your OTP code is: " + otp;

        // Create a MailBody object for OTP email
        MailBody otpMailBody = new MailBody(user.getEmail(), otpSubject, otpBody);

        // Send email with the OTP
        emailService.sendSimpleMessage(otpMailBody);

        // Prepare email content for account creation
        String subject = "ZETDC Reporting System Account Creation";
        String body = "Your account has been successfully created. Your password is: " + generatedPassword;

        // Create a MailBody object for account creation email
        MailBody mailBody = new MailBody(user.getEmail(), subject, body);

        // Optionally send email with the generated password
         emailService.sendSimpleMessage(mailBody);

        // Generate JWT token for the user
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // Map user roles to authorities
        var roles = user.getRole().getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        // Build and return the AuthenticationResponse
        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .email(user.getEmail())
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .cell(user.getCell())
                .password(generatedPassword) // Optionally include the generated password in the response
                .refreshToken(refreshToken.getToken())
                .roles(roles)
                .sectionId(user.getSectionId()) // Use sectionId
                .departmentId(user.getDepartmentId())
                .divisionId(user.getDivisionId())
                .temporaryPassword(user.isTemporaryPassword())
                .tokenType(TokenType.BEARER.name())
                .message("User created successfully. An OTP has been sent to your email.")
                .build();
    }

    // Generate OTP method
    @Override
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    // Generate Random Password
    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Starting authentication for identifier: {}", request.getEmail());

        User user;

        try {
            // Find the user by email
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

            if (!optionalUser.isPresent()) {
                log.error("User not found for identifier: {}", request.getEmail());
                throw new AuthenticationException("Invalid email or password");
            }

            user = optionalUser.get();
            log.info("Attempting to authenticate user: {}", user.getEmail());

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
            );

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for identifier: {}", request.getEmail());
            throw new AuthenticationException("Invalid email or password");
        }

        log.info("Successfully authenticated user: {}", user.getEmail());

        // Check if the user has a temporary password
        if (user.isTemporaryPassword()) {
            log.warn("User has a temporary password: {}", user.getEmail());
            return AuthenticationResponse.builder()
                    .accessToken(null) // No access token for temporary password users
                    .roles(Collections.emptyList())
                    .email(user.getEmail())
                    .id(user.getId())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .cell(user.getCell())
                    .sectionId(user.getSectionId())
                    .departmentId(user.getDepartmentId())
                    .divisionId(user.getDivisionId())
                    .temporaryPassword(user.isTemporaryPassword())
                    .refreshToken(null) // No refresh token for temporary password users
                    .message("Please change your temporary password.") // Include a message field
                    .build();
        }

        // Generate OTP for the user
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes
        userRepository.save(user); // Save the OTP

        // Send OTP to the user's email
        String otpSubject = "Your OTP Code";
        String otpBody = "Your OTP code is: " + otp;
        MailBody mailBody = new MailBody(user.getEmail(), otpSubject, otpBody);
        emailService.sendSimpleMessage(mailBody);

        // Prompt the user to enter OTP
        return AuthenticationResponse.builder()
                .accessToken(null) // No access token yet
                .roles(Collections.emptyList())
                .email(user.getEmail())
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .cell(user.getCell())
                .sectionId(user.getSectionId())
                .departmentId(user.getDepartmentId())
                .divisionId(user.getDivisionId())
                .temporaryPassword(user.isTemporaryPassword())
                .refreshToken(null) // No refresh token yet
                .message("An OTP has been sent to your email. Please enter it to proceed.") // Prompt for OTP
                .build();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    @Transactional
    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        // Fetch the existing user
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Update user fields
        copyNonNullProperties(userUpdateRequest, existingUser);
        // Save the updated user
        return userRepository.save(existingUser);
    }

    public void copyNonNullProperties(Object source, Object target) {
        BeanWrapper src = new BeanWrapperImpl(source);
        Set<String> ignoreSet = new HashSet<>();

        for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                ignoreSet.add(pd.getName());
            }
        }

        // Add fields to ignore regardless of their value
        ignoreSet.add("roles");

        BeanUtils.copyProperties(source, target, ignoreSet.toArray(new String[0]));
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Validate the current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new IllegalArgumentException("Invalid current password");
            }

            // Hash the new password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setTemporaryPassword(false);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found with the provided email");
        }
    }



}
