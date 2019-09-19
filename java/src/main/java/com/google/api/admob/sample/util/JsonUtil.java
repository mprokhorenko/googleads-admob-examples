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

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Helps to process json.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
public final class JsonUtil {
  @Inject JsonFactory jsonFactory;

  public <T> T loadAndParseJson(String filePath, Class<T> type) throws IOException {
    return parseJson(getClass().getResourceAsStream(filePath), type);
  }

  public <T> T parseJson(InputStream input, Class<T> type) throws IOException {
    return new JsonObjectParser(jsonFactory).parseAndClose(input, StandardCharsets.UTF_8, type);
  }
}
