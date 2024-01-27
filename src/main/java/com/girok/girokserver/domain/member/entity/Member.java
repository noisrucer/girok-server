package com.girok.girokserver.domain.member.entity;

import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.global.baseentity.AuditBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Entity(name = "member")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Member extends AuditBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @OneToMany(mappedBy = "member")
    private List<Category> categories;

    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }

    // Relation methods

    /**
     * Add a category
     */
    public void addCategory(Category category) {
        this.categories.add(category);
        category.setMember(this);
    }
}
