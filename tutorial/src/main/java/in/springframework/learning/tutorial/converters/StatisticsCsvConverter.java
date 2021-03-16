package in.springframework.learning.tutorial.converters;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import in.springframework.learning.tutorial.pojos.Statistics;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class StatisticsCsvConverter extends AbstractHttpMessageConverter<List<Statistics>> {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    public StatisticsCsvConverter() {
        super(new MediaType("text", "csv", Charset.forName("UTF-8")));
    }
    @Override
    protected boolean supports(Class<?> aClass) {
        List<Statistics> ls = new ArrayList<>();
        return ls.getClass().isAssignableFrom(aClass);
    }

    @SneakyThrows
    @Override
    protected List<Statistics> readInternal(Class<? extends List<Statistics>> aClass,
                                            HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        CSVReader csvReader = new CSVReader(new InputStreamReader(httpInputMessage.getBody()));
        Iterator<String[]> csvri = csvReader.iterator();
        String[] header = csvri.next();
        List<Statistics> statisticsList = new ArrayList<>();
        while(csvri.hasNext()) {
            statisticsList.add(getStatistics(header, csvri.next()));
        }
        csvReader.close();
        return statisticsList;
    }
    private Statistics getStatistics(String[] header, String[] line) throws ParseException {
        Statistics.StatisticsBuilder builder = Statistics.builder();
        for (int i = 0; i < header.length; ++i) {
            if (header[i].equals("id")) {
                builder.id(line[i]);
            }
            else if(header[i].equals("PerformanceRun")) {
                builder.performanceRunId(line[i]);
            }
            else if(header[i].equals("RecordType")) {
                builder.recordType(Statistics.RECORD_TYPE.valueOf(line[i]));
            }
            else if(header[i].equals("OperationType")) {
                builder.operationType(Statistics.OPERATION_TYPE.valueOf(line[i]));
            }
            else if(header[i].equals("Count")) {
                builder.count(Long.parseLong(line[i]));
            }
            else if(header[i].equals("Base")) {
                builder.base(Long.parseLong(line[i]));
            }
            else if(header[i].equals("StartTime")) {
                builder.startTime(simpleDateFormat.parse(line[i]));
            }
            else if(header[i].equals("EndTime")) {
                builder.endTime(simpleDateFormat.parse(line[i]));
            }
            else if(header[i].equals("NanoSecs")) {
                builder.nanoseconds(Long.parseLong(line[i]));
            }
        }
        return builder.build();
    }
    @Override
    protected void writeInternal(List<Statistics> statistics,
                                 HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(httpOutputMessage.getBody()));
        csvWriter.writeNext(new String[] {"id",
                "PerformanceRun",
                "RecordType",
                "OperationType",
                "Count",
                "Base",
                "StartTime",
                "EndTime",
                "NanoSecs"});
        for (Statistics s:statistics) {

            csvWriter.writeNext(new String[] {s.getId(),
                    s.getPerformanceRunId(),
                    s.getRecordType().name(),
                    s.getOperationType().name(),
                    String.valueOf(s.getCount()),
                    String.valueOf(s.getBase()),
                    simpleDateFormat.format(s.getStartTime()),
                    simpleDateFormat.format(s.getEndTime()),
                    String.valueOf(s.getNanoseconds())});
        }
        csvWriter.close();

    }

}
