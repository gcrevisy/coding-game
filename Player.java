import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Grab the pellets as fast as you can!
 * seed=782918738956375040
 **/
enum ActionType {
    MOVE, SWITCH, SPEED, NOTHING
}

class Player {
    private static Map<Integer, Pac> myTeam = new HashMap<Integer, Pac>();
    private static Map<Integer, Pac> otherTeam = new HashMap<Integer, Pac>();

    private static List<Cellule> targetedCells = new ArrayList<Cellule>();

    private static String ROCK = "ROCK";
    private static String PAPER = "PAPER";
    private static String SCISSORS = "SCISSORS";

    public static void main(String args[]) {
        List<Cellule> listeCells = new ArrayList<Cellule>();
        Map<Integer, List<Cellule>> carte;

        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }

        carte = new HashMap<Integer, List<Cellule>>();
        for (int x = 0; x < width; x++) {
            carte.put(x, new ArrayList<Cellule>());
        }

        System.err.println("w " + width + " h " + height);
        for (int y = 0; y < height; y++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            for (int x = 0; x < width; x++) {
                String v = row.substring(x, x + 1);
                if (!"#".equals(v)) {
                    carte.get(x).add(new Cellule(x, y, 0));
                }
            }
        }

        // game loop
        while (true) {
            initTeams(in);

            int visiblePelletCount = in.nextInt(); // all pellets in sight
            // System.err.println("visiblePelletCount -> " + visiblePelletCount);
            listeCells.clear();
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth
                Cellule cell = new Cellule(x, y, value);
                // System.err.println(cell);
                listeCells.add(cell);
            }

