package com.emtech.dairyapp.bulksms;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/smsTemplates")
public class SMSTemplatesController {
    @Autowired
    private SMSTemplatesRepository smsTemplatesRepository;

    @PostMapping("add")
    public ResponseEntity<?> createNewTemplate(@RequestBody SMSTemplates templates) {
        if (smsTemplatesRepository.findByTemplateName(templates.getTemplateName()).isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Template Name already Exists!"), HttpStatus.BAD_REQUEST);
        } else {
            smsTemplatesRepository.save(templates);
            return new ResponseEntity<>(new MessageResponse("Template Created Successfully!"), HttpStatus.OK);
        }
    }

    @PutMapping("edit")
    public ResponseEntity<?> editTemplate(@RequestBody SMSTemplates templates) {
        smsTemplatesRepository.save(templates);
        return new ResponseEntity<>(new MessageResponse("Template Updated Successfully!"), HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteTemplate(@RequestParam("id") Long id) {
        smsTemplatesRepository.deleteById(id);
        return new ResponseEntity<>(new MessageResponse("Template Deleted Successfully!"), HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<?> getTemplates() {
        return new ResponseEntity<>(smsTemplatesRepository.findTop5ByOrderByIdDesc(), HttpStatus.OK);
    }
}
