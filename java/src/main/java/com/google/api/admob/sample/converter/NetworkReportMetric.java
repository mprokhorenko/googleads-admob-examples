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

import static com.google.api.admob.sample.converter.NetworkReportValueType.METRIC_DOUBLE;
import static com.google.api.admob.sample.converter.NetworkReportValueType.METRIC_INTEGER;
import static com.google.api.admob.sample.converter.NetworkReportValueType.METRIC_MICROS;

/**
 * A network report metric enum.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
public enum NetworkReportMetric {
  AD_REQUESTS(METRIC_INTEGER),
  CLICKS(METRIC_INTEGER),
  ESTIMATED_EARNINGS(METRIC_MICROS),
  IMPRESSIONS(METRIC_INTEGER),
  IMPRESSION_CTR(METRIC_DOUBLE),
  IMPRESSION_RPM(METRIC_MICROS),
  MATCHED_REQUESTS(METRIC_INTEGER),
  MATCH_RATE(METRIC_DOUBLE),
  SHOW_RATE(METRIC_DOUBLE);

  private NetworkReportValueType valueType;

  NetworkReportMetric(NetworkReportValueType type) {
    this.valueType = type;
  }

  public NetworkReportValueType valueType() {
    return valueType;
  }
}
