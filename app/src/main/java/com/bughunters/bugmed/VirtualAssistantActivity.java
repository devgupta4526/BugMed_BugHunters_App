package com.bughunters.bugmed;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class VirtualAssistantActivity extends AppCompatActivity {
    private SpeechRecognizer recognizer;
    private TextView tvResult;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_assistant);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        System.exit(0);
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        findById();
        initilizeResults();
        initializeTextTospeech();
    }

    private void initializeTextTospeech() {
        tts = new TextToSpeech(  this, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int i){
                if (tts.getEngines().size()==0){
                    Toast.makeText(VirtualAssistantActivity.this, "Engine is not available", Toast.LENGTH_SHORT).show();
                }else{
                    speak("Hi I am Lily, Your personal health assistant...");
                }
            }
        });
    }

    private void speak(String msg) {
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null,  null);

    }

    private void findById() {
        tvResult=(TextView)findViewById(R.id.tvResult);
    }

    private void initilizeResults() {
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            recognizer= SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle bundle) {

                    ArrayList<String> result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    Toast.makeText(VirtualAssistantActivity.this, ""+result.get(0), Toast.LENGTH_SHORT).show();
                    tvResult.setText(result.get(0));
                    response(result.get(0));


                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void response(String msg) {
        String msgs = msg.toLowerCase();
        if (msgs.contains("hi")){
            speak("Hello Sir! How are you?");
        }
        if (msgs.contains("fine")){
            speak( "Its good to know that you are fine...");
            speak( "How can I help you?");
        }
        if (msgs.contains("who")){
            if (msgs.contains("created")){
                if (msgs.contains("you")){
                    speak( "I was Created by The team Bug Hunters.");
                }
            }

        }
        if (msgs.contains("who")){
            if (msgs.contains("made")){
                if (msgs.contains("you")){
                    speak( "I was Created by The team Bug Hunters.");
                }
            }

        }
        if (msgs.contains("give")){
            if (msgs.contains("your")){
                if (msgs.contains("introduction")){
                    speak( "Hii, I am Lily. Your personal health assistant.");
                }
            }

        }
        if (msgs.contains("who")){
            if (msgs.contains("are")){
                if (msgs.contains("are")){
                    speak( "Hii, I am Lily. Your personal health assistant.");
                }
            }

        }
        if (msgs.contains("what")){
            if (msgs.contains("can")){
                if (msgs.contains("you")){
                    if (msgs.contains("do")){
                        speak("I was created with a purpose to give you some tips on your health. And I can also prescribe Medicines for You whenever needed. Just tell me the symptoms.");
                    }

                }
            }

        }
        if (msgs.contains("how")){
            if (msg.contains("can")){
                if (msgs.contains("you")){
                    if (msgs.contains("help")){
                        speak( "I was created with a purpose to give you some tips on your health. And I can also prescribe Medicines for You whenever needed. Just tell me the symptoms.");
                    }
                }
            }

        }
        if (msgs.contains("i")){
            if (msgs.contains("have")){
                if (msgs.contains("fever")){
                    speak( "You can take Paracetamol or Asprin... In case it doesnot work, please contact a doctor.");
                }
            }

        }
        if (msgs.contains("i")){
            if (msgs.contains("am")){
                if (msgs.contains("having")){
                    if (msgs.contains("fever")){
                        speak( "You can take Paracetamol or Asprin... In case it doesnot work, please contact a doctor.");
                    }
                    if (msgs.contains("headache")){
                        speak( "I recommend you to take Disprin.");
                    }
                    if (msgs.contains("cold")){
                        if (msgs.contains("and")){
                            if (msgs.contains("cough")){
                                speak( "You can have Amoxicillin.");
                            }
                        }
                    }
                    if (msgs.contains("diabetes")){
                        speak( "You can take Insulin Lispro but please contact a doctor for this.");
                    }
                    if (msgs.contains("stomach")){
                        if (msgs.contains("pain")){
                            speak( "You must have ate something unhealthy!! Don't worry, you can take Panadol.");
                        }
                    }
                    if (msgs.contains("skin")){
                        if (msgs.contains("allergies")){
                            speak( "You can take Allegra... But please contact a skin expert as soon as possible.");
                        }
                    }
                }
            }
            if (msgs.contains("have")){
                if (msgs.contains("chicken")){
                    if (msgs.contains("pox")){
                        speak( "You can take a bath with cold water added with baking soda...or you can also take Benadryl.");
                    }
                }
                if (msgs.contains("arthritis")){
                    speak( "You can take Immunosupressive drug and analgesics and please contact a physician.");
                }
                if (msgs.contains("asthma")){
                    speak( "You can take Anti Anflammatory drugs..... and Please stay way from cigarettes and other smokers.");
                }
                if (msgs.contains("bipolar")){
                    if (msgs.contains("disorder")){
                        speak( "You can take any Antipsychotic drugs.");
                    }
                }
                if (msgs.contains("chest")){
                    if (msgs.contains("pain")){
                        speak( "You can take Nitroglycerine drugs.");
                    }
                }
                if (msgs.contains("conjunctvitis")){
                    speak( "You should maintain hygeine and can self heal with cold compress.");
                }
                if (msgs.contains("constipation")){
                    speak( "You are recommended to take Stool Softener and Fibre supplements.");
                }
            }
            if (msgs.contains("am")){
                if (msgs.contains("a")){
                    if (msgs.contains("dibetic")){
                        if (msgs.contains("patient")){
                            speak("You can take Insulin Lispro but please contact a doctor for this.");
                        }
                    }
                    if (msgs.contains("asthma")){
                        if (msgs.contains("patient")){
                            speak( "You can take Anti Anflammatory drugs..... and Please stay way from cigarettes and other smokers.");
                        }
                    }
                }
                if (msgs.contains("in")){
                    if (msgs.contains("depression")){
                        speak( "Lexapro will give you relief.");
                    }
                }
                if (msgs.contains("depressed")){
                    speak( "Lexapro will give you relief.");
                }
                if (msgs.contains("dehydrated")){
                    speak( "You can take Oral Rehydration solution....and please drink a lot of water.");
                }
            }
            if (msgs.contains("feel")){
                if (msgs.contains("like")){
                    if (msgs.contains("vomiting")){
                        speak( "You can take Dolasetron.");
                    }
                }
            }
        }
        if (msgs.contains("food poisoining")){
            speak( "Please ensure Adequate Hydration and take rehydration solution and contact a doctor as early as posible.");
        }
        if (msgs.contains("flu")){
            speak( "I recommend you to take Antiviral drugs and to take good rest.");
        }
        if (msgs.contains("indigestion")){
            speak( "You can take Antacids and Oral Suspension medicines.");
        }
        if (msgs.contains("insomnia")){
            speak( "You can have Sedatives and Anti depressants.");
        }
        if (msgs.contains("malaria")){
            speak( "You can take Anti Parasites and Antibiotics.");
        }
        if (msgs.contains("malnutrition")){
            speak( "You should take high protein diet and nutiritive suppliments.");
        }
        if (msgs.contains("obesity")){
            speak("You should indulge in physical exercises and should take low fat diet.");
        }
        if (msgs.contains("panic disorder")){
            speak( "You can take Anti Depressants.");
        }
        if (msgs.contains("scabies")){
            speak( "You can take Anti Parasite drugs.");
        }
        if (msgs.contains("yellow fever")){
            speak( "You can take Anti Depressants.");
        }
        if (msgs.contains("acne")){
            speak( "You can apply Aloe Vera and take Vitamin A derivatives.");
        }
        if (msgs.contains("my")){
            if (msgs.contains("head")){
                if (msgs.contains("is")){
                    if (msgs.contains("paining")){
                        speak( "I recommend you to take Disprin.");
                    }
                }
            }
            if (msgs.contains("stomach")){
                if (msgs.contains("is")){
                    if (msgs.contains("paining")){
                        speak( "You must have ate something unhealthy!! Don't worry, you can take Panadol.");
                    }
                }
            }
            if (msgs.contains("chest")){
                if (msgs.contains("is")){
                    if (msgs.contains("paining")){
                        speak( "You can take Nitroglycerine drugs.");
                    }
                }
            }
        }
//        else{
//            speak("I have no idea about this, you can ask me something else.");
//        }
    }


    public void startRecording(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        recognizer.startListening(intent);

    }
}