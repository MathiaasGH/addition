package Application;

import javax.swing.*;
import Exceptions.ProblemException;
import Simulation.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class mainPanel extends JPanel {
    private ArrayList<Structure> structures = new ArrayList<>();
    private ArrayList<Link> links = new ArrayList<>();

    private Structure problem;
    private Structure chunk;
    private Structure ans_mem;
    private Structure pro_mem;
    private Structure end;

    private Link link1;
    private Link link2;
    private Link link3;
    private Link link5;
    private Link link6;

    private Structure rectangleSurvole = null;
    private Link linkSurvole = null;
    private int mouseX;
    private int mouseY;

    private Boule boule = Boule.getInstance();
    private boolean showBoule = false;
    private Model model = new Model("breaker");
    private Timer timer;

    private int initialWidth;
    private int initialHeight;
    private boolean initialized = false;
    
    private appli appli;
    
    private char letter;
    private String problemName;
    private int idxRetrieved=0;
    
    private Problem currentProblem;

    public mainPanel() {
        boule.setMainPanel(this);
        setBackground(Color.WHITE);

        initialWidth = 800;
        initialHeight = 600;

        createStructuresAndLinks();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	for (Structure r : structures) {
                    // reset ALL selections
                    r.setSelected(false);
                }
                for (Structure r : structures) {
                    if (r.contient(e.getX(), e.getY())) {
                        r.setSelected(true);
                        appli.select(r);
                        repaint();
                        break;
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                rectangleSurvole = null;
                for (Structure r : structures) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                    if (r.contient(e.getX(), e.getY())) {
                        rectangleSurvole = r;
                        break;
                    }
                }
//                linkSurvole = null;
//                for (Link l : links) {
//                    mouseX = e.getX();
//                    mouseY = e.getY();
//                    if (l.survole(e.getX(), e.getY())) {
//                        linkSurvole = l;
//                        break;
//                    }
//                }
                repaint();
            }
        });
    }

    private void createStructuresAndLinks() {
        int panelWidth = getWidth() > 0 ? getWidth() : initialWidth;
        int panelHeight = getHeight() > 0 ? getHeight() : initialHeight;

        problem = new Structure((panelWidth - 100) / 2, 50, 100, 100, Color.CYAN, "Problem");
        chunk = new Structure((panelWidth - 100) / 2, 200, 100, 100, Color.ORANGE, "Chunk");
        ans_mem = new Structure(panelWidth / 4 - 50, 310, 100, 100, Color.PINK, "Answer Memory");
        pro_mem = new Structure(panelWidth * 3 / 4 - 50, 310, 100, 100, Color.LIGHT_GRAY, "Procedural Memory");
        end = new Structure((panelWidth - 100) / 2, 450, 100, 100, Color.MAGENTA, "End");

        structures.add(problem);
        structures.add(chunk);
        structures.add(ans_mem);
        structures.add(pro_mem);
        structures.add(end);

        link1 = new Link(problem, chunk);
        link2 = new Link(chunk, ans_mem);
        link3 = new Link(chunk, pro_mem);
        link5 = new Link(ans_mem, end);
        link6 = new Link(pro_mem, end);

        links.add(link1);
        links.add(link2);
        links.add(link3);
        links.add(link5);
        links.add(link6);

        if (!initialized) {
            boule.setCurrentLink(link1);
            initialized = true;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Link l : links) {
            l.dessiner(g, Color.black, 1);
        }

        for (Structure r : structures) {
            r.dessiner(g);
        }

        if (rectangleSurvole != null) {
            g.setColor(Color.YELLOW);
            g.drawRect(rectangleSurvole.getX(), rectangleSurvole.getY(),
                    rectangleSurvole.getWidth(), rectangleSurvole.getHeight());
        }

        if (linkSurvole != null) {
            linkSurvole.dessiner(g, Color.red, 3);
        }

        if (showBoule && boule != null) {
            boule.draw(g);
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        updateRectanglesPositionAndSize();
        repaint();
    }

    private void updateRectanglesPositionAndSize() {
        float scaleX = getWidth() / (float) initialWidth;
        float scaleY = getHeight() / (float) initialHeight;

        for (Structure str : structures) {
            int newX = (int) (str.getX() * scaleX);
            int newY = (int) (str.getY() * scaleY);
            int newWidth = (int) (str.getWidth() * scaleX);
            int newHeight = (int) (str.getHeight() * scaleY);

            str.setX(newX);
            str.setY(newY);
            str.setWidth(newWidth);
            str.setHeight(newHeight);
        }

        initialWidth = getWidth();
        initialHeight = getHeight();
    }

    public void start(String name) {
        try {
        	idxRetrieved=0;
        	problemName = name;
            boule.setCurrentLink(link1);
            boule.resetPosition();
            currentProblem = new Problem(name, model);
            model.addProblem(currentProblem);
            boule.setHistoric(currentProblem.trace());
            showBoule = true;
            boule.setProblem(currentProblem);
            boule.setComeBack(false);
            boule.start();
            appli.create(name);
            letter=name.charAt(0);

            if (timer != null)
                timer.stop();

            timer = new Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boule.avancer();
                    repaint();
                    if (boule.isAnimationEnded()) {
                        stop();
                    }
                }
            });
            timer.start();
        } catch (ProblemException e) {
            System.out.println(e);
        }
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        appli.auto();
    }

    public Map<String, Link> getLinkMap(String currentStr) {
        Map<String, Link> map = new HashMap<>();
        map.put("increment", link3);
        map.put("incrementOnlyLetter", link3);
        map.put("answerRetrieved", link2);
        if (currentStr.equals("Answer Memory")) {
            map.put("pop_goal", link5);
        } else
            map.put("pop_goal", link6);
        return map;
    }

    public void setVitesse(int vitesse) {
        boule.setVitesse(vitesse / 150.0);
    }

    public void setParam(double dd, double ed, double wi, int ip, int incp, double is, double incs) {
        model.setDecisionDeterminism(dd);
        model.setErrorDiscount(ed);
        model.setWeightIncrease(wi);
        model.setinitialPracticeWithoutReset(ip);
        model.setincreasePractice(incp);
        model.setinitialStrength(is);
        model.setincreaseStrength(incs);
    }

    public void session(ArrayList<String> charList) {
        model.session(charList, true);
        appli.updateInfos();
    }

    public void session10(ArrayList<String> charList) {
        for (int i = 0; i < 10; i++)
            model.session(charList, true);
        appli.updateInfos();
    }
    
    public void setAppli(appli appli) {
    	this.appli=appli;
    }
    
    public void newRule(String rule) {
    	appli.newRule(rule);
    	if(rule.equals("increment")) {
    		appli.newChunk(letter, true);
    		letter++;
    	}
    	if(rule.equals("incrementOnlyLetter")) {
    		appli.newChunk(letter, false);
    		letter++;
    	}
    	if(rule.equals("answerRetrieved")) {
    		String ans = currentProblem.getHistoryRetrieved().get(idxRetrieved);
    		appli.ansRet(ans);
    		letter=ans.charAt(4);
    	}
    }
    
    public void solution(boolean error) {
    	appli.solution(letter, problemName, error);
    	repaint();
    	revalidate();
    }
    
    public Model getModel() {
    	return model;
    }
}
