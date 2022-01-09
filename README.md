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

## License
MIT License

Copyright (c) 2022 Ralf Lehmann, BladeCodeDev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



