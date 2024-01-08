package com.uspray.uspray.service;

import com.uspray.uspray.DTO.category.CategoryRequestDto;
import com.uspray.uspray.DTO.category.CategoryResponseDto;
import com.uspray.uspray.Enums.CategoryType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.uspray.uspray.infrastructure.PrayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PrayRepository prayRepository;
    private final PrayFacade prayFacade;

    private static int getNewOrder(int index, List<Category> categories, Category category) {
        validateIndex(index, categories.size());
        Category targetPosition = categories.get(index - 1);
        if (targetPosition.equals(category)) { // 원하는 위치가 지금 내 위치와 같으면
            return targetPosition.getOrder();
        }
        if (index == 1) { // 원하는 위치가 첫번째면
            return getFirstPositionOrder(categories);
        }
        if (index == categories.size()) { // 원하는 위치가 마지막이면
            return getLastPositionOrder(targetPosition);
        }
        return getMiddlePositionOrder(targetPosition, categories.get(index - 2));
    }

    private static void validateIndex(int index, int size) {
        if (index < 1 || index > size) {
            throw new NotFoundException(ErrorStatus.INDEX_OUT_OF_BOUND_EXCEPTION,
                ErrorStatus.INDEX_OUT_OF_BOUND_EXCEPTION.getMessage());
        }
    }

    private static int getFirstPositionOrder(List<Category> categories) {
        return categories.get(0).getOrder() / 2;
    }

    private static int getLastPositionOrder(Category prevCategory) {
        return prevCategory.getOrder() + 1024;
    }

    private static int getMiddlePositionOrder(Category targetCategory, Category prevCategory) {
        // 원하는 위치가 중간이면 원하는 자리와 그 앞자리 중간으로
        System.out.println("prevCategory.getOrder() : " + targetCategory.getOrder());
        System.out.println("nextCategory.getOrder() : " + prevCategory.getOrder());
        return (targetCategory.getOrder() + prevCategory.getOrder()) / 2;
    }

    public CategoryResponseDto createCategory(String username,
        CategoryRequestDto categoryRequestDto) {
        Member member = memberRepository.getMemberByUserId(username);

        int maxCategoryOrder = categoryRepository.checkDuplicateAndReturnMaxOrder(
            categoryRequestDto.getName(), member, categoryRequestDto.getType());
        Category category = categoryRequestDto.toEntity(member, maxCategoryOrder + 1024);
        categoryRepository.save(category);
        return CategoryResponseDto.of(category);
    }

    public CategoryResponseDto deleteCategory(String username, Long categoryId) {
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId,
            memberRepository.getMemberByUserId(username));
        prayRepository.findByCategoryId(category.getId()).forEach(prayFacade::convertPrayToHistory);
        categoryRepository.delete(category);
        return CategoryResponseDto.of(category);
    }

    public CategoryResponseDto updateCategory(String username, Long categoryId,
        CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId,
            memberRepository.getMemberByUserId(username));
        category.update(categoryRequestDto);
        return CategoryResponseDto.of(category);
    }

    public CategoryResponseDto getCategory(String username, Long categoryId) {
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId,
            memberRepository.getMemberByUserId(username));
        return CategoryResponseDto.of(category);
    }

    public CategoryResponseDto updateCategoryOrder(String username, Long categoryId, int index) {
        Member member = memberRepository.getMemberByUserId(username);
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId, member);
        List<Category> categories = categoryRepository.getCategoriesByMemberAndCategoryTypeOrderByOrder(
            member, category.getCategoryType());

        int newOrder = getNewOrder(index, categories, category);

        category.updateOrder(newOrder);

        categoryRepository.save(category);

        return CategoryResponseDto.of(category);
    }

    public List<CategoryResponseDto> getCategoryList(String username, String categoryType) {
        Member member = memberRepository.getMemberByUserId(username);
        CategoryType convertCategoryType = CategoryType.valueOf(categoryType.toUpperCase());
        List<Category> categories = categoryRepository.getCategoriesByMemberAndCategoryTypeOrderByOrder(
            member, convertCategoryType);
        return categories.stream()
            .map(CategoryResponseDto::of)
            .collect(Collectors.toList());
    }
}
