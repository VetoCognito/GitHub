package com.example.tim.lostnfound;



import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tim.lostnfound.LocationAddress;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static android.content.Intent.EXTRA_TEXT;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.widget.Toast.LENGTH_LONG;
import static com.example.tim.lostnfound.LocationAddress.getAddressFromLocation;


public class Post extends AppCompatActivity implements LocationListener {

    // Reference to database
    private DatabaseReference dataReference;

    // The FirebaseStorage object is used to upload the pictures. StorageReference is the reference to the particular file uploaded
    private FirebaseStorage storage;
    private StorageReference storageReference;

    // Declaring local variables
    private List<String> yourAnimalList;
    private String editAnimalID;
    private boolean isEditInstance;

    // Declaring UI elements
    private Spinner typeDropdown;
    private Spinner statusDropdown;
    private Button picButton;
    private String typeSelection;
    private String statusSelection;
    private ImageButton submitButton;
    private ImageButton selectImageButton;
    private EditText nameView;
    private EditText colorView;
    private EditText dateView;
    private EditText descView;
    private EditText locationView;
    private EditText phoneView;
    private EditText emailView;

    // PICK_IMAGE_REQUEST is a request code to switch to the activity for picking images from gallery/camera
    private final int PICK_IMAGE_REQUEST = 71;

    protected LocationManager locationManager;
    protected Location location;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // TODO handle location based errors
        // finds current location (lat long coordinates) to place the pin on the map
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
        location = getLastKnownLocation();
        LocationAddress locationAddress = new LocationAddress();
        getAddressFromLocation(location.getLatitude(), location.getLongitude(),
                getApplicationContext(), new GeocoderHandler());


        // Verify local data file exists, and create it if not verified
        FileUtils.createFile(getApplicationContext());

        // Import animal data from local file
        yourAnimalList = FileUtils.readFromFile(getApplicationContext());

        // Create reference to database
        dataReference = DatabaseUtils.getReference(DatabaseUtils.getDatabase());

        // Initialize UI elements
        nameView = (EditText) findViewById(R.id.postName);
        colorView = (EditText) findViewById(R.id.postColor);
        dateView = (EditText) findViewById(R.id.postDate);
        emailView = (EditText) findViewById(R.id.postEmail);
        descView = (EditText) findViewById(R.id.postDescription);
        phoneView = (EditText) findViewById(R.id.postPhone);
        locationView = (EditText) findViewById(R.id.postLocation);


