package in.srmup.odms.config;

import in.srmup.odms.model.StudentMaster;
import in.srmup.odms.repository.StudentMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class TestDataLoader {

    @Autowired
    private StudentMasterRepository studentMasterRepository;

    @Bean
    CommandLineRunner loadTestStudents() {
        return args -> {
            if (studentMasterRepository.count() == 0) {
                studentMasterRepository.save(new StudentMaster("RA2111003010001", "John Doe", 2024, "CSE", "A", "Computer Science"));
                studentMasterRepository.save(new StudentMaster("RA2111003010002", "Jane Smith", 2024, "CSE", "A", "Computer Science"));
                studentMasterRepository.save(new StudentMaster("RA2111003010003", "Bob Johnson", 2024, "CSE", "B", "Computer Science"));
                studentMasterRepository.save(new StudentMaster("RA2111003010004", "Alice Cooper", 2024, "ECE", "A", "Electronics"));
                studentMasterRepository.save(new StudentMaster("RA2111003010005", "Charlie Brown", 2023, "CSE", "C", "Computer Science"));
                studentMasterRepository.save(new StudentMaster("RA2211003010001", "David Lee", 2023, "CSE", "A", "Computer Science"));
                studentMasterRepository.save(new StudentMaster("RA2211003010002", "Emma Watson", 2023, "ECE", "B", "Electronics"));
                studentMasterRepository.save(new StudentMaster("CS2111003010001", "Frank Miller", 2024, "CSE", "A", "Computer Science"));
                studentMasterRepository.save(new StudentMaster("CS2111003010002", "Grace Hopper", 2024, "CSE", "B", "Computer Science"));
                studentMasterRepository.save(new StudentMaster("EC2111003010001", "Henry Ford", 2024, "ECE", "A", "Electronics"));
                
                System.out.println("✅ Test students loaded successfully!");
            }
        };
    }
}
