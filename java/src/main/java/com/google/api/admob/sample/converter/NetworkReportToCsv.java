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
package com.google.api.admob.sample.converter;

import com.google.api.services.admob.v1.model.GenerateNetworkReportRequest;
import com.google.api.services.admob.v1.model.GenerateNetworkReportResponse;
import com.google.api.services.admob.v1.model.ReportRow;
import com.google.api.services.admob.v1.model.ReportRowDimensionValue;
import com.google.api.services.admob.v1.model.ReportRowMetricValue;
import com.google.common.base.Joiner;
import com.google.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts network report response to a csv string.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
@Singleton
public class NetworkReportToCsv {

  private static final Joiner CSV_JOINER = Joiner.on(",");
  private static final BigDecimal MICROS = BigDecimal.valueOf(1_000_000L);

  public String convert(
      GenerateNetworkReportRequest request, List<GenerateNetworkReportResponse> networkReportRows) {
    List<String> requestedDimensions = request.getReportSpec().getDimensions();
    List<String> requestedMetrics = request.getReportSpec().getMetrics();
    StringBuilder result = new StringBuilder();

    result
        .append(createCsvHeader(requestedDimensions, requestedMetrics))
        .append(System.lineSeparator());

    for (GenerateNetworkReportResponse row : networkReportRows) {
      if (row.getRow() != null) {
        result
            .append(processResponseRow(requestedDimensions, requestedMetrics, row.getRow()))
            .append(System.lineSeparator());
      }
    }

    return result.toString();
  }

  private String createCsvHeader(List<String> requestedDimensions, List<String> requestedMetrics) {
    List<String> result = new ArrayList<>();
    for (String dimensionName : requestedDimensions) {
      NetworkReportDimension dimension = NetworkReportDimension.valueOf(dimensionName);
      for (NetworkReportValueType type : dimension.valueTypes()) {
        switch (type) {
          case DIMENSION_VALUE_INTEGER:
          case DIMENSION_VALUE_STRING:
            result.add(dimensionName);
            break;
          case DIMENSION_DISPLAY_LABEL:
            result.add(String.format("%s_LABEL", dimensionName));
            break;
          default:
            throw new IllegalArgumentException("Unknown dimension type.");
        }
      }
    }

    result.addAll(requestedMetrics);

    return CSV_JOINER.join(result);
  }

  private String processResponseRow(
      List<String> requestedDimensions, List<String> requestedMetrics, ReportRow reportRow) {
    List<String> result = new ArrayList<>();

    for (String dimensionName : requestedDimensions) {
      ReportRowDimensionValue value = reportRow.getDimensionValues().get(dimensionName);

      for (NetworkReportValueType type :
          NetworkReportDimension.valueOf(dimensionName).valueTypes()) {
        switch (type) {
          case DIMENSION_VALUE_INTEGER:
            result.add(value.getValue());
            break;
          case DIMENSION_VALUE_STRING:
            result.add(String.format("\"%s\"", value.getValue()));
            break;
          case DIMENSION_DISPLAY_LABEL:
            result.add(String.format("\"%s\"", value.getDisplayLabel()));
            break;
          default:
            throw new IllegalArgumentException(String.format("Unknown dimension [%s] type.", type));
        }
      }
    }

    for (String metricName : requestedMetrics) {
      ReportRowMetricValue value = reportRow.getMetricValues().get(metricName);
      NetworkReportValueType type = NetworkReportMetric.valueOf(metricName).valueType();
      switch (type) {
        case METRIC_DOUBLE:
          result.add(value.getDoubleValue().toString());
          break;
        case METRIC_INTEGER:
          result.add(value.getIntegerValue().toString());
          break;
        case METRIC_MICROS:
          result.add(BigDecimal.valueOf(value.getMicrosValue()).divide(MICROS).toString());
          break;
        default:
          throw new IllegalArgumentException(String.format("Unknown metric [%s] type.", type));
      }
    }

    return CSV_JOINER.join(result);
  }
}
