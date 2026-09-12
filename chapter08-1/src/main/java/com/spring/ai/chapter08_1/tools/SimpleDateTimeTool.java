package com.spring.ai.chapter08_1.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleDateTimeTool {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Tool(description = "Get the current date and time in users zone.")
    public String getCurrentDateTime() {
        this.logger.info("Tool calling: getCurrentDateTime");
        // 사용자 시간대 기반 현재 시간 반환
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();
    }

    @Tool(description = "Set the alarm for given time.")
    public void setAlarm(@ToolParam(description = "Time in ISO-8601 format") String time) {

        var dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        this.logger.info("알람 설정 완료: {}", dateTime);
    }

}