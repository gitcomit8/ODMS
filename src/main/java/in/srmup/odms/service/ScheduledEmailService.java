package in.srmup.odms.service;

import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.FacultyMaster;
import in.srmup.odms.model.Participant;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import in.srmup.odms.repository.FacultyMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScheduledEmailService {

    @Autowired
    private EventRequestRepository eventRequestRepository;
    @Autowired
    private FacultyMasterRepository facultyMasterRepository;
    @Autowired
    private JavaMailSender mailSender;

    // Cron Expression: second, minute, hour, day, month, weekday
    // This runs at 3:00 PM (15:00) every day in the specified timezone.
    @Scheduled(cron = "0 0 15 * * *", zone = "Asia/Kolkata")
    public void sendDailyOdDigest() {
        System.out.println("Running daily OD digest job at 3 PM IST...");

        // 1. Find all requests approved today
        // Note: You will need to add a new 'approvedDate' field to your EventRequest entity
        // and a corresponding 'findByApprovedDate' method to your repository.
        List<EventRequest> approvedToday = eventRequestRepository.findByStatusAndApprovedDateAndIsHiddenFalse(RequestStatus.APPROVED, LocalDate.now());

        if (approvedToday.isEmpty()) {
            System.out.println("No new ODs approved today. No emails sent.");
            return;
        }

        // 2. Consolidate all participants by their class teacher
        Map<FacultyMaster, List<Participant>> notifications = approvedToday.stream()
                .flatMap(request -> request.getParticipants().stream()) // Get a single stream of all participants
                .collect(Collectors.groupingBy(
                        p -> facultyMasterRepository.findByBranchAndSection(p.getBranch(), p.getSection()).orElse(null)
                ));

        // 3. Send one email to each faculty member
        for (Map.Entry<FacultyMaster, List<Participant>> entry : notifications.entrySet()) {
            if (entry.getKey() != null) {
                sendDigestEmail(entry.getKey(), entry.getValue());
            }
        }
    }

    private void sendDigestEmail(FacultyMaster faculty, List<Participant> students) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(faculty.getFacultyEmail());
        message.setSubject("Daily On-Duty Student Digest");

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear ").append(faculty.getFacultyName()).append(",\n\n");
        emailBody.append("This is your daily digest. The following students from your class (")
                .append(faculty.getBranch()).append(" - ").append(faculty.getSection())
                .append(") have been approved for On-Duty leave today:\n\n");

        // Group students by the event they are in
        Map<String, List<Participant>> studentsByEvent = students.stream()
                .collect(Collectors.groupingBy(p -> p.getEventRequest().getEventName()));

        for (Map.Entry<String, List<Participant>> eventEntry : studentsByEvent.entrySet()) {
            emailBody.append("Event: '").append(eventEntry.getKey()).append("'\n");
            for (Participant student : eventEntry.getValue()) {
                emailBody.append("- ").append(student.getName()).append(" (").append(student.getRegNo()).append(")\n");
            }
            emailBody.append("\n");
        }

        emailBody.append("Thank you,\nOD Request System");

        message.setText(emailBody.toString());
        mailSender.send(message);
        System.out.println("Sent daily digest to: " + faculty.getFacultyEmail());
    }
}