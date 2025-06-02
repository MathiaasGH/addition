package Application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Structure {
    private int x, y, width, height;
    private Color couleur;
    private boolean selectionne = false;
    private String titre;

    public Structure(int x, int y, int width, int height, Color couleur, String titre) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.couleur = couleur;
        this.titre= titre;
    }

    // Dessiner le rectangle
    public void dessiner(Graphics g) {
        g.setColor(couleur);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        if (selectionne) {
            g.setColor(Color.RED);
            g.drawRect(x + 2, y + 2, width - 4, height - 4); // encadré rouge autour
        }
        
     // Ajouter le titre au centre du rectangle
        g.setColor(Color.BLACK);  // Couleur du texte
        g.setFont(new Font("Arial", Font.BOLD, 14)); // Définir la police, la taille et le style
        int stringWidth = g.getFontMetrics().stringWidth(titre);  // Calculer la largeur du texte
        int stringHeight = g.getFontMetrics().getHeight(); // Calculer la hauteur du texte

        // Calculer les coordonnées pour centrer le texte dans le rectangle
        int textX = x + (width - stringWidth) / 2; // Position X du texte centré
        int textY = y + (height + stringHeight) / 2 - 2; // Position Y du texte centré (ajuster pour centrer verticalement)

        // Dessiner le texte
        g.drawString(titre, textX, textY);
    }

    // Vérifier si un clic est dans les coordonnées du rectangle
    public boolean contient(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    public void setSelected(boolean selected) {
        this.selectionne = selected;
    }

    // Getter pour X et Width pour recalculer le X centré
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public void setX(int X) {
    	x=X;
    }
    
    public void setY(int Y) {
    	y=Y;
    }
    
    public void setWidth(int width) {
    	this.width=width;
    }
    
    public void setHeight(int height) {
    	this.height=height;
    }
    
    public String getName() {
    	return titre;
    }
}
