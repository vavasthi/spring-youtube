package in.springframework.learning.tutorial.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document
@CompoundIndexes({
        @CompoundIndex(name = "course_student_id", def = "{'courseId':1, 'studentId':2}")
})
public class CoursesEnrolled {
    public enum Status {
        Enrolled,
        Completed,
        Dropped
    }
    @Id
    private String id;
    private String courseId;
    private String courseOfferedId;
    private String studentId;
    private Status status;
}
