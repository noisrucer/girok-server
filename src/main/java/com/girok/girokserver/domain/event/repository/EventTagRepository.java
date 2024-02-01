package com.girok.girokserver.domain.event.repository;

import com.girok.girokserver.domain.event.entity.EventTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventTagRepository extends JpaRepository<EventTag, Long> {

    @Query("SELECT DISTINCT et.name FROM EventTag et WHERE et.event.id IN :eventIds")
    List<String> findAllDistinctTagNamesByEventIds(@Param("eventIds") List<Long> eventIds);

}
