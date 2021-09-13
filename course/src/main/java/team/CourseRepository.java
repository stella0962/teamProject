package team;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="courses", path="courses")
public interface CourseRepository extends PagingAndSortingRepository<Course, Long>{


}
