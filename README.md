Less Compiler Daemon
===========

Less Compiler Daemon is a simple java based application which allows you to compile __.less__ files automatically while running in the background. You can add any given number of directories for the daemon to listen for changes.

Download executable __.jar__ file [here](/binary/less-daemon-0.0.1.jar?raw=true)

The daemon is also listening for changes in __@import__'ed files and will re-compile the related file in the added directory.

The main features are:

* Tray icon for ease of access

![Tray Icon](/screenshots/tray-icon.png?raw=true "Tray Menu")

* Auto-compile multiple __.less__ files in configurable directories

![Less Options](/screenshots/prefs-folder-manager.png?raw=true "Tray Menu")

* Choose the destination from: 
** same directory
** '../css' folder
** custom output directory

![Less Options](/screenshots/prefs-less-options.png?raw=true "Tray Menu")

* Dependency Tree widget allows you to traverse your __.less @import__ tree. This tree is automatically updated when you add/remove imports in files.

![Less Options](/screenshots/events-tree.png?raw=true "Tray Menu")

* Event Log allows you to easily view compile status and error

![Less Options](/screenshots/events-log.png?raw=true "Tray Menu")

Status
===========

This program was made as an atempt to make my life easier. Because it made, I decided to share it.

Although I didn't find any major issues, this is a work in progress, if you think something is wrong, don't hesitate to comment and contribute.
 
Building and Runing
===========

Maven 3 is needed to build and package the application. So to build and run just follow these steps:
```bash
git clone https://github.com/lflobo/less-daemon.git
cd less-daemon
mvn package
```

This will create an all-in-one __.jar__ file in `target` that you can execute using `java -jar {jarfile}` or by double clicking in your file system browser.
