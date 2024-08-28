package com.booksajo.bookPanda.category.service;


import com.booksajo.bookPanda.category.domain.Category;
import com.booksajo.bookPanda.category.repository.CategoryRepository;
import com.booksajo.bookPanda.cart.exception.errorCode.CategoryErrorCode;
import com.booksajo.bookPanda.cart.exception.exception.CategoryException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    @Transactional
    public Category createCategory(String name){
        Category category = Category.builder().name(name).build();
        return categoryRepository.save(category);
    }



    @Transactional(readOnly = true)
    public List<Category> findAllCategory(){
        return categoryRepository.findAll();
    }


    @Transactional(readOnly = true)
    public Category findCategoryById(Long id){
        return categoryRepository.findById(id).orElseThrow(()-> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Category findCategoryByName(String name){
        return categoryRepository.findByName(name).orElseThrow(()-> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
    }


    @Transactional
    public Category updateCategoryName(Long id, String name){
        Category category = categoryRepository.findById(id).orElseThrow(()-> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
        category.setName(name);
        return category;
    }



    @Transactional
    public void deleteCategoryById(Long id){
        categoryRepository.deleteById(id);
    }


}
