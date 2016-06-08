package pbru.fame.itpbru;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Explicit
    private MyManage myManage;
    private static final String urlJSON = "http://swiftcodingthai.com/pbru2/get_user_master.php";
    private EditText userEditText, passwordEditText;
    private String userString, passwordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind Widget
        userEditText = (EditText) findViewById(R.id.editText5) ;
        passwordEditText = (EditText) findViewById(R.id.editText6);

        myManage = new MyManage(this);

        //Test Add New User
        //myManage.addNewUser("1", "name", "sur", "user", "pass");

        //Delete All SQLite
        deleteAllSQLite();

        mySynJSON();

    } // Main Method

    public void clickSingIn(View view) {

        userString = userEditText.getText().toString().trim();
        passwordString = userEditText.getText().toString().trim();

        if (userString.equals("")||passwordString.equals("")) {
            MyAlert myAlert = new MyAlert();
            myAlert.myDialog(this, "Have Space", "Please input your username or password");
        } else {
            checkUserAndPassword();
        }


    } //clickSignIn

    private void checkUserAndPassword() {

    }//checkUser

    private void deleteAllSQLite() {
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
                MODE_PRIVATE, null);

        sqLiteDatabase.delete(MyManage.user_table, null, null);
    }

    private void mySynJSON() {
        ConnectedUserTABLE connectedUserTABLE = new ConnectedUserTABLE(this);
        connectedUserTABLE.execute();
    }

    //Create Inner Class
    private class ConnectedUserTABLE extends AsyncTask<Void, Void, String> {

        private Context context;
        private ProgressDialog progressDialog;


        public ConnectedUserTABLE(Context context) {
            this.context = context;
        }//Constructor

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context, "Synchronize Server",
                    "Please Wait ... Process Synchronize");

        }//onPre

        @Override
        protected String doInBackground(Void... voids) {
            try {

                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(urlJSON).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("7June", "error DoIn ==>" + e.toString());
            }
            return null;
        } // doInBack

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                progressDialog.dismiss();
                Log.d("7June", "JSON ==>" + s);

                JSONArray jsonArray = new JSONArray(s);

                String[] idString = new String[jsonArray.length()];
                String[] nameString = new String[jsonArray.length()];
                String[] surnameString = new String[jsonArray.length()];
                String[] userString = new String[jsonArray.length()];
                String[] passwordString = new String[jsonArray.length()];

                for (int i=0;i<jsonArray.length();i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    idString[i] = jsonObject.getString("id");
                    nameString[i] = jsonObject.getString(MyManage.column_name);
                    surnameString[i] = jsonObject.getString(MyManage.column_surname);
                    userString[i] = jsonObject.getString(MyManage.column_user);
                    passwordString[i] = jsonObject.getString(MyManage.column_password);

                    myManage.addNewUser(idString[i], nameString[i],
                            surnameString[i], userString[i], passwordString[i]);



                }//for

            } catch (Exception e) {
                e.printStackTrace();
            }
        }//Connected Class

//    private void deleteAllSQLite() {
//
//        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MyOpenHelper.database_name,
//                MODE_PRIVATE, null);
//
//        sqLiteDatabase.delete(MyManage.user_table, null, null);
//
//    } // delelteAllSQLite

        //  shift+command=enter(จะได้{})
        public void clickSignUpMain(View view) {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));//Intent other Layout

        }
    }
}// Main Class
