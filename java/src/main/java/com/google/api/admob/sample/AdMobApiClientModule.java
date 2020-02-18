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

import com.google.api.services.admob.v1.AdMob;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import javax.security.auth.login.CredentialException;

/**
 * A module initiates AdMob service client.
 *
 * @author mprokhorenko@google.com (Maksym Prokhorenko)
 */
public final class AdMobApiClientModule extends AbstractModule {

  /**
   * An AdMob API's reporting scope.
   */
  private static final String REPORT_SCOPE = "https://www.googleapis.com/auth/admob.report";

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  /**
   * Performs all necessary setup steps for running requests against the API.
   *
   * @return An initialized AdMob service object.
   */
  @Provides
  @Singleton
  AdMob provideAdMobClient(
      HttpTransport httpTransport, JsonFactory jsonFactory, Credential credential) {
    // Set up AdMob API client.
    return new AdMob.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  /** Authorizes the installed application to access user's protected data. */
  @Provides
  @Singleton
  Credential provideCredential(
      HttpTransport httpTransport,
      JsonFactory jsonFactory,
      DataStoreFactory dataStoreFactory,
      GoogleClientSecrets clientSecrets)
      throws IOException {

    // Set up authorization code flow
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                jsonFactory,
                clientSecrets,
                Collections.singleton(REPORT_SCOPE))
            .setDataStoreFactory(dataStoreFactory)
            .build();

    // Authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /** Load client secrets from the src/main/resources/client_secrets.json file. */
  @Provides
  @Singleton
  GoogleClientSecrets provideClientSecrets(JsonFactory jsonFactory)
      throws CredentialException, IOException {

    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(
            jsonFactory,
            new InputStreamReader(
                AdMobApiClientModule.class.getResourceAsStream("/client_secrets.json")));

    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter")) {
      throw new CredentialException(
          "Enter OAuth Client ID and Secret from your cloud project "
              + "https://console.developers.google.com/apis/credentials into "
              + "src/main/resources/client_secrets.json");
    }

    return clientSecrets;
  }

  /** Global instance of the HTTP transport. */
  @Provides
  @Singleton
  public HttpTransport provideHttpTransport() {
    return new NetHttpTransport();
  }

  /** Global instance of the JSON factory. */
  @Provides
  @Singleton
  JsonFactory provideJsonFactory() {
    return new JacksonFactory();
  }

  /** Global instance of the {@link DataStoreFactory}. */
  @Provides
  @Singleton
  DataStoreFactory provideDataStorageFactory() throws IOException {
    // Directory to store user credentials.
    return new FileDataStoreFactory(
        new java.io.File(System.getProperty("user.home"), ".store/admob_sample"));
  }
}
