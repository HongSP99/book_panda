package com.booksajo.bookPanda.category.controller;

import com.booksajo.bookPanda.category.DTO.CategoryDTO;
import com.booksajo.bookPanda.category.domain.Category;
import com.booksajo.bookPanda.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "카테고리 API" , description = "카테고리 API 입니다.")
public class CategoryController {


    private final CategoryService categoryService;



    @Operation(summary = "카테고리 id로 카테고리를 가져올 수 있습니다."
            , description = "url상에 /api/category/카테고리 아이디 형식으로 카테고리를 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 요청 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 찾을 수 없음")
    })
    @GetMapping("/api/category/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@Parameter(description = "카테고리의 아이디 값 (Long)") @PathVariable(name = "categoryId" ) Long categoryId){
        Category category = categoryService.findCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }


    @Operation(summary = "카테고리를 추가합니다."
            , description = "요청의 body에 name: 을 설정하면 해당 카테고리를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 요청 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 찾을 수 없음")
    })
    @PostMapping("/api/category")
    public ResponseEntity<Category> addCategory(@RequestBody CategoryDTO categoryDTO){
            Category category = categoryService.createCategory(categoryDTO.getName());

            return ResponseEntity.ok(category);
    }


    @Operation(summary = "카테고리를 수정합니다."
            , description = "요청의 body에 name: 을 설정하면 해당 카테고리를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 요청 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 찾을 수 없음")
    })
    @PutMapping("/api/category/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable(name = "categoryId") Long categoryId,@RequestBody CategoryDTO categoryDTO){
        Category target = categoryService.updateCategoryName(categoryId , categoryDTO.getName());
        return ResponseEntity.ok(target);
    }


    @Operation(summary = "카테고리를 모두 가져옵니다."
            , description = "카테고리를 모두 가져옵니다.")

    @ApiResponses(
            value = {
                    @ApiResponse(
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array= @ArraySchema(schema = @Schema(implementation = Category.class))
                                    )
                            }
                    )
            }
    )
    @GetMapping("/api/category")
    public ResponseEntity<List<Category>> getAllCategory(){
        List<Category> categories = categoryService.findAllCategory();
        return ResponseEntity.ok(categories);
    }



    @Operation(summary = "카테고리를 삭제 합니다."
            , description = "카테고리를 아이디를 사용해 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 요청 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 찾을 수 없음")
    })
    @DeleteMapping("/api/category/{categoryId}")
    public ResponseEntity<String> deleteCategoryById(@Parameter(description = "카테고리의 아이디 값 (Long)")  @PathVariable(name = "categoryId") Long categoryId){
        categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.ok("삭제 완료");
    }


}
