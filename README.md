# TSViewer
This is a simple TeamSpeak client which connects every x minutes to get the connected clients and displays them in a notification and/or quick settings tile. This works by using a Teamspeak Query account to login. That means it is required to enter the ip and login credentials into the app. As this is sensitive, the ip and credentials are saved only locally and encrypted to make sure that other apps cannot scrape the data.

The app uses the new Material You adaptive colors, but is also backwards compatible to devices below Android 12.

## Features
- Automated checks running in the background (customizable time between 15 and 120 minutes)
- Amount of clients and their nicknames will be written into a notification
- Amount of clients will be written in the label of a qs tile (Android 10+)
- Android 12 adaptive color theming support
- Locally stored and encryped IP and login credentials
- No trackers like Firebase and Crashlytics

[License](/license.md)




