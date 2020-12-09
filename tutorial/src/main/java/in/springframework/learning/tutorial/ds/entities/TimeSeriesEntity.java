package in.springframework.learning.tutorial.ds.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "time_series", uniqueConstraints =
        {
                @UniqueConstraint(name = "uq_timestamp_key", columnNames = {"ts", "key"})
        })
public class TimeSeriesEntity {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "ts")
    private Date timestamp;
    private String key;
    private String value;
}
