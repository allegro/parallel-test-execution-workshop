import org.apache.commons.cli.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.IntSummaryStatistics;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Stats {

    public static void main(String[] args) throws IOException {
        CommandLine commandLine = parseArgs(args);
        if (commandLine != null) {
            File file = new File(commandLine.getOptionValue("stats_file"));
            if (!file.exists()) {
                System.out.println(file + " not found.");
                return;
            }
            printStats(file, commandLine.getOptionValue("task_name"));
        }
    }

    private static CommandLine parseArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        final Options options = new Options();
        HelpFormatter formatter = new HelpFormatter();
        Option statsFile = Option.builder("stats_file")
                .argName("stats_file")
                .hasArg()
                .desc("A CSV file created by buildtimetracker")
                .required()
                .build();
        Option taskName = Option.builder("task_name")
                .argName("task_name")
                .hasArg()
                .required()
                .build();
        options.addOption(statsFile);
        options.addOption(taskName);
        try {
            return parser.parse(options, args);
        } catch (ParseException exp) {
            System.err.println(exp.getMessage());
            formatter.printHelp("stats", options);
        }
        return null;
    }

    private static void printStats(File statsFile, String taskName) throws IOException {
        Reader fileReader = new FileReader(statsFile);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build().parse(fileReader);
        Stream<String> timesStream = StreamSupport.stream(records.spliterator(), false)
                .filter(it -> it.get("task").equals(taskName)
                        && it.get("success").equals("true")
                        && it.get("did_work").equals("true")
                        && it.get("skipped").equals("false"))
                .map(it -> it.get("ms"));
        IntSummaryStatistics statistics = timesStream
                .mapToInt(Integer::valueOf)
                .summaryStatistics();
        if (statistics.getCount() > 0) {
            System.out.println("min: " + statistics.getMin());
            System.out.println("max: " + statistics.getMax());
            System.out.println("mean: " + Math.round(statistics.getAverage()));
            System.out.println("count: " + statistics.getCount());
        }
    }
}
