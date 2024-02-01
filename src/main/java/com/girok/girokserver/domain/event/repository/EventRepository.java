package com.girok.girokserver.domain.event.repository;

import com.girok.girokserver.domain.event.entity.Event;
import com.girok.girokserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category WHERE e.member = :member AND e.id = :id")
    Optional<Event> findByMemberAndId(@Param("member") Member member, @Param("id") Long id);

    @Query("SELECT e.id FROM Event e WHERE e.member.id = :memberId")
    List<Long> findEventIdsByMemberId(@Param("memberId") Long memberId);

}
