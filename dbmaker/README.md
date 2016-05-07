To build application execute following command:

```gradle clean build

To run:

```gradle bootRun

For this first time it is necessary to authenticate application with associated google account, so for the first time when application started you will see following message in log:

Please open the following address in your browser:
  https://accounts.google.com/o/oauth2/auth?client_id=1001707891374-e30n5nhlo53ek1npe6pb7scldqr62qi9.apps.googleusercontent.com&redirect_uri=http://localhost:8080/Callback&response_type=code&scope=https://www.googleapis.com/auth/youtube.readonly

Open provided link in browser and signin in cyberwalkaboutgae@gmail.com account.
Once done application will proceed with execution printing following log messages:

.23:18:39.448 [main] INFO  c.c.c.d.s.YoutubeContentPopulator - Populate database with data from youtube.com
.23:18:39.467 [main] DEBUG c.c.c.d.s.YoutubeContentPopulator - Retrieved 1 youtube ids from 'exercise' table
.23:18:40.214 [main] INFO  com.cyberwalkabout.cyberfit.DBMaker - DB successfully created at '~/CyberFit_Android/dbmaker/cyberfit.db'

Once execution finished created database could be found by provided path on the log.