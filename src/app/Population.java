package app;

import org.apache.commons.math3.util.FastMath;
import java.text.DecimalFormat;

class Population {

	private Paint z;
	private double g, h, s, r, k;
	private int delay;
	private double gRest;
	private double hRest;
	private boolean hWins = false;
	private double hBuffer;
	private double gBuffer;
	private boolean allDead = false;

	Population(double g, double h, double s, double r, Paint z, int delay) {
		this.g = g;
		this.h = h;
		this.s = s;
		this.r = r;
		this.k = Math.sqrt(s * r);
		this.z = z;
		this.delay = delay;
	}

	void initSimulation() {

		double gH = g / h;
		double hG = h / g;
		DecimalFormat nFormat = new DecimalFormat("0.000");

		if ((s * g * g) - (r * h * h) > 0) {
			calcForces(this.getEndtimeG(hG, nFormat));
		} else if ((s * g * g) - (r * h * h) < 0) {
			calcForces(this.getEndtimeH(gH, nFormat));
		} else {
			calcForces(this.getEndtime(gH));
		}
	}

	private double getEndtime(double gH){
		double endzeit = FastMath.atanh(gH * (k / r)) / k;
		System.out.println("Endzeit Unentschieden: " + endzeit);

		return endzeit;
	}

	private double getEndtimeH(double gH, DecimalFormat nFormat){
		double endtimeH = FastMath.atanh(gH * (k / r)) / k;
		System.out.println("Endzeit H: " + nFormat.format(endtimeH));

		return endtimeH;
	}

	private double getEndtimeG(double hG, DecimalFormat nFormat){
		double endtimeG = FastMath.atanh(hG * (k / s)) / k;
		System.out.println("Endtime G: " + nFormat.format(endtimeG));
		return endtimeG;
	}

	private boolean doesGWin(){
		return (s * g * g) - (r * h * h) > 0;
	}

	private boolean doesHWin(){
		return (s * g * g) - (r * h * h) < 0;
	}

	private void checkWhoWins(){
		if (this.doesHWin()) {
			hWins = true;
			this.calcStartforcesWhenHWins();
		} else if (this.doesGWin()) {
			this.calcStartforcesWhenGWins();
		}
		hBuffer = hRest;
		gBuffer = gRest;
	}

	private void calcStartforcesWhenGWins(){
		gRest = Math.round(g * Math.cosh(0) - (r / k) * h * Math.sinh(0));
		hRest = Math.round(h * Math.cosh(0) - (s / k) * g * Math.sinh(0));
	}

	private void calcStartforcesWhenHWins(){
		hRest = Math.round(h * Math.cosh(0) - (s / k) * g * Math.sinh(0));
		gRest = Math.round(g * Math.cosh(0) - (r / k) * h * Math.sinh(0));
	}

	private void updateLabel(double endTime, double i){
		z.setLab(
				String.valueOf(endTime),
				String.valueOf(gRest),
				String.valueOf(hRest),
				String.valueOf(i),
				String.valueOf(g-gRest),
				String.valueOf(h-hRest));
	}

	private void calcRestInRealtime(double i){
		if (hWins) {
			this.calcRestInRealtimeWhenHWins(i);
		} else {
			this.calcRestInRealtimeWhenGWins(i);
		}
	}

	private void calcGRest(double i){
		gRest = Math.round(g * Math.cosh(i * k) - (r / k) * h * Math.sinh(i * k));
	}

	private void calcHRest(double i){
		hRest = Math.round(h * Math.cosh(i * k) - (s / k) * g * Math.sinh(i * k));
	}

	private void calcRestInRealtimeWhenGWins(double i){
		this.calcGRest(i);
		this.calcHRest(i);
	}

	private void calcRestInRealtimeWhenHWins(double i){
		this.calcGRest(i);
		this.calcHRest(i);
	}

	private void isBufferBiggerThanRest(){
		this.isBufferHBiggerThanHRest();
		this.isBufferGBiggerThanGRest();
	}

	private void isBufferHBiggerThanHRest(){
		if (hBuffer >= hRest) {
			double hDifferenz = hBuffer - hRest;
			for (int j = 0; j < hDifferenz; j++)
				z.deleteCircles(true);
			hBuffer = hRest;
		}
	}

	private void isBufferGBiggerThanGRest(){
		if (gBuffer >= gRest) {
			double gDifferenz = gBuffer - gRest;
			for (int j = 0; j < gDifferenz; j++)
				z.deleteCircles(false);
			gBuffer = gRest;
		}
	}

	private synchronized void setThreadSleep(){
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void loopWin(double endTime){
		for (double i = 0.000; i <= endTime; i += 0.0001) {
			this.calcRestInRealtime(i);
			this.updateLabel(endTime, i);
			this.printRest(gRest, hRest, i);
			this.isBufferBiggerThanRest();
			z.draw();
			this.setThreadSleep();
		}
	}

	private void checkIfAllDead(double i){
		if (gRest == 0 && hRest == 0) {
			this.printAllDead(i);
			allDead = true;
		}
	}

	private void checkIfHWins(){
		if (hBuffer > hRest) {
			double hDifferenz = hBuffer - hRest;
			for (int j = 0; j < hDifferenz; j++)
				z.deleteCircles(true);
			hBuffer = hRest;
		}
	}

	private void checkIfGWins(){
		if (gBuffer > gRest) {
			double gDifferenz = gBuffer - gRest;
			for (int j = 0; j < gDifferenz; j++)
				z.deleteCircles(false);
			gBuffer = gRest;
		}
	}

	private void calcRestWhenAllDead(double i){
		this.calcRestInRealtimeWhenHWins(i);
	}

	private void loopAllDead(double endTime){
		for (double i = 0.000; i<= endTime && !allDead; i += 0.001) {
			this.calcRestWhenAllDead(i);
			z.setLab(String.valueOf(endTime), String.valueOf(gRest), String.valueOf(hRest), String.valueOf(i), String.valueOf(g-gRest), String.valueOf(h-hRest));
			this.printRest(gRest, hRest, i);
			this.checkIfAllDead(i);
			this.checkIfHWins();
			this.checkIfGWins();

			z.draw();
			this.setThreadSleep();
		}
	}

	private void calcForces(double endTime) {

		if (this.doesGWin() || this.doesHWin()) {
			this.checkWhoWins();
			this.loopWin(endTime);
		} else {
			gRest = Math.round(g * Math.cosh(0) - (r / k) * h * Math.sinh(0));
			hRest = Math.round(h * Math.cosh(0) - (s / k) * g * Math.sinh(0));
			gBuffer = gRest;
			hBuffer = hRest;
			this.loopAllDead(endTime);
		}
	}

	private void printAllDead(double i){
		DecimalFormat nFormat = new DecimalFormat("0.000");
		System.out.println("G und H haben sich gegenseitig ausgelöscht zum Zeitpunkt: " + nFormat.format(i));
	}

	private void printRest(double gRest, double hRest, double i){
		DecimalFormat nFormat = new DecimalFormat("0.000");
		System.out.println("Troupnumber of G : " + gRest + " at : " + nFormat.format(i));
		System.out.println("Troupnumber of H : " + hRest + " at: " + nFormat.format(i));
	}

	double getH() {
		return h;
	}

	double getG() {
		return g;
	}
}