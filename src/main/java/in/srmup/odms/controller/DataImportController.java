package in.srmup.odms.controller;

import in.srmup.odms.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public String showImportPage() {
        return "admin-import";
    }

    @PostMapping("/students")
    public String handleStudentImport(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/admin/import";
        }

        try {
            int importedCount = dataImportService.importStudentsFromCsv(file);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully imported " + importedCount + " students.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred during file import: " + e.getMessage());
        }

        return "redirect:/admin/import";
    }
}