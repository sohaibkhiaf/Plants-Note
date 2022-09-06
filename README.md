# Plants-Note
This is a mobile app project that represents a note of plants, and it is created for training purpose. you may need to get an insight into a part/all of the project's code if you are interested in one or all of the partitions used to build it:

-Material design (MaterialToolbar, TextViewEditText, FloatingActionBar..).

-Getting access to public external storage using ARL and getting an image then making a copy of it and storing it in the internal storage of the app 
          & taking READ_EXTERNAL_STORAGE permission from the user by showing the permissions dialog.

-CRUD operations on internal database (create ,retrieve, update, delete).

-Dealing with internal SQLite database (storing an image to database as URI, and other data such Plant's name and location).

-Recyclerview (adapter with recyclerview item click listener interface).

-Menu (includes search for Recyclerview items by Plant name from the database, then showing them in the recyclerview every time the search text changes).
          
-Snackbars with actions (UNDO: when deleting or modifying and OK: when adding a Plant item).

-the user has to click twice on back button in 2s to exit from the app in the MainActivity.