        // Initialize dropdown selection spinner for animal type
        typeDropdown = (Spinner) findViewById(R.id.post_type_spinner);
        final String[] typesList = {"Dog", "Cat", "Hamster/Guinea Pig", "Mouse/Rat", "Bird", "Snake/Lizard", "Ferret", "Other"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(Post.this, android.R.layout.simple_spinner_item, typesList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeDropdown.setAdapter(typeAdapter);

        typeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeSelection = typesList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                typeSelection = null;
            }
        });

        // Same as above, Initializing dropdown selection spinner for status
        statusDropdown = (Spinner) findViewById(R.id.post_status_spinner);
        final String[] statusList = {"Lost", "Found"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(Post.this, android.R.layout.simple_spinner_item, statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusDropdown.setAdapter(statusAdapter);

        statusDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusSelection = statusList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                statusSelection = null;
            }
        });

        // button to add picture?
        // TODO add picture functionality









        // Retrieve animalID from the intent used to start this instance of Profile
        Intent intent = getIntent();

        // If the intent has a passed animalID, this must be an instance of editing
        // an existing post rather than normal posting
        // We want to change the existing animal with the entered data rather than create an entirely
        // new listing for the same animal with slightly edited data elements
        // Because we are editing existing data, the current data is retrieved and is automatically
        // filled in. The user should edit whatever data elements are incorrect and hit submit.
        if (intent.hasExtra("animalID")) {
            isEditInstance = true;
            editAnimalID = intent.getStringExtra("animalID");

            // Find the particular animal in the database according to the animalID passed in the intent
            dataReference = dataReference.child(editAnimalID);

            // Contact database, retrieve data elements and display them in the appropriate view
            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    @SuppressWarnings("unchecked")
                    HashMap<String, String> animal = (HashMap<String, String>) dataSnapshot.getValue();

                    nameView.setText(animal.get("name"));
                    colorView.setText(animal.get("color"));
                    dateView.setText(animal.get("date"));
                    emailView.setText(animal.get("email"));
                    descView.setText(animal.get("description"));
                    phoneView.setText(animal.get("phone"));
                    locationView.setText(animal.get("location"));

                    typeSelection = animal.get("type");
                    for (int i = 0; i < typesList.length; i++) {
                        if (typeSelection.equals(typesList[i])) {
                            typeDropdown.setSelection(i);
                            break;
                        }
                    }

                    statusSelection = animal.get("found");
                    for (int i = 0; i < statusList.length; i++) {
                        if (typeSelection.equals(statusList[i])) {
                            typeDropdown.setSelection(i);
                            break;
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("DEBUG", "Failure");
                }
            });

            // TODO retrieve already uploaded picture for editing
        }


        // Initialize submit button and listener
        submitButton = (ImageButton) findViewById(R.id.postSubmit);
        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String animalID;

                // find out whether to run the onEdit method or the onSubmit method
                if (isEditInstance) {

                    // Once the submit button is clicked, the onEdit method will do the work of editing the info in the database.
                    // animalID is the database key for animal.
                    animalID = onEdit(editAnimalID);

                    // TODO add code for submission verification before displaying success toast
                    Toast toast = Toast.makeText(getApplicationContext(), "Your pet listing has been edited!", LENGTH_LONG);
                    toast.show();

                } else {
                    // Once the submit button is clicked, the onSubmitAnimal method will do the work of submitting the info to the database.
                    // animalID is the database key for animal.
                    animalID = onSubmit();

                    // TODO add code for submission verification before displaying success toast
                    Toast toast = Toast.makeText(getApplicationContext(), "Your pet has been posted!", LENGTH_LONG);
                    toast.show();

                }


                // Create intent to open profile of submitted/edited animal
                Intent intent = new Intent(Post.this, Profile.class);
                intent.putExtra("animalID", animalID);
                finish();
                startActivity(intent);

            }
        });

    }

    // Implementing method to open the image chooser
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private String onEdit(String editAnimalID) {


        // Find the old animal in yourAnimalList and delete it
        if (yourAnimalList.size() > 0) {
            for (String animal : yourAnimalList) {

                if (animal.equals(editAnimalID)) {
                    yourAnimalList.remove(animal);
                }
            }
        }


        // Declare HashMap to enter animal data into, and declare reference to database to update
        HashMap<String, Object> animal = new HashMap<>();

        // Collect entered data and add it to the HashMap
        animal.put("name", nameView.getText().toString());
        animal.put("color", colorView.getText().toString());
        animal.put("date", dateView.getText().toString());
        animal.put("email", emailView.getText().toString());
        animal.put("description", descView.getText().toString());
        animal.put("phone", phoneView.getText().toString());
        animal.put("location", locationView.getText().toString());
        animal.put("latitude", Double.toString(location.getLatitude()));
        animal.put("longitude", Double.toString(location.getLongitude()));
        animal.put("type", typeSelection);
        animal.put("found", statusSelection);
        animal.put("key", editAnimalID);


        //TODO add picture functionality


        // Update database with edited information
        dataReference.updateChildren(animal);

        // Add animal to local list of animals and save it to the data file
        yourAnimalList.add(animal.get("key").toString());
        FileUtils.writeToFile(yourAnimalList, getApplicationContext());

        return animal.get("key").toString();
    }



    // Implementing method to submit animal to database and return its unique key
    private String onSubmit() {

        // Declare HashMap to enter animal data into, and declare reference to database to add the HashMap to
        HashMap<String, Object> animal = new HashMap<>();

        // Collect entered data and add it to the HashMap
        animal.put("name", nameView.getText().toString());
        animal.put("color", colorView.getText().toString());
        animal.put("date", dateView.getText().toString());
        animal.put("email", emailView.getText().toString());
        animal.put("description", descView.getText().toString());
        animal.put("phone", phoneView.getText().toString());
        animal.put("location", locationView.getText().toString());
        animal.put("latitude", Double.toString(location.getLatitude()));
        animal.put("longitude", Double.toString(location.getLongitude()));
        animal.put("type", typeSelection);
        animal.put("found", statusSelection);

        //TODO add picture functionality

        // Create new key for animal
        DatabaseReference newAnimalRef = dataReference.push();
        String key = newAnimalRef.getKey();

        // Add animal's own key to its database entry
        animal.put("key", key);

        // Add animal to database
        newAnimalRef.setValue(animal);

        // Add animal to local list of animals and save it to the data file
        yourAnimalList.add(animal.get("key").toString());
        FileUtils.writeToFile(yourAnimalList, getApplicationContext());

        return key;
    }



    private static class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.d("location", locationAddress);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


//    // for taking the pictures
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//
//    // for saving the image with a new filename
//    String mCurrentPhotoPath;
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

//    // to display the image
//    private void setPic() {
//        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        mImageView.setImageBitmap(bitmap);
//    }




}