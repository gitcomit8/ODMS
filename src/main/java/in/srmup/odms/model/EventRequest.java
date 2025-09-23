package in.srmup.odms.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EventRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private LocalDate approvedDate;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @OneToMany(
            mappedBy = "eventRequest",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Participant> participants = new ArrayList<>();

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setEventRequest(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    
    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}
