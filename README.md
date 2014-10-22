Features
====================
Semi-automatic login with the use of SharedPreferences which stores the username and password of the last user that logged in on a particular device. 
Dynamic population of the interface components, such as spinner through obtaining the records from cloud based NoSQL datastore called Firebase.
Validation of password and email using a regular expression.
Concurrency handling which "guarantees" that only one user will get an appointment at a time.

The application involves multi-threaded sending of emails upon the following three scenarios: 
•	User registration
•	Appointment being booked
•	Appointment being cancelled.
Upon booking an appointment the user has an option of using the Internal Calendar to save the appointment with automatically filled out data by the application. 
Additionally when booking an appointment the application schedules a notification to be displayed in precise amount of time and the notification will be displayed regardless or the application state. Another word the notification will get displayed even when the application is killed by the user or the operating system itself.
Upon a PAA cancelling an appointment, the student’s application (if not killed) will recognise the appointment being cancelled in a background manner and will trigger a notification to the student to inform about the changes.
Also an use has been made of notifications and vibrations on a mobile device upon various events being triggered by the application.

Usability
===========
The conducted interview with beta testers indicated that the application usability relies on the following: 
Less time consuming to accomplish the booking or cancellation of the appointments and easiness of browsing through the appointments and selecting one with no hurry, as opposed to going through lengthy process with the Undergraduate Office by phone.
Interactive through the communication with the user by toasts, internal calendar, notifications and email messaging.


Since the beta testers were only interested in the student side of the application, I myself had the joy of reviewing the PAA side (interface) of it, which has shown that the PAAs benefit from the application through the following:
Easy to use and less time consuming to cancel a particular appointment either through all appointments list or by selecting a particular date and time or even cancel the entire day. 
Interactive by the means of communication with the PAA about the appointments through email messaging and notifications.

As an advantage of the application being produced on a mobile device as opposed to PC, the usability of the application was meant to be enhanced with the usage of GPS location systems for determination of the distance of the student to the university, however due to the strict deadline the use cases were never designed, therefore the GPS functionality wasn't used for any sensible addition to the application.

