package com.expenseiq.controller;

import com.expenseiq.dto.request.ContributeGoalRequest;
import com.expenseiq.dto.request.GoalRequest;
import com.expenseiq.dto.response.ApiResponse;
import com.expenseiq.dto.response.GoalResponse;
import com.expenseiq.security.SecurityUser;
import com.expenseiq.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @AuthenticationPrincipal SecurityUser currentUser,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse response = goalService.createGoal(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Goal created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request) {
        GoalResponse response = goalService.updateGoal(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Goal updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        goalService.deleteGoal(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoal(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id) {
        GoalResponse response = goalService.getGoalById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Goal retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getAllGoals(
            @AuthenticationPrincipal SecurityUser currentUser,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<GoalResponse> goals;
        if (activeOnly) {
            goals = goalService.getActiveGoals(currentUser.getId());
        } else {
            goals = goalService.getAllGoals(currentUser.getId());
        }
        return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goals));
    }

    @PostMapping("/{id}/contribute")
    public ResponseEntity<ApiResponse<GoalResponse>> contributeToGoal(
            @AuthenticationPrincipal SecurityUser currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ContributeGoalRequest request) {
        GoalResponse response = goalService.contributeToGoal(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Contribution added successfully", response));
    }
}
