# Language Logger Research App: Backend

Welcome to the repo for the LanguageLogger app's backend. In order to conduct user studies the system consists an Android App and a  Web Server, which communicates with all the users smartphones but and allows the researcher to setup his study aswell as get overview of the collected keyboard events in form of an web interface.

The repository for the Android App can be found here:

https://github.com/Flo890/languagelogger-app


# Server
The server consists a web app based on Play Java 2.5.3 and a MySQL Database

## For Development: Deploy locally

Prerequistities:
* jdk 8 (jdk 9 is not supported)

### Step 1: Setup MySQL Server

#### 1.1 Install MySQL (Ubuntu)
* install MySql Server (we are using v5.7.21 in this guide):

    `sudo apt-get install mysql-server`
    
* During the installation proccess you will be asked to define a password for the "root" user. Enter a password and remember it.

* ensure that `character_set_server=utf8mb4` is set in the MySQL config files (for emojis)

#### 1.2 Initialize database and user

* Once finished, type

    `mysql -u root -p`
    
    to enter MySQL server. Enter just defined password for root user.


* Create our Database:
    
    `create database languageloggerdb default character set utf8mb4 default collate utf8mb4_general_ci;`


* Create database user and grant access to newly created database:

1. `create user 'languagelogger'@'localhost' identified by 'G8BCjNk';`

**Note: Do not use the username and password listed here on your system - exchange them with custom ones!**
    
2. `grant all on languageloggerdb.* to 'languagelogger'@'localhost';`
    
3. `flush privileges;`


    
    

### Step 2: Configure, compile and run Play Application

Open the commandline, and navigate into the project directory if you didn't so far:

* `cd languagelogger-backend` 

On Ubuntu we first have to make the activator script runnable...
* `chmod +x activator`

...then you can run the server using
* `./activator run -Dplay.evolutions.db.default.autoApply=true`

On Windows you can usually just run
* `activator run -Dplay.evolutions.db.default.autoApply=true`

* Wait till you see:

     `[info] p.c.s.NettyServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000`
     
* Open `http://localhost:9000/cms/login` in browser.

* You might see a red warning page, telling you that your database layout does not fit. Hit the "Apply" to initialize the database layout. **Attention: This will delete all tables and data in `languageloggerdb`!**

* After a short while you should see the login page:

    ![Evolutions database migration](./images/login.png)
    
    The default user is: `LanguageLogger`

    The default password is: `$LL773`
    
### Step 3 (eventually): Reverse Proxy

Usually you will not be able to access a locally setup backend from an Android smartphone. For development purposes, we suggest to use the reverse proxy ngrok:

* Install [ngrok](https://ngrok.com/) on your local system    
* on the commandline, run `ngrok http 9000` (Where 9000 is the port on which your local LanguageLogger backend is running)
* copy the created ngrok forwarding url, and use it as server url configured in the Android app

### Step 4: Configure LanguageLogger abstraction logic

TODO 

### Additional settings
By editing the file `conf/application.conf` u can modify further settings for example:
* Changing database user and password:
```conf
db.default.username = "<your_username>"
db.default.password = "<your_password>"
```

* Changing Dashboard login:
```conf
dashboard.user = "<your_username>"
dashboard.pwd = "<your_password>"
```


## Production

prerequisites:
jdk 8 (jdk 9 is not supported)

* Setup a mysql database as described in the local deployment area
* clone project
* cd `languagelogger-backend`
* `chmod +x activator`
* `./activator dist` (this generates a languagelogger-1.0-SNAPSHOT.zip in `target/universal`)
* `unzip target/universal/languagelogger-1.0-SNAPSHOT.zip`
* `chmod +x languagelogger-1.0-SNAPSHOT.zip/bin/languagelogger`
* `languagelogger-1.0-SNAPSHOT/bin/languagelogger -Dplay.crypto.secret="researchime" -Dhttps.keyStore="languagelogger-1.0-SNAPSHOT/conf/keystore.jks" -Dhttps.keyStorePassword="researchime" -Dplay.evolutions.db.default.autoApply=true -Dhttp.port=disabled`





