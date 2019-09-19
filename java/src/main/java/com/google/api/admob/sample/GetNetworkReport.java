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
package com.google.api.admob.sample;

import static com.google.api.admob.sample.util.DateUtil.daysBeforeNow;
import static com.google.api.admob.sample.util.DateUtil.yesterday;

import com.google.admob.v1.model.Date;
import com.google.admob.v1.model.DateRange;
import com.google.admob.v1.model.GenerateNetworkReportRequest;
import com.google.admob.v1.model.GenerateNetworkReportResponse;
import com.google.admob.v1.model.PublisherAccount;
import com.google.api.admob.sample.converter.NetworkReportToCsv;
import com.google.api.admob.sample.facade.AdMobApiFacade;
import com.google.api.admob.sample.util.DateUtil;
import com.google.api.admob.sample.util.JsonUtil;
import com.google.inject.Inject;
import java.io.IOException;
import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * This example gets an publisher account for the logged in user, and generates network report.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
final class GetNetworkReport implements AdMobApiCommand {
  // Network report request template. File located at the project resources/ directory.
  private static final String DEFAULT_REPORT_TEMPLATE = "/network_report_request.json";

  private static final String TEMPLATE_ARGUMENT = "--template=";
  private static final String PUBLISHER_ARGUMENT = "--publisher=";
  private static final String START_DATE_ARGUMENT = "--startDate=";
  private static final String END_DATE_ARGUMENT = "--endDate=";
  private static final String FORMAT_ARGUMENT = "--format=";

  @Inject private AdMobApiFacade apiFacade;
  @Inject private JsonUtil jsonUtil;
  @Inject private NetworkReportToCsv csvConverter;

  @Override
  public String execute(String[] args) throws IOException {
    // 1. Get account call allows to get publisher id and reporting time-zone if not set with args.
    //    The publisher Id would be used in generate network report call.
    //    The time-zone would be used to create request dates.
    PublisherAccount publisher = publisherAccount(args);
    Clock reportClock = Clock.system(ZoneId.of(publisher.getReportingTimeZone()));

    // 2. Generate Network report
    //   2.1. Loads report request template. The step simplifies request building.
    GenerateNetworkReportRequest networkReportRequest = networkReportRequestTemplate(args);
    //   2.2. Updates the template with new dates.
    networkReportRequest.getReportSpec().setDateRange(dateRange(args, reportClock));
    //   2.3 Calls generate network report API method.
    List<GenerateNetworkReportResponse> networkReport =
        apiFacade.generateNetworkReport(publisher.getPublisherId(), networkReportRequest);

    return processResponse(args, networkReportRequest, networkReport);
  }

  private String processResponse(
      String[] args,
      GenerateNetworkReportRequest networkReportRequest,
      List<GenerateNetworkReportResponse> networkReportRows) {

    Optional<String> formatter = getArgumentValue(args, FORMAT_ARGUMENT);
    if (formatter.isPresent()) {
      if ("csv".equalsIgnoreCase(formatter.get())) {
        return csvConverter.convert(networkReportRequest, networkReportRows);
      } else if ("raw".equalsIgnoreCase(formatter.get())) {
        return String.valueOf(networkReportRows);
      }
      throw new IllegalArgumentException(String.format("Unknown [%s] format.", formatter.get()));
    }

    return String.valueOf(networkReportRows);
  }

  private GenerateNetworkReportRequest networkReportRequestTemplate(String[] args)
      throws IOException {

    return jsonUtil.loadAndParseJson(
        getArgumentValue(args, TEMPLATE_ARGUMENT).orElse(DEFAULT_REPORT_TEMPLATE),
        GenerateNetworkReportRequest.class);
  }

  private PublisherAccount publisherAccount(String[] args) throws IOException {
    PublisherAccount result;
    Optional<String> publisherId = getArgumentValue(args, PUBLISHER_ARGUMENT);
    if (publisherId.isPresent()) {
      result = apiFacade.getPublisherAccount(publisherId.get());
    } else {
      result = apiFacade.getDefaultPublisherAccount();
    }

    return result;
  }

  private DateRange dateRange(String[] args, Clock reportClock) {
    Date rangeStartDate;
    Date rangeEndDate;

    Optional<String> startDate = getArgumentValue(args, START_DATE_ARGUMENT);
    Optional<String> endDate = getArgumentValue(args, END_DATE_ARGUMENT);

    if (startDate.isPresent()) {
      rangeStartDate = DateUtil.toDate(DateTimeFormatter.BASIC_ISO_DATE.parse(startDate.get()));
      if (!endDate.isPresent())
        throw new IllegalArgumentException(
            String.format("No %s argument value provided.", END_DATE_ARGUMENT));
    } else {
      rangeStartDate = daysBeforeNow(reportClock, 7);
    }

    if (endDate.isPresent()) {
      rangeEndDate = DateUtil.toDate(DateTimeFormatter.BASIC_ISO_DATE.parse(endDate.get()));
      if (!startDate.isPresent())
        throw new IllegalArgumentException(
            String.format("No %s.. argument value provided.", START_DATE_ARGUMENT));
    } else {
      rangeEndDate = yesterday(reportClock);
    }

    return new DateRange().setStartDate(rangeStartDate).setEndDate(rangeEndDate);
  }
}
