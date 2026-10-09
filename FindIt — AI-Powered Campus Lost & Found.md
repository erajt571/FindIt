# FindIt — AI-Powered Campus Lost & Found
## Software Requirements & Project Specification

**Project Type:** Full-Stack Web Application  
**Domain:** University Campus Services  
**Primary Goal:** Help students recover lost belongings through AI-powered matching.  
**Target Users:** Students, faculty members, and campus administrators.

---

## 1. Project Overview

FindIt is an AI-powered campus lost-and-found platform that helps people report lost belongings and connect them with people who have found those items.

Users can submit a report containing an item name, description, category, location, date, and optional photograph. The system uses AI-assisted matching to compare lost and found reports and recommends potential matches based on item descriptions, visual information where supported, location, and time.

Instead of manually searching through social media posts or contacting multiple people, users can browse relevant reports and receive suggested matches through a single platform.

## 2. Problem Statement

Students frequently lose personal belongings such as ID cards, wallets, keys, headphones, chargers, and water bottles around university campuses.

Existing approaches often depend on social media groups, informal announcements, or campus security desks. These approaches can make it difficult to find relevant reports, identify duplicate posts, and connect the correct owner with the person who found an item.

FindIt addresses these problems by providing a centralized reporting system with intelligent matching, searchable listings, and a structured item recovery process.

## 3. Project Objectives

- Provide a centralized platform for lost and found items.
- Allow users to create and manage lost and found reports.
- Use AI to identify potentially matching reports.
- Notify users when a promising match is identified.
- Track an item's recovery status.
- Reduce manual searching and improve the chances of recovering lost belongings.
- Protect users' personal information and discourage fraudulent claims.

## 4. Target Users and Roles

### 4.1 Registered User
A student or staff member who can report lost or found items, view listings, receive match suggestions, and manage their own reports.

### 4.2 Administrator
An authorized campus moderator who can review reports, remove inappropriate or fraudulent content, manage users, and monitor reported items.

For the initial version, a user may report either a lost or a found item using the same account.

## 5. Functional Requirements

### FR-01: User Authentication
- Users can register and log in securely.
- Users can log out of their accounts.
- Only authenticated users can create reports or claim items.
- Users can access and manage their own reports.

### FR-02: Create an Item Report
Users can submit a report with the following fields:

- Report type: Lost or Found.
- Item name and category.
- Item description and distinguishing characteristics.
- Date the item was lost or found.
- Location on campus.
- Optional item photograph.
- Optional additional notes.

The system validates required fields before saving a report.

### FR-03: Browse and Search Reports
- Display lost and found items in a searchable listing.
- Filter reports by category, location, date, and report type.
- Allow users to view report details.
- Exclude resolved, removed, or otherwise inactive reports from the default active listing.

### FR-04: AI-Powered Matching
When a new lost or found report is submitted, the system searches for potentially corresponding reports of the opposite type.

The matching process considers:

- Semantic similarity between item descriptions.
- Item category compatibility.
- Similarity of the reported locations.
- Proximity of the reported dates.
- Optional visual similarity if image-matching functionality is supported by the selected AI service.

The system generates a ranked list of potential matches and assigns a match score or confidence label.

For the MVP, matching can use AI-generated text embeddings and a weighted scoring algorithm. Image matching is an optional enhancement rather than a requirement.

### FR-05: Match Suggestions
- Display potential matches on the report details page.
- Show a short explanation of why a match was suggested.
- Allow users to mark a suggestion as relevant or irrelevant.
- Allow users to contact the reporting user through a controlled in-app process.

A suggested match does not guarantee that two reports refer to the same physical item.

### FR-06: Notifications
- Notify users when a promising potential match is found for their report.
- Display notifications in an in-app notification panel.
- Allow users to open the related report directly from a notification.

Email notifications may be added later.

### FR-07: Item Recovery and Status Management
Each report has a status:

- **Active:** The item has not been recovered or the search is ongoing.
- **Pending Verification:** A potential owner and finder are verifying the match.
- **Resolved:** The item has been successfully returned or the report has otherwise been closed.

Report owners can update the status of their reports. Administrators can moderate status changes when necessary.

### FR-08: User Dashboard
Users can view:

