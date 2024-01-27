package com.girok.girokserver.domain.category.vo;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.core.exception.ErrorInfo;
import com.girok.girokserver.domain.category.exception.RootCategoryNameAccessException;
import com.girok.girokserver.domain.category.exception.RootCategoryParentAccessException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
final public class CategoryPath {

    private final List<String> path;

    public CategoryPath(List<String> path) {
        this.path = path;
    }

    public CategoryPath getParentCategoryPath() {
        if (isRoot()) {
            throw new RootCategoryParentAccessException();
        }
        return new CategoryPath(
                new ArrayList<>(path.subList(0, path.size() - 1))
        );
    }

    public String getLastCategoryName() {
        if (isRoot()) {
            throw new RootCategoryNameAccessException();
        }
        return path.get(path.size() - 1);
    }

    public int getPathLength() {
        return path.size();
    }

    public boolean isRoot() {
        return path.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryPath that = (CategoryPath) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
