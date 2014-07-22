less-daemon
===========

Less Compiler Daemon

About
===========

Less Compiler Daemon is a simple java based application which allows you to compile __.less__ files automatically while running in the background. You can add any given number of directories for the daemon to listen for changes.

The daemon is also listening for changes in __@import__ed files and will re-compile the related file. 

The main features are:

* Auto-compile multiple __.less__ containing directories
* Tray icon for ease of access

![Alt text](/screenshots/tray-icon.png?raw=true "Tray Menu")

* Choose the destination from: 
** same directory
** '../css' folder
** custom output directory
* Dependency tree allows you to traverse your __.less @import__ tree
* Event Log allows you to easily view compile status and error 
 