package com.picksave.product_service.Controler;

import com.picksave.product_service.Model.FetchJob;
import com.picksave.product_service.Service.FetchJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fetch")
public class FetchJobController {

    private final FetchJobService fetchJobService;

    public FetchJobController(FetchJobService fetchJobService) {
        this.fetchJobService = fetchJobService;
    }

    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    @PostMapping("/start/{shop}")
    public ResponseEntity<FetchJob> startJob(@PathVariable String shop) {
        return ResponseEntity.ok(fetchJobService.startJob(shop));
    }

    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    @PostMapping("/stop/{shop}")
    public ResponseEntity<FetchJob> stopJob(@PathVariable String shop) {
        return ResponseEntity.ok(fetchJobService.stopJob(shop));
    }

    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    @PostMapping("/resume/{shop}")
    public ResponseEntity<FetchJob> resumeJob(@PathVariable String shop) {
        return ResponseEntity.ok(fetchJobService.resumeJob(shop));
    }

    @PreAuthorize("hasAuthority('ADMIN_CREATE')")
    @PostMapping("/complete/{shop}")
    public ResponseEntity<FetchJob> completeJob(@PathVariable String shop) {
        return ResponseEntity.ok(fetchJobService.completeJob(shop));
    }
}
