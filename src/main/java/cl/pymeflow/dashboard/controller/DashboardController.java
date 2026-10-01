package cl.pymeflow.dashboard.controller;

import cl.pymeflow.dashboard.dto.DashboardResponse;
import cl.pymeflow.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> obtenerDashboard() {
        return ResponseEntity.ok(
                dashboardService.obtenerDashboard()
        );
    }
}