package in.srmup.odms.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "od_requests")
public class ODRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String applicantName;
    private String registrationNumber;
    private String eventName;
    private LocalDate eventDate;
    private LocalTime fromTime;
    private Integer academicYear;
    private String branch;
    private String section;
    private String department;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    //TODO: Add getters and setters
}