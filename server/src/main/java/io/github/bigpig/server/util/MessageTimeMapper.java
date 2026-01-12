package io.github.bigpig.server.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageTimeMapper {

    public String fullTimeToString(LocalDateTime localDateTime) {
        int year = localDateTime.getYear();
        int day = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();

        String month = localDateTime.getMonth().toString();
        month = month.charAt(0) + month.substring(1).toLowerCase();

        return String.format("%d:%d, %s %d, %d", hour, minute, month, day, year);
    }
}
