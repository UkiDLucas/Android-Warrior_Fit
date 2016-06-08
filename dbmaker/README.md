# Database preparation

#### Fetching values from YouTube video metadata

To enable or disable Youtube API 

private boolean populateFromYoutube = true;

dbmaker/src/main/java/com/warriorfitapp/db/sqlite/**SQLiteDBCreator.java**



#### Youtube API enabled

Generate **client id and client secret** that will be used to authenticate with Youtube API 

https://developers.google.com/api-client-library/python/guide/aaa_client_secrets

Put them in file in dbmaker/src/main/resources folder **google_client_secrets.json**

#### Run Database Generator

> platform-tools $ cd ~/android-warrior-fit/dbmaker 
> dbmaker $ gradle bootRun
DB successfully created at '~/android-warrior-fit/dbmaker/build/db/cyberfit_v2.db' 

Copy generated db file to `mobile/src/main/assets`