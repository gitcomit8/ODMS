package in.srmup.odms.controller;

import in.srmup.odms.model.ODRequest;
import in.srmup.odms.service.ODRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/od-requests")
public class ODRequestController {
    private final ODRequestService odRequestService;

    @Autowired
    public ODRequestController(ODRequestService odRequestService) {
        this.odRequestService = odRequestService;
    }

    //Method to display form
    @GetMapping("/new")
    public String showRequestForm(Model model) {
        //Empty ODRequest object for the form to bind to
        model.addAttribute("odRequest", new ODRequest());
        return "od-request-form"; //HTML File is returned
    }

    @PostMapping("/submit")
    public String submitRequestForm(@ModelAttribute ODRequest odRequest) {
        odRequestService.createOdRequest(odRequest);
        return "redirect:/od-requests/success";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "success-page";
    }
}
