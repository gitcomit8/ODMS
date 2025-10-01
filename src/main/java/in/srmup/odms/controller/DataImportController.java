package in.srmup.odms.controller;

import in.srmup.odms.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/import")
public class DataImportController {

    @Autowired
    private DataImportService dataImportService;

    @GetMapping
    public String showImportPage(Model model) {
        return "admin-import";
    }

    @PostMapping("/students")
    public String handleStudentImport(@RequestParam("file") MultipartFile file, 
                                    @RequestParam(value = "clearData", required = false) boolean clearData,
                                    RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/admin/import";
        }

        try {
            if (clearData) {
                dataImportService.clearAllStudentData();
            }
            int importedCount = dataImportService.importStudentsFromCsv(file);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Successfully imported " + importedCount + " students." + 
                (clearData ? " Previous data was cleared." : ""));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "An error occurred during student import: " + e.getMessage());
        }

        return "redirect:/admin/import";
    }

    @PostMapping("/faculty")
    public String handleFacultyImport(@RequestParam("file") MultipartFile file, 
                                    @RequestParam(value = "clearData", required = false) boolean clearData,
                                    RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/admin/import";
        }

        try {
            if (clearData) {
                dataImportService.clearAllFacultyData();
            }
            int importedCount = dataImportService.importFacultyFromCsv(file);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Successfully imported " + importedCount + " faculty members." + 
                (clearData ? " Previous data was cleared." : ""));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "An error occurred during faculty import: " + e.getMessage());
        }

        return "redirect:/admin/import";
    }
}