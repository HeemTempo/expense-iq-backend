package com.expenseiq.service.impl;

import com.expenseiq.dto.request.ContributeGoalRequest;
import com.expenseiq.dto.request.GoalRequest;
import com.expenseiq.dto.response.GoalResponse;
import com.expenseiq.entity.Goal;
import com.expenseiq.entity.User;
import com.expenseiq.exception.ResourceNotFoundException;
import com.expenseiq.repository.GoalRepository;
import com.expenseiq.repository.UserRepository;
import com.expenseiq.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GoalResponse createGoal(Long userId, GoalRequest request) {
        User user = getUserOrThrow(userId);

        Goal goal = Goal.builder()
                .user(user)
                .name(request.getName())
                .targetAmount(request.getTargetAmount())
                .currentAmount(BigDecimal.ZERO)
                .deadline(request.getDeadline())
                .icon(request.getIcon())
                .completed(false)
                .build();

        goal = goalRepository.save(goal);
        return mapToResponse(goal);
    }

    @Override
    @Transactional
    public GoalResponse updateGoal(Long userId, Long id, GoalRequest request) {
        Goal goal = getGoalOrThrow(id, userId);

        goal.setName(request.getName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setDeadline(request.getDeadline());
        goal.setIcon(request.getIcon());

        // Check if goal is completed
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setCompleted(true);
        }

        goal = goalRepository.save(goal);
        return mapToResponse(goal);
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long id) {
        Goal goal = getGoalOrThrow(id, userId);
        goalRepository.delete(goal);
    }

    @Override
    public GoalResponse getGoalById(Long userId, Long id) {
        Goal goal = getGoalOrThrow(id, userId);
        return mapToResponse(goal);
    }

    @Override
    public List<GoalResponse> getAllGoals(Long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId);
        return goals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GoalResponse> getActiveGoals(Long userId) {
        List<Goal> goals = goalRepository.findByUserIdAndCompletedFalse(userId);
        return goals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GoalResponse contributeToGoal(Long userId, Long id, ContributeGoalRequest request) {
        Goal goal = getGoalOrThrow(id, userId);

        BigDecimal newAmount = goal.getCurrentAmount().add(request.getAmount());
        goal.setCurrentAmount(newAmount);

        // Check if goal is completed
        if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setCompleted(true);
        }

        goal = goalRepository.save(goal);
        return mapToResponse(goal);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Goal getGoalOrThrow(Long id, Long userId) {
        return goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    }

    private GoalResponse mapToResponse(Goal goal) {
        Double progressPercentage = goal.getCurrentAmount()
                .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        return GoalResponse.builder()
                .id(goal.getId())
                .name(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .deadline(goal.getDeadline())
                .icon(goal.getIcon())
                .completed(goal.getCompleted())
                .progressPercentage(progressPercentage)
                .build();
    }
}
