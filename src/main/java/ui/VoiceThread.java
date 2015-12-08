package ui;

/**
 * Created by Scott on 12/6/2015.
 */
public class VoiceThread extends Thread {
    String text;

    public VoiceThread(String input){
        text = input;
    }


    public void run(){
        TextToVoice freeTTS = new TextToVoice(text);
        freeTTS.speak();
    }
}
