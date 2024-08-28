package com.booksajo.bookPanda.category.service;

import com.booksajo.bookPanda.category.domain.Category;
import com.booksajo.bookPanda.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 테스트(){

        assertTrue(categoryRepository != null);

    }


    @Test
    void 카테코리_생성(){
        //given
        String name = "기술 서적";
        Category category = new Category(1L,name);
        when(categoryRepository.save(any())).thenReturn(category);

        //when
        Category result = categoryService.createCategory(name);

        //then
        assertEquals(category , result);
    }






}