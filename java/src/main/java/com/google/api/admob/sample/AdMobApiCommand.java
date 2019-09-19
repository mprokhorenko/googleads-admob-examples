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

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Common commands interface.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
public interface AdMobApiCommand {
  String execute(String[] args) throws IOException;

  default Optional<String> getArgumentValue(String[] args, String argMatchTemplate) {
    Optional<String> result =
        Arrays.stream(args).filter(a -> a.startsWith(argMatchTemplate)).findFirst();

    if (result.isPresent()) {
      result = Optional.of(result.get().substring(argMatchTemplate.length()));
    }

    return result;
  }
}
