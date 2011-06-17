import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.Timer;


public class Main implements ActionListener, KeyListener, MouseListener{

	JFrame mainwindow;
	Canvas c;
	ArrayList<ArrayList<Tile>> tapes;
	ArrayList<Tile> tape;
	Random r = new Random();
	int current = 5;
	boolean playing = false;
	boolean direction = true;
	boolean changing = true;
	boolean feedback = true;
	BufferStrategy buffer ;

	public Main() {
		mainwindow = new JFrame("Game 1");
		mainwindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c = new Canvas();
		c.setBounds(0, 0, 800, 600);
		c.setBackground(Color.BLACK);
		c.setVisible(true);
		mainwindow.setIgnoreRepaint(true);
		c.setIgnoreRepaint(true);
		mainwindow.add(c);
		mainwindow.pack();
		mainwindow.setVisible(true);
		c.addKeyListener(this);
		c.addMouseListener(this);

		c.createBufferStrategy( 2 );
		buffer = c.getBufferStrategy();

		this.tapes = new ArrayList<ArrayList<Tile>>();
		tapes.add(new ArrayList<Tile>());
		for (int i = 0; i < 50; i++) {
			tapes.get(0).add(new Tile(0));
		}
		this.tape = tapes.get(0);
	}

	public static void main(String[] args) {

		Main m =  new Main();
		Timer t = new Timer(200, m);
		t.start();
		m.startRedraw();
	}

	public boolean isPlaying() {
		return this.playing;
	}

	public void update() {

		int[] hz = new int[tapes.size()];
		for (int i = 0; i < tapes.size(); i++) {
			Tile t = tapes.get(i).get(current);
			hz[i] = t.getHertz();
		}
		Tile.generateTone(hz, 202, 100);

		Tile t = tape.get(current);

		if (changing) {
			switch(t.getState()) {
			case 0: 
				t.setState(1);
				break;
			case 1:
				t.setState(2);
				this.direction = !this.direction;
				break;
			case 2:
				t.setState(3);
				break;
			case 3:
				t.setState(4);
				break;
			case 4:
				t.setState(0);
			}
		}
		
		if (this.direction)
			this.current ++;
		else 
			this.current --;
		
		if (this.current < 0) {
			this.direction = true;
			this.current = 0;
		}
		if (this.current >= this.tape.size()) {
			this.direction = false;
			this.current = tape.size() - 1;

		}
		


	}

	public void togglePlaying() {
		this.playing = !this.playing;
	}

	public void startRedraw() {


		Graphics g = null;
		while(true) {
			try {
				g = buffer.getDrawGraphics();
				g.setColor( Color.BLACK );
				g.fillRect( 0, 0, 799, 599 );

				if (changing) {
					g.drawString("State transitions active", 300, 20);
				} else {
					g.drawString("State transitions disabled", 300, 20);
				}
				int y_drop = 0;
				synchronized (this) {
					for (ArrayList<Tile> tt: tapes) {
						int offsetX = -current + 4;
						for (Tile t: tt) {
							t.draw(g, offsetX * 85, 30 + 85 * y_drop);
							offsetX ++;
						}
						y_drop ++;
					}
				}
				g.setColor(Color.WHITE);
				g.drawLine(5 * 85 + 40, 0, 5 * 85 + 40, 600);
				g.drawString("How to play", 300, 400);
				g.drawString("Click on a block: change tone", 300, 420);
				g.drawString("F or B: forward or back 5 steps", 300, 440);
				g.drawString("Enter: Start playing", 300, 460);
				g.drawString("C: Toggle state transitions", 300, 480);
				g.drawString("D: Toggle feedback mode (hear changes in pitch)", 300, 500);
				g.drawString("A or R: Add or remove a track (minimum 1)", 300, 520);
				g.drawString("G: Generate random music (Shuffle all tiles)", 300, 540);
				g.drawString("Q: Quompose random music (Shuffle tiles with chords)", 300, 560);
				if( !buffer.contentsLost() )
					buffer.show();

				// Let the OS have a little time...
				Thread.yield();
			} finally {
				if( g != null ) 
					g.dispose();
			}
		}


	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.isPlaying()) {
			this.update();
		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		//Which tile was clicked?
		int xid = x/85;
		int yid = (y - 30) / 85;
		//Increment it.
		if (yid >= 0 && yid < tapes.size()) {
			this.tapes.get(yid).get(xid + current - 4).nextState();
			if (this.feedback)
				this.tapes.get(yid).get(xid + current - 4).playSound();
		}


	}


	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case (KeyEvent.VK_ENTER) :
			this.togglePlaying();
		break;
		case KeyEvent.VK_F : 
			current += 5;
			break;
		case KeyEvent.VK_B :
			current -= 5;
			break;
		case KeyEvent.VK_C :
			changing = !changing;
			break;
		case KeyEvent.VK_D :
			feedback = !feedback;
			break;
		case KeyEvent.VK_A :

			if (tapes.size() < 3) {

				ArrayList<Tile> newTape = new ArrayList<Tile>();
				for (int i = 0; i < 50; i++) {
					newTape.add(new Tile(0));
				}
				synchronized(this) {
					tapes.add(newTape);
				}

			}
			break;
		case KeyEvent.VK_R :
			if (tapes.size() > 1) {
				synchronized(this) {
					tapes.remove(tapes.size() - 1);
				}
			}
			break;
		case KeyEvent.VK_G :
			for (ArrayList<Tile> tt: this.tapes) {
				for (Tile t: tt) {
					t.setState(r.nextInt(Tile.NUM_STATES));
				}
			}
			break;
		case KeyEvent.VK_Q :
			for (int i = 0; i < tapes.size(); i++) {
				for (int j = 0; j < tapes.get(i).size(); j++) {
					if (i == 0) {
						tapes.get(i).get(j).setState(r.nextInt(Tile.NUM_STATES));
					} else {
						tapes.get(i).get(j).setState(tapes.get(i - 1).get(j).getHarmonyState(r));
					}
				}
			}
			break;
		}
	}



	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}


}
