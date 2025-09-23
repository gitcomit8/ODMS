package in.srmup.odms.service;

import in.srmup.odms.model.User;
import in.srmup.odms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Basic validation for the role
        if (newRole != null && !newRole.isBlank()) {
            user.setRole(newRole);
            userRepository.save(user);
        }
    }
}