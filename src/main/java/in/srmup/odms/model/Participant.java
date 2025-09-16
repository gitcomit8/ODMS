package in.srmup.odms.model;

import jakarta.persistence.*;

@Entity
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String regNo;
    private Integer academicYr;
    private String branch;
    private String section;
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_request_id")
    private EventRequest eventRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public Integer getAcademicYr() {
        return academicYr;
    }

    public void setAcademicYr(Integer academicYr) {
        this.academicYr = academicYr;
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

    public EventRequest getEventRequest() {
        return eventRequest;
    }

    public void setEventRequest(EventRequest eventRequest) {
        this.eventRequest = eventRequest;
    }
}
