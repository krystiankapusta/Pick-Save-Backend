package Pick_Save.Product_Service.Responses;

import lombok.Data;

@Data
public class CategoryResponse {
    private String categoryName;

    public CategoryResponse(String categoryName){
        this.categoryName = categoryName;
    }
}
