/*
 * Copyright (c) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.api.admob.sample.util;

import com.google.admob.v1.model.Date;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

/**
 * Helps to create reporting spec Date object.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
public final class DateUtil {

  private DateUtil() {}

  public static Date today(Clock clock) {
    ZonedDateTime dateTime = ZonedDateTime.now(clock);
    return toDate(dateTime);
  }

  public static Date yesterday(Clock clock) {
    return daysBeforeNow(clock, 1L);
  }

  public static Date daysBeforeNow(Clock clock, long days) {
    ZonedDateTime dateTime = ZonedDateTime.now(clock).minusDays(days);
    return toDate(dateTime);
  }

  public static Date toDate(ZonedDateTime dateTime) {
    return new Date()
        .setYear(dateTime.getYear())
        .setMonth(dateTime.getMonthValue())
        .setDay(dateTime.getDayOfMonth());
  }

  public static Date toDate(TemporalAccessor temporalAccessor) {
    return new Date()
        .setYear(temporalAccessor.get(ChronoField.YEAR))
        .setMonth(temporalAccessor.get(ChronoField.MONTH_OF_YEAR))
        .setDay(temporalAccessor.get(ChronoField.DAY_OF_MONTH));
  }
}
