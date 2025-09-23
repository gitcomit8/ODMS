package in.srmup.odms.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String role; // e.g., "ROLE_STUDENT_ORGANIZER", "ROLE_ADMIN"

    private String otp;
    private LocalDateTime otpRequestedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getOtpRequestedTime() {
        return otpRequestedTime;
    }

    public void setOtpRequestedTime(LocalDateTime otpRequestedTime) {
        this.otpRequestedTime = otpRequestedTime;
    }
}