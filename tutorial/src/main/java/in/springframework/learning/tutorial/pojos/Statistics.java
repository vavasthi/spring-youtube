package in.springframework.learning.tutorial.pojos;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.Id;
import java.util.Date;

@Data
@Builder
public class Statistics {

    public enum RECORD_TYPE {
        STUDENT,
        ENROLMENT
    }
    public enum OPERATION_TYPE {
        INSERT,
        UPDATE,
        QUERY_IN_UNINDEXED,
        QUERY_UNINDEXED,
        QUERY,
        QUERY_IN_ID,
        QUERY_INDEXED,
        QUERY_IN_INDEXED;
    }
    @Id
    private String id;
    private String performanceRunId;
    private RECORD_TYPE recordType;
    private OPERATION_TYPE operationType;
    private long count;
    private long base;
    private Long existingRecords;
    @Indexed
    private Date startTime;
    @Indexed
    private Date endTime;
    private Long milliseconds;
}
