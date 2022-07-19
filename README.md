# ChatO

## About this app
ChatO is a Messaging app with many features, the most important one is Chatting with friends.
You can also chat in groups, create your OWN group 'Community'

## Features of ChatO
- Realtime Messaging
- Group Chat
- Private Chat
- Push Notifications to notify you when there's a new message
- Joining an existing group, based on your interests or other interests you can select from.
- Selecting your interests and topics that grab your attention
- Creating a new group based on a category
- Authentication with Google / Email&Password


## Project Architecture
#### MVI (Model-View-Intent)
Seperation of UI and Logic, declaring the screen 'Intents' on a ScreenNameIntent.kt file
and States which the UI follows on a ScreenNameState.kt file

##### Data flow: DataSource <-> Repository <-> ViewModel <-> UI

## Frameworks and Libraries used
- Firebase (Authentication, Database, Storage and Push Notifications with FCM)
- Room (Caching)
- DataStore (Another way of caching)
- Coroutines (Asynchrounos and Reactive Programming)
- ViewBinding (Binding layouts to Activites/Fragments)
- Dagger Hilt (Dependency Injection)
- Sdp/Ssp (Responsive design)
- Gson (Serialization)
- Glide (Image loading)
- Retrofit (HTTP Requests. Used with FCM)
- Shimmer (Image preloading effect)
- Design with XML








