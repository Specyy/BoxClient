package com.box.client.main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Box implements KeyListener, Serializable {
	private static final long serialVersionUID = 1449764394855700137L;
	private Socket s;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Color color;

	private boolean keys[];
	private double x;
	private double y;

	private double speed = 3;

	public Box(Socket s) {
		this(s, Color.RED, 0.0D, 0.0D);

	}

	public Box(Socket s, Color color) {
		this(s, color, 0.0D, 0.0D);
	}

	public Box(Socket s, Color color, double x, double y) {
		this.s = s;
		this.keys = new boolean[999];
		this.color = color;

		try {
			this.oos = new ObjectOutputStream(this.s.getOutputStream());
			this.ois = new ObjectInputStream(this.s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.x = x;
		this.y = y;

	}

	public Socket getSocket() {
		return s;
	}

	public void setSocket(Socket s) {
		this.s = s;
	}

	public ObjectInputStream getObjectInputStream() {
		return ois;
	}

	public void setObjectInputStream(ObjectInputStream ois) {
		this.ois = ois;
	}

	public ObjectOutputStream getObjectOutputStream() {
		return oos;
	}

	public void setObjectOutputStream(ObjectOutputStream oos) {
		this.oos = oos;
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean[] getKeys() {
		return keys;
	}

	public void setKeys(boolean[] keys) {
		this.keys = keys;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void render(Graphics2D g) {
		BoxMain.drawBox(g, x, y, color);
	}

	public void tick() {
		if (keys[KeyEvent.VK_UP]) {
			y -= speed;
		}

		if (keys[KeyEvent.VK_DOWN]) {
			y += speed;
		}
		if (keys[KeyEvent.VK_LEFT]) {
			x -= speed;
		}

		if (keys[KeyEvent.VK_RIGHT]) {
			x += speed;
		}
		try {
			this.getObjectOutputStream().writeDouble(x);
			this.getObjectOutputStream().writeDouble(y);
			this.getObjectOutputStream().writeObject(color);
			this.getObjectOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

}
