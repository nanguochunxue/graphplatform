package com.haizhi.graph.dc.inbound.util;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.google.common.base.Preconditions;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.dc.core.constant.TaskStatus;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by chengangxiong on 2019/04/10
 */
public class CronUtil {

    private static final int NEXT_TIME_COUNT = 5;

    public static final CronExpression validCronExpression(String cron) {
        if (StringUtils.isNotBlank(cron)) {
            try {
                return new CronExpression(cron);
            } catch (ParseException e) {
                throw new UnexpectedStatusException(TaskStatus.CRON_EXPRESSION_ERROR);
            }
        }
        return null;
    }

    public static final Date findNextTime(CronExpression ce, Date date) {
        return ce.getNextValidTimeAfter(date);
    }

    public static final List<Date> findNextNumTime(@NonNull String cron, int num) {
        Preconditions.checkArgument(num > 0 & num < 10);
        return findNextNumTime(validCronExpression(cron), new Date(), num);
    }

    public static final String findDescription(@NonNull String cronString) {
        CronDescriptor descriptor = CronDescriptor.instance(Locale.CHINESE);
        CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        Cron cron = new CronParser(cronDef).parse(cronString);
        String description = descriptor.describe(cron);
        return description;
    }

    private static final List<Date> findNextNumTime(CronExpression ce, Date date, int num) {
        List<Date> timeList = new ArrayList<>(num);
        return findNextTime(ce, timeList, date, num);
    }

    private static final List<Date> findNextTime(CronExpression ce, List<Date> timeList, Date date, int num) {
        if (num < 1) {
            return timeList;
        }
        num--;
        Date nextDate = ce.getNextValidTimeAfter(date);
        if (nextDate == null) {
            return timeList;
        }
        timeList.add(nextDate);
        return findNextTime(ce, timeList, nextDate, num);
    }
}
