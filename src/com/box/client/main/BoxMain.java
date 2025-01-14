package com.box.client.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JFrame;

@SuppressWarnings({ "static-access" })
public class BoxMain implements Runnable, WindowListener {
	private static JFrame j;
	private static Canvas c;
	private static boolean running = false;
	private static Thread thread;
	private static Box box;
	private static String host;
	private static int port;

	public BoxMain(String[] args) throws BoxClientException {
		if (args == null) {
			throw new BoxClientException("Run argument error. Format: host=host port=port");
		}
		if (args.length <= 1) {
			throw new BoxClientException("Run argument error. Format: host=host port=port");
		}
		if (!(args[0].split("=")[0].equalsIgnoreCase("host")) || !(args[1].split("=")[0].equalsIgnoreCase("port"))) {
			throw new BoxClientException("Run argument error. Format: host=host port=port");
		}
		this.host = args[0].split("=")[1];
		this.port = Integer.parseInt(args[1].split("=")[1]);

		this.j = new JFrame("Box Client | " + this.host + " | " + String.valueOf(this.port));
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setSize(1280, 720);
		j.setResizable(true);

		c = new Canvas();
		c.setFocusable(true);
		c.setSize(1280, 720);

		j.add(c);
		j.pack();

		j.setLocationRelativeTo(null);
		j.setVisible(true);
		j.setFocusable(false);

		thread = new Thread(this);

		start();
	}

	public static void main(String[] args) {
		try {
			new BoxMain(args);
		} catch (BoxClientException e) {
			e.printStackTrace();
		}
	}

	private void tick() {
		if (box != null)
			box.tick();
	}

	private void render(Graphics2D g) {
		if (box == null)
			return;

		try {
			// this.box.getObjectOutputStream().flush();
			ObjectInputStream objIn = this.box.getObjectInputStream();
			if (objIn.available() > 0) {
				double x = objIn.readDouble();
				double y = objIn.readDouble();
				Color color = (Color) objIn.readObject();
				
				Rectangle mainRec = new Rectangle((int) box.getX(), (int) box.getY(), 20, 20);
				Rectangle otherRec = new Rectangle((int) x, (int) y, 20, 20);
				
				if (mainRec.intersects(otherRec)) {
					color = Color.GREEN;
				}
				
				drawBox(g, x, y, color);

			}
			box.render(g);
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
		}
	}

	public static final void drawBox(Graphics2D g, double x, double y, Color c) {
		Color oldColor = g.getColor();
		g.setColor(c == null ? Color.RED : c);
		g.fillRect((int) x, (int) y, 20, 20);
		g.setColor(oldColor);
	}

	private void init() {
		Socket socket = null;
		try {
			socket = new Socket(this.host, this.port);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				throw new BoxClientException(this.host + ":" + this.port + " is not an available server");
			} catch (BoxClientException e1) {
				e1.printStackTrace();
			}
		}
		this.box = new Box(socket, Color.RED, 100, 100);
		this.j.addWindowListener(this);
		this.c.addKeyListener(this.box);
		System.out.println("Initialized!");

	}

	private void start() {
		if (this.thread == null || this.running)
			return;
		this.running = true;
		this.thread.start();
	}

	private void stop() {
		if (!this.running)
			return;
		this.running = false;
		if (this.box != null) {
			try {
				this.box.getObjectInputStream().close();
				this.box.getObjectOutputStream().close();
				this.box.getSocket().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (this.thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long now = System.nanoTime();
		final long timePerTick = 1000000000 / 60;
		long delta = 0;

		init();

		while (this.running) {
			BufferStrategy bs = this.c.getBufferStrategy();
			if (bs == null) {
				this.c.createBufferStrategy(3);
				continue;
			}
			Graphics2D g = (Graphics2D) bs.getDrawGraphics();

			now = System.nanoTime();
			delta = (now - lastTime);

			if (delta >= timePerTick) {
				g.clearRect(0, 0, 1280, 720);
				lastTime = now;
				tick();
				render(g);
			}
			g.dispose();
			bs.show();
		}

		stop();
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.stop();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
