package in.srmup.odms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "faculty_master")
public class FacultyMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String facultyName;
    private String facultyEmail;

    // The class this faculty is in charge of
    private String branch;
    private String section;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyEmail() {
        return facultyEmail;
    }

    public void setFacultyEmail(String facultyEmail) {
        this.facultyEmail = facultyEmail;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}