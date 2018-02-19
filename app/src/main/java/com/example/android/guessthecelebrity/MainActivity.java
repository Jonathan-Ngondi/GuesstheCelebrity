package com.example.android.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

  String result;
  ArrayList<String> imgLinks = new ArrayList<String>();
  ArrayList<String> names = new ArrayList<String>();
  Button button0;
  Button button1;
  Button button2;
  Button button3;
  int locationOfCorrectAnswer;
  ArrayList<String> answers;
  ImageView celebImage;


  public void chooseAnswer(View view) {

    if(view.getTag().equals(Integer.toString(locationOfCorrectAnswer))){

      Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
      generateAnswers();
    }
    else {
      Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
      generateAnswers();
    }
  }



  public void downloadImage(String url){

    ImageDownloader imgdownloadTask = new ImageDownloader();
    Bitmap myImage;
    try {
      myImage = imgdownloadTask.execute(url).get();
      celebImage.setImageBitmap(myImage);


    } catch (Exception e){
      e.printStackTrace();
    }




  }




  public void generateAnswers(){

    if(imgLinks.size()>=4 && names.size()>=4){
      Random rand = new Random();
      int randomImage = rand.nextInt(imgLinks.size());
      int otherAnswers = rand.nextInt(names.size());
      locationOfCorrectAnswer = rand.nextInt(4);

      downloadImage(imgLinks.get(randomImage));
      String correctName = names.get(randomImage);
      String randomName = names.get(otherAnswers);

      answers.clear();

      for(int i=0; i<4; i++){

        if(i==locationOfCorrectAnswer){

          answers.add(correctName);

        }

        while (randomName == correctName){
          otherAnswers = rand.nextInt(names.size());
          randomName = names.get(otherAnswers);
        }
        answers.add(randomName);
        otherAnswers = rand.nextInt(names.size());
        randomName =  names.get(otherAnswers);

      }

      button0.setText(answers.get(0));
      button1.setText(answers.get(1));
      button2.setText(answers.get(2));
      button3.setText(answers.get(3));

    }

  }




  public void htmlSifter(String rawString){


    Pattern patternSrc = Pattern.compile("img src=\"(.*?)\"");
    Matcher matchSrc = patternSrc.matcher(rawString);
    Pattern patternName = Pattern.compile("alt=\"(.*?)\"");
    Matcher matchName = patternName.matcher(rawString);

    while (matchSrc.find() && matchName.find()){

      imgLinks.add(matchSrc.group(1));
      names.add(matchName.group(1));
    }



  }


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    HTMLDownloader script = new HTMLDownloader();
    try {

      result = script.execute("http://www.posh24.se/kandisar").get();

    } catch (Exception e){
      e.printStackTrace();

    }
    answers = new ArrayList<String>();
    button0 = (Button) findViewById(R.id.button0);
    button1 = (Button) findViewById(R.id.button1);
    button2 = (Button) findViewById(R.id.button2);
    button3 = (Button) findViewById(R.id.button3);
    celebImage = (ImageView) findViewById(R.id.imageView);
    htmlSifter(result);
    generateAnswers();






  }

  public class HTMLDownloader extends AsyncTask <String, Void, String>{

    @Override protected String doInBackground(String... html) {

      String result = "";

      try{
        URL url = new URL(html[0]);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream htmlStream = urlConnection.getInputStream();
        InputStreamReader streamReader = new InputStreamReader(htmlStream);

        int data = streamReader.read();
        while (data != -1){

          char current = (char) data;
          result += current;
          data = streamReader.read();

        }
        return result;


      } catch (Exception e){}

      return "Finished";
    }
  }

  public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    @Override protected Bitmap doInBackground(String... urls) {

      try{

        URL url = new URL(urls[0]);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream inputStream = connection.getInputStream();
        Bitmap celebImage = BitmapFactory.decodeStream(inputStream);

        return celebImage;



      } catch (MalformedURLException e){
        e.printStackTrace();
      } catch (IOException e){
        e.printStackTrace();
      }
      return null;
    }


  }
}
