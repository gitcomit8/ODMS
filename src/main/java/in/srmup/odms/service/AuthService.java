package in.srmup.odms.service;

import in.srmup.odms.model.User;
import in.srmup.odms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private static final long OTP_VALID_DURATION_MINUTES = 10; // OTP is valid for 10 minutes

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserDetailsService userDetailsService;

    public void generateAndSendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        String otp = new Random().ints(6, 0, 10)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

        user.setOtp(otp);
        user.setOtpRequestedTime(LocalDateTime.now());
        userRepository.save(user);

        sendOtpEmail(email, otp);
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OD Request System Login OTP");
        message.setText("Your One-Time Password is: " + otp + "\nIt is valid for 10 minutes.");
        mailSender.send(message);
    }

    public boolean validateOtpAndLogin(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if OTP is valid and not expired
        if (user.getOtp() != null && user.getOtp().equals(otp) &&
                user.getOtpRequestedTime().isAfter(LocalDateTime.now().minusMinutes(OTP_VALID_DURATION_MINUTES))) {

            // OTP is valid, clear it to prevent reuse
            user.setOtp(null);
            user.setOtpRequestedTime(null);
            userRepository.save(user);

            // Manually authenticate the user in Spring Security
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return true;
        }
        return false;
    }
}