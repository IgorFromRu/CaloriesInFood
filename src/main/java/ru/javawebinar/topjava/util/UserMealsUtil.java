package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles

        List<UserMealWithExcess> userMealWithExcessList = new ArrayList<>(); // вся еда с полем избытка
        Map<LocalDate, Integer> contCalories = new HashMap<>(); // дата и количество калорий за день
        List<UserMealWithExcess> userForReturn = new ArrayList<>(); // лист для возврата

        // добавить в Map по ключу даты по значению плюсуем калории
        for (UserMeal userMeal : meals) {
            if (contCalories.containsKey(userMeal.getDateTime().toLocalDate())) {
                Integer count = userMeal.getCalories() + contCalories.get(userMeal.getDateTime().toLocalDate());
                contCalories.put(userMeal.getDateTime().toLocalDate(), count);
            } else {
                contCalories.put(userMeal.getDateTime().toLocalDate(), userMeal.getCalories());
            }
        }
        // переформатируем UserMeal в UserMealWitchExcess, добавляем в лист userMealWithExcessList
        for (Map.Entry<LocalDate, Integer> entry : contCalories.entrySet()) {
            for (UserMeal meal : meals) {
                if (entry.getKey().equals(meal.getDateTime().toLocalDate())) {
                    boolean excess = false;
                    if (entry.getValue() > caloriesPerDay) excess = true;
                    userMealWithExcessList.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
                }
            }
        }
        // сортируем userMealWithExcessList по входящему времени и записываем userForReturn
        for (UserMealWithExcess user : userMealWithExcessList) {
            if (user.getDateTime().toLocalTime().isAfter(startTime) && user.getDateTime().toLocalTime().isBefore(endTime)) {
                userForReturn.add(user);
            }
        }
        return userForReturn;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream().collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream()
                .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> new UserMealWithExcess(m.getDateTime(), m.getDescription(), m.getCalories(),
                        caloriesSumByDate.get(m.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }
}
