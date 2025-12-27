package com.bigcomp.accesscontrol.util;

import com.bigcomp.accesscontrol.model.AccessRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Log manager utility class.
 * Responsible for logging access requests to CSV files.
 * Logs are organized by date in the directory structure: logs/YYYY/MM/YYYY-MM-DD.csv
 * Team members: [Team Member 1], [Team Member 2], [Team Member 3]
 */
public class LogManager {
    /** Directory where log files are stored */
    private static final String LOGS_DIR = "logs";
    
    /** Date formatter for CSV date column */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy,MMM,dd,EEE");
    
    /** Time formatter for CSV time column */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Singleton instance */
    private static LogManager instance;

    /**
     * Private constructor to enforce singleton pattern
     */
    private LogManager() {
        initializeLogDirectory();
    }

    /**
     * Gets the singleton instance of LogManager
     * @return the LogManager instance
     */
    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }

    private void initializeLogDirectory() {
        try {
            Path logsPath = Paths.get(LOGS_DIR);
            if (!Files.exists(logsPath)) {
                Files.createDirectories(logsPath);
            }
        } catch (IOException e) {
            System.err.println("创建日志目录失败: " + e.getMessage());
        }
    }

    /**
     * Logs an access request to a CSV file.
     * Format: year,month,day,weekday,time,badgeId,readerId,resourceId,userId,userName,status
     * @param request the AccessRequest to log
     */
    public void logAccessRequest(AccessRequest request) {
        LocalDateTime requestTime = request.getRequestTime();
        int year = requestTime.getYear();
        int month = requestTime.getMonthValue();
        int day = requestTime.getDayOfMonth();

        // Create directory structure: logs/year/month/
        String yearDir = String.valueOf(year);
        String monthDir = String.format("%02d", month);
        Path logDir = Paths.get(LOGS_DIR, yearDir, monthDir);

        try {
            Files.createDirectories(logDir);
        } catch (IOException e) {
            System.err.println("创建日志目录失败: " + e.getMessage());
            return;
        }

        // Log file name: YYYY-MM-DD.csv
        String fileName = String.format("%04d-%02d-%02d.csv", year, month, day);
        Path logFile = logDir.resolve(fileName);

        // Format date and time
        String dateStr = requestTime.format(DATE_FORMATTER);
        String timeStr = requestTime.format(TIME_FORMATTER);

        // Build CSV line
        // Format: year,month,day,weekday,time,badgeId,readerId,resourceId,userId,userName,status
        String csvLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                dateStr,
                timeStr,
                request.getBadgeId() != null ? request.getBadgeId() : "",
                request.getReaderId() != null ? request.getReaderId() : "",
                request.getResourceId() != null ? request.getResourceId() : "",
                request.getUserId() != null ? request.getUserId() : "",
                request.getUserName() != null ? request.getUserName() : "",
                request.getStatus() != null ? request.getStatus().name() : "UNKNOWN"
        );

        // Write to file (append mode)
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, 
                java.nio.file.StandardOpenOption.CREATE, 
                java.nio.file.StandardOpenOption.APPEND)) {
            writer.write(csvLine);
        } catch (IOException e) {
            System.err.println("写入日志文件失败: " + e.getMessage());
        }
    }

    /**
     * Reads a log file for a specific date
     * @param year the year
     * @param month the month (1-12)
     * @param day the day of month
     * @param reader the LogReader callback to process each line
     */
    public void readLogFile(int year, int month, int day, LogReader reader) {
        String monthDir = String.format("%02d", month);
        String fileName = String.format("%04d-%02d-%02d.csv", year, month, day);
        Path logFile = Paths.get(LOGS_DIR, String.valueOf(year), monthDir, fileName);

        if (!Files.exists(logFile)) {
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(logFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                reader.processLine(line);
            }
        } catch (IOException e) {
            System.err.println("读取日志文件失败: " + e.getMessage());
        }
    }

    /**
     * Interface for processing log file lines
     */
    public interface LogReader {
        /**
         * Processes a single line from a log file
         * @param line the CSV line to process
         */
        void processLine(String line);
    }

    /**
     * Gets the logs directory path
     * @return the logs directory path
     */
    public String getLogsDirectory() {
        return LOGS_DIR;
    }
}

