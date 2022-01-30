/*
 * File: CmidPlayer.java
 * Java translation of original C++ software by Phil Hassey and Simon Peter.
 * This translation copyright (C) 2008 Robson Cozendey <robson.cozendey.rj@gmail.com>
 * Original copyright notices available ahead.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */ 

package com.cozendey.opl3player;

//******************************************************************************    
// mid.h:  
//******************************************************************************
    
/*
 * Adplug - Replayer for many OPL2/OPL3 audio file formats.
 * Copyright (C) 1999 - 2005 Simon Peter, <dn.tlp@gmx.net>, et al.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * mid.h - LAA, SCI, MID & CMF Player by Philip Hassey <philhassey@hotmail.com>
 */

import com.cozendey.opl3.OPL3;

import java.io.*;

abstract class CmidPlayer_h {

// Only data in mid.h are covered here. Method prototypes are declared only in 
// the class CmidPlayer    
    
  abstract boolean load(byte[] file) throws FileNotFoundException, IOException;  
  abstract boolean update();
  abstract void rewind(int subsong);
  abstract float getrefresh();
  abstract String gettype();
  StringBuffer gettitle() {
      return title;
  }
  StringBuffer getauthor() {
      return author;
  }
  StringBuffer getdesc() {
      return remarks;
  }
  int getinstruments() {
      return tins;
  }
  int getsubsongs() {
    return subsongs;
  }
    

 class midi_channel {
    int inum;
    int[] ins = new int[11];
    int vol;
    int nshift;
    int on;
 }; 
  
  class midi_track {
    long tend;
    long spos;
    long pos;
    long iwait;
    int on;
    int pv;
  };

  StringBuffer author,title,remarks,emptystr;
  long flen;
  long pos;
  long sierra_pos; 
  int subsongs;
  int[] data;
  int[] adlib_data = new int[256];
  int adlib_style;
  int adlib_mode;
  int[][] myinsbank = new int[128][16];
  int[][] smyinsbank = new int[128][16];
  midi_channel[] ch = new midi_channel[16];
  
  int[][] chp = new int[18][3];
  long deltas;
  long msqtr;
  midi_track[] track = new midi_track[16];
  int curtrack;
  float fwait;
  long iwait;
  int doing;
  int type,tins,stins;

  abstract boolean load_sierra_ins(String fname) throws FileNotFoundException, IOException;
  abstract void midiprintf(String format, Object... args);
  abstract int datalook(long pos);
  abstract long getnexti(long num);
  abstract long getnext(long num);
  abstract long getval();
  abstract void sierra_next_section();
  abstract void midi_write_adlib(int r, int v);
  abstract void midi_fm_instrument(int voice, int[] inst);
  abstract void midi_fm_percussion(int ch, int[] inst);
  abstract void midi_fm_volume(int voice, int volume);
  abstract void midi_fm_playnote(int voice, int note, int volume);
  abstract void midi_fm_endnote(int voice);
  abstract void midi_fm_reset();

}


    
/*
 * Adplug - Replayer for many OPL2/OPL3 audio file formats.
 * Copyright (C) 1999 - 2006 Simon Peter, <dn.tlp@gmx.net>, et al.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * MIDI & MIDI-like file player - Last Update: 10/15/2005
 *                  by Phil Hassey - www.imitationpickles.org
 *                                   philhassey@hotmail.com
 *
 * Can play the following
 *      .LAA - a raw save of a Lucas Arts Adlib music
 *             or
 *             a raw save of a LucasFilm Adlib music
 *      .MID - a "midi" save of a Lucas Arts Adlib music
 *           - or general MIDI files
 *      .CMF - Creative Music Format
 *      .SCI - the sierra "midi" format.
 *             Files must be in the form
 *             xxxNAME.sci
 *             So that the loader can load the right patch file:
 *             xxxPATCH.003  (patch.003 must be saved from the
 *                            sierra resource from each game.)
 *
 * 6/2/2000:  v1.0 relased by phil hassey
 *      Status:  LAA is almost perfect
 *                      - some volumes are a bit off (intrument too quiet)
 *               MID is fine (who wants to listen to MIDI vid adlib anyway)
 *               CMF is okay (still needs the adlib rythm mode implemented
 *                            for real)
 * 6/6/2000:
 *      Status:  SCI:  there are two SCI formats, orginal and advanced.
 *                    original:  (Found in SCI/EGA Sierra Adventures)
 *                               played almost perfectly, I believe
 *                               there is one mistake in the instrument
 *                               loader that causes some sounds to
 *                               not be quite right.  Most sounds are fine.
 *                    advanced:  (Found in SCI/VGA Sierra Adventures)
 *                               These are multi-track files.  (Thus the
 *                               player had to be modified to work with
 *                               them.)  This works fine.
 *                               There are also multiple tunes in each file.
 *                               I think some of them are supposed to be
 *                               played at the same time, but I'm not sure
 *                               when.
 * 8/16/2000:
 *      Status:  LAA: now EGA and VGA lucas games work pretty well
 *
 * 10/15/2005: Changes by Simon Peter
 *	Added rhythm mode support for CMF format.
 *
 * Other acknowledgements:
 *  Allegro - for the midi instruments and the midi volume table
 *  SCUMM Revisited - for getting the .LAA / .MIDs out of those
 *                    LucasArts files.
 *  FreeSCI - for some information on the sci music files
 *  SD - the SCI Decoder (to get all .sci out of the Sierra files)
 */
  

