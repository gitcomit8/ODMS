package in.srmup.odms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_master")
public class StudentMaster {

    @Id
    @Column(unique = true, nullable = false)
    private String registrationNumber;

    private String name;
    private Integer academicYear;
    private String branch;
    private String section;
    private String department;

    @Column(nullable = false)
    private int odLeaveCount = 0;

    // No-argument constructor required by JPA
    public StudentMaster() {
    }

    // Constructor for easy object creation
    public StudentMaster(String registrationNumber, String name, Integer academicYear, String branch, String section, String department) {
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.academicYear = academicYear;
        this.branch = branch;
        this.section = section;
        this.department = department;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getOdLeaveCount() {
        return odLeaveCount;
    }

    public void setOdLeaveCount(int odLeaveCount) {
        this.odLeaveCount = odLeaveCount;
    }
}