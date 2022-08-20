# Nearby

The map-based social media Android app

## About 

Nearby is our new Map-Oriented social media application. Where users can post their own events/pictures as clickable pins on a main map page for other users to see. Users can then discuss or reply to comments about those location pictures/event posts in their dedicated comment forum. Users can filter posts by distance, time, and interest to find nearby events or posts of their interests!

View our website here: https://cmpt362-mobile-application.github.io/Nearby-Website/

## Tech Stack
- Android Studio
- Kotlin
- Firebase
- Google Maps
- Glide

## How to Run
- You can build using Android Studio by downloading our source code

You need to add your own API keys:
- In AndroidManifest.xml in Nearby/app/src/main
  - Add your Google Maps API key to line 38 underneath android:name="com.google.android.geo.API_KEY"

- In google-service.json in Nearby/app/
  - Add your Firebase API key to line 23 as the key-value pair for "api_key"

Refer to this link for Firebase Setup on Android Studio: https://firebase.google.com/docs/android/setup
