# Final Mini Project: VIBEY

## About VIBEY
```
VIBEY is an application that is designed to support our local artistes by 
streamlining tip transactions from supporters, as well as providing artistes 
a platform to give back to their supporters.

On VIBEY, we take the payee-payer relationship between supporters and artistes
and translate that into our core user roles - Vibees and Vibers.
```

### User Authentication
```
Users are required to submit their email (must be unique in DB), password,
phone number, and desired role. Details will be sent to the server, and password
will be encoded before being saved with the other details. User ID is generated
as the primary key, which will be used in JWT Token for user authentication
throughout the application for client-side and client-server communication.

Security configurations are implemented with the JWT filter to authenticate
every authorised request to the backend, and authentication/role guards are
used on client-side to facilitate routing between components. Component Store
is used to store the token for authorisation headers and User ID/User Role
to facilitate routing and user access.

On logout, component store and local storage are cleared.
```

### Registered Vibers
```
Vibers will have access to the full list of Vibees who are on our application.
A search function has been implemented here, where the list of Vibees are
filtered according to the search query. They will be able to see the Vibee profile 
details, and access a share button that brings them to the selected Vibee's 
tipping page.

Vibers will be able to input their name, message, and email on the tipping page.
If their email is inserted, they will receive a personalised email containining
the Vibee's thank you message as well as an exclusive Spotify playlist as
curated by the Vibee. The email is sent using SendGrid.

Tipping page uses Stripe to accept card details. Form values and card details
are sent to the server, which sends a client secret back to the client. The
client then checks the payment confirmation via Stripe.js. An error is thrown
if the payment fails, and only successful payments are sent back to the server
to be saved on MySQL.

The receipt email can also be used as an advertising avenue for event sponsors
and partners, where Vibers will get latest event news or coupons.
```

## Registered Vibees
```
Vibees will have access to everything Vibers can access, with added access to
their Dashboard. The Dashboard has four components:

1. If the Vibee has not yet created a profile on the application, they will not
be able to access the Dashboard. A profile quiz will be shown, where they will 
fill up their profile details. Here, they will also be connected to VIBEY's
Stripe Connect via Stripe OAuth 2.0. Their profile and Stripe account details
are saved to MongoDB and MySQL, before they are redirected to the Dashboard.

2. Overview - Vibee's tip analytics and summary data will be displayed here. 
Chart.js is set up here. Tips are retrieved from MySQL, and analytics are
calculated and summarised on client-side. 

3. Tips History - A table view of each tip the Vibee has earned. The tips
are also retrieved from MySQL, and the details are shown on each row
after relevant calculations on client-side. Functions here include a date 
sorter as well as a paginator.

4. Profile - A form that has patched values from the Vibee profile quiz,
which they are able to edit and save again to update their profiles on
MongoDB. Here, the QR Code generated for their tipping page will be
retrieved and displayed. The share button copies the link to clipboard.
There is also an Exclusive Spotify Playlist section, where Vibees can search
tracks which will be sent to the server to query the Spotify API via RestTemplate.
This secton uses OAuth to connect the user first, and the playlist curated
from selected tracks will be created on their Spotify accounts with a set
name and description. One Vibee may only curate one playlist at a time,
which means every write will overwrite the MongoDB save of their playlist
details. They will be able to share the playlist via the Share button.

```

### Extra
```
A service worker is used to precache application assets - this includes:
image assets, navigation urls (api paths are excluded), font styles, index
html, and .js files.

Angular Material is used to design the application, with a consistent colour
theme applied across. Additional imports from Angular Material include Mat icons,
SnackBar, MatChips, etc.
```