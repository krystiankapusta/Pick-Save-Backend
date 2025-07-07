package Pick_Save.Product_Service.Controler;

import Pick_Save.Product_Service.DataTransferObject.ProductDTO;
import Pick_Save.Product_Service.Model.Product;
import Pick_Save.Product_Service.Responses.MessageResponse;
import Pick_Save.Product_Service.Responses.ProductResponse;
import Pick_Save.Product_Service.Service.ProductService;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO){
        try {
            logger.info("ProductController contain of productDTO -> {}", productDTO);
            ProductResponse response = productService.addProduct(productDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO){
        try {
            logger.info("ProductController update endpoint contain productDTO -> {}", productDTO);
            ProductResponse response = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/display-all")
    public ResponseEntity<?> displayProducts() {
        try {
            List<ProductResponse> products = productService.getAllProducts();
            return ResponseEntity.ok(products);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to fetch products list"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> productDetails(@PathVariable Long id){
        try {
            ProductResponse response = productService.getProduct(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Failed to fetch product details"));
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(new MessageResponse("Product successfully deleted!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Product delete failed"));
        }
    }
}
