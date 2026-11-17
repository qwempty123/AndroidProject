# CourseMarket

CourseMarket is a Kotlin + Android Views demonstration marketplace for educational courses.

## Product features

- Home page with featured programs, learning directions and product highlights.
- Catalog of 12 populated courses across Development, Design and Game Design.
- Search by course title, instructor, category and description.
- Category filters and sorting by rating or price.
- Course detail view with description, instructor, level, duration, lesson count, curriculum and learning outcomes.
- Favorites with local persistence.
- Shopping cart with item removal and automatic total calculation.
- Demonstration checkout and a purchased-courses section in the profile.
- Responsive dark interface with consistent typography, spacing and bottom navigation.
- Favorites, cart and purchased course IDs are stored locally with SharedPreferences.

## Technology

- Kotlin
- Android Views (programmatic UI)
- AndroidX AppCompat and Core
- Material Components
- SharedPreferences

## Run

1. Open the project in Android Studio.
2. Pull the latest changes from the `main` branch.
3. Wait for Gradle Sync to finish.
4. Select the `app` run configuration and an emulator or connected Android device.
5. Run the application.

## Demo limitations

Course information is bundled in the application. The cart and purchases are stored locally on the device. Checkout demonstrates the purchase flow only: there is no real payment provider, remote backend, cloud synchronization or user authentication.
