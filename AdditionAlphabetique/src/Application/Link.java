package Application;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.BasicStroke;

public class Link {
    private Structure startStructure;
    private Structure endStructure;

    // Constructeur sans épaisseur
    public Link(Structure startStructure, Structure endStructure) {
        this.startStructure = startStructure;
        this.endStructure = endStructure;
    }

    // Dessiner le trait reliant les deux rectangles avec une épaisseur spécifiée
    public void dessiner(Graphics g, Color couleur, int epaisseur) {
        // Convertir Graphics en Graphics2D pour pouvoir changer l'épaisseur du trait
        Graphics2D g2d = (Graphics2D) g;

        // Appliquer l'épaisseur du trait
        g2d.setStroke(new BasicStroke(epaisseur));  // Définit l'épaisseur du trait

        // Calculer les coordonnées des bords des rectangles
        int startX = startStructure.getX() + startStructure.getWidth() / 2;
        int startY = startStructure.getY() + startStructure.getHeight() / 2;
        int endX = endStructure.getX() + endStructure.getWidth() / 2;
        int endY = endStructure.getY() + endStructure.getHeight() / 2;

        // Dessiner le trait entre les deux centres
        g2d.setColor(couleur);  // Couleur du trait
        g2d.drawLine(startX, startY, endX, endY);  // Dessiner la ligne
    }

    // Vérifier si un point (la souris) survole la ligne
    public boolean survole(int mouseX, int mouseY) {
        // Récupérer les coordonnées des bords des rectangles
        int startX = startStructure.getX() + startStructure.getWidth();
        int startY = startStructure.getY() + startStructure.getHeight() / 2;
        int endX = endStructure.getX();
        int endY = endStructure.getY() + endStructure.getHeight() / 2;

        // Calculer la distance entre le point souris (mouseX, mouseY) et la ligne
        double distance = distancePointToLine(startX, startY, endX, endY, mouseX, mouseY);

        // Définir un seuil de tolérance pour considérer que la souris survole le lien
        int tolerance = 5;  // La tolérance peut être ajustée selon le besoin

        return distance <= tolerance;
    }

    // Méthode pour calculer la distance entre un point et une ligne
    private double distancePointToLine(int x1, int y1, int x2, int y2, int px, int py) {
        double nominator = Math.abs((y2 - y1) * px - (x2 - x1) * py + x2 * y1 - y2 * x1);
        double denominator = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
        return nominator / denominator;
    }

    // Effacer la ligne en redessinant un fond (ici, un fond blanc)
    public void efface(Graphics g) {
        // Récupérer les coordonnées des bords des rectangles
        int startX = startStructure.getX() + startStructure.getWidth() / 2;
        int startY = startStructure.getY() + startStructure.getHeight() / 2;
        int endX = endStructure.getX() + endStructure.getWidth() / 2;
        int endY = endStructure.getY() + endStructure.getHeight() / 2;

        // Dessiner un rectangle blanc à la place de la ligne (supposons que le fond est blanc)
        g.setColor(Color.WHITE);  // Couleur de fond pour effacer
        g.fillRect(Math.min(startX, endX) - 1, Math.min(startY, endY) - 1, Math.abs(startX - endX) + 2, Math.abs(startY - endY) + 2);
    }
    
    public Point getPointAt(double t) {
    	int x1 = startStructure.getX() + startStructure.getWidth() / 2;
    	int y1 = startStructure.getY() + startStructure.getHeight() / 2;
    	int x2 = endStructure.getX() + endStructure.getWidth() / 2;
    	int y2 = endStructure.getY() + endStructure.getHeight() / 2;
        int x = (int) (x1 + t * (x2 - x1));
        int y = (int) (y1 + t * (y2 - y1));
        return new Point(x, y);
    }
    
    public Structure getStartStructure() {
    	return startStructure;
    }
    
    public Structure getEndStructure() {
    	return endStructure;
    }
    
    public String getName() {
    	return startStructure.getName() + " -> " + endStructure.getName();
    }
}
