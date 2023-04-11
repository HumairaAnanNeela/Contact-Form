package edu.ewubd.contactform;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView titleTv,nameTv,emailTv,phoneHomeTv,phoneOfficeTv;
    private EditText nameEt,emailEt,phoneHomeEt,phoneOfficeEt;
    private Button btnCancel,btnSave;
    private ImageView contactPic;

    String name,image,email,phoneHome,phoneoffice;
    String imageString;
    String img;

    KeyValueDB db=new KeyValueDB(this);

    int REQUEST_CODE_SELECT_IMAGE=200;
    int GALLERY_REQ_CODE=300;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TextView Initialization
        titleTv=findViewById(R.id.titleTv);
        nameTv=findViewById(R.id.nameTv);
        emailTv=findViewById(R.id.emailTv);
        phoneHomeTv=findViewById(R.id.phoneHomeTv);
        phoneOfficeTv=findViewById(R.id.phoneOfficeTv);

        //EditText Initialization
        nameEt=findViewById(R.id.nameEt);
        emailEt=findViewById(R.id.emailEt);
        phoneHomeEt=findViewById(R.id.phoneHomeEt);
        phoneOfficeEt=findViewById(R.id.phoneOfficeEt);

        //Button Initialization
        btnCancel=findViewById(R.id.btnCancel);
        btnSave=findViewById(R.id.btnSave);

        //Image Initialization
        contactPic=findViewById(R.id.contactPic);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();
            }
        });

        contactPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);


            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                // Compress and convert the bitmap to a Base64 encoded string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                byte[] decodedBytes = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                // Do something with the imageString
                contactPic.setImageBitmap(decodedBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    public void saveData(){

        //taking input from user editText field
        name=nameEt.getText().toString();
        email=emailEt.getText().toString();
        phoneHome=phoneHomeEt.getText().toString();
        phoneoffice=phoneOfficeEt.getText().toString();

        String errorMessage="";

        if(name.isEmpty()){
            errorMessage+="Name is not valid\n";
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage+="Email is not valid\n";
        }
        if(phoneHome.length()!=11){
            if(phoneHome.charAt(0)!='0' && phoneHome.charAt(1)!='1'){

                errorMessage+="Home Phone number is not valid(must be 11 digits starting with 01)\n";

            }

        }

        if(phoneoffice.length()!=11){
            if(phoneoffice.charAt(0)!='0' && phoneoffice.charAt(1)!='1'){

                errorMessage+="Office Phone number is not valid(must be 11 digits starting with 01)\n";

            }

        }

        if(errorMessage.isEmpty()){
            showDialog("Do you want to save this information ?","Info","Yes","No",1);


        }
        else{
            showDialog(errorMessage,"Error","Ok","Back",0);
        }
    }

    private void showDialog(String message, String title, String btn1,String btn2,int flag){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);

        builder.setCancelable(false)
                .setPositiveButton(btn1,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int id){
                        if(flag==1){

//                            try{
//
//                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image);
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                                byte[] imageBytes = baos.toByteArray();
//                                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//                                byte[] decodedBytes = Base64.decode(imageString, Base64.DEFAULT);
//                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//                            // Do something with the imageString
//                               contactPic.setImageBitmap(decodedBitmap);
//                            }
//                            catch (Exception e){
//                                e.printStackTrace();
//                            }

                            long time = System.currentTimeMillis()/1000;
                            String uid = Long.toString(time);

                            String values = uid+"\n"+name+"\n"+email+"\n"+phoneHome+"\n"+phoneoffice+"\n"+imageString;
                            db.insertKeyValue(uid,values);
                            Toast.makeText(MainActivity.this, "Data Inserted Successfully\n", Toast.LENGTH_SHORT).show();
                            nameEt.setText("");
                            emailEt.setText("");
                            phoneHomeEt.setText("");
                            phoneOfficeEt.setText("");
                            String val= db.getValueByKey(uid);
                            System.out.println(val);

                        }
                    }
                })
                .setNegativeButton(btn2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        AlertDialog alert=builder.create();
        alert.show();
    }


}