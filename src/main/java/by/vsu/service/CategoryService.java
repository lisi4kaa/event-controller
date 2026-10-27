package by.vsu.service;

import by.vsu.dao.CategoryDao;
import by.vsu.domain.Category;
import by.vsu.dto.CategoryCreateUpdateDto;
import by.vsu.dto.CategoryResponseDto;
import by.vsu.util.TransactionTemplate;

import java.util.*;

public class CategoryService {
    private final TransactionTemplate tt;
    private final CategoryDao categoryDao;

    public CategoryService(TransactionTemplate tt, CategoryDao categoryDao) {
        this.tt = tt;
        this.categoryDao = categoryDao;
    }

    private void validate(CategoryCreateUpdateDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidateException("Категория не может не иметь названия");
        }
        if (dto.getName().length() > 255) {
            throw new ValidateException("Название слишком длинное");
        }
    }

    private Category toDomain(CategoryCreateUpdateDto dto, Category parent) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setParent(parent);
        return category;
    }

    private CategoryResponseDto toResponse(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        } else {
            dto.setParentId(null);
        }
        return dto;
    }

    public CategoryResponseDto create(CategoryCreateUpdateDto dto) {
        if (dto.getId() != null) {
            throw new ValidateException("При создании id должен быть пустым");
        }
        validate(dto);
        return tt.execute(conn -> {
            Category parent = null;
            if (dto.getParentId() != null) {
                parent = categoryDao.findCategoryById(dto.getParentId(), conn)
                        .orElseThrow(() -> new ServiceException("Родительская категория не найдена: " + dto.getParentId()));
            }
            Category category = toDomain(dto, parent);
            categoryDao.save(category, conn);

            return toResponse(category);
        });
    }

    public CategoryResponseDto update(CategoryCreateUpdateDto dto) {
        if (dto.getId() == null) {
            throw new ValidateException("При обновлении id не может быть пустым");
        }
        validate(dto);
        return tt.execute(conn -> {
            Category category = categoryDao.findCategoryById(dto.getId(), conn)
                    .orElseThrow(() -> new ServiceException("Категория не найдена: " + dto.getId()));

            Category parent = null;
            if (dto.getParentId() != null) {
                if (dto.getParentId().equals(dto.getId())) {
                    throw new ServiceException("Категория не может быть родителем самой себя");
                }
                parent = categoryDao.findCategoryById(dto.getParentId(), conn)
                        .orElseThrow(() -> new ServiceException("Родительская категория не найдена: " + dto.getParentId()));

                Map<Long, Long> parentById = new HashMap<>();
                for (Category c : categoryDao.findAll(conn)) {
                    parentById.put(c.getId(), c.getParent() != null ? c.getParent().getId() : null);
                }
                Long current = parent.getId();
                while (current != null) {
                    if (current.equals(dto.getId())) {
                        throw new ServiceException("Нельзя переместить категорию в собственного потомка");
                    }
                    current = parentById.get(current);
                }
            }

            category.setName(dto.getName());
            category.setParent(parent);
            if (!categoryDao.update(category, conn)) {
                throw new ServiceException("Не удалось обновить категорию: " + dto.getId());
            }
            return toResponse(category);
        });
    }

    public boolean delete(Long id) {
        return tt.execute(conn -> {
            Category category = categoryDao.findCategoryById(id, conn)
                    .orElseThrow(() -> new ServiceException("Категория не найдена: " + id));

            boolean hasChildren = categoryDao.findAll(conn).stream()
                    .anyMatch(c -> c.getParent() != null && id.equals(c.getParent().getId()));
            if (hasChildren) {
                throw new ServiceException("Нельзя удалить категорию с подкатегориями");
            }

            return categoryDao.delete(id, conn);
        });

    }

    public CategoryResponseDto findById(Long id) {
        return tt.execute(conn -> {
            return categoryDao.findCategoryById(id, conn)
                    .map(this::toResponse)
                    .orElseThrow(() -> new ServiceException("Категория не найдена: " + id));
        });
    }

    public List<CategoryResponseDto> findAll() {
        return tt.execute(conn -> {
            List<Category> categories = categoryDao.findAll(conn);
            return categories.stream().map(this::toResponse).toList();
        });
    }

    public List<CategoryResponseDto> getTree() {
        return tt.execute(conn -> {
            List<Category> categories = categoryDao.findAll(conn);

            Map<Long, CategoryResponseDto> map = new HashMap<>();
            for (Category c : categories) {
                CategoryResponseDto dto = toResponse(c);
                map.put(dto.getId(), dto);
            }
            List<CategoryResponseDto> tree = new ArrayList<>();
            for (CategoryResponseDto c : map.values()) {
                if (c.getParentId() == null) {
                    tree.add(c);
                } else {
                    CategoryResponseDto parent = map.get(c.getParentId());
                    if (parent != null) {
                        parent.getChildren().add(c);
                    } else {
                        tree.add(c);
                    }
                }
            }
            return tree;
        });
    }

    public boolean move(Long id, Long newParentId) {
        return tt.execute(conn -> {
            Category category = categoryDao.findCategoryById(id, conn)
                    .orElseThrow(() -> new ServiceException("Категория " + id + " не найдена"));

            Category newParent = null;
            if (newParentId != null) {
                newParent = categoryDao.findCategoryById(newParentId, conn)
                        .orElseThrow(() -> new ServiceException("Категория " + newParentId + " не найдена"));

                Map<Long, Long> parentById = new HashMap<>();
                for (Category c : categoryDao.findAll(conn)) {
                    parentById.put(c.getId(), c.getParent() != null ? c.getParent().getId() : null);
                }
                Long current = newParent.getId();
                while (current != null) {
                    if (current.equals(id)) {
                        throw new ServiceException("Нельзя переместить категорию в саму себя или в собственного потомка");
                    }
                    current = parentById.get(current);
                }
            }
            category.setParent(newParent);
            return categoryDao.update(category, conn);
        });
    }

    public List<CategoryResponseDto> getSubtree(Long id) {
        return tt.execute(conn -> {
            categoryDao.findCategoryById(id, conn)
                    .orElseThrow(() -> new ServiceException("Категория не найдена: " + id));

            List<Category> all = categoryDao.findAll(conn);

            Map<Long, List<Category>> childrenByParent = new HashMap<>();
            Map<Long, Category> byId = new HashMap<>();
            for (Category c : all) {
                byId.put(c.getId(), c);
                Long parentId = c.getParent() != null ? c.getParent().getId() : null;
                if (parentId != null) {
                    childrenByParent.computeIfAbsent(parentId, k -> new ArrayList<>()).add(c);
                }
            }

            List<CategoryResponseDto> result = new ArrayList<>();
            Deque<Long> queue = new ArrayDeque<>();
            queue.add(id);

            while (!queue.isEmpty()) {
                Long currentId = queue.poll();
                Category current = byId.get(currentId);
                if (current == null) continue;
                result.add(toResponse(current));
                for (Category child : childrenByParent.getOrDefault(currentId, List.of())) {
                    queue.add(child.getId());
                }
            }
            return result;
        });
    }
}
