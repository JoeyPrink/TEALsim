/*
 * TEALsim - MIT TEAL Project
 * Copyright (c) 2004 The Massachusetts Institute of Technology. All rights reserved.
 * Please see license.txt in top level directory for full license.
 * 
 * http://icampus.mit.edu/teal/TEALsim
 * 
 * $Id: MidiSynthesizer.java,v 1.7 2007/07/16 22:04:43 pbailey Exp $
 * 
 */

package teal.audio;

import javax.sound.midi.*;

/**
 *  
 * @author Andrew McKinney
 * @author Phil Bailey
 * @author Michael Danziger
 * @version $Revision: 1.7 $
 */

/**
 * MidiSynthesizer can be used to play notes on an available midi device (typically the soundcard, by default).  No 
 * available midi devices will result in a MidiUnavailableException.
 */
public class MidiSynthesizer {

    private Synthesizer synth;
    private Receiver receiver;

    public MidiSynthesizer() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            receiver = synth.getReceiver();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void startNote(int note) {
        startNote(note, 70);
    }

    public void startNote(int note, int vel) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(ShortMessage.NOTE_ON, 0, note, vel);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        receiver.send(message, -1);
    }

    public void stopNote(int note) {
        stopNote(note, 70);
    }

    public void stopNote(int note, int vel) {
        ShortMessage message = new ShortMessage();

        try {
            message.setMessage(ShortMessage.NOTE_OFF, 0, note, vel);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        receiver.send(message, -1);
    }

    public void playNote(int note, int duration) {
        playNote(note, duration, 70);
    }

    public void playNote(int note, int duration, int vel) {
        startNote(note, vel);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopNote(note, vel);
    }

    public void listAvailableInstruments() {
        Instrument[] instrument = synth.getAvailableInstruments();
        for (int i = 0; i < instrument.length; i++) {
            System.out.println(i + "   " + instrument[i].getName());
        }
    }

    public void setInstrument(int instrument) {
        synth.getChannels()[0].programChange(instrument);
    }

    public void playMajorChord(int baseNote) {
        playNote(baseNote, 1000);
        playNote(baseNote + 4, 1000);
        playNote(baseNote + 7, 1000);
        startNote(baseNote);
        startNote(baseNote + 4);
        playNote(baseNote + 7, 2000);
        stopNote(baseNote + 4);
        stopNote(baseNote);
    }

}
