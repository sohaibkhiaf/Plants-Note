# Plants-Note
This is a mobile app project that represents a note of plants, and it is created for training purpose. you may need to see a part/all of the project's code if you are interested in one/all of the partitions used to build it:

-Material design (MaterialToolbar, TextViewEditText, FloatingActionBar..).
-Getting access to public external storage using ARL and getting an image then making a copy of it and storing it in the internal storage of the app 
          & taking READ_EXTERNAL_STORAGE permission from the user by showing the permissions dialog.
-CRUD operations on internal database (create ,retrieve, update, delete).
-Dealing with internal SQLite database (storing an image to database as URI, and other data such Plant's name and location).
-Recyclerview (adapter with recyclerview item click listener interface).
-Menu (includes search for Recyclerview items by Plant name from the database, then showing them in the recyclerview every time the search text changes).
-Snackbars with actions (UNDO: when deleting or modifying and OK: when adding a Plant item).
-the user has to click twice on back button in 2s to exit from the app in the MainActivity.

if you want to install Plants Note on your Android device : [Download APK file](https://www.mediafire.com/file/7b0mdkbj4tgc7tc/Plants-Note.apk/file)



<table>
  <tr>
    <td>List Empty</td>
     <td>Add Plant</td>
     <td>Fill Fields</td>
  </tr>
  <tr>
    <td><img src="screenshots/1-list-empty.png" width=270 height=480></td>
    <td><img src="screenshots/2-add-plant.png" width=270 height=480></td>
    <td><img src="screenshots/3-fill-fields.png" width=270 height=480></td>
  </tr>
          
   <tr>
    <td>Item Inserted</td>
     <td>Add Another Item</td>
     <td>Many Items</td>
  </tr>
  <tr>
    <td><img src="screenshots/4-item-inserted.png" width=270 height=480></td>
    <td><img src="screenshots/5-add-another-item.png" width=270 height=480></td>
    <td><img src="screenshots/6-many-items.png" width=270 height=480></td>
  </tr>
          
  <tr>
    <td>Search</td>
     <td>Modify Item</td>
     <td>Item Modified</td>
  </tr>
  <tr>
    <td><img src="screenshots/7-search.png" width=270 height=480></td>
    <td><img src="screenshots/8-edit-item.png" width=270 height=480></td>
    <td><img src="screenshots/9-item-modified.png" width=270 height=480></td>
  </tr>
          
   <tr>
    <td>Click Again To Exit</td>
  </tr>
  <tr>
    <td><img src="screenshots/10-click-again-to-exit.png" width=270 height=480></td>
  </tr>
 </table>
