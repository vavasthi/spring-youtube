package in.springframework.learning.tutorial.pojos;

import lombok.Data;

import javax.persistence.Id;

@Data
public class Course {
    @Id
    private String id;
    private String code;
    private String name;
    private Integer creditHours;
}
