package in.srmup.odms.controller;


import in.srmup.odms.model.EventRequest;
import in.srmup.odms.model.Participant;
import in.srmup.odms.model.RequestStatus;
import in.srmup.odms.repository.EventRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/event-requests")
public class EventRequestController {

    @Autowired
    private EventRequestRepository eventRequestRepository;

    @GetMapping("/new")
    public String showRequestForm(Model model) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.addParticipant(new Participant());
        model.addAttribute("eventRequest", eventRequest);
        return "event-request-form";
    }

    @PostMapping("/submit")
    public String submitRequestForm(@ModelAttribute EventRequest eventRequest) {
        eventRequest.setStatus(RequestStatus.SUBMITTED);
        for (Participant p : eventRequest.getParticipants()) {
            p.setEventRequest(eventRequest);
        }
        eventRequestRepository.save(eventRequest);
        return "redirect:/event-requests/success";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success-page";
    }
}