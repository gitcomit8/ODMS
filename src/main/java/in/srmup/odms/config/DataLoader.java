package in.srmup.odms.config;

import in.srmup.odms.model.User;
import in.srmup.odms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only create users if they don't already exist
        if (userRepository.count() == 0) {
            // Create test users
            User admin = new User();
            admin.setEmail("admin@test.com");
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);

            User organizer = new User();
            organizer.setEmail("organizer@test.com");
            organizer.setRole("ROLE_STUDENT_ORGANIZER");
            userRepository.save(organizer);

            User coordinator = new User();
            coordinator.setEmail("coordinator@test.com");
            coordinator.setRole("ROLE_EVENT_COORDINATOR");
            userRepository.save(coordinator);

            User welfare = new User();
            welfare.setEmail("welfare@test.com");
            welfare.setRole("ROLE_STUDENT_WELFARE");
            userRepository.save(welfare);

            User hod = new User();
            hod.setEmail("hod@test.com");
            hod.setRole("ROLE_HOD");
            userRepository.save(hod);

            User faculty = new User();
            faculty.setEmail("faculty@test.com");
            faculty.setRole("ROLE_FACULTY");
            userRepository.save(faculty);

            System.out.println("Test users created successfully!");
        }
    }
}