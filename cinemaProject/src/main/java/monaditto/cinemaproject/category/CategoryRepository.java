package monaditto.cinemaproject.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryName(String categoryName);

    @Query(
            value = "SELECT c.category_id, COUNT(*) AS category_count " +
                    "FROM purchases p " +
                    "JOIN screenings s ON p.screening_id = s.screening_id " +
                    "JOIN movies m ON s.movie_id = m.movie_id " +
                    "JOIN movie_category mc ON m.movie_id = mc.movie_id " +
                    "JOIN categories c ON mc.category_id = c.category_id " +
                    "WHERE p.reservation_status = 'PAID' AND p.user_id = :userId " +
                    "GROUP BY c.category_id " +
                    "ORDER BY category_count DESC " +
                    "LIMIT 1",
            nativeQuery = true)
    List<Object[]> findMostPurchasedCategoryByUserId(@Param("userId") Long userId);

}
