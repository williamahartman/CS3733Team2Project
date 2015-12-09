package ui;
import com.sun.glass.ui.SystemClipboard;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.*;

import java.io.File;
import java.util.Properties;

/**
 * Created by Scott on 12/6/2015.
 */
public class TextToVoice  extends Thread{
    private static final String VOICENAME_kevin = "kevin";
    private String text; // string to speech

    public TextToVoice(String text) {
        this.text = text;
        Properties props = System.getProperties();
        String path = System.getProperty("user.dir");
        path = path + "/freetts.voicesfile";
        props.setProperty("freetts.voicesfile", path);
    }

    public void speak() {
        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(VOICENAME_kevin);
        voice.setRate(110);
        voice.allocate();
        voice.speak(text);
        voice.deallocate();
    }
    public void run(){
        this.speak();
    }
}
