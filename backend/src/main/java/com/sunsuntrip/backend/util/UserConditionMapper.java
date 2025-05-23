package com.sunsuntrip.backend.util;

import com.sunsuntrip.backend.domain.Theme;
import com.sunsuntrip.backend.domain.UserCondition;
import com.sunsuntrip.backend.dto.UserConditionRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConditionMapper {

    public static UserCondition toEntity(UserConditionRequestDTO dto, List<Theme> selectedThemes) {
        UserCondition condition = new UserCondition();
        condition.setDays(dto.getDays());
//        condition.setBudget(dto.getBudget());
//        condition.setStartDate(dto.getStartDate());
//        condition.setNumberOfPeople(dto.getNumberOfPeople());
        condition.setThemes(selectedThemes);  // 조회된 Theme 리스트 주입
        return condition;
    }
}
