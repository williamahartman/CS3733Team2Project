package ui;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory;

import java.util.Properties;

/**
 * Created by Scott on 12/6/2015.
 */
public class TextToVoice  extends Thread{
    private String text; // string to speech

    public TextToVoice(String text) {
        this.text = text;
    }

    public void speak() {
        Voice voice;
        KevinVoiceDirectory kevins = new KevinVoiceDirectory();
        voice = kevins.getVoices()[1];
        voice.setRate(110);
        voice.allocate();
        voice.speak(text);
        voice.deallocate();
    }
    public void run(){
        this.speak();
    }
}