            System.out.println(action(carte, listeCells));

        }
    }

    /**
     * Parcours des cellules.
     * @param carte
     * @return
     */
    public static String action(Map<Integer, List<Cellule>> carte, List<Cellule> listeVisibleCells) {

        String result = "";
        for (Integer pacId : myTeam.keySet()) {
            Pac pac = myTeam.get(pacId);
            if (pac == null) {
                continue;
            }

            boolean arrive = pac.getTargetedCell() != null && pac.getX() == pac.getTargetedCell().getX()
                    && pac.getY() == pac.getTargetedCell().getY();

            boolean targetcelldeleted = pac.getTargetedCell() != null && pac.getTargetedCell().getValeur() == 10
                    && !listeVisibleCells.contains(pac.getTargetedCell());

            if (arrive || targetcelldeleted) {
                pac.setTargetedCell(null);
                pac.setActionType(ActionType.NOTHING);
            }

            System.err.println("avant => " + pac.getTargetedCell());
            if (pac.getTargetedCell() == null) {

                // ciblage des cellule a valeur 10 en priorite

                List<Cellule> liste = listeVisibleCells.stream().filter(item -> item.getValeur() == 10)
                        .collect(Collectors.toList());
                if (liste.size() > 0) {
                    Cellule targetedCell = determinerCellulePlusProche(pac, liste);
                    pac.setTargetedCell(targetedCell);
                    pac.setActionType(ActionType.MOVE);
                    targetedCells.add(targetedCell);
                    if (pac.getTargetedCell() != null)
                        continue;
                }

                System.err.println("on ne chasse pas");
                int x = pac.getX() + pac.getId();
                if (x >= 34) {
                    x = 0;
                }
                List<Cellule> cells = carte.get(x);

                if (pac.getY() > 7) {
                    cells.sort((Cellule c1, Cellule c2) -> new CelluleComparator().compare(c1, c2));
                } else {
                    cells.sort((Cellule c1, Cellule c2) -> new CelluleComparator().compare(c2, c1));
                }
                pac.setTargetedCell(cells.get(0));
                pac.setActionType(ActionType.MOVE);

                // chasse ?
                System.err.println("on chasse ?");
                if (ActionType.NOTHING.equals(pac.getActionType()) && otherTeam.size() > 0) {
                    Pac otherPac = determinerPacPlusProche(pac, otherTeam);
                    System.err.println(pac.getId() + " chasse " + otherPac);
                    if (ROCK.equals(otherPac.getTypeId())) {
                        pac.setTypeId(PAPER);
                        pac.setActionType(ActionType.SWITCH);
                        continue;
                    } else if (PAPER.equals(otherPac.getTypeId())) {
                        pac.setTypeId(SCISSORS);
                        pac.setActionType(ActionType.SWITCH);
                        continue;
                    } else if (SCISSORS.equals(otherPac.getTypeId())) {
                        pac.setTypeId(ROCK);
                        pac.setActionType(ActionType.SWITCH);
                        continue;
                    }
                    pac.setTargetedCell(new Cellule(otherPac.getX(), otherPac.getY(), 0));
                    pac.setActionType(ActionType.MOVE);
                    continue;
                }

            }
        }

        for (Integer pacId : myTeam.keySet()) {
            Pac pac = myTeam.get(pacId);
            if (pac == null) {
                continue;
            }
            System.err.println("apres => " + pac.getTargetedCell());
            result += pac.doSomething() + " | ";
        }

        return result;
    }

    private static Pac determinerPacPlusProche(Pac pac, Map<Integer, Pac> liste) {
        System.err.println("determinerPacPlusProche " + pac);
        Double proche = Double.MAX_VALUE;
        Pac result = null;
        for (Integer pacId : liste.keySet()) {

            Pac otherPac = liste.get(pacId);
            Double dist = Math.sqrt(Math.pow(Math.abs(pac.getX() - otherPac.getX()), 2)
                    + Math.pow(Math.abs(pac.getY() - otherPac.getY()), 2));
            if (dist < proche) {
                proche = dist;
                result = otherPac;
            }
        }
        return result;
    }

    private static Cellule determinerCellulePlusProche(Pac pac, List<Cellule> liste) {
        System.err.println("determinerCellulePlusProche " + pac);
        Double proche = Double.MAX_VALUE;
        Cellule result = null;
        for (Cellule cellule : liste) {
            if (targetedCells.contains(cellule))
                continue;

            Double dist = Math.sqrt(Math.pow(Math.abs(pac.getX() - cellule.getX()), 2)
                    + Math.pow(Math.abs(pac.getY() - cellule.getY()), 2));
            if (dist < proche) {
                proche = dist;
                result = cellule;
            }
        }
        return result;
    }

    public static void initTeams(Scanner in) {

        // int myScore = 
        in.nextInt();
        // int opponentScore = 
        in.nextInt();
        int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
        for (int i = 0; i < visiblePacCount; i++) {
            int pacId = in.nextInt(); // pac number (unique within a team)
            boolean mine = in.nextInt() != 0; // true if this pac is yours
            int currentX = in.nextInt(); // position in the grid
            int currentY = in.nextInt(); // position in the grid
            String typeId = in.next(); // unused in wood leagues
            int speedTurnsLeft = in.nextInt(); // unused in wood leagues
            int abilityCooldown = in.nextInt(); // unused in wood leagues
            if (mine) {
                if (myTeam.get(pacId) == null) {
                    Pac pac = new Pac(pacId, currentX, currentY, typeId, speedTurnsLeft, abilityCooldown, null,
                            ActionType.NOTHING);
                    myTeam.put(pacId, pac);
                } else {
                    myTeam.get(pacId).setX(currentX);
                    myTeam.get(pacId).setY(currentY);
                }
            } else {
                if (otherTeam.get(pacId) == null) {
                    Pac pac = new Pac(pacId, currentX, currentY, typeId, speedTurnsLeft, abilityCooldown, null,
                            ActionType.NOTHING);
                    otherTeam.put(pacId, pac);
                } else {
                    otherTeam.get(pacId).setX(currentX);
                    otherTeam.get(pacId).setY(currentY);
                }
            }
        }
    }
}

class CelluleComparator implements Comparator<Cellule> {

    @Override
    public int compare(Cellule o1, Cellule o2) {
        int result = 0;
        if (o1.getY() > o2.getY()) {
            result = 1;
        } else {
            result = -1;
        }
        return result;
    }

}

class Cellule {

    private int x, y, valeur;

