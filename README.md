# Android Picture Engine
Wallpaper app made using Hilt, Retrofit, Room, Navigation Components, MVI, Coroutines, Flows, ViewModel, LiveData, Datastore Preference.

## Description

- The app fetches pictures from <a href="https://unsplash.com/" target="_blank">Unsplash</a> showing them to the user in a scrolling list.
- The list of pictures is cached in the local database and gets refreshed every 10 minutes.
- From the bottom app bar the user will be able to sort the list of pictures and tapping on the search icon will direct them to a new screen where they will be able to make custom searches.
- Always from the bottom app bar, tapping on the setting icon will open up a bottom sheet dialog where the user will be able to change the app theme and get some info from the about section.
- Tapping on one of the picture will open a details screen with additional information where the user will be able to download the picture or setting the picture as a wallpaper.
- The app handles error cases showing appropriate messages to the user.

## Screenshots
<img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_01.png" width="275"/><img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_02.png" width="275"/><img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_03.png" width="275"/>

<img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_04.png" width="271"/><img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_05.png" width="271"/><img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_06.png" width="270"/>

<img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_07.png" width="270"/><img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_08.png" width="270"/><img src="https://github.com/simoneconigliaro/android_picture_engine/blob/master/Screenshot_09.png" width="270"/>

## Getting Started
The app uses Unsplash API. You can get an API key signing up <a href="https://unsplash.com/developers" target="_blank">here</a> and set it in gradle.properties.

## Credits
- App icon from <a href="https://endlessicons.com/free-icons/mountain-icon-1/" target="_blank">Endlessicons</a>.
- Ghosts Icon from <a href="https://www.flaticon.com/free-icon/ghost_1150381?term=ghost&page=1&position=55&page=1&position=55&related_id=1150381&origin=tag" target="_blank">Flaticon</a>.
