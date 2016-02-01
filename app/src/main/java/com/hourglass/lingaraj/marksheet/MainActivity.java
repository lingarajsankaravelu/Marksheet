package com.hourglass.lingaraj.marksheet;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hourglass.lingaraj.marksheet.serializableClassStudentList.StudentDetail;
import com.hourglass.lingaraj.marksheet.serializableClassStudentList.StudentList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button skip, submit;
    Spinner className, section, examname, gradeSystem, markslistSpinner;
    TextView displayName, displayRollNo,classSelectedTextview,sectionSelectedTextview,examSelectedTextview;
    android.support.v7.widget.AppCompatAutoCompleteTextView enteredMarkAutoCompleteTextview;
    ArrayAdapter<String> classSpinnerAdapter, sectionSpinnerAdapter, gradeSystemSpinnerAdapter, examNameSpinnerAdapter, markListSpinnerAdapter;
    String classItemSelected, sectionItemSelected,examNameSelected,gradeNameSelected,marksSelected;
    RelativeLayout parentRelativeLayout;
    InternetConnectionDetector internetConnectionDetector;
    boolean isInternetConnected;
    ProgressDialog markSheetProgressDialog;
    StudentList studentList;
    int initialIndex;
    int totalStudent;
    String enteredmark;
    ImageView nextStudent, previousStudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        studentList = new StudentList();
        classSelectedTextview = (TextView) findViewById(R.id.class_selected);
        examSelectedTextview =(TextView) findViewById(R.id.exam_type);
        sectionSelectedTextview = (TextView) findViewById(R.id.section_selected);
        skip = (Button) findViewById(R.id.skip_button);
        nextStudent = (ImageView) findViewById(R.id.next_student);
        previousStudent = (ImageView) findViewById(R.id.previous_student);
        enteredMarkAutoCompleteTextview = (android.support.v7.widget.AppCompatAutoCompleteTextView) findViewById(R.id.auto_completeTextview_mark);
        className = (Spinner) findViewById(R.id.class_name);
        section = (Spinner) findViewById(R.id.section);
        examname = (Spinner) findViewById(R.id.exam_name);
        gradeSystem = (Spinner) findViewById(R.id.grade_system);
        markslistSpinner = (Spinner) findViewById(R.id.marks_list);

        displayName = (TextView) findViewById(R.id.display_name_textview);
        displayRollNo = (TextView) findViewById(R.id.display_rollno_textview);
        submit = (Button) findViewById(R.id.submit);
        parentRelativeLayout = (RelativeLayout) findViewById(R.id.parent_relative_layout_marksheet);
        internetConnectionDetector = new InternetConnectionDetector(MainActivity.this);
        markSheetProgressDialog = new ProgressDialog(MainActivity.this);
        setSpinnerDataClassAndSection();

        nextStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (enteredMarkAutoCompleteTextview.getText().toString().isEmpty()) {
                    Snackbar.make(parentRelativeLayout, "You can't move to next student without giving mark/ if absent press skip", Snackbar.LENGTH_SHORT).show();
                } else {
                    studentList.studentDetails.get(initialIndex).scoredMarks = enteredMarkAutoCompleteTextview.getText().toString();
                    enteredMarkAutoCompleteTextview.setText("");
                    if (previousStudent.getVisibility() == View.INVISIBLE || previousStudent.getVisibility() == View.GONE) {
                        previousStudent.setVisibility(View.VISIBLE);
                    }
                    if (initialIndex + 1 == totalStudent - 1) {
                        nextStudent.setVisibility(View.GONE);
                        submit.setVisibility(View.VISIBLE);
                    }

                    loadNextStudent();

                }
            }
        });
        previousStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enteredMarkAutoCompleteTextview.getText().toString().isEmpty()) {
                    Snackbar.make(parentRelativeLayout, "You can't move to previous student without giving marks for the current one,if absent press skip", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (initialIndex - 1 == 0) {
                        previousStudent.setVisibility(View.INVISIBLE);
                    }
                    studentList.studentDetails.get(initialIndex).scoredMarks = enteredMarkAutoCompleteTextview.getText().toString();
                    enteredMarkAutoCompleteTextview.setText("");
                    enteredMarkAutoCompleteTextview.clearFocus();
                    loadPreviousStudent();


                }

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Todo check autoCompleteTextview for null
                //Todo store data to serializable class, display progress dialog and add data to server.
                if (enteredMarkAutoCompleteTextview.getText().toString().isEmpty()) {
                    Snackbar.make(parentRelativeLayout, "You can't move to previous student without giving marks for the current one,if absent press skip", Snackbar.LENGTH_SHORT).show();
                } else {
                    studentList.studentDetails.get(initialIndex).scoredMarks = enteredMarkAutoCompleteTextview.getText().toString();
                    submitAssessmentDataBackToServer();

                }
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredMarkAutoCompleteTextview.setText("absent");


            }
        });


        className.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classItemSelected = parent.getItemAtPosition(position).toString();
                if(classItemSelected.equals("class"))
                {

                }
                else
                {
                    classSelectedTextview.setText(classItemSelected);
                }
               /* if(classItemSelected.equals("class"))
                {
                    section.setVisibility(View.INVISIBLE);
                }
                else
                {
                   section.setVisibility(View.VISIBLE);
                }*/
                loadStudentDataForAssessment();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sectionItemSelected = parent.getItemAtPosition(position).toString();
                if(sectionItemSelected.equals("section"))
                {

                }
                else
                {
                   sectionSelectedTextview.setText(sectionItemSelected);
                }
                loadStudentDataForAssessment();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        examname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                examNameSelected =   parent.getItemAtPosition(position).toString();
                if(examNameSelected.equals("exam type"))
                {

                }
                else
                {
                    examSelectedTextview.setText(examNameSelected);
                }
                loadStudentDataForAssessment();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gradeSystem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gradeNameSelected = parent.getItemAtPosition(position).toString();
                if(gradeNameSelected.equals("Marks"))
                {
                    markslistSpinner.setVisibility(View.VISIBLE);
                }
                else
                {
                    markslistSpinner.setVisibility(View.GONE);
                }
                loadStudentDataForAssessment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void submitAssessmentDataBackToServer() {


        markSheetProgressDialog.setMessage("Writing Data Back to Server");
        markSheetProgressDialog.show();
        String postTestUrl = "https://api.mongolab.com/api/1/databases/myfirstchoicelaundry/collections/test?apiKey=-VZcF6RVY-q-7X6TFd4PBs5x9X6y-p91";
        Gson gson = new Gson();
        String postValues = gson.toJson(studentList.studentDetails);


        JsonObjectRequest volleyJsonArrayPostRequest = new JsonObjectRequest(Request.Method.POST, postTestUrl, postValues, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String value;
                    value = response.toString();
                    Log.i("response", response.toString());
                    Snackbar.make(parentRelativeLayout, "Data added to database Successfully", Snackbar.LENGTH_SHORT).show();
                    studentList.studentDetails.clear();
                    enteredMarkAutoCompleteTextview.setText("");
                    displayName.setText("Name");
                    displayRollNo.setText("RollNo");
                    submit.setVisibility(View.INVISIBLE);
                    nextStudent.setVisibility(View.VISIBLE);
                    submit.setEnabled(false);
                    skip.setEnabled(false);
                    nextStudent.setEnabled(false);
                    previousStudent.setEnabled(false);
                    skip.setEnabled(false);
                    markSheetProgressDialog.dismiss();

                } catch (Exception e) {
                    Log.e("postError", e.toString());

                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("volleyPoster", error.toString());
                error.printStackTrace();
                markSheetProgressDialog.dismiss();
                Snackbar.make(parentRelativeLayout, "Unsuccessfull submit", Snackbar.LENGTH_SHORT).show();
            }
        });
        VolleyClassHandleHttprequest.getInstance().getRequestQueue().add(volleyJsonArrayPostRequest);


    }

    private void loadPreviousStudent() {
        if (initialIndex - 1 >= 0) {
            initialIndex = initialIndex - 1;
            setStudentDataOnTextViews(initialIndex);
            if (initialIndex == 0) {
                Snackbar.make(parentRelativeLayout, "Currently Giving Assessment For First student", Snackbar.LENGTH_SHORT).show();
            }
            if (initialIndex == totalStudent - 2 && submit.getVisibility() == View.VISIBLE) {
                submit.setVisibility(View.INVISIBLE);
                nextStudent.setVisibility(View.VISIBLE);
            }
        }

    }

    private void loadNextStudent() {
         /* Check's whether total student and current Index+1 matches are not overlapping,

           *
          */
        if (initialIndex + 1 < totalStudent) {
            initialIndex = initialIndex + 1;
            setStudentDataOnTextViews(initialIndex);

        } else {
            Snackbar.make(parentRelativeLayout, "Providing Mark for the final student in class", Snackbar.LENGTH_SHORT).show();
        }


    }

    private void setStudentDataOnTextViews(int index) {
        displayName.setText(studentList.studentDetails.get(index).name);
        displayRollNo.setText(studentList.studentDetails.get(index).rollno);
        if (!studentList.studentDetails.get(index).scoredMarks.isEmpty()) {
            enteredMarkAutoCompleteTextview.setText(studentList.studentDetails.get(index).scoredMarks);
        } else {
            enteredMarkAutoCompleteTextview.setText("");
        }

    }


    private void loadStudentDataForAssessment() {

        if (classItemSelected.equals("class")) {
            Snackbar.make(parentRelativeLayout, "Choose  Class to Proceed Further", Snackbar.LENGTH_SHORT).show();
        }
        else if (sectionItemSelected.equals("section")) {
            Snackbar.make(parentRelativeLayout, "Choose a Section to Proceed Further", Snackbar.LENGTH_SHORT).show();

        }
        else if (examNameSelected.equals("exam type"))
        {
            Snackbar.make(parentRelativeLayout, "Choose a exam Type to Proceed Further", Snackbar.LENGTH_SHORT).show();


        }
        else if(gradeNameSelected.equals("Grade system"))
        {
            Snackbar.make(parentRelativeLayout, "Choose a Grade System to Proceed Further", Snackbar.LENGTH_SHORT).show();

        }
        else if (classItemSelected.equals("class") && sectionItemSelected.equals("section") && examNameSelected.equals("exam type") && gradeNameSelected.equals("Grade system"))
        {
            /*
            *Do nothing, By default spinner will select the first item, so first item in both drop donw is select
            * classname and section spinner will fire onItemClickByDefault, so this function will handle it and does nothing.
            */
        }
        else
        {
            isInternetConnected = internetConnectionDetector.isConnectingToInternet();
            if (isInternetConnected) {
                getStudentDetailDataFromServer();
                markSheetProgressDialog.setMessage("Getting Student Data For Assessment");
                markSheetProgressDialog.show();
            }


        }


    }

    private void getStudentDetailDataFromServer() {

        String url = "https://demo5585860.mockable.io/marksheettest";
        JsonArrayRequest volleyJsonArrayRequest = new JsonArrayRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Gson gson = new Gson();
                    // ArrayList<StudentDetail> studentDetails = new ArrayList<StudentDetail>();
                    Type arrayType = new TypeToken<ArrayList<StudentDetail>>() {
                    }.getType();
                    studentList.studentDetails = gson.fromJson(response.toString(), arrayType);
                    initialIndex = 0;
                    totalStudent = studentList.studentDetails.size();
                    previousStudent.setVisibility(View.INVISIBLE);
                    displayName.setText(studentList.studentDetails.get(0).name);
                    displayRollNo.setText(studentList.studentDetails.get(0).rollno);
                    previousStudent.setEnabled(true);
                    skip.setEnabled(true);
                    nextStudent.setEnabled(true);
                    submit.setEnabled(true);
                    skip.setEnabled(true);
                    markSheetProgressDialog.dismiss();
                } catch (Exception e) {
                    Log.e("getstudentDetailEx", e.toString());
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleyClassHandleHttprequest.getInstance().getRequestQueue().add(volleyJsonArrayRequest);

    }


    private void setSpinnerDataClassAndSection()
    {
       /* Setting Dropdown spinner value with data from String.xml string array classname, section.
        *
        */
        try {
            classSpinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.classname));
            classSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            className.setAdapter(classSpinnerAdapter);
        } catch (Exception e) {
            Log.e("classSpinnerMsheet", e.toString());
            e.printStackTrace();
        }

        try {
            sectionSpinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.section));
            sectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            section.setAdapter(sectionSpinnerAdapter);

        } catch (Exception e) {
            Log.e("classSpinnerMsheet", e.toString());
            e.printStackTrace();
        }
        try {
            gradeSystemSpinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Grade_system));
            sectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            gradeSystem.setAdapter(gradeSystemSpinnerAdapter);
        } catch (Exception e) {
            Log.e("gradeSystemSpinnerer", e.toString());
            e.printStackTrace();
        }
        try {
            examNameSpinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.exam_type));
            sectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            examname.setAdapter(examNameSpinnerAdapter);
        } catch (Exception e) {
            Log.e("examNameSpinnerer", e.toString());
            e.printStackTrace();
        }
        try
        {
           markListSpinnerAdapter= new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.marks_list));
            sectionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            markslistSpinner.setAdapter(markListSpinnerAdapter);
        }
        catch (Exception e)
        {
            Log.e("marksSpinner",e.toString());
            e.printStackTrace();
        }

    }
}