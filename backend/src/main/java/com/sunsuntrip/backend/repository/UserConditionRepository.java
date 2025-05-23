package com.sunsuntrip.backend.repository;

import com.sunsuntrip.backend.domain.UserCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConditionRepository extends JpaRepository<UserCondition, Long> {
    // 필요 시 사용자 조건에 따른 조회 기능을 여기에 추가할 수 있습니다.
}
