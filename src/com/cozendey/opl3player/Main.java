package com.cozendey.opl3player;

import com.cozendey.opl3.OPL3;
import com.cozendey.opl3player.CmidPlayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
	// write your code here

        OPL3 fm = new OPL3(OPL3.MODE_OPL3);
        CmidPlayer player = new CmidPlayer((fm));

        //code looks like a port from C done using a code converter. Not java. just crap and hard to deal with.

        //load a smf and see what happens. code is using default package access
        // so I put my main class in same package.

        //read a midi file into byte array
        String fileName = args[0];
        Path path = Paths.get(fileName);

        byte[] midiData = new byte[0];
        try {
            midiData = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


        try {
            player.load(midiData);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        /*
        player.midi_fm_playnote(1, 64, 100);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        player.midi_fm_endnote(1);
        */


        //how to get my opl3 data???
        int count = 0;

        while (null != fm.read() && (count < 1000)) {
            count++;
            print(fm.read());

        }

    }

    private static void print(short[] read) {

        for (int i = 0; i < read.length; i++) System.out.print(read[i] + " ");
    }
}
