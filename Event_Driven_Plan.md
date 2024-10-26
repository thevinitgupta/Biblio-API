# Event-Driven Architecture Integration Plan

## **MVP 1: User Authentication and Basic Book Posting**
1. **Email Verification (Event: User Registration)**
    - **When to add EDA**: After a user registers, trigger an event like `UserRegistered` to handle OTP generation and sending an email asynchronously.
    - **How to add**: Use an event broker (e.g., RabbitMQ, Kafka) to emit a `UserRegistered` event. A listener service handles OTP generation and email dispatch.
    - **Benefits**: Decouples the registration process from email sending, improving user registration performance.

2. **Book Posting (Event: New Book Post Created)**
    - **When to add EDA**: After a book post is created, emit an event like `BookPostCreated` that triggers downstream actions like notifying other services or performing further processing.
    - **How to add**: Upon post submission, publish a `BookPostCreated` event, which other services (like search indexing) can subscribe to.
    - **Benefits**: Decouples post creation from additional features, like notifications or logging, for better scalability.

## **MVP 2: Basic Search and Post Suggestion**
1. **Post Suggestion (Event: Post Creation)**
    - **When to add EDA**: Once a new post is created, trigger the `PostCreated` event to suggest similar posts.
    - **How to add**: Upon post creation, emit an event that the suggestion service listens to. This service processes the new post to find and display similar posts.
    - **Benefits**: Keeps the post creation flow clean and decouples suggestions from core features.

## **MVP 3: Enhanced Book Details and Book Lists**
1. **Book Lists (Event: Book Added/Removed from List)**
    - **When to add EDA**: Emit `BookAddedToList` or `BookRemovedFromList` events when users update their reading lists.
    - **How to add**: After the book is added or removed from a list, publish an event that can be used for analytics, recommendations, or notifications.
    - **Benefits**: Allows other services to act on these changes (e.g., updating recommendations, notifying users, etc.) asynchronously.

## **MVP 4: External Links and Price Comparison**
1. **Price Comparison (Event: Price Check Request)**
    - **When to add EDA**: Trigger an event when a user requests price comparisons for a book (`PriceComparisonRequested`).
    - **How to add**: Emit an event when a user views book details. A background service can then scrape external sites and return prices.
    - **Benefits**: Price scraping can be done asynchronously, improving the user experience by not blocking the main flow.

## **MVP 5: Basic Recommendation System**
1. **Recommendations (Event: Post Interaction or List Updates)**
    - **When to add EDA**: Trigger events like `PostLiked` or `BookAddedToFavorites` to feed into the recommendation engine.
    - **How to add**: Whenever users interact with a post or update their lists, emit events that a recommendation service subscribes to for suggesting similar books.
    - **Benefits**: Provides real-time, personalized recommendations based on user interactions without coupling the recommendation logic with the main app.

## **MVP 6: Trending Books and Genre Communities**
1. **Trending Books (Event: New Reviews, List Updates)**
    - **When to add EDA**: Emit events like `ReviewSubmitted`, `BookAddedToReadList`, etc., to calculate trending books.
    - **How to add**: Publish events whenever users review or add books. A background service listens to these events and updates trending metrics.
    - **Benefits**: Asynchronous event handling improves scalability and performance when calculating trends.

---

## Tools for Event-Driven Architecture:
- **Message Brokers**: RabbitMQ, Kafka, or AWS SNS/SQS can manage your events.
- **Event Consumers**: Microservices or worker processes can subscribe to events for tasks like sending emails, handling recommendations, and scraping data.

By progressively adding EDA, you decouple services, improve scalability, and make your app more modular.
