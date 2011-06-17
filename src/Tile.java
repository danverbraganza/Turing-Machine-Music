import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class Tile {

	public static final int NUM_STATES = 6;

	private int state;

	public Tile(int state) {
		this.setState(state);
	}

	public void setState(int state) {
		this.state = state % NUM_STATES;
	}

	public int getState() {
		return state;
	}

	public void nextState() {
		this.setState(this.getState() + 1);
	}

	public Color colorFor(int i) {
		switch (i) {
		case 0: return Color.CYAN;
		case 1: return Color.GREEN;
		case 2: return Color.RED;
		case 3: return Color.YELLOW;
		case 4: return Color.BLUE;
		}
		return Color.MAGENTA;
	}

	public void draw(Graphics g, int offsetX, int offsetY) {
		Color c = g.getColor();
		g.setColor(colorFor(state));
		g.fillRoundRect(offsetX, offsetY, 80, 80, 20, 20);
		g.setColor(c);
	}

	public void playSound() {
		switch (this.state) {
		case 0: Tile.generateTone(262, 500, 100); break;
		case 1: Tile.generateTone(294, 500, 100);break;
		case 2: Tile.generateTone(329, 500, 100);break;
		case 3: Tile.generateTone(392, 500, 100);break;	
		case 4: Tile.generateTone(440, 500, 100); break;
		case 5: Tile.generateTone(0, 500, 100);
		}
	}
	public int getHertz() {
		switch (this.state) {
		case 0: return 262;
		case 1: return 294;
		case 2: return 329;
		case 3: return 392;
		case 4: return 440;
		}
		return 0;

	}

	public static void generateTone(int[] hz, int msecs, int volume) {
		float frequency = 44100;
		byte[] buf;
		AudioFormat af;
		volume /= hz.length;

		buf = new byte[1];
		af = new AudioFormat(frequency,8,1,true,false);
		try {
			SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
			sdl = AudioSystem.getSourceDataLine(af);
			sdl.open(af);
			sdl.start();
			for(int i=0; i<msecs*frequency/1000; i++){
				for (int j = 0; j < hz.length; j ++){

					double angle = i/(frequency/hz[j])*2.0*Math.PI;
					if (j == 0)
						buf[0] = (byte)(Math.sin(angle)*volume);
					else
						buf[0] += (byte)(Math.sin(angle)*volume);
				}
				sdl.write(buf,0,1);
			}
			sdl.drain();
			sdl.stop();
			sdl.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

	}



	public static void generateTone(int hz,int msecs, int volume) {

		float frequency = 44100;
		byte[] buf;
		AudioFormat af;

		buf = new byte[1];
		af = new AudioFormat(frequency,8,1,true,false);
		try {
			SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
			sdl = AudioSystem.getSourceDataLine(af);
			sdl.open(af);
			sdl.start();
			for(int i=0; i<msecs*frequency/1000; i++){
				double angle = i/(frequency/hz)*2.0*Math.PI;
				buf[0]=(byte)(Math.sin(angle)*volume);

				sdl.write(buf,0,1);
			}
			sdl.drain();
			sdl.stop();
			sdl.close();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getHarmonyState(Random r) {
		int retval;
		if (this.getState() == 6) retval = 1;
		else retval = this.getState() + r.nextInt(2) + 2;
		if (retval > 5) {
			retval -= 1;
			retval %= 5;
		}
		return retval;
	}
}
