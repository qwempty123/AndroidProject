# CourseMarket

CourseMarket is a Kotlin + Android Views learning marketplace prototype. It combines a course catalog, a local checkout demonstration and an offline learning area.

## Product features

- Home page with featured programs, category shortcuts and a resume-learning card.
- Catalog of 12 populated courses across Development, Design and Game Design.
- Search by title, instructor, category and description.
- Category filters and sorting by rating or price.
- Course details with instructor, level, duration, curriculum and learning outcomes.
- **61 authored lesson materials** across all 12 courses.
- Each lesson includes an explanation, key ideas, a worked example, a practical assignment and a multiple-choice knowledge check.
- Lessons can be completed in sequence; per-course progress is saved locally and can be resumed.
- Purchased courses open the learning area rather than only showing a purchase label.
- Favorites and shopping cart with local persistence.
- Checkout form with buyer name and email validation.
- Three demonstration payment choices: bank card, SBP and invoice.
- A local order number and receipt summary are stored in order history.
- Profile with purchased courses, lesson progress and the latest demonstration order.

## Technology

- Kotlin
- Android Views (programmatic UI)
- AndroidX AppCompat and Core
- Material Components
- SharedPreferences for local state
- Android JSON classes for local order history

## Run

1. Open the project in Android Studio.
2. Pull the latest changes from the `main` branch.
3. Wait for Gradle Sync to finish.
4. Select the `app` run configuration and an emulator or connected Android device.
5. Run the application.

## Important demo limitations

This is a self-contained local prototype. Course materials are bundled in the application; favorites, cart, purchases, order history and lesson progress are stored on the current device.

The checkout is deliberately a **demonstration flow**. It validates buyer name and email, lets the user choose a payment presentation, creates a local order number and receipt, and unlocks the courses. It does not contact a bank, generate a real SBP payment, issue a payable invoice or charge money. Do not enter or store real card numbers, CVC codes or banking passwords. A production payment flow requires a secure backend and an approved payment provider.

There is no remote account, cloud synchronization, hosted video platform or server-side course delivery.