    public Cellule() {
    }

    public Cellule(int x, int y, int valeur) {
        this.x = x;
        this.y = y;
        this.valeur = valeur;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValeur() {
        return this.valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public Cellule x(int x) {
        this.x = x;
        return this;
    }

    public Cellule y(int y) {
        this.y = y;
        return this;
    }

    public Cellule valeur(int valeur) {
        this.valeur = valeur;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Cellule)) {
            return false;
        }
        Cellule cellule = (Cellule) o;
        return x == cellule.x && y == cellule.y && valeur == cellule.valeur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, valeur);
    }

    @Override
    public String toString() {
        return "{" + " x='" + getX() + "'" + ", y='" + getY() + "'" + ", valeur='" + getValeur() + "'" + "}";
    }

}

class Pac {
    private int id;
    private int x;
    private int y;
    private String typeId;
    private int speedTurnsLeft;
    private int abilityCooldown;
    private Cellule targetedCell;
    private ActionType actionType;

    public String doSomething() {
        String result = "";
        if (ActionType.MOVE.equals(actionType)) {
            result += "MOVE " + getId() + " " + getTargetedCell().getX() + " " + getTargetedCell().getY();
        } else if (ActionType.SPEED.equals(actionType)) {

        } else if (ActionType.SWITCH.equals(actionType)) {

        }
        return result;
    }

    public Pac() {
    }

    public Pac(int id, int x, int y, String typeId, int speedTurnsLeft, int abilityCooldown, Cellule targetedCell,
            ActionType actionType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.typeId = typeId;
        this.speedTurnsLeft = speedTurnsLeft;
        this.abilityCooldown = abilityCooldown;
        this.targetedCell = targetedCell;
        this.actionType = actionType;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getTypeId() {
        return this.typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public int getSpeedTurnsLeft() {
        return this.speedTurnsLeft;
    }

    public void setSpeedTurnsLeft(int speedTurnsLeft) {
        this.speedTurnsLeft = speedTurnsLeft;
    }

    public int getAbilityCooldown() {
        return this.abilityCooldown;
    }

    public void setAbilityCooldown(int abilityCooldown) {
        this.abilityCooldown = abilityCooldown;
    }

    public Cellule getTargetedCell() {
        return this.targetedCell;
    }

    public void setTargetedCell(Cellule targetedCell) {
        this.targetedCell = targetedCell;
    }

    public ActionType getActionType() {
        return this.actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Pac id(int id) {
        this.id = id;
        return this;
    }

    public Pac x(int x) {
        this.x = x;
        return this;
    }

    public Pac y(int y) {
        this.y = y;
        return this;
    }

    public Pac typeId(String typeId) {
        this.typeId = typeId;
        return this;
    }

    public Pac speedTurnsLeft(int speedTurnsLeft) {
        this.speedTurnsLeft = speedTurnsLeft;
        return this;
    }

    public Pac abilityCooldown(int abilityCooldown) {
        this.abilityCooldown = abilityCooldown;
        return this;
    }

    public Pac targetedCell(Cellule targetedCell) {
        this.targetedCell = targetedCell;
        return this;
    }

    public Pac actionType(ActionType actionType) {
        this.actionType = actionType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Pac)) {
            return false;
        }
        Pac pac = (Pac) o;
        return id == pac.id && x == pac.x && y == pac.y && Objects.equals(typeId, pac.typeId)
                && speedTurnsLeft == pac.speedTurnsLeft && abilityCooldown == pac.abilityCooldown
                && Objects.equals(targetedCell, pac.targetedCell) && Objects.equals(actionType, pac.actionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, typeId, speedTurnsLeft, abilityCooldown, targetedCell, actionType);
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", x='" + getX() + "'" + ", y='" + getY() + "'" + ", typeId='"
                + getTypeId() + "'" + ", speedTurnsLeft='" + getSpeedTurnsLeft() + "'" + ", abilityCooldown='"
                + getAbilityCooldown() + "'" + ", targetedCell='" + getTargetedCell() + "'" + ", actionType='"
                + getActionType() + "'" + "}";
    }

}
