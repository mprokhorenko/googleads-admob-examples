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
package com.google.api.admob.sample.facade;

import com.google.api.services.admob.v1.AdMob;
import com.google.api.services.admob.v1.model.GenerateNetworkReportRequest;
import com.google.api.services.admob.v1.model.GenerateNetworkReportResponse;
import com.google.api.services.admob.v1.model.PublisherAccount;
import com.google.api.admob.sample.util.JsonUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * An AdMob API Facade.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
@Singleton
public final class AdMobApiFacade {
  private static final String URI_ACCOUNT_TEMPLATE = "accounts/%s";

  @Inject private AdMob adMobClient;
  @Inject private JsonUtil jsonUtil;

  public PublisherAccount getPublisherAccount(String publisherId) throws IOException {
    return adMobClient.accounts().get(account(publisherId)).execute();
  }

  public PublisherAccount getDefaultPublisherAccount() throws IOException {
    return adMobClient.accounts().list().execute().getAccount().get(0);
  }

  public List<PublisherAccount> getPublisherAccounts() throws IOException {
    return adMobClient.accounts().list().execute().getAccount();
  }

  public List<GenerateNetworkReportResponse> generateNetworkReport(
      String publisherId, GenerateNetworkReportRequest request) throws IOException {

    InputStream response =
        adMobClient
            .accounts()
            .networkReport()
            .generate(account(publisherId), request)
            .executeAsInputStream();

    return Arrays.asList(jsonUtil.parseJson(response, GenerateNetworkReportResponse[].class));
  }

  private String account(String publisherId) {
    return String.format(URI_ACCOUNT_TEMPLATE, publisherId);
  }
}
