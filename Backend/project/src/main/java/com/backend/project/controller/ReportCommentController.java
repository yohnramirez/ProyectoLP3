package com.backend.project.controller;

import com.backend.project.dto.CreateRequestReportDto;
import com.backend.project.dto.ReportCommentDto;
import com.backend.project.dto.ReportDto;
import com.backend.project.logic.ReportCommentLogic;
import com.backend.project.logic.ReportLogic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports-comment")
public class ReportCommentController {

    /**
     * Instance of the logic layer
     */
    private final ReportCommentLogic reportCommentLogic;

    /**
     * Constructor
     * @param reportCommentLogic instance of the logic layer
     */
    public ReportCommentController(ReportCommentLogic reportCommentLogic) {
        this.reportCommentLogic = reportCommentLogic;
    }

    /**
     * Create report comment
     * @param data data info
     * @return ReportDto
     */
    @PostMapping("/create")
    public ResponseEntity<ReportCommentDto> create(@RequestBody ReportCommentDto data) {
        ReportCommentDto createComment = this.reportCommentLogic.createComment(data);
        return ResponseEntity.ok(createComment);
    }

    /**
     * Get comments by report
     * @param id report id
     * @return ReportCommentDto list
     */
    @GetMapping("/report/{id}")
    public ResponseEntity<List<ReportCommentDto>> getCommentsByReportId(@PathVariable Long id) {
        List<ReportCommentDto> commentsByReport = this.reportCommentLogic.getCommentsByReportId(id);
        return ResponseEntity.ok(commentsByReport);
    }

    /**
     * delete comment report
     * @param id comment report id
     * @return String
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        if (id > 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid id");

        this.reportCommentLogic.deleteComment(id);

        return ResponseEntity.ok("Comment with id " + id + "was deleted successfully");
    }
}
