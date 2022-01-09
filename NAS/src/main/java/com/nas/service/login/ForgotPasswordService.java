package com.nas.service.login;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.communicationsecurity.service.exception.ExpiredTokenException;
import com.nas.communicationsecurity.service.exception.InvalidCredentialsException;
import com.nas.communicationsecurity.service.exception.NotValidTokenException;
import com.nas.configuration.SecurityConfiguration;
import com.nas.persistence.dto.EmailDTO;
import com.nas.persistence.dto.OperationDTO;
import com.nas.persistence.dto.ResetPasswordDTO;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.User;
import com.nas.persistence.model.UserView;
import com.nas.persistence.model.VerificationToken;
import com.nas.persistence.repository.UserRepository;
import com.nas.persistence.repository.VerificationTokenRepository;
import com.nas.proxy.MailSenderServiceProxy;
import feign.RetryableException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@NoArgsConstructor
@Data
public class ForgotPasswordService {

    @NonNull
    @Autowired
    private UserRepository userRepository;

    @NonNull
    @Autowired
    private MailSenderServiceProxy mailSenderServiceProxy;

    @Autowired
    @NonNull
    private JwtService jwtService;

    @Autowired
    @NonNull
    private PasswordEncoder passwordEncoder;

    @NonNull
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    @NonNull
    private SecurityConfiguration securityConfiguration;

    public ResponseEntity<HttpStatus> forgotPassword(EmailDTO emailDTO) {
        User user = userRepository.findByEmail(emailDTO.getEmail());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(new UserView(user));
        ResponseEntity<HttpStatus> response;
        try {
            response = mailSenderServiceProxy.sendEmail(jwtService.generateToken(userDetailsDTO), OperationDTO.types.RESET_PASSWORD.toString());
        } catch (RetryableException e) {
            response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return response;
    }

    public String showResetPasswordPage(Model model, String token) throws NotValidTokenException, ExpiredTokenException, InvalidCredentialsException {
        if (securityConfiguration.isLogged()) {
            jwtService.validateToken(token);
            model.addAttribute("jwt", token);
            return "fragments/login/resetPassword";
        } else {
            VerificationToken vt = verificationTokenRepository.findByToken(token);
            if (vt != null && !vt.isUsed() && vt.getOperation().equals(OperationDTO.types.RESET_PASSWORD.toString()) && Instant.now().isBefore(vt.getExpiryDate())) {
                model.addAttribute("jwt", token);
                return "login/resetPassword";
            } else {
                throw new NotValidTokenException("Not a valid token!");
            }
        }
    }

    public ResponseEntity<HttpStatus> resetUserpassword(ResetPasswordDTO resetPasswordDTO) throws ExpiredTokenException, InvalidCredentialsException {
        if (securityConfiguration.isLogged()) {
            User user = new User(jwtService.validateToken(resetPasswordDTO.getToken()));
            if (passwordEncoder.matches(resetPasswordDTO.getCurrentPassword(), user.getPassword())) {
                if (!resetPasswordDTO.getPassword1().equals(resetPasswordDTO.getPassword2())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                } else if (!resetPasswordDTO.getPassword1().isEmpty()) {
                    user.setPassword(new BCryptPasswordEncoder(11).encode(resetPasswordDTO.getPassword1()));
                    userRepository.save(user);
                }
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } else {
            VerificationToken vt = verificationTokenRepository.findByToken(resetPasswordDTO.getToken());
            if (vt != null && !vt.isUsed() && vt.getOperation().equals(OperationDTO.types.RESET_PASSWORD.toString()) && Instant.now().isBefore(vt.getExpiryDate())) {
                if (!resetPasswordDTO.getPassword1().equals(resetPasswordDTO.getPassword2())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                } else if (!resetPasswordDTO.getPassword1().isEmpty()) {
                    User user = vt.getUser();
                    user.setPassword(new BCryptPasswordEncoder(11).encode(resetPasswordDTO.getPassword1()));
                    userRepository.save(user);
                    vt.setUsed(true);
                    verificationTokenRepository.save(vt);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
