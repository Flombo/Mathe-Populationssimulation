package app;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import scene.Circle;
import javax.swing.*;

public class Paint extends JFrame {

	private Population p;
	private ArrayList<Circle> g1;
	private ArrayList<Circle> h1;
	private static final long serialVersionUID = 1L;
	private BufferedImage backBuffer;
	private static JTextField h = new JTextField("300", 8);
	private static JTextField g = new JTextField("3000", 8);
	private static JTextField s = new JTextField("1", 8);
	private static JTextField r = new JTextField("1", 8);
	private static JTextField delay = new JTextField("1", 8);
	private static JLabel labEnd = new JLabel("");
	private static JLabel gRest = new JLabel("");
	private static JLabel hRest = new JLabel("");
	private static JLabel zeit = new JLabel("");
	private static JLabel gfallene = new JLabel("");
	private static JLabel hfallene = new JLabel("");
	private boolean drawAtAll = false;
	private Thread tLayout;

	void setLab(String end, String RestG, String RestH, String t, String gFallene, String hFallene) {
		labEnd.setText("Endtime: " + end);
		gRest.setText("Rest G: " + RestG);
		hRest.setText("Rest H: " + RestH);
		if (t.length() >= 3) {
			zeit.setText("time took: " + t.substring(0, 3));
		} else {
			zeit.setText("time took: " + t);
		}

		gfallene.setText("Dead G: "+ gFallene);
		hfallene.setText("Dead H: "+ hFallene);
	}



	private Paint() {
		this.setFrameAttributes();
		backBuffer = new BufferedImage(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);

		JLabel armyG = new JLabel("Army of G");
		JLabel armyH = new JLabel("Army of H");
		JLabel weaponsG = new JLabel("Power of G");
		JLabel weaponsH = new JLabel("Power of H");
		JLabel delay = new JLabel("delay");
		JLabel space = new JLabel("");
		JButton start = new JButton("Start");

		this.setButtonAttributes(start);
		this.setClickHandler(start);

		JPanel panel = new JPanel();
		this.setPanelAttributes(panel, armyG, armyH, weaponsG, weaponsH, delay, space, start);

		// panel wird in Frame eingefügt
		add(panel, BorderLayout.NORTH);

		//zweites Panel für Zeit, G, H und Endzeit
		JPanel panelE = new JPanel();
		this.setLabelColor();
		this.addElementsToPanel(panelE);
		add(panelE);
		setVisible(true);
	}

	private void addElementsToPanel(JPanel panel){
		panel.add(labEnd);
		panel.add(gRest);
		panel.add(hRest);
		panel.add(zeit);
		panel.add(gfallene);
		panel.add(hfallene);
	}

	private void setLabelColor(){
		gRest.setForeground(Color.red);
		hRest.setForeground(Color.blue);
		gfallene.setForeground(Color.green);
		hfallene.setForeground(Color.DARK_GRAY);
	}

	private void setClickHandler(JButton start){
		start.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				drawAtAll = true;
				if (tLayout != null)
					tLayout.interrupt();

				Runnable run = () -> {
					drawAtAll = true;
					try {
						startDrawing((Integer.parseInt(g.getText())), (Integer.parseInt(h.getText())),
								(Integer.parseInt(s.getText())), (Integer.parseInt(r.getText())),
								(Integer.parseInt(delay.getText())));
					} catch (Exception e1) {
						System.out.println("Error:" + e1.getMessage());
					}
				};
				tLayout = new Thread(run);
				tLayout.start();
			}
		});
	}

	private void setButtonAttributes(JButton start){
		start.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
		start.setBackground(Color.ORANGE);
	}

	private void setPanelAttributes(
			JPanel panel,
			JLabel armyG,
			JLabel armyH,
			JLabel weaponsG,
			JLabel weaponsH,
			JLabel delay,
			JLabel space,
			JButton start
	){
		panel.setLayout(new GridLayout(2, 10, 30, 0));
		panel.setBackground(Color.LIGHT_GRAY);
		this.addToInnerPanel(panel, armyG, armyH,weaponsG, weaponsH, delay, space, start);
	}

	private void addToInnerPanel(
			JPanel panel,
			JLabel armyG,
			JLabel lab2,
			JLabel lab3,
			JLabel lab4,
			JLabel lab5,
			JLabel space,
			JButton start
	){
		GridBagConstraints c = new GridBagConstraints();
		panel.add(armyG, c);
		panel.add(g, c);
		panel.add(lab2, c);
		panel.add(h, c);
		panel.add(lab3, c);
		panel.add(s, c);
		panel.add(lab4, c);
		panel.add(r, c);
		panel.add(lab5, c);
		panel.add(delay, c);
		panel.add(space, c);
		panel.add(start, c);
	}

	private void setFrameAttributes(){
		setTitle("Populationssimulation");
		setSize(Constants.WINDOW_WIDTH + 10, Constants.WINDOW_HEIGHT + 110);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void startDrawing(double g, double h, double s, double r, int delay){

		p = new Population(g, h, s, r, this, delay);
		this.g1 = new ArrayList<>();
		this.h1 = new ArrayList<>();
		createCircles();
		setVisible(true);
		p.initSimulation();
	}

	private void createCircles() {

		for (int i = 0; i < p.getG(); i++){
			Circle k = new Circle();
			this.setCircleAttributes(k);
			this.g1.add(k);
		}

		for (int i = 0; i < p.getH(); i++){
			Circle k = new Circle();
			this.setCircleAttributes(k);
			this.h1.add(k);
		}
	}

	private void setCircleAttributes(Circle k){
		Random generator = new Random();
		k.diameter = 80;
		k.positionX = generator.nextInt((getSize().width - 10) - 20) + 20;
		k.positionY = generator.nextInt((getSize().height - 110) - 20) + 20;
		k.con_width = getSize().width - 10;
		k.con_height = getSize().height - 110;
	}

	void deleteCircles(boolean delete) {
		int deleteG = (int) (Math.floor(Math.random() * g1.size()));
		int deleteH = (int) (Math.floor(Math.random() * h1.size()));

		if (g1.size() > 0 && !delete) {
			g1.remove(deleteG);
		}
		if (h1.size() > 0 && delete) {
			h1.remove(deleteH);
		}
	}

	// Zeichnet automatisch ==> braucht man fürs Fenster skalieren
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		draw();
	}

	void draw() {

		if (!drawAtAll) // Wenn überhaupt auf Start geklickt wurde zeichne
			// Kreise
			return;

		Constants.WINDOW_WIDTH = getSize().width - 10;
		Constants.WINDOW_HEIGHT = getSize().height - 110;
		backBuffer = new BufferedImage(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics g = getGraphics();
		Graphics bbg = backBuffer.getGraphics();
		bbg.setColor(Color.white);
		bbg.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
		bbg.setColor(Color.blue);
		drawOval(bbg, h1);
		bbg.setColor(Color.red);
		drawOval(bbg, g1);
		g.drawImage(backBuffer, 0, 100, null);
	}

	private void drawOval(Graphics bbg, ArrayList<Circle> h1) {
		for (Circle circle : h1) {

			float x = ((float) Constants.WINDOW_WIDTH / (float) circle.con_width) * (float) circle.positionX;
			float y = ((float) Constants.WINDOW_HEIGHT / (float) circle.con_height)
					* (float) circle.positionY;

			bbg.fillOval((int) (x), (int) (y), 10, 10);
		}
	}

	public static void main(String[] args) {
		new Paint();
	}
}