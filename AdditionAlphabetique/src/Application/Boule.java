package Application;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import Simulation.Problem;

public class Boule {
	private double t; // Position sur le pont, entre 0 et 1
	private final int radius;
	private double vitesse; // Vitesse par tick (modifiable)
	public static Boule instance = new Boule(0, 10, 0.01);
	private List<String> historic;
	private boolean comeBack=false;
	private Link currentLink;
	private String currentStructure;
	private mainPanel mainPanel;
	private boolean end=true;
	private boolean cheminDejaAffiche=false;
	private boolean onRevient=false;
	private String step;
	private Problem currentProblem;


	private Boule(double t, int radius, double vitesse) {
		this.t=t;
		this.radius=radius;
		this.vitesse=vitesse;
		this.historic=new ArrayList<String>();
	}

	public static Boule getInstance() {
		return instance;
	}

	public void avancer() {
		
		if(currentStructure!=null && currentStructure.equals("End")) {
			end=true;
			mainPanel.solution(currentProblem.error());
		}
		else {
			if(cheminDejaAffiche==false) {
				//System.out.println(currentLink.getStartStructure().getName() + " à " + currentLink.getEndStructure().getName());
				cheminDejaAffiche=true;
			}
			if (!comeBack && t < 1 && !onRevient) {
				if(t+vitesse>1)
					t=1;
				else t += vitesse;
			} 
			else if (comeBack && t > 0) {
				if(t-vitesse<0)
					t=0;
				else
					t -= vitesse;
				if (t <= 0) {
					comeBack = false;  // On a fini le retour
				}
			}
			else if (!comeBack && !onRevient) {
				System.out.println(currentLink.getName() + "...");

				currentStructure = currentLink.getEndStructure().getName();
				cheminDejaAffiche=false;
				if (historic.size() > 0) {
					step = historic.remove(0);
					if(!step.equals("z reach"))
						System.out.println(step);
					mainPanel.newRule(step);
					if(step.equals("z reach")) {
						mainPanel.stop();
						return;
					}
					// Si on est sur le chemin "Chunk → AnsMem/ProMem" et ce n'est pas un pop_goal, on revient
					if (!step.equals("pop_goal") && currentLink.getStartStructure().getName().equals("Chunk")) {
						comeBack = true;
						onRevient=true;
					} else {
						//System.out.println("Je test avec " + currentStructure + " comme départ");
						Link nextLink = mainPanel.getLinkMap(currentStructure).get(step);
						if(nextLink==null) {
						}
						else
						currentLink = nextLink;
						t = 0;
					}
				}
			}
			else if(onRevient) {
				onRevient=false;
				Link nextLink = mainPanel.getLinkMap(currentStructure).get(step);
				if(nextLink==null) {
				}
				else
				currentLink = nextLink;
				t = 0;
				
			}
		}
		
	}


	public void setVitesse(double v) {
		this.vitesse = v;
	}

	public double getVitesse() {
		return vitesse;
	}

	public void draw(Graphics g) {
		if (currentLink == null) return;
		Point p = currentLink.getPointAt(t);
		g.setColor(Color.RED);
		g.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
	}


	public void setHistoric(List<String> historic) {
		this.historic=historic;
		//System.out.println(historic);
		//System.out.println(vitesse);
	}

	public void setCurrentLink(Link link) {
		this.currentLink = link;
		setCurrentStructure(currentLink.getEndStructure().getName());
	}

	public void setCurrentStructure(String str) {
		this.currentStructure= str;
	}

	public void setMainPanel(mainPanel mainPanel) {
		this.mainPanel=mainPanel;
	}

	public void setComeBack(boolean flag) {
		comeBack=flag;
	}

	public List<String> getHistoric(){
		return historic;
	}

	public boolean isAnimationEnded() {
		return end;
	}

	public void start() {
		end=false;
	}

	public void stop() {
		mainPanel.stop();
	}

	public Link getLink(){
		return currentLink;
	}
	
	public void resetPosition() {
		t=0;
	}
	
	public Link getCurrentLink() {
		return currentLink;
	}
	
	public void setProblem (Problem pb) {
		currentProblem=pb;
	}
	
	public Problem getCurrentProblem() {
		return currentProblem;
	}
}
