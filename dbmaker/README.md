# Database preparation

1. To enable/disable Youtube API navigate to dbmaker/src/main/java/com/cyberwalkabout/cyberfit/db/sqlite/SQLiteDBCreator.java and adjust populateFromYoutube variable as needed
  1. If Youtube API enabled
    1. Generate client id and client secret that will be used to authenticate with Youtube API [Reference](https://developers.google.com/api-client-library/python/guide/aaa_client_secrets)
    2. Put google_client_secrets.json file in dbmaker/src/main/resources folder
2. To run database generator navigate to dbmaker directory and execute `gradle bootRun`.
3. Successful output should print something like this `.22:21:30.066 [main] INFO  com.cyberwalkabout.cyberfit.DBMaker - DB successfully created at '/Volumes/FusionDrive/Cyber/android-warrior-fit/dbmaker/build/db/cyberfit_v2.db'`
4. Copy generated db file to `CyberFit/src/main/assets`