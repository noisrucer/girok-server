package com.girok.girokserver.domain.category.repository;

import com.girok.girokserver.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByParentAndName(Category parentCategory, String name);

}
