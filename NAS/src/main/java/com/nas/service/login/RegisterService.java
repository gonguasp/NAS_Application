package com.nas.service.login;

import com.nas.communicationsecurity.service.JwtService;
import com.nas.communicationsecurity.service.exception.NotValidTokenException;
import com.nas.persistence.dto.OperationDTO;
import com.nas.persistence.dto.RegisterDTO;
import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.Role;
import com.nas.persistence.model.User;
import com.nas.persistence.model.UserView;
import com.nas.persistence.model.VerificationToken;
import com.nas.persistence.repository.RoleRepository;
import com.nas.persistence.repository.UserRepository;
import com.nas.persistence.repository.VerificationTokenRepository;
import com.nas.proxy.MailSenderServiceProxy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RegisterService {

    @NonNull
    @Autowired
    private UserRepository userRepository;

    @NonNull
    @Autowired
    private RoleRepository roleRepository;

    @NonNull
    @Autowired
    private MailSenderServiceProxy mailSenderServiceProxy;

    @Autowired
    @NonNull
    private JwtService jwtService;

    @NonNull
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public ResponseEntity<HttpStatus> registerUser(RegisterDTO registerDTO) {
        if (registerDTO.getPassword1().equals(registerDTO.getPassword2())) {
            if (userRepository.findByEmail(registerDTO.getEmail()) != null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            User user = userRepository.save(new User(registerDTO.getEmail(), new BCryptPasswordEncoder(11).encode(registerDTO.getPassword1()), registerDTO.getName()));
            user.setRoles(new ArrayList<>());
            user.getRoles().add(roleRepository.save(new Role("ROLE_USER", user)));

            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(new UserView(user));
            return mailSenderServiceProxy.sendEmail(jwtService.generateToken(userDetailsDTO), OperationDTO.types.REGISTER.toString());
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    public String activateAccount(String token) throws NotValidTokenException {
        VerificationToken vt = verificationTokenRepository.findByToken(token);
        if (vt != null && !vt.isUsed() && vt.getOperation().equals(OperationDTO.types.REGISTER.toString()) && Instant.now().isBefore(vt.getExpiryDate())) {
            User user = vt.getUser();
            user.setEnabled(true);
            userRepository.save(user);
            vt.setUsed(true);
            verificationTokenRepository.save(vt);
        } else {
            throw new NotValidTokenException("Not a valid token!");
        }

        return "login/registerConfirm";
    }
}
