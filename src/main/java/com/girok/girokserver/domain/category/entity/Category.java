package com.girok.girokserver.domain.category.entity;

import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.global.baseentity.AuditBase;
import com.girok.girokserver.global.enums.CategoryColor;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parent_id", "name"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Category extends AuditBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    private CategoryColor color;

    // Constructor methods
    public static Category createCategory(Member member, Category parentCategory, String name, CategoryColor color) {
        Category category = Category.builder()
                .member(member)
                .parent(parentCategory) // parent can be null
                .name(name)
                .color(color)
                .build();

        member.addCategory(category); // TODO: member.addCategory(category)하면 category.assignMember() 중복
        if (parentCategory != null) {
            parentCategory.addChildCategory(category);
        }
        return category;
    }

    // Relation methods
    /**
     * Add a child category (<->)
     */
    public void assignChildren(List<Category> children) {
        this.children.clear();
        this.children.addAll(children);
        for (Category child : children) {
            child.parent = this;
        }
    }


    public void addChildCategory(Category childCategory) {
        this.children.add(childCategory);
        childCategory.parent = this;
    }

    public void assignParent(Category parentCategory) {
        this.parent = parentCategory;
        if (parentCategory != null) {
            parentCategory.getChildren().add(this);
        }
    }

    // Business Logics
    public Long getParentId() {
        return this.parent == null ? null : this.parent.getId();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(CategoryColor color) {
        this.color = color;
    }


}
