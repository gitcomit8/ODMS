package in.srmup.odms.controller.api;

import in.srmup.odms.model.StudentMaster;
import in.srmup.odms.repository.StudentMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentApiController {

    @Autowired
    private StudentMasterRepository studentMasterRepository;

    @GetMapping("/{regNo}")
    public ResponseEntity<StudentMaster> getStudentDetails(@PathVariable String regNo) {
        return studentMasterRepository.findById(regNo)
                .map(ResponseEntity::ok) // If found, return 200 OK with student data
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404 Not Found
    }
}