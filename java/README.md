# Java sample

The sample is a command-line client that allows calling any AdMob API endpoints. It is not a production-ready solution, just a demonstration of API usage.

## How To
 * Update credentials info in the ```src/main/resources/client_secrets.json``` file;
 * Build with ```mvn clean install``` command.
 * Run network report:
   ```
   mvn exec:java -Dexec.mainClass="com.google.api.admob.sample.Main" -Dexec.arguments=networkReport,--format=csv
   ```
 * Command usage info: ```mvn exec:java -Dexec.mainClass="com.google.api.admob.sample.Main"```
