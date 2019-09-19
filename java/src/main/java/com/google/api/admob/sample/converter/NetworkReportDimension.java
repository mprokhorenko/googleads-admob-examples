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

import static com.google.api.admob.sample.converter.NetworkReportValueType.DIMENSION_DISPLAY_LABEL;
import static com.google.api.admob.sample.converter.NetworkReportValueType.DIMENSION_VALUE_INTEGER;
import static com.google.api.admob.sample.converter.NetworkReportValueType.DIMENSION_VALUE_STRING;

import com.google.common.collect.ImmutableList;

/**
 * A network report dimension enum.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
public enum NetworkReportDimension {
  DATE(ImmutableList.of(DIMENSION_VALUE_INTEGER)),
  MONTH(ImmutableList.of(DIMENSION_VALUE_INTEGER)),
  WEEK(ImmutableList.of(DIMENSION_VALUE_INTEGER)),
  AD_UNIT(ImmutableList.of(DIMENSION_VALUE_STRING, DIMENSION_DISPLAY_LABEL)),
  APP(ImmutableList.of(DIMENSION_VALUE_STRING, DIMENSION_DISPLAY_LABEL)),
  COUNTRY(ImmutableList.of(DIMENSION_VALUE_STRING)),
  FORMAT(ImmutableList.of(DIMENSION_VALUE_STRING)),
  PLATFORM(ImmutableList.of(DIMENSION_VALUE_STRING));

  private ImmutableList<NetworkReportValueType> valueTypes;

  NetworkReportDimension(ImmutableList<NetworkReportValueType> type) {
    this.valueTypes = type;
  }

  public ImmutableList<NetworkReportValueType> valueTypes() {
    return valueTypes;
  }
}
