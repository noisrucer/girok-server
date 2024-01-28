package com.girok.girokserver.domain.category.repository;

import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.children WHERE c.member = :member")
    List<Category> findAllWithChildrenByMember(@Param("member") Member member);

    Optional<Category> findByMemberAndParentAndName(Member member, Category parentCategory, String name);

    Optional<Category> findByMemberAndParent_IdAndName(Member member, Long parentId, String name);

    Optional<Category> findByMemberAndId(Member member, Long categoryId);

}