public class CmidPlayer extends CmidPlayer_h {
    
void midiprintf(String format, Object... args) { };

final int LUCAS_STYLE =  1;
final int CMF_STYLE = 2;        
final int MIDI_STYLE = 4;
final int SIERRA_STYLE = 8;
final int ADLIB_MELODIC	= 0;
final int ADLIB_RYTHM =	1;
final int FILE_LUCAS = 1;
final int FILE_MIDI = 2;
final int FILE_CMF = 3;
final int FILE_SIERRA = 4;
final int FILE_ADVSIERRA = 5;
final int FILE_OLDLUCAS = 6;

static final int[] adlib_opadd = {0x00  ,0x01 ,0x02  ,0x08  ,0x09  ,0x0A  ,0x10 ,0x11  ,0x12}; 

static final int[] ops = {0x20,0x20,0x40,0x40,0x60,0x60,0x80,0x80,0xe0,0xe0,0xc0};
// map CMF drum channels 12 - 15 to corresponding AdLib drum operators
// bass drum (channel 11) not mapped, cause it's handled like a normal instrument
static final int[] map_chan = { 0x14, 0x12, 0x15, 0x11 };
// Standard AdLib frequency table
static final int[] fnums = { 0x16b,0x181,0x198,0x1b0,0x1ca,0x1e5,0x202,0x220,0x241,0x263,0x287,0x2ae };
// Map CMF drum channels 11 - 15 to corresponding AdLib drum channels
static final int[] percussion_map = { 6, 7, 8, 8, 7 };
static CmidPlayer factory(OPL3 opl3)
{
  return new CmidPlayer(opl3);
}
OPL3 opl;       

public CmidPlayer(OPL3 opl3)
{
    opl = opl3;
    author = title = remarks = emptystr = null;
    flen = 0;
    data = null;
    for(int i=0; i<ch.length; i++) ch[i] = new midi_channel();
    for(int i=0; i<track.length; i++) track[i] = new midi_track();
}

int datalook(long pos)
{
    if (pos<0 || pos >= flen) return(0);
    return(data[(int)pos]);
}

long getnexti(long num)
{
	long v=0;
	long i;
    for (i=0; i<num; i++)
        {
        v+=(datalook(pos)<<(8*i)); pos++;
        }
    return(v);
}

long getnext(long num)
{
    long v=0;
    long i;

    for (i=0; i<num; i++)
        {
        v<<=8;
        v+=datalook(pos); pos++;
        }
    return(v);
}

long getval()
{
    int v=0;
    int b;

    b=(int)getnext(1);
	v=b&0x7f;
	while ((b&0x80) !=0)
		{
        b=(int)getnext(1);    
        v = (v << 7) + (b & 0x7F);
		}
	return(v);
}

boolean load_sierra_ins(String fname) throws FileNotFoundException, IOException
{
    long i,j,k,l;
    int[] ins = new int[28];
    StringBuffer pfilename;
    FileInputStream f; 
    pfilename = new StringBuffer(fname.length()+9);
    pfilename.append(fname);
    j=0;
    for(i=pfilename.length()-1; i >= 0; i--)
        if(pfilename.charAt((int)i) == '/' || pfilename.charAt((int)i) == '\\') {
	j = i+1;
	break;
      }
    pfilename.replace((int)(j+3), pfilename.length()-1, "patch.003");
    File file = new File(pfilename.toString());
    f = new FileInputStream(file);
    
    if(f==null) return false;

    f.skip(2);
    stins = 0;
    for (i=0; i<2; i++)
        {
        for (k=0; k<48; k++)
            {
            l=i*48+k;
            midiprintf ("\n%2d: ",l);
            for (j=0; j<28; j++)
                ins[(int)j] = f.read();

            myinsbank[(int)l][0]=
                (ins[9]*0x80) + (ins[10]*0x40) +
                (ins[5]*0x20) + (ins[11]*0x10) +
                ins[1];   //1=ins5
            myinsbank[(int)l][1]=
                (ins[22]*0x80) + (ins[23]*0x40) +
                (ins[18]*0x20) + (ins[24]*0x10) +
                ins[14];  //1=ins18

            myinsbank[(int)l][2]=(ins[0]<<6)+ins[8];
            myinsbank[(int)l][3]=(ins[13]<<6)+ins[21];

            myinsbank[(int)l][4]=(ins[3]<<4)+ins[6];
            myinsbank[(int)l][5]=(ins[16]<<4)+ins[19];
            myinsbank[(int)l][6]=(ins[4]<<4)+ins[7];
            myinsbank[(int)l][7]=(ins[17]<<4)+ins[20];

            myinsbank[(int)l][8]=ins[26];
            myinsbank[(int)l][9]=ins[27];

            myinsbank[(int)l][10]=((ins[2]<<1))+(1-(ins[12]&1));

            for (j=0; j<11; j++)
                midiprintf ("%02X ",myinsbank[(int)l][(int)j]);
			stins++;
            }
		f.skip(2);
        }

    f.close();
    smyinsbank = myinsbank.clone();
    return true;
}

void sierra_next_section()
{
    int i,j;

    for (i=0; i<16; i++)
        track[i].on=0;

    midiprintf("\n\nnext adv sierra section:\n");

    pos=sierra_pos;
    i=0;j=0;
    while (i!=0xff)
       {
       getnext(1);
       curtrack=j; j++;
       track[curtrack].on=1;
	   track[curtrack].spos = getnext(1);
	   track[curtrack].spos += (getnext(1) << 8) + 4;	//4 best usually +3? not 0,1,2 or 5
       track[curtrack].tend=flen; //0xFC will kill it
       track[curtrack].iwait=0;
       track[curtrack].pv=0;
       midiprintf ("track %d starts at %lx\n",curtrack,track[curtrack].spos);

       getnext(2);
       i=(int)getnext(1);
       }
    getnext(2);
    deltas=0x20;
    sierra_pos=pos;

    fwait=0;
    doing=1;
}

boolean load(byte[] file) throws FileNotFoundException, IOException
{
    ByteArrayInputStream f = new ByteArrayInputStream(file);
    if(f==null) return false; 
    
    int good;
    int[] s = new int[6];

    byte[] buffer = new byte[6];
    f.read(buffer);
    for(int i=0;i<6;i++) s[i] = ((int)buffer[i]) & 0xFF; // Trim negative values to unsigned bytes.
    good=0;
    subsongs=0;
    switch(s[0])
        {
        case 'A':
            if (s[1]=='D' && s[2]=='L') 
                good=FILE_LUCAS;
            break;
        case 'M':
            if (s[1]=='T' && s[2]=='h' && s[3]=='d') good=FILE_MIDI;
            break;
        case 'C':
            if (s[1]=='T' && s[2]=='M' && s[3]=='F') good=FILE_CMF;
            break;
        case 0x84:
            if (s[1]==0x00 && load_sierra_ins(file.toString()))
                if (s[2]==0xf0)
                    good=FILE_ADVSIERRA;
                    else
                    good=FILE_SIERRA;
            break;
        default:
            if (s[4]=='A' && s[5]=='D') good=FILE_OLDLUCAS;
            break;
        }

    if (good!=0)
		subsongs=1;
    else {
      f.close();
      return false;
    }

    type=good;
    f.close();
    f = new ByteArrayInputStream(file);
    
    flen = f.available();
    data = new int[(int)flen];
    buffer = new byte[(int)flen];
    f.read(buffer);
    for(int i=0;i<flen;i++) data[i] = ((int)buffer[i]) & 0xFF; // Turn negative values to unsigned bytes.

    f.close();
    rewind(0);
    return true;
}
void midi_write_adlib(int r, int v)
{
  opl.write(0, r, v);  
  adlib_data[r]=v;
  
}

void midi_fm_instrument(int voice, int[] inst)
{
    if ((adlib_style&SIERRA_STYLE)!=0)
        midi_write_adlib(0xbd,0);  //just gotta make sure this happens..
                                      //'cause who knows when it'll be
                                      //reset otherwise.


    midi_write_adlib(0x20+adlib_opadd[voice],inst[0]);
    midi_write_adlib(0x23+adlib_opadd[voice],inst[1]);

    if ((adlib_style&LUCAS_STYLE)!=0)
        {
        midi_write_adlib(0x43+adlib_opadd[voice],0x3f);
        if ((inst[10] & 1)==0)
            midi_write_adlib(0x40+adlib_opadd[voice],inst[2]);
            else
            midi_write_adlib(0x40+adlib_opadd[voice],0x3f);
        }
        else
        {
        if ((adlib_style&SIERRA_STYLE)!=0)
            {
            midi_write_adlib(0x40+adlib_opadd[voice],inst[2]);
            midi_write_adlib(0x43+adlib_opadd[voice],inst[3]);
            }
            else
            {
            midi_write_adlib(0x40+adlib_opadd[voice],inst[2]);
            if ((inst[10] & 1)==0)
                midi_write_adlib(0x43+adlib_opadd[voice],inst[3]);
                else
                midi_write_adlib(0x43+adlib_opadd[voice],0);
            }
        }

    midi_write_adlib(0x60+adlib_opadd[voice],inst[4]);
    midi_write_adlib(0x63+adlib_opadd[voice],inst[5]);
    midi_write_adlib(0x80+adlib_opadd[voice],inst[6]);
    midi_write_adlib(0x83+adlib_opadd[voice],inst[7]);
    midi_write_adlib(0xe0+adlib_opadd[voice],inst[8]);
    midi_write_adlib(0xe3+adlib_opadd[voice],inst[9]);

}

void midi_fm_percussion(int ch, int[] inst)
{
  int	opadd = map_chan[ch - 12];

  midi_write_adlib(0x20 + opadd, inst[0]);
  midi_write_adlib(0x40 + opadd, inst[2]);
  midi_write_adlib(0x60 + opadd, inst[4]);
  midi_write_adlib(0x80 + opadd, inst[6]);
  midi_write_adlib(0xe0 + opadd, inst[8]);
}

void midi_fm_volume(int voice, int volume)
{
    int vol;

    if ((adlib_style&SIERRA_STYLE)==0)  //sierra likes it loud!
    {
    vol=volume>>2;

    if ((adlib_style&LUCAS_STYLE)!=0)
        {
        if ((adlib_data[0xc0+voice]&1)==1)
            midi_write_adlib(0x40+adlib_opadd[voice], (int)((63-vol) |
            (adlib_data[0x40+adlib_opadd[voice]]&0xc0)));
        midi_write_adlib(0x43+adlib_opadd[voice], (int)((63-vol) |
            (adlib_data[0x43+adlib_opadd[voice]]&0xc0)));
        }
        else
        {
        if ((adlib_data[0xc0+voice]&1)==1)
            midi_write_adlib(0x40+adlib_opadd[voice], (int)((63-vol) |
            (adlib_data[0x40+adlib_opadd[voice]]&0xc0)));
        midi_write_adlib(0x43+adlib_opadd[voice], (int)((63-vol) |
           (adlib_data[0x43+adlib_opadd[voice]]&0xc0)));
        }
    }
}

void midi_fm_playnote(int voice, int note, int volume)
{
if(note<0) note = 12-(note % 12);
    int freq=fnums[note%12];
    int oct=note/12;
	int c;

    midi_fm_volume(voice,volume);
    midi_write_adlib(0xa0+voice,(int)(freq&0xff));

	c=((freq&0x300) >> 8)+(oct<<2) + (adlib_mode == ADLIB_MELODIC || voice < 6 ? (1<<5) : 0);
    midi_write_adlib(0xb0+voice,(int)c);
}

void midi_fm_endnote(int voice)
{
    midi_write_adlib(0xb0+voice,(int)(adlib_data[0xb0+voice]&(255-32)));
}

void midi_fm_reset()
{
    int i;

    for (i=0; i<256; i++)
        midi_write_adlib(i,0);
    
    for(i=0xC0; i<=0xC8; i++) midi_write_adlib(i,0xF0);
    
    midi_write_adlib(0x01, 0x20);
    midi_write_adlib(0xBD,0xc0);
}

boolean update()
{
    long w,v,note,vel,ctrl,nv,x,l,lnum;
    int i=0,j,c;
    int on,onl,numchan;
    int ret;

    if (doing == 1)
        {
        for (curtrack=0; curtrack<16; curtrack++)
            if (track[curtrack].on != 0)
                {
                pos=track[curtrack].pos;
                if (type != FILE_SIERRA && type !=FILE_ADVSIERRA)
                    track[curtrack].iwait+=getval();
                    else
                    track[curtrack].iwait+=getnext(1);
                track[curtrack].pos=pos;
                }
        doing=0;
        }

    iwait=0;
    ret=1;

    while (iwait==0 && ret==1)
        {
        for (curtrack=0; curtrack<16; curtrack++)
        if (track[curtrack].on != 0 && track[curtrack].iwait==0 &&
            track[curtrack].pos < track[curtrack].tend)
        {
        pos=track[curtrack].pos;

		v=getnext(1);

        if (v<0x80) {v=track[curtrack].pv; pos--;}
        track[curtrack].pv=(int)v;

		c=(int)(v&0x0f);
        midiprintf ("[%2X]",v);
        switch((int)v&0xf0)
            {
			case 0x80: //note off
				note=getnext(1); vel=getnext(1);
                for (i=0; i<9; i++)
                    if (chp[i][0]==c && chp[i][1]==note)
                        {
                        midi_fm_endnote(i);
                        chp[i][0]=-1;
                        }
                break;
            case 0x90: //note on
                note=getnext(1); vel=getnext(1);

		if(adlib_mode == ADLIB_RYTHM)
		  numchan = 6;
		else
		  numchan = 9;

                if (ch[c].on!=0)
                {
		  for (i=0; i<18; i++)
                    chp[i][2]++;

		  if(c < 11 || adlib_mode == ADLIB_MELODIC) {
		    j=0;
		    on=-1;onl=0;
		    for (i=0; i<numchan; i++)
		      if (chp[i][0]==-1 && chp[i][2]>onl)
			{ onl=chp[i][2]; on=i; j=1; }

		    if (on==-1)
		      {
			onl=0;
			for (i=0; i<numchan; i++)
			  if (chp[i][2]>onl)
			    { onl=chp[i][2]; on=i; }
		      }

		    if (j==0)
		      midi_fm_endnote(on);
		  } else
		    on = percussion_map[c - 11];

                 if (vel!=0 && ch[c].inum>=0 && ch[c].inum<128)
                    {

                    if (adlib_mode == ADLIB_MELODIC || c < 12)
		      midi_fm_instrument(on,ch[c].ins);
		    else
 		      midi_fm_percussion(c, ch[c].ins);

                    if ((adlib_style&MIDI_STYLE)!=0)
                        {
                        nv=((ch[c].vol*vel)/128);
                        if ((adlib_style&LUCAS_STYLE)!=0)
                            nv*=2;
                        if (nv>127) nv=127;
                        nv=my_midi_fm_vol_table[(int)nv];
                        if ((adlib_style&LUCAS_STYLE)!=0)
                            nv=(int)((float)Math.sqrt((float)nv)*11);
                        }
                        else
                        {
                        nv=vel;
                        }

		    midi_fm_playnote(on,(int)(note+ch[c].nshift),(int)nv*2);
                    
                    chp[on][0]=c;
                    chp[on][1]=(int)note;
                    chp[on][2]=0;

		    if(adlib_mode == ADLIB_RYTHM && c >= 11) {
		      midi_write_adlib(0xbd, adlib_data[0xbd] & ~(0x10 >> (c - 11)));
		      midi_write_adlib(0xbd, adlib_data[0xbd] | (0x10 >> (c - 11)));
		    }

                    }
                    else
                    {
                    if (vel==0)  //same code as end note
                        {
                        for (i=0; i<9; i++)
                            if (chp[i][0]==c && chp[i][1]==note)
                                {
                                midi_fm_endnote(i);
                                chp[i][0]=-1;
                                }
                        }
                        else
                        {        // i forget what this is for.
                        chp[on][0]=-1;
                        chp[on][2]=0;
                        }
                    }
                midiprintf(" [%d:%d:%d:%d]\n",c,ch[c].inum,note,vel);                
                }
                else
                midiprintf ("off");
                break;
            case 0xa0: //key after touch 
                note=getnext(1); vel=getnext(1);
                
                break;
            case 0xb0: //control change .. pitch bend? 
                ctrl=getnext(1); vel=getnext(1);

                switch((int)ctrl)
                    {
                    case 0x07:
                        midiprintf ("(pb:%d: %d %d)",c,ctrl,vel);
                        ch[c].vol=(int)vel;
                        midiprintf("vol");
                        break;
                    case 0x67:
                        midiprintf ("\n\nhere:%d\n\n",vel);
                        if ((adlib_style&CMF_STYLE)!=0) {
			  adlib_mode=(int)vel;
			  if(adlib_mode == ADLIB_RYTHM)
			    midi_write_adlib(0xbd, adlib_data[0xbd] | (1 << 5));
			  else
			    midi_write_adlib(0xbd, adlib_data[0xbd] & ~(1 << 5));
			}
                        break;
                    }
                break;
            case 0xc0: //patch change
	      x=getnext(1);
	      ch[c].inum=(int)x;
	      for (j=0; j<11; j++)
		ch[c].ins[j]=myinsbank[ch[c].inum][j];
	      break;
            case 0xd0: //chanel touch
                x=getnext(1);
                break;
            case 0xe0: //pitch wheel
                x=getnext(1);
                x=getnext(1);
                break;
            case 0xf0:
                switch((int)v)
                    {
                    case 0xf0:
                    case 0xf7: //sysex
		      l=getval();
		      if (datalook(pos+l)==0xf7)
			i=1;
		      midiprintf("{%d}",l);
		      midiprintf("\n");

                        if (datalook(pos)==0x7d &&
                            datalook(pos+1)==0x10 &&
                            datalook(pos+2)<16)
							{
                            adlib_style=LUCAS_STYLE|MIDI_STYLE;
							for (i=0; i<l; i++)
								{
                                midiprintf ("%x ",datalook(pos+i));
                                if ((i-3)%10 == 0) midiprintf("\n");
								}
                            midiprintf ("\n");
                            getnext(1);
                            getnext(1);
							c=(int)getnext(1);
							getnext(1);

                            ch[c].ins[0]=(int)((getnext(1)<<4)+getnext(1));
                            ch[c].ins[2]=(int)(0xff-(((getnext(1)<<4)+getnext(1))&0x3f));
                            ch[c].ins[4]=(int)(0xff-((getnext(1)<<4)+getnext(1)));
                            ch[c].ins[6]=(int)(0xff-((getnext(1)<<4)+getnext(1)));
                            ch[c].ins[8]=(int)((getnext(1)<<4)+getnext(1));

                            ch[c].ins[1]=(int)((getnext(1)<<4)+getnext(1));
                            ch[c].ins[3]=(int)(0xff-(((getnext(1)<<4)+getnext(1))&0x3f));
                            ch[c].ins[5]=(int)(0xff-((getnext(1)<<4)+getnext(1)));
                            ch[c].ins[7]=(int)(0xff-((getnext(1)<<4)+getnext(1)));
                            ch[c].ins[9]=(int)((getnext(1)<<4)+getnext(1));

                            i=(int)((getnext(1)<<4)+getnext(1));
                            ch[c].ins[10]=i;


                            midiprintf ("\n%d: ",c);
							for (i=0; i<11; i++)
                                midiprintf ("%2X ",ch[c].ins[i]);
                            getnext(l-26);
							}
                            else
                            {
                            midiprintf("\n");
                            for (j=0; j<l; j++)
                                midiprintf ("%2X ",getnext(1));
                            }

                        midiprintf("\n");
						if(i==1)
							getnext(1);
                        break;
                    case 0xf1:
                        break;
                    case 0xf2:
                        getnext(2);
                        break;
                    case 0xf3:
                        getnext(1);
                        break;
                    case 0xf4:
                        break;
                    case 0xf5:
                        break;
                    case 0xf6: //something
                    case 0xf8:
                    case 0xfa:
                    case 0xfb:
                    case 0xfc:
                        //this ends the track for sierra.
                        if (type == FILE_SIERRA ||
                            type == FILE_ADVSIERRA)
                            {
                            track[curtrack].tend=pos;
                            midiprintf ("endmark: %ld -- %lx\n",pos,pos);
                            }
                        break;
                    case 0xfe:
                        break;
                    case 0xfd:
                        break;
                    case 0xff:
                        v=getnext(1);
                        l=getval();
                        midiprintf ("\n");
                        midiprintf("{%X_%X}",v,l);
                        if (v==0x51)
                            {
                            lnum=getnext(l);
                            msqtr=lnum; //set tempo
                            midiprintf ("(qtr=%ld)",msqtr);
                            }
                            else
                            {
                            for (i=0; i<l; i++)
                                midiprintf ("%2X ",getnext(1));
                            }
                        break;
					}
                break;
            default: midiprintf("!",v); // if we get down here, a error occurred 
			break;
            }

        if (pos < track[curtrack].tend)
            {
            if (type != FILE_SIERRA && type !=FILE_ADVSIERRA)
                w=getval();
                else
                w=getnext(1);
            track[curtrack].iwait=w;
            
                
            }
            else
            track[curtrack].iwait=0;

        track[curtrack].pos=pos;
        }


        ret=0; //end of song.
        iwait=0;
        for (curtrack=0; curtrack<16; curtrack++)
            if (track[curtrack].on == 1 &&
                track[curtrack].pos < track[curtrack].tend)
                ret=1;  //not yet..

        if (ret==1)
            {
            iwait=0xffffff;  // bigger than any wait can be!
            for (curtrack=0; curtrack<16; curtrack++)
               if (track[curtrack].on == 1 &&
                   track[curtrack].pos < track[curtrack].tend &&
                   track[curtrack].iwait < iwait)
                   iwait=track[curtrack].iwait;
            }
        }


    if (iwait !=0 && ret==1)
        {
        for (curtrack=0; curtrack<16; curtrack++)
            if (track[curtrack].on != 0)
                track[curtrack].iwait-=iwait;

        
fwait=1.0f/(((float)iwait/(float)deltas)*((float)msqtr/(float)1000000));
        }
        else
        fwait=50;  // 1/50th of a second

    midiprintf ("\n");
    for (i=0; i<16; i++)
        if (track[i].on != 0)
            if (track[i].pos < track[i].tend)
                midiprintf ("<%d>",track[i].iwait);
                else
                midiprintf("stop");

    
    

	if(ret != 0)
		return true;
	else
		return false;
}

float getrefresh()
{
    return (fwait > 0.01f ? fwait : 0.01f);
}

void rewind(int subsong)
{
    long i,j,n,m,l;
    long o_sierra_pos;
    int[] ins = new int[16];

    pos=0; tins=0;
    adlib_style=MIDI_STYLE|CMF_STYLE;
    adlib_mode=ADLIB_MELODIC;
    for (i=0; i<128; i++)        
    {
        for (j=0; j<14; j++)
            myinsbank[(int)i][(int)j]=midi_fm_instruments[(int)i][(int)j];
            myinsbank[(int)i][14]=0;
            myinsbank[(int)i][15]=0;
    }
    
	for (i=0; i<16; i++)
        {
        ch[(int)i].inum=0;
        for (j=0; j<11; j++)
            ch[(int)i].ins[(int)j]=myinsbank[ch[(int)i].inum][(int)j];
        ch[(int)i].vol=127;
        ch[(int)i].nshift=-25;
        ch[(int)i].on=1;
        }

    for (i=0; i<9; i++)
        {
        chp[(int)i][0]=-1;
        chp[(int)i][2]=0;
        }

    deltas=250;  // just a number,  not a standard
    msqtr=500000;
    fwait=123; // gotta be a small thing.. sorta like nothing
    iwait=0;

    subsongs=1;

    for (i=0; i<16; i++)
        {
        track[(int)i].tend=0;
        track[(int)i].spos=0;
        track[(int)i].pos=0;
        track[(int)i].iwait=0;
        track[(int)i].on=0;
        track[(int)i].pv=0;
        }
    curtrack=0;


        pos=0;
        i=getnext(1);
        switch(type)
            {
            case FILE_LUCAS:
                getnext(24);  //skip junk and get to the midi.
                adlib_style=LUCAS_STYLE|MIDI_STYLE;
                //note: no break, we go right into midi headers...
            case FILE_MIDI:
                if (type != FILE_LUCAS)
                    tins=128;
                getnext(11);  //skip header
                deltas=getnext(2);
                midiprintf ("deltas:%ld\n",deltas);
                getnext(4);

                curtrack=0;
                track[curtrack].on=1;
                track[curtrack].tend=getnext(4);
                track[curtrack].spos=pos;
                midiprintf ("tracklen:%ld\n",track[curtrack].tend);
                break;
            case FILE_CMF:
                getnext(3);  // ctmf
                getnexti(2); //version
                n=getnexti(2); // instrument offset
                m=getnexti(2); // music offset
                deltas=getnexti(2); //ticks/qtr note
                msqtr=1000000/getnexti(2)*deltas;
                   //the stuff in the cmf is click ticks per second..

                i=getnexti(2);
                if(i!=0) {
                    title = new StringBuffer();
                    for(;i<data.length;i++) {
                        title.append((char)data[(int)i]);
                        if(data[(int)i]==0) break;
                    }
                }
                
                i=getnexti(2);
                if(i!=0) {
                    author = new StringBuffer();
                    for(;i<data.length;i++) {
                        author.append((char)data[(int)i]);
                        if(data[(int)i]==0) break;
                    }
                }
                
                i=getnexti(2);
                if(i!=0) {
                    remarks = new StringBuffer();
                    for(;i<data.length;i++) {
                        remarks.append((char)data[(int)i]);
                        if(data[(int)i]==0) break;
                    }
                }
                

                getnext(16); // channel in use table ..
                i=getnexti(2); // num instr
                if (i>128) i=128; // to ward of bad numbers...
                getnexti(2); //basic tempo

                midiprintf("\nioff:%d\nmoff%d\ndeltas:%ld\nmsqtr:%ld\nnumi:%d\n",
                    n,m,deltas,msqtr,i);
                pos=n;  // jump to instruments
                tins=(int)i;
                for (j=0; j<i; j++)
                    {
                    midiprintf ("\n%d: ",j);
                    for (l=0; l<16; l++)
                        {
                        myinsbank[(int)j][(int)l]=(int)getnext(1);
                        midiprintf ("%2X ",myinsbank[(int)j][(int)l]);
                        }
                    }

                for (i=0; i<16; i++)
                    ch[(int)i].nshift=-13;

                adlib_style=CMF_STYLE;

                curtrack=0;
                track[curtrack].on=1;
                track[curtrack].tend=flen;  // music until the end of the file
                track[curtrack].spos=m;  //jump to midi music
                break;
            case FILE_OLDLUCAS:
                msqtr=250000;
                pos=9;
                deltas=getnext(1);

                i=8;
                pos=0x19;  // jump to instruments
                tins=(int)i;
                for (j=0; j<i; j++)
                    {
                    midiprintf ("\n%d: ",j);
                    for (l=0; l<16; l++)
                        ins[(int)l]=(int)getnext(1);

                    myinsbank[(int)j][10]=ins[2];
                    myinsbank[(int)j][0]=ins[3];
                    myinsbank[(int)j][2]=ins[4];
                    myinsbank[(int)j][4]=ins[5];
                    myinsbank[(int)j][6]=ins[6];
                    myinsbank[(int)j][8]=ins[7];
                    myinsbank[(int)j][1]=ins[8];
                    myinsbank[(int)j][3]=ins[9];
                    myinsbank[(int)j][5]=ins[10];
                    myinsbank[(int)j][7]=ins[11];
                    myinsbank[(int)j][9]=ins[12];

                    for (l=0; l<11; l++)
                        midiprintf ("%2X ",myinsbank[(int)j][(int)l]);
                    }

                for (i=0; i<16; i++)
                    {
                    if (i<tins)
                        {
                        ch[(int)i].inum=(int)i;
                        for (j=0; j<11; j++)
                            ch[(int)i].ins[(int)j]=myinsbank[ch[(int)i].inum][(int)j];
                        }
                    }

                adlib_style=LUCAS_STYLE|MIDI_STYLE;

                curtrack=0;
                track[curtrack].on=1;
                track[curtrack].tend=flen;  // music until the end of the file
                track[curtrack].spos=0x98;  //jump to midi music
                break;
            case FILE_ADVSIERRA:
              myinsbank = smyinsbank.clone();  
	      tins = stins;
                deltas=0x20;
                getnext(11); //worthless empty space and "stuff" :)

                o_sierra_pos=sierra_pos=pos;
                sierra_next_section();
                while (datalook(sierra_pos-2)!=0xff)
                    {
                    sierra_next_section();
                    subsongs++;
                    }

                if (subsong < 0 || subsong >= subsongs) subsong=0;

                sierra_pos=o_sierra_pos;
                sierra_next_section();
                i=0;
                while (i != subsong)
                    {
                    sierra_next_section();
                    i++;
                    }

                adlib_style=SIERRA_STYLE|MIDI_STYLE;  //advanced sierra tunes use volume
                break;
            case FILE_SIERRA:
              myinsbank = smyinsbank.clone();  
	      tins = stins;
                getnext(2);
                deltas=0x20;

                curtrack=0;
                track[curtrack].on=1;
                track[curtrack].tend=flen;  // music until the end of the file

                for (i=0; i<16; i++)
                    {
                    ch[(int)i].nshift=-13;
                    ch[(int)i].on=(int)getnext(1);
                    ch[(int)i].inum=(int)getnext(1);
                    for (j=0; j<11; j++)
                        ch[(int)i].ins[(int)j]=myinsbank[ch[(int)i].inum][(int)j];
                    }

                track[curtrack].spos=pos;
                adlib_style=SIERRA_STYLE|MIDI_STYLE;
                break;
            }



        for (i=0; i<16; i++)
            if (track[(int)i].on != 0)
                {
                track[(int)i].pos=track[(int)i].spos;
                track[(int)i].pv=0;
                track[(int)i].iwait=0;
                }

    doing=1;
    midi_fm_reset();
}

String gettype()
{
	switch(type) {
	case FILE_LUCAS:
		return "LucasArts AdLib MIDI";
	case FILE_MIDI:
		return "General MIDI";
	case FILE_CMF:
		return "Creative Music Format (CMF MIDI)";
	case FILE_OLDLUCAS:
		return "Lucasfilm Adlib MIDI";
	case FILE_ADVSIERRA:
		return "Sierra On-Line VGA MIDI";
	case FILE_SIERRA:
		return "Sierra On-Line EGA MIDI";
	default:
		return "MIDI unknown";
	}
}

/*
 * Adplug - Replayer for many OPL2/OPL3 audio file formats.
 * Copyright (C) 1999, 2000, 2001 Simon Peter, <dn.tlp@gmx.net>, et al.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * FM instrument definitions below borrowed from the Allegro library by
 * Phil Hassey, <philhassey@hotmail.com> - see "adplug/players/mid.cpp"
 * for further acknowledgements.
 */
 
int[][] midi_fm_instruments =
{

 /* This set of GM instrument patches was provided by Jorrit Rouwe...
    */

   { 0x21, 0x21, 0x8f, 0x0c, 0xf2, 0xf2, 0x45, 0x76, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Acoustic Grand */
   { 0x31, 0x21, 0x4b, 0x09, 0xf2, 0xf2, 0x54, 0x56, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Bright Acoustic */
   { 0x31, 0x21, 0x49, 0x09, 0xf2, 0xf2, 0x55, 0x76, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Electric Grand */
   { 0xb1, 0x61, 0x0e, 0x09, 0xf2, 0xf3, 0x3b, 0x0b, 0x00, 0x00, 0x06, 0, 0, 0 }, /* Honky-Tonk */
   { 0x01, 0x21, 0x57, 0x09, 0xf1, 0xf1, 0x38, 0x28, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Electric Piano 1 */
   { 0x01, 0x21, 0x93, 0x09, 0xf1, 0xf1, 0x38, 0x28, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Electric Piano 2 */
   { 0x21, 0x36, 0x80, 0x17, 0xa2, 0xf1, 0x01, 0xd5, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Harpsichord */
   { 0x01, 0x01, 0x92, 0x09, 0xc2, 0xc2, 0xa8, 0x58, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Clav */
   { 0x0c, 0x81, 0x5c, 0x09, 0xf6, 0xf3, 0x54, 0xb5, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Celesta */
   { 0x07, 0x11, 0x97, 0x89, 0xf6, 0xf5, 0x32, 0x11, 0x00, 0x00, 0x02, 0, 0, 0 }, /* Glockenspiel */
   { 0x17, 0x01, 0x21, 0x09, 0x56, 0xf6, 0x04, 0x04, 0x00, 0x00, 0x02, 0, 0, 0 }, /* Music Box */
   { 0x18, 0x81, 0x62, 0x09, 0xf3, 0xf2, 0xe6, 0xf6, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Vibraphone */
   { 0x18, 0x21, 0x23, 0x09, 0xf7, 0xe5, 0x55, 0xd8, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Marimba */
   { 0x15, 0x01, 0x91, 0x09, 0xf6, 0xf6, 0xa6, 0xe6, 0x00, 0x00, 0x04, 0, 0, 0 }, /* Xylophone */
   { 0x45, 0x81, 0x59, 0x89, 0xd3, 0xa3, 0x82, 0xe3, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* Tubular Bells */
   { 0x03, 0x81, 0x49, 0x89, 0x74, 0xb3, 0x55, 0x05, 0x01, 0x00, 0x04, 0, 0, 0 }, /* Dulcimer */
   { 0x71, 0x31, 0x92, 0x09, 0xf6, 0xf1, 0x14, 0x07, 0x00, 0x00, 0x02, 0, 0, 0 }, /* Drawbar Organ */
   { 0x72, 0x30, 0x14, 0x09, 0xc7, 0xc7, 0x58, 0x08, 0x00, 0x00, 0x02, 0, 0, 0 }, /* Percussive Organ */
   { 0x70, 0xb1, 0x44, 0x09, 0xaa, 0x8a, 0x18, 0x08, 0x00, 0x00, 0x04, 0, 0, 0 }, /* Rock Organ */
   { 0x23, 0xb1, 0x93, 0x09, 0x97, 0x55, 0x23, 0x14, 0x01, 0x00, 0x04, 0, 0, 0 }, /* Church Organ */
   { 0x61, 0xb1, 0x13, 0x89, 0x97, 0x55, 0x04, 0x04, 0x01, 0x00, 0x00, 0, 0, 0 }, /* Reed Organ */
   { 0x24, 0xb1, 0x48, 0x09, 0x98, 0x46, 0x2a, 0x1a, 0x01, 0x00, 0x0c, 0, 0, 0 }, /* Accoridan */
   { 0x61, 0x21, 0x13, 0x09, 0x91, 0x61, 0x06, 0x07, 0x01, 0x00, 0x0a, 0, 0, 0 }, /* Harmonica */
   { 0x21, 0xa1, 0x13, 0x92, 0x71, 0x61, 0x06, 0x07, 0x00, 0x00, 0x06, 0, 0, 0 }, /* Tango Accordian */
   { 0x02, 0x41, 0x9c, 0x89, 0xf3, 0xf3, 0x94, 0xc8, 0x01, 0x00, 0x0c, 0, 0, 0 }, /* Acoustic Guitar(nylon) */
   { 0x03, 0x11, 0x54, 0x09, 0xf3, 0xf1, 0x9a, 0xe7, 0x01, 0x00, 0x0c, 0, 0, 0 }, /* Acoustic Guitar(steel) */
   { 0x23, 0x21, 0x5f, 0x09, 0xf1, 0xf2, 0x3a, 0xf8, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Electric Guitar(jazz) */
   { 0x03, 0x21, 0x87, 0x89, 0xf6, 0xf3, 0x22, 0xf8, 0x01, 0x00, 0x06, 0, 0, 0 }, /* Electric Guitar(clean) */
   { 0x03, 0x21, 0x47, 0x09, 0xf9, 0xf6, 0x54, 0x3a, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Electric Guitar(muted) */
   { 0x23, 0x21, 0x4a, 0x0e, 0x91, 0x84, 0x41, 0x19, 0x01, 0x00, 0x08, 0, 0, 0 }, /* Overdriven Guitar */
   { 0x23, 0x21, 0x4a, 0x09, 0x95, 0x94, 0x19, 0x19, 0x01, 0x00, 0x08, 0, 0, 0 }, /* Distortion Guitar */
   { 0x09, 0x84, 0xa1, 0x89, 0x20, 0xd1, 0x4f, 0xf8, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Guitar Harmonics */
   { 0x21, 0xa2, 0x1e, 0x09, 0x94, 0xc3, 0x06, 0xa6, 0x00, 0x00, 0x02, 0, 0, 0 }, /* Acoustic Bass */
   { 0x31, 0x31, 0x12, 0x09, 0xf1, 0xf1, 0x28, 0x18, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Electric Bass(finger) */
   { 0x31, 0x31, 0x8d, 0x09, 0xf1, 0xf1, 0xe8, 0x78, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Electric Bass(pick) */
   { 0x31, 0x32, 0x5b, 0x09, 0x51, 0x71, 0x28, 0x48, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* Fretless Bass */
   { 0x01, 0x21, 0x8b, 0x49, 0xa1, 0xf2, 0x9a, 0xdf, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Slap Bass 1 */
   { 0x21, 0x21, 0x8b, 0x11, 0xa2, 0xa1, 0x16, 0xdf, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Slap Bass 2 */
   { 0x31, 0x31, 0x8b, 0x09, 0xf4, 0xf1, 0xe8, 0x78, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Synth Bass 1 */
   { 0x31, 0x31, 0x12, 0x09, 0xf1, 0xf1, 0x28, 0x18, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Synth Bass 2 */
   { 0x31, 0x21, 0x15, 0x09, 0xdd, 0x56, 0x13, 0x26, 0x01, 0x00, 0x08, 0, 0, 0 }, /* Violin */
   { 0x31, 0x21, 0x16, 0x09, 0xdd, 0x66, 0x13, 0x06, 0x01, 0x00, 0x08, 0, 0, 0 }, /* Viola */
   { 0x71, 0x31, 0x49, 0x09, 0xd1, 0x61, 0x1c, 0x0c, 0x01, 0x00, 0x08, 0, 0, 0 }, /* Cello */
   { 0x21, 0x23, 0x4d, 0x89, 0x71, 0x72, 0x12, 0x06, 0x01, 0x00, 0x02, 0, 0, 0 }, /* Contrabass */
   { 0xf1, 0xe1, 0x40, 0x09, 0xf1, 0x6f, 0x21, 0x16, 0x01, 0x00, 0x02, 0, 0, 0 }, /* Tremolo Strings */
   { 0x02, 0x01, 0x1a, 0x89, 0xf5, 0x85, 0x75, 0x35, 0x01, 0x00, 0x00, 0, 0, 0 }, /* Pizzicato Strings */
   { 0x02, 0x01, 0x1d, 0x89, 0xf5, 0xf3, 0x75, 0xf4, 0x01, 0x00, 0x00, 0, 0, 0 }, /* Orchestral Strings */
   { 0x10, 0x11, 0x41, 0x09, 0xf5, 0xf2, 0x05, 0xc3, 0x01, 0x00, 0x02, 0, 0, 0 }, /* Timpani */
   { 0x21, 0xa2, 0x9b, 0x0a, 0xb1, 0x72, 0x25, 0x08, 0x01, 0x00, 0x0e, 0, 0, 0 }, /* String Ensemble 1 */
   { 0xa1, 0x21, 0x98, 0x09, 0x7f, 0x3f, 0x03, 0x07, 0x01, 0x01, 0x00, 0, 0, 0 }, /* String Ensemble 2 */
   { 0xa1, 0x61, 0x93, 0x09, 0xc1, 0x4f, 0x12, 0x05, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* SynthStrings 1 */
   { 0x21, 0x61, 0x18, 0x09, 0xc1, 0x4f, 0x22, 0x05, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* SynthStrings 2 */
   { 0x31, 0x72, 0x5b, 0x8c, 0xf4, 0x8a, 0x15, 0x05, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Choir Aahs */
   { 0xa1, 0x61, 0x90, 0x09, 0x74, 0x71, 0x39, 0x67, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Voice Oohs */
   { 0x71, 0x72, 0x57, 0x09, 0x54, 0x7a, 0x05, 0x05, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* Synth Voice */
   { 0x90, 0x41, 0x00, 0x09, 0x54, 0xa5, 0x63, 0x45, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Orchestra Hit */
   { 0x21, 0x21, 0x92, 0x0a, 0x85, 0x8f, 0x17, 0x09, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* Trumpet */
   { 0x21, 0x21, 0x94, 0x0e, 0x75, 0x8f, 0x17, 0x09, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* Trombone */
   { 0x21, 0x61, 0x94, 0x09, 0x76, 0x82, 0x15, 0x37, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* Tuba */
   { 0x31, 0x21, 0x43, 0x09, 0x9e, 0x62, 0x17, 0x2c, 0x01, 0x01, 0x02, 0, 0, 0 }, /* Muted Trumpet */
   { 0x21, 0x21, 0x9b, 0x09, 0x61, 0x7f, 0x6a, 0x0a, 0x00, 0x00, 0x02, 0, 0, 0 }, /* French Horn */
   { 0x61, 0x22, 0x8a, 0x0f, 0x75, 0x74, 0x1f, 0x0f, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Brass Section */
   { 0xa1, 0x21, 0x86, 0x8c, 0x72, 0x71, 0x55, 0x18, 0x01, 0x00, 0x00, 0, 0, 0 }, /* SynthBrass 1 */
   { 0x21, 0x21, 0x4d, 0x09, 0x54, 0xa6, 0x3c, 0x1c, 0x00, 0x00, 0x08, 0, 0, 0 }, /* SynthBrass 2 */
   { 0x31, 0x61, 0x8f, 0x09, 0x93, 0x72, 0x02, 0x0b, 0x01, 0x00, 0x08, 0, 0, 0 }, /* Soprano Sax */
   { 0x31, 0x61, 0x8e, 0x09, 0x93, 0x72, 0x03, 0x09, 0x01, 0x00, 0x08, 0, 0, 0 }, /* Alto Sax */
   { 0x31, 0x61, 0x91, 0x09, 0x93, 0x82, 0x03, 0x09, 0x01, 0x00, 0x0a, 0, 0, 0 }, /* Tenor Sax */
   { 0x31, 0x61, 0x8e, 0x09, 0x93, 0x72, 0x0f, 0x0f, 0x01, 0x00, 0x0a, 0, 0, 0 }, /* Baritone Sax */
   { 0x21, 0x21, 0x4b, 0x09, 0xaa, 0x8f, 0x16, 0x0a, 0x01, 0x00, 0x08, 0, 0, 0 }, /* Oboe */
   { 0x31, 0x21, 0x90, 0x09, 0x7e, 0x8b, 0x17, 0x0c, 0x01, 0x01, 0x06, 0, 0, 0 }, /* English Horn */
   { 0x31, 0x32, 0x81, 0x09, 0x75, 0x61, 0x19, 0x19, 0x01, 0x00, 0x00, 0, 0, 0 }, /* Bassoon */
   { 0x32, 0x21, 0x90, 0x09, 0x9b, 0x72, 0x21, 0x17, 0x00, 0x00, 0x04, 0, 0, 0 }, /* Clarinet */
   { 0xe1, 0xe1, 0x1f, 0x09, 0x85, 0x65, 0x5f, 0x1a, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Piccolo */
   { 0xe1, 0xe1, 0x46, 0x09, 0x88, 0x65, 0x5f, 0x1a, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Flute */
   { 0xa1, 0x21, 0x9c, 0x09, 0x75, 0x75, 0x1f, 0x0a, 0x00, 0x00, 0x02, 0, 0, 0 }, /* Recorder */
   { 0x31, 0x21, 0x8b, 0x09, 0x84, 0x65, 0x58, 0x1a, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Pan Flute */
   { 0xe1, 0xa1, 0x4c, 0x09, 0x66, 0x65, 0x56, 0x26, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Blown Bottle */
   { 0x62, 0xa1, 0xcb, 0x09, 0x76, 0x55, 0x46, 0x36, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Skakuhachi */
   { 0x62, 0xa1, 0xa2, 0x09, 0x57, 0x56, 0x07, 0x07, 0x00, 0x00, 0x0b, 0, 0, 0 }, /* Whistle */
   { 0x62, 0xa1, 0x9c, 0x09, 0x77, 0x76, 0x07, 0x07, 0x00, 0x00, 0x0b, 0, 0, 0 }, /* Ocarina */
   { 0x22, 0x21, 0x59, 0x09, 0xff, 0xff, 0x03, 0x0f, 0x02, 0x00, 0x00, 0, 0, 0 }, /* Lead 1 (square) */
   { 0x21, 0x21, 0x0e, 0x09, 0xff, 0xff, 0x0f, 0x0f, 0x01, 0x01, 0x00, 0, 0, 0 }, /* Lead 2 (sawtooth) */
   { 0x22, 0x21, 0x46, 0x89, 0x86, 0x64, 0x55, 0x18, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Lead 3 (calliope) */
   { 0x21, 0xa1, 0x45, 0x09, 0x66, 0x96, 0x12, 0x0a, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Lead 4 (chiff) */
   { 0x21, 0x22, 0x8b, 0x09, 0x92, 0x91, 0x2a, 0x2a, 0x01, 0x00, 0x00, 0, 0, 0 }, /* Lead 5 (charang) */
   { 0xa2, 0x61, 0x9e, 0x49, 0xdf, 0x6f, 0x05, 0x07, 0x00, 0x00, 0x02, 0, 0, 0 }, /* Lead 6 (voice) */
   { 0x20, 0x60, 0x1a, 0x09, 0xef, 0x8f, 0x01, 0x06, 0x00, 0x02, 0x00, 0, 0, 0 }, /* Lead 7 (fifths) */
   { 0x21, 0x21, 0x8f, 0x86, 0xf1, 0xf4, 0x29, 0x09, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Lead 8 (bass+lead) */
   { 0x77, 0xa1, 0xa5, 0x09, 0x53, 0xa0, 0x94, 0x05, 0x00, 0x00, 0x02, 0, 0, 0 }, /* Pad 1 (new age) */
   { 0x61, 0xb1, 0x1f, 0x89, 0xa8, 0x25, 0x11, 0x03, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Pad 2 (warm) */
   { 0x61, 0x61, 0x17, 0x09, 0x91, 0x55, 0x34, 0x16, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* Pad 3 (polysynth) */
   { 0x71, 0x72, 0x5d, 0x09, 0x54, 0x6a, 0x01, 0x03, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Pad 4 (choir) */
   { 0x21, 0xa2, 0x97, 0x09, 0x21, 0x42, 0x43, 0x35, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Pad 5 (bowed) */
   { 0xa1, 0x21, 0x1c, 0x09, 0xa1, 0x31, 0x77, 0x47, 0x01, 0x01, 0x00, 0, 0, 0 }, /* Pad 6 (metallic) */
   { 0x21, 0x61, 0x89, 0x0c, 0x11, 0x42, 0x33, 0x25, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Pad 7 (halo) */
   { 0xa1, 0x21, 0x15, 0x09, 0x11, 0xcf, 0x47, 0x07, 0x01, 0x00, 0x00, 0, 0, 0 }, /* Pad 8 (sweep) */
   { 0x3a, 0x51, 0xce, 0x09, 0xf8, 0x86, 0xf6, 0x02, 0x00, 0x00, 0x02, 0, 0, 0 }, /* FX 1 (rain) */
   { 0x21, 0x21, 0x15, 0x09, 0x21, 0x41, 0x23, 0x13, 0x01, 0x00, 0x00, 0, 0, 0 }, /* FX 2 (soundtrack) */
   { 0x06, 0x01, 0x5b, 0x09, 0x74, 0xa5, 0x95, 0x72, 0x00, 0x00, 0x00, 0, 0, 0 }, /* FX 3 (crystal) */
   { 0x22, 0x61, 0x92, 0x8c, 0xb1, 0xf2, 0x81, 0x26, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* FX 4 (atmosphere) */
   { 0x41, 0x42, 0x4d, 0x09, 0xf1, 0xf2, 0x51, 0xf5, 0x01, 0x00, 0x00, 0, 0, 0 }, /* FX 5 (brightness) */
   { 0x61, 0xa3, 0x94, 0x89, 0x11, 0x11, 0x51, 0x13, 0x01, 0x00, 0x06, 0, 0, 0 }, /* FX 6 (goblins) */
   { 0x61, 0xa1, 0x8c, 0x89, 0x11, 0x1d, 0x31, 0x03, 0x00, 0x00, 0x06, 0, 0, 0 }, /* FX 7 (echoes) */
   { 0xa4, 0x61, 0x4c, 0x09, 0xf3, 0x81, 0x73, 0x23, 0x01, 0x00, 0x04, 0, 0, 0 }, /* FX 8 (sci-fi) */
   { 0x02, 0x07, 0x85, 0x0c, 0xd2, 0xf2, 0x53, 0xf6, 0x00, 0x01, 0x00, 0, 0, 0 }, /* Sitar */
   { 0x11, 0x13, 0x0c, 0x89, 0xa3, 0xa2, 0x11, 0xe5, 0x01, 0x00, 0x00, 0, 0, 0 }, /* Banjo */
   { 0x11, 0x11, 0x06, 0x09, 0xf6, 0xf2, 0x41, 0xe6, 0x01, 0x02, 0x04, 0, 0, 0 }, /* Shamisen */
   { 0x93, 0x91, 0x91, 0x09, 0xd4, 0xeb, 0x32, 0x11, 0x00, 0x01, 0x08, 0, 0, 0 }, /* Koto */
   { 0x04, 0x01, 0x4f, 0x09, 0xfa, 0xc2, 0x56, 0x05, 0x00, 0x00, 0x0c, 0, 0, 0 }, /* Kalimba */
   { 0x21, 0x22, 0x49, 0x09, 0x7c, 0x6f, 0x20, 0x0c, 0x00, 0x01, 0x06, 0, 0, 0 }, /* Bagpipe */
   { 0x31, 0x21, 0x85, 0x09, 0xdd, 0x56, 0x33, 0x16, 0x01, 0x00, 0x0a, 0, 0, 0 }, /* Fiddle */
   { 0x20, 0x21, 0x04, 0x8a, 0xda, 0x8f, 0x05, 0x0b, 0x02, 0x00, 0x06, 0, 0, 0 }, /* Shanai */
   { 0x05, 0x03, 0x6a, 0x89, 0xf1, 0xc3, 0xe5, 0xe5, 0x00, 0x00, 0x06, 0, 0, 0 }, /* Tinkle Bell */
   { 0x07, 0x02, 0x15, 0x09, 0xec, 0xf8, 0x26, 0x16, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Agogo */
   { 0x05, 0x01, 0x9d, 0x09, 0x67, 0xdf, 0x35, 0x05, 0x00, 0x00, 0x08, 0, 0, 0 }, /* Steel Drums */
   { 0x18, 0x12, 0x96, 0x09, 0xfa, 0xf8, 0x28, 0xe5, 0x00, 0x00, 0x0a, 0, 0, 0 }, /* Woodblock */
   { 0x10, 0x00, 0x86, 0x0c, 0xa8, 0xfa, 0x07, 0x03, 0x00, 0x00, 0x06, 0, 0, 0 }, /* Taiko Drum */
   { 0x11, 0x10, 0x41, 0x0c, 0xf8, 0xf3, 0x47, 0x03, 0x02, 0x00, 0x04, 0, 0, 0 }, /* Melodic Tom */
   { 0x01, 0x10, 0x8e, 0x09, 0xf1, 0xf3, 0x06, 0x02, 0x02, 0x00, 0x0e, 0, 0, 0 }, /* Synth Drum */
   { 0x0e, 0xc0, 0x00, 0x09, 0x1f, 0x1f, 0x00, 0xff, 0x00, 0x03, 0x0e, 0, 0, 0 }, /* Reverse Cymbal */
   { 0x06, 0x03, 0x80, 0x91, 0xf8, 0x56, 0x24, 0x84, 0x00, 0x02, 0x0e, 0, 0, 0 }, /* Guitar Fret Noise */
   { 0x0e, 0xd0, 0x00, 0x0e, 0xf8, 0x34, 0x00, 0x04, 0x00, 0x03, 0x0e, 0, 0, 0 }, /* Breath Noise */
   { 0x0e, 0xc0, 0x00, 0x09, 0xf6, 0x1f, 0x00, 0x02, 0x00, 0x03, 0x0e, 0, 0, 0 }, /* Seashore */
   { 0xd5, 0xda, 0x95, 0x49, 0x37, 0x56, 0xa3, 0x37, 0x00, 0x00, 0x00, 0, 0, 0 }, /* Bird Tweet */
   { 0x35, 0x14, 0x5c, 0x11, 0xb2, 0xf4, 0x61, 0x15, 0x02, 0x00, 0x0a, 0, 0, 0 }, /* Telephone ring */
   { 0x0e, 0xd0, 0x00, 0x09, 0xf6, 0x4f, 0x00, 0xf5, 0x00, 0x03, 0x0e, 0, 0, 0 }, /* Helicopter */
   { 0x26, 0xe4, 0x00, 0x09, 0xff, 0x12, 0x01, 0x16, 0x00, 0x01, 0x0e, 0, 0, 0 }, /* Applause */
   { 0x00, 0x00, 0x00, 0x09, 0xf3, 0xf6, 0xf0, 0xc9, 0x00, 0x02, 0x0e, 0, 0, 0 }  /* Gunshot */

};

/* logarithmic relationship between midi and FM volumes */
static int[] my_midi_fm_vol_table = {
   0,  11, 16, 19, 22, 25, 27, 29, 32, 33, 35, 37, 39, 40, 42, 43,
   45, 46, 48, 49, 50, 51, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62,
   64, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 75, 76, 77,
   78, 79, 80, 80, 81, 82, 83, 83, 84, 85, 86, 86, 87, 88, 89, 89,
   90, 91, 91, 92, 93, 93, 94, 95, 96, 96, 97, 97, 98, 99, 99, 100,
   101, 101, 102, 103, 103, 104, 104, 105, 106, 106, 107, 107, 108,
   109, 109, 110, 110, 111, 112, 112, 113, 113, 114, 114, 115, 115,
   116, 117, 117, 118, 118, 119, 119, 120, 120, 121, 121, 122, 122,
   123, 123, 124, 124, 125, 125, 126, 126, 127
};


}
