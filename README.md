# TSViewer
This is a simple TeamSpeak client which connects every x minutes to get the connected clients and displays them in a notification and/or quick settings tile. This works by using a Teamspeak Query account to login. That means it is required to enter the ip and login credentials into the app. As this is sensitive, the ip and credentials are saved only locally and encrypted to make sure that other apps cannot scrape the data.

The app uses the new Material You adaptive colors, but is also backwards compatible to devices below Android 12.

## Features
- Automated checks running in the background (customizable time between 15 and 120 minutes)
- Amount of clients and their nicknames will be written into a notification
- Amount of clients will be written in the label of a qs tile (Android 10+)
- Amount of clients and time will be added to a database to be later viewed in a graph view
- Android 12 adaptive color theming support
- Android 13 adaptive icon support
- Locally stored and encryped IP and login credentials
- No trackers like Firebase and Crashlytics

## Screenshots
### Main Screen
<img height=600 src="https://user-images.githubusercontent.com/60486125/170534753-be64c25a-5ea6-453f-8b09-db455c88013d.png" />

### Graph Screen
<img height=600 src="https://user-images.githubusercontent.com/60486125/170534804-e2a0bb1d-3787-493f-b539-cd9d09d17a4c.png" />

### Notifications and Quick Settings
<img height=600 src="https://user-images.githubusercontent.com/60486125/170534768-bfe1b828-80e7-49b8-af82-52d9eaddfcd7.png" />

## Video Demo
![DEMO](https://user-images.githubusercontent.com/60486125/148692387-03ac0eac-58e2-417e-ba43-cac4bc41f90a.gif)

## Download
[<img height=80 alt="Get it on F-Droid" src="https://user-images.githubusercontent.com/60486125/154999401-f69b1a74-dd6d-44e6-b729-e49f6b00ec4c.png" />](https://f-droid.org/en/packages/com.blazecode.tsviewer/)
[<img height=80 alt="Get it on GitHub" src="https://user-images.githubusercontent.com/60486125/154999292-534a685f-5fbf-49cd-8c47-6c0475ab2aaf.png" />](https://github.com/BlazeCodeDev/TSViewer/releases)

## Bug reporting
If you encounter any bugs please open an issue in the repo or send me an e-mail via the "Send E-Mail" menu entry in the app. Please consider including a logcat/screenshot or screen recording. If you open an issue please also include the app version and android version of your device.

## Privacy
Please tell the users of your TeamSpeak server that you are using this kind of software, because this may be seen as a violation of privacy. You could use a text like this in your default channel: 
```
This server is using one or more instances of [url=https://github.com/BlazeCodeDev/TSViewer/]TSViewer[/url]. 
Please contact the admin/owner if you do not agree to this.
```

## Disclaimer
You are using this software at your own risk, I cannot be held responsible for any kind of damage this may produce in any way. Please only use the app when downloaded from official sources linked above.

## License
MIT License

Copyright (c) 2022 Ralf Lehmann, BladeCodeDev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



