package in.springframework.learning.tutorial.pojos;

import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

@Data
public class CourseOffered {
    @Id
    private String id;
    private String courseId;
    private String facultyId;
    private Date startDate;
    private Date endDate;
}
