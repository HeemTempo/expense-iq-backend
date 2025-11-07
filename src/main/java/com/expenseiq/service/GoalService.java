package com.expenseiq.service;

import com.expenseiq.dto.request.ContributeGoalRequest;
import com.expenseiq.dto.request.GoalRequest;
import com.expenseiq.dto.response.GoalResponse;

import java.util.List;

public interface GoalService {
    GoalResponse createGoal(Long userId, GoalRequest request);
    GoalResponse updateGoal(Long userId, Long id, GoalRequest request);
    void deleteGoal(Long userId, Long id);
    GoalResponse getGoalById(Long userId, Long id);
    List<GoalResponse> getAllGoals(Long userId);
    List<GoalResponse> getActiveGoals(Long userId);
    GoalResponse contributeToGoal(Long userId, Long id, ContributeGoalRequest request);
}
