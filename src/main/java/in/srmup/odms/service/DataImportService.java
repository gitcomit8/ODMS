package in.srmup.odms.service;

import in.srmup.odms.model.FacultyMaster;
import in.srmup.odms.model.StudentMaster;
import in.srmup.odms.repository.FacultyMasterRepository;
import in.srmup.odms.repository.StudentMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataImportService {

    @Autowired
    private StudentMasterRepository studentMasterRepository;

    @Autowired
    private FacultyMasterRepository facultyMasterRepository;

    public int importStudentsFromCsv(MultipartFile file) throws Exception {
        List<StudentMaster> studentsToSave = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6) {
                    StudentMaster student = new StudentMaster(
                            data[0].trim(), // registrationNumber
                            data[1].trim(), // name
                            Integer.parseInt(data[2].trim()), // academicYear
                            data[3].trim(), // branch
                            data[4].trim(), // section
                            data[5].trim()  // department
                    );
                    studentsToSave.add(student);
                }
            }
        }

        studentMasterRepository.saveAll(studentsToSave);
        return studentsToSave.size();
    }

    public int importFacultyFromCsv(MultipartFile file) throws Exception {
        List<FacultyMaster> facultyToSave = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    FacultyMaster faculty = new FacultyMaster();
                    faculty.setFacultyName(data[0].trim());
                    faculty.setFacultyEmail(data[1].trim());
                    faculty.setBranch(data[2].trim());
                    faculty.setSection(data[3].trim());
                    facultyToSave.add(faculty);
                }
            }
        }

        facultyMasterRepository.saveAll(facultyToSave);
        return facultyToSave.size();
    }

    public void clearAllStudentData() {
        studentMasterRepository.deleteAll();
    }

    public void clearAllFacultyData() {
        facultyMasterRepository.deleteAll();
    }
}