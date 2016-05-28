# Database preparation

#### Fetching values from YouTube video metadata

To enable or disable Youtube API 

private boolean populateFromYoutube = true;

dbmaker/src/main/java/com/cyberwalkabout/cyberfit/db/sqlite/**SQLiteDBCreator.java**



#### Youtube API enabled

Generate **client id and client secret** that will be used to authenticate with Youtube API 

https://developers.google.com/api-client-library/python/guide/aaa_client_secrets

Put them in file in dbmaker/src/main/resources folder **google_client_secrets.json**

#### Run Database Generator

To run database generator navigate to dbmaker directory and execute `gradle bootRun`.

Successful output should print something like this

`.22:21:30.066 [main] INFO  com.cyberwalkabout.cyberfit.DBMaker - DB successfully created at '/Volumes/FusionDrive/Cyber/android-warrior-fit/dbmaker/build/db/cyberfit_v2.db'`

Copy generated db file to `CyberFit/src/main/assets`