- Their lost item reports.
- Their found item reports.
- Suggested matches.
- Notifications.
- Current report statuses.

### FR-09: Administration and Moderation
Administrators can:

- View and search reports.
- Remove inappropriate or suspicious listings.
- Review user-submitted complaints.
- Suspend accounts when justified.
- Monitor platform activity.

## 6. AI and Matching Logic

The AI matching system is the primary differentiating feature of FindIt.

### Step 1: Collect Report Information
Extract the item's name, description, category, location, and date from the submitted report.

### Step 2: Find Candidate Reports
Retrieve active reports of the opposite type, optionally filtering out reports with incompatible categories or dates.

### Step 3: Calculate Similarity
Compare the new report with candidate reports using semantic text similarity. Combine this with location, date, and category compatibility.

An example scoring model is:

**Match Score =**
- 60% semantic description similarity
- 20% category compatibility
- 10% location proximity
- 10% date proximity

These weights are initial design choices and should be tuned using sample reports. They are not validated accuracy measurements.

### Step 4: Rank Potential Matches
Sort candidate reports by their overall score and show the highest-ranked results.

### Step 5: Notify Relevant Users
If a match exceeds a configurable threshold, create a match record and notify the relevant users.

### Example

**Lost Report:** Black wireless headphones lost in the university library on October 8.

**Found Report:** Black Bluetooth headphones discovered near the library on October 8.

The system identifies similarities in the item description, category, location, and date, then recommends the found report to the person who submitted the lost report.

The users must verify the item before it is considered recovered.

## 7. Non-Functional Requirements

### Security
- Store passwords using a secure password-hashing algorithm.
- Enforce authorization on protected API endpoints.
- Validate and sanitize user inputs.
- Restrict uploaded image types and file sizes.
- Prevent unauthorized users from modifying or deleting other users' reports.
- Avoid exposing sensitive personal contact information publicly.

### Performance
- Load report listings and report details efficiently.
- Paginate large result sets.
- Perform matching asynchronously if AI requests cause noticeable delays.
- Display loading states and helpful error messages during AI processing.

### Usability
- Provide a responsive interface for desktop and mobile.
- Use clear labels for lost and found reports.
- Make report submission straightforward.
- Display match scores with understandable explanations.

### Reliability
- Handle AI API failures gracefully.
- Preserve submitted reports even if matching temporarily fails.
- Prevent duplicate notifications for the same match.
- Maintain consistent report and recovery statuses.

## 8. Technology Stack

### Frontend
- **Next.js:** Application pages, routing, and frontend rendering.
- **React:** Reusable interface components.
- **Tailwind CSS:** Responsive styling and layout.

### Backend
- **Spring Boot:** REST API, authentication, report management, and business logic.
- **Java:** Backend implementation language.

### Database
- **PostgreSQL:** Persistent storage for users, reports, matches, notifications, and related records.

### AI Integration
- **AI embedding API:** Convert report descriptions into numerical representations for semantic similarity.
- **Similarity scoring:** Rank candidate reports using semantic similarity and structured metadata.
- **Optional vision API:** Analyze item photographs if time and API availability permit.

### Deployment and Supporting Services
- **GitHub:** Source control and collaboration.
- **Render:** Hosting and deployment.
- **Cloudinary:** Optional image storage and delivery.
- **Brevo:** Optional email notifications.

The initial implementation should favor the fewest external services needed to deliver a working product.

## 9. Database Design

### Users
- `id` — Primary key.
- `name` — User's display name.
- `email` — Unique email address.
- `password_hash` — Securely hashed password.
- `role` — USER or ADMIN.
- `created_at` — Account creation timestamp.

### Item Reports
- `id` — Primary key.
- `user_id` — Foreign key referencing Users.
- `report_type` — LOST or FOUND.
- `item_name` — Name of the item.
- `category` — Item category.
- `description` — Item details.
- `location` — Reported campus location.
- `incident_date` — Date the item was lost or found.
- `image_url` — Optional photograph URL.
- `status` — ACTIVE, PENDING_VERIFICATION, or RESOLVED.
- `created_at` — Report creation timestamp.
- `updated_at` — Last update timestamp.

