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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The command runner.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
public class Main {
  private static final ImmutableMap<String, Class<? extends AdMobApiCommand>> COMMAND =
      ImmutableMap.<String, Class<? extends AdMobApiCommand>>builder()
          .put("networkReport", GetNetworkReport.class)
          .build();

  public static void main(String[] args) throws IOException, URISyntaxException {

    Injector injector = Guice.createInjector(new AdMobApiClientModule());

    if (args.length == 0 || !COMMAND.containsKey(args[0])) {
      printUsage();
    } else {
      String result = injector.getInstance(COMMAND.get(args[0])).execute(args);
      System.out.println(result);
    }
  }

  static void printUsage() throws URISyntaxException, IOException {
    Files.readAllLines(
            Paths.get(Main.class.getResource("/usage.txt").toURI()), StandardCharsets.UTF_8)
        .forEach(System.out::println);
  }
}
