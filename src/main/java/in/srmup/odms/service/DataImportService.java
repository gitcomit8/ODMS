package in.srmup.odms.service;

import in.srmup.odms.model.StudentMaster;
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

    public int importStudentsFromCsv(MultipartFile file) throws Exception {
        //studentMasterRepository.deleteAllInBatch();

        List<StudentMaster> studentsToSave = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                StudentMaster student = new StudentMaster(
                        data[0], // registrationNumber
                        data[1], // name
                        Integer.parseInt(data[2]), // academicYear
                        data[3], // branch
                        data[4], // section
                        data[5]  // department
                );
                studentsToSave.add(student);
            }
        }

        studentMasterRepository.saveAll(studentsToSave);
        return studentsToSave.size();
    }
}