### Matches
- `id` — Primary key.
- `lost_report_id` — Foreign key referencing a lost report.
- `found_report_id` — Foreign key referencing a found report.
- `similarity_score` — Computed match score.
- `match_reason` — Explanation of the suggested match.
- `status` — SUGGESTED, CONFIRMED, REJECTED, or CLOSED.
- `created_at` — Match creation timestamp.

### Notifications
- `id` — Primary key.
- `user_id` — Foreign key referencing Users.
- `match_id` — Optional foreign key referencing Matches.
- `message` — Notification text.
- `is_read` — Read status.
- `created_at` — Notification timestamp.

### Additional Implementation Notes
- Use database constraints to prevent duplicate match records for the same lost/found report pair.
- Index report type, status, category, and incident date where appropriate.
- Store images outside PostgreSQL and retain their URLs in the database.
- Store AI embeddings in a suitable vector index or a PostgreSQL vector extension if available. For a small prototype, embeddings can instead be computed and compared in application code.

## 10. Main Application Pages

1. **Landing Page:** Project introduction, search bar, recent reports, and call-to-action buttons.
2. **Register/Login:** User account creation and authentication.
3. **Dashboard:** Overview of the user's reports, match suggestions, and notifications.
4. **Report Item:** Form for creating a lost or found report.
5. **Browse Reports:** Searchable and filterable list of active reports.
6. **Report Details:** Full item information, photograph, status, and potential matches.
7. **Notifications:** List of match alerts and relevant updates.
8. **Admin Dashboard:** Report moderation and user management.

## 11. Main User Workflow

1. A user registers and logs in.
2. The user submits a lost or found report.
3. The backend validates and stores the report.
4. The matching service identifies relevant reports of the opposite type.
5. The system displays ranked match suggestions.
6. The user reviews a suggested match and initiates contact through the application.
7. The parties verify ownership and arrange a safe return.
8. The report is marked as resolved.

## 12. MVP Scope and Priorities

### Must Have
- User registration and login.
- Create lost and found reports.
- Browse, search, and filter reports.
- AI-assisted text matching.
- Ranked potential match suggestions.
- Basic user dashboard.
- Report status management.
- Responsive interface.

### Should Have
- In-app notifications.
- Image uploads.
- Match explanations.
- Basic administrative moderation.

### Could Have
- Image-based similarity.
- Email notifications.
- Interactive campus map.
- Duplicate-report detection.
- Analytics on common lost-item locations.

For the workshop build, prioritize the must-have features and implement a simple, functional version of notifications and moderation only if time permits.

## 13. Acceptance Criteria

The MVP is considered successful when:

1. A user can register, log in, and create a report.
2. A lost report and a corresponding found report can be stored in PostgreSQL.
3. The matching service can identify and rank plausible matches.
4. Potential matches are visible to the relevant user.
5. Users can search and filter active reports.
6. Report owners can update their report statuses.
7. Users cannot modify reports they do not own unless authorized as administrators.
8. The application works on desktop and mobile screens.
9. AI service failures do not cause saved reports to be lost.
10. The main workflow can be demonstrated using realistic sample data.

## 14. Demo Scenario

During the final presentation:

1. Log in as Student A.
2. Submit a lost-item report for black wireless headphones.
3. Log in as Student B and submit a found-item report describing similar headphones.
4. Show FindIt identifying the reports as a potential match.
5. Display the match score and explanation.
6. Open the suggested match and initiate the verification process.
7. Mark the item as resolved after demonstrating the return workflow.

This demonstrates the frontend, backend, database, and AI integration in a single coherent scenario.

## 15. Limitations and Future Improvements

The accuracy of matching depends on report quality, the selected AI service, and the scoring configuration. Similar-looking items may not belong to the same person, and an AI score must not be treated as proof of ownership.

Future versions may include university email verification, campus-specific location selection, stronger image matching, multilingual descriptions, duplicate-report detection, and integration with official campus security services.

## Conclusion

FindIt is a practical full-stack application that combines standard software engineering with a clearly defined AI feature. Its modular design makes it possible to build a useful minimum viable product within a constrained workshop while leaving room for future improvements.

The project's success will be measured by its ability to connect relevant lost and found reports, provide a smooth user experience, and support a safe item recovery process.