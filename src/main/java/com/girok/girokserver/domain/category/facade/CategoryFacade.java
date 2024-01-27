package com.girok.girokserver.domain.category.facade;

import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.category.service.CategoryService;
import com.girok.girokserver.domain.category.vo.CategoryPath;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.domain.member.service.MemberService;
import com.girok.girokserver.global.enums.CategoryColor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryFacade {

    private final CategoryService categoryService;
    private final MemberService memberService;

    @Transactional
    public Long createCategory(Long userId, CategoryColor color, CategoryPath path) {
        Member member = memberService.findMemberById(userId);
        System.out.println("A");
        Category category = categoryService.createCategory(member, color, path);
        return category.getId();
    }
}
