package cl.pymeflow.plan.controller;

import cl.pymeflow.plan.dto.MiPlanResponse;
import cl.pymeflow.plan.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mi-plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(
            PlanService planService
    ) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<MiPlanResponse> obtenerMiPlan() {

        return ResponseEntity.ok(
                planService.obtenerMiPlan()
        );
    }
}