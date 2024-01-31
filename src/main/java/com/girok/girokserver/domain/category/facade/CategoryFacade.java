package com.girok.girokserver.domain.category.facade;

import com.girok.girokserver.domain.category.controller.dto.CategoryResponseDto;
import com.girok.girokserver.domain.category.controller.mapper.CategoryMapper;
import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.category.facade.dto.CategoryUpdateDto;
import com.girok.girokserver.domain.category.service.CategoryService;
import com.girok.girokserver.domain.category.vo.CategoryPath;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.domain.member.service.MemberService;
import com.girok.girokserver.global.enums.CategoryColor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryFacade {

    private final CategoryService categoryService;
    private final MemberService memberService;

    public List<Category> getCategoriesAsTree(Long memberId) {
        Member member = memberService.getMemberById(memberId);
        return categoryService.getCategoriesAsTree(member);
    }


    @Transactional
    public Long createCategoryByPath(Long memberId, CategoryColor color, CategoryPath path) {
        Member member = memberService.getMemberById(memberId);
        Category category = categoryService.createCategoryByPath(member, color, path);
        return category.getId();
    }

    @Transactional
    public Long createCategoryById(Long memberId, CategoryColor color, Long parentId, String categoryName) {
        Member member = memberService.getMemberById(memberId);
        Category category = categoryService.createCategoryById(member, color, parentId, categoryName);
        return category.getId();
    }

    @Transactional
    public void deleteCategoryById(Long memberId, Long categoryId) {
        categoryService.deleteCategoryById(memberId, categoryId);
    }

    public Long getCategoryIdByPath(Long memberId, CategoryPath path) {
        Member member = memberService.getMemberById(memberId);
        Category category = categoryService.getCategoryByPath(member, path);
        return category.getId();
    }

    @Transactional
    public void updateCategoryInfo(Long memberId, Long categoryId, CategoryUpdateDto categoryUpdateDto) {
        Member member = memberService.getMemberById(memberId);
        categoryService.updateCategoryInfo(member, categoryId, categoryUpdateDto);
    }

    @Transactional
    public void updateCategoryParent(Long memberId, Long categoryId, Long newParentId) {
        Member member = memberService.getMemberById(memberId);
        categoryService.updateCategoryParent(member, categoryId, newParentId);
    }

}
