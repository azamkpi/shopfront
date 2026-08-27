package uz.azam.shopfront.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.azam.shopfront.domain.Product;
import uz.azam.shopfront.domain.ProductImage;
import uz.azam.shopfront.domain.ProductVariant;
import uz.azam.shopfront.repo.CategoryRepository;
import uz.azam.shopfront.repo.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopContext shopContext;

    @Transactional
    public Product create(CaptionParser.ParsedProduct parsed,
                          List<MediaGroupBuffer.PhotoRef> photos) {

        Long shopId = shopContext.shopId();

        Product product = new Product();
        product.setShopId(shopId);
        product.setName(parsed.name());
        product.setDescription(parsed.description());
        product.setNegotiable(parsed.negotiable());

        if (parsed.categorySlug() != null) {
            categoryRepository.findByShopIdAndSlug(shopId, parsed.categorySlug())
                    .ifPresent(c -> product.setCategoryId(c.getId()));
        }

        ProductVariant variant = new ProductVariant();
        variant.setPrice(parsed.price());
        variant.setInStock(true);
        product.addVariant(variant);

        int order = 0;
        for (MediaGroupBuffer.PhotoRef photo : photos) {
            ProductImage image = new ProductImage();
            image.setFileId(photo.fileId());
            image.setFileUniqueId(photo.fileUniqueId());
            image.setSortOrder(order++);
            product.addImage(image);
        }

        return productRepository.save(product);
    }
}
