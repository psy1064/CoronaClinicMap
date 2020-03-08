package com.example.coronaclinicmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadingActivity extends AppCompatActivity {
    final String TAG = "Loading Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ArrayList<Clinic> arrayList = xml_parse();
        Log.d(TAG, String.valueOf(arrayList.size()));
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        intent.putExtra("clinic", arrayList);
        startActivity(intent);
    }

    private ArrayList<Clinic> xml_parse() {
        ArrayList<Clinic> clinicsList = new ArrayList<Clinic>();
        InputStream inputStream = getResources().openRawResource(R.raw.selectiveclinic_20200305);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        XmlPullParserFactory xmlPullParserFactory = null;
        XmlPullParser xmlPullParser = null;

        try {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(reader);

            Clinic clinic = null;
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        Log.i(TAG, "xml START");
                        break;
                    case XmlPullParser.START_TAG:
                        String startTag = xmlPullParser.getName();
                        Log.i(TAG, "Start TAG :" + startTag);
                        if(startTag.equals("Row")) {
                            clinic = new Clinic();
                            Log.d(TAG, "clinic 추가");
                        }
                        else if(startTag.equals("연번")) {
                            clinic.setNumber(xmlPullParser.nextText());
                            Log.d(TAG, clinic.getNumber());
                            Log.d(TAG, "clinic 연변");
                        }
                        else if(startTag.equals("검체채취가능여부")) {
                            clinic.setSample(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("시도")) {
                            clinic.setCity(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("시군구")) {
                            clinic.setDistrict(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("의료기관명")) {
                            clinic.setName(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("주소")) {
                            clinic.setAddress(xmlPullParser.nextText());
                        }
                        else if(startTag.equals("대표전화번호")) {
                            clinic.setPhoneNumber(xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = xmlPullParser.getName();
                        Log.i(TAG,"End TAG : "+ endTag);
                        if (endTag.equals("Row")) {
                            clinicsList.add(clinic);
                        }
                        break;
                }
                try {
                    eventType = xmlPullParser.next();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(reader !=null) reader.close();
                if(inputStreamReader !=null) inputStreamReader.close();
                if(inputStream !=null) inputStream.close();
            }catch(Exception e2){
                e2.printStackTrace();
            }
        }
        return clinicsList;
    }
}
