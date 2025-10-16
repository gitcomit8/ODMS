package in.srmup.odms.controller.api;

import in.srmup.odms.model.StudentMaster;
import in.srmup.odms.repository.StudentMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentApiController {

    @Autowired
    private StudentMasterRepository studentMasterRepository;

    @GetMapping("/{regNo}")
    public ResponseEntity<StudentMaster> getStudentDetails(@PathVariable String regNo) {
        return studentMasterRepository.findById(regNo)
                .map(ResponseEntity::ok) // If found, return 200 OK with student data
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentSuggestion>> searchStudents(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        
        String searchQuery = query.trim().toUpperCase();
        List<StudentMaster> students = studentMasterRepository.findAll();
        
        List<StudentSuggestion> suggestions = students.stream()
                .filter(s -> s.getRegistrationNumber() != null && s.getRegistrationNumber().toUpperCase().startsWith(searchQuery))
                .limit(10)
                .map(s -> new StudentSuggestion(s.getRegistrationNumber(), s.getName(), s.getBranch(), s.getAcademicYear()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(suggestions);
    }

    // Inner class for autocomplete suggestions
    public static class StudentSuggestion {
        private String regNo;
        private String name;
        private String branch;
        private Integer year;

        public StudentSuggestion(String regNo, String name, String branch, Integer year) {
            this.regNo = regNo;
            this.name = name;
            this.branch = branch;
            this.year = year;
        }

        public String getRegNo() {
            return regNo;
        }

        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }
    }
}