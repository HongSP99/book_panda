package com.booksajo.bookPanda.category.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "카테고리 요청 DTO")
@Data
public class CategoryDTO {

    @Schema(description = "Id" , defaultValue = "80")
    private Long id;

    @Schema(description = "카테고리 이름" , defaultValue = "신문")
    private String name;
}
