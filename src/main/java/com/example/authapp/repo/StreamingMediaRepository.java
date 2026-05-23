package com.example.authapp.repo;

import com.example.authapp.model.StreamingMedia;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StreamingMediaRepository extends JpaRepository<StreamingMedia, Long> {
    List<StreamingMedia> findTop8ByFeaturedTrueOrderByViewsDesc();
    List<StreamingMedia> findTop10ByTopTenTrueOrderByViewsDesc();
    List<StreamingMedia> findTop8ByOrderByCreatedAtDesc();
    List<StreamingMedia> findTop8ByOrderByRatingDesc();
    List<StreamingMedia> findByGenresContainingIgnoreCase(String genre);
    List<StreamingMedia> findTop5ByTitleContainingIgnoreCaseOrderByViewsDesc(String title);

    @Query("""
            select distinct m from StreamingMedia m
            left join m.genres g
            left join m.castMembers c
            where (:q is null or :q = '' or lower(m.title) like lower(concat('%', :q, '%'))
                or lower(m.director) like lower(concat('%', :q, '%'))
                or lower(c) like lower(concat('%', :q, '%')))
            and (:genre is null or :genre = '' or lower(g) = lower(:genre))
            and (:type is null or :type = '' or lower(m.type) = lower(:type))
            and (:year = 0 or m.releaseYear = :year)
            order by m.views desc
            """)
    List<StreamingMedia> search(@Param("q") String q,
                                @Param("genre") String genre,
                                @Param("type") String type,
                                @Param("year") int year);

    @Query("select distinct g from StreamingMedia m join m.genres g order by g")
    List<String> findAllGenres();

    @Query("select m from StreamingMedia m order by m.views desc")
    List<StreamingMedia> mostWatched(Pageable pageable);
}
