import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {

    private static Ground ground;
    private static List<Cellule> listeCells = new ArrayList<Cellule>();
    private static Team team = new Team();

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
        }

        ground = new Ground();
        ground.init(in);

        // game loop
        while (true) {
            team.init(in);
            /**
            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            int currentX = -1;
            int currentY = -1;
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                if (mine) {
                    currentX = in.nextInt(); // position in the grid
                    currentY = in.nextInt(); // position in the grid
                } else {
                    in.nextInt();
                    in.nextInt();
                }
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues
            }*/

            int visiblePelletCount = in.nextInt(); // all pellets in sight
            System.err.println("visiblePelletCount -> " + visiblePelletCount);
            listeCells.clear();
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth
                listeCells.add(new Cellule(x, y, value));
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            // System.err.println("targetedCell -> " + targetedCell);
            // System.err.println("x, y, value -> " + currentX + ", " + currentY);

            // FIXME a remplacer if (targetedCell != null && currentX == targetedCell.getX() && currentY == targetedCell.getY()) {
            //     targetedCell = null;
            // }

            // if (targetedCell == null) {
            //     Optional<Cellule> item = listeCells.stream().filter(c -> c.getValeur() == 10).findFirst();
            //     if (item.isPresent()) {
            //         targetedCell = item.get();
            //     } else {
            //         targetedCell = listeCells.get(0);
            //     }
            //     listeCells.remove(targetedCell);
            // }
            // System.out.println("MOVE 0 " + targetedCell.getX() + " " + targetedCell.getY() + ""); // MOVE <pacId> <x> <y>
            System.out.println(team.move(listeCells));

        }
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

    public Pac() {
    }

    public Pac(int id, int x, int y, String typeId, int speedTurnsLeft, int abilityCooldown, Cellule targetedCell) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.typeId = typeId;
        this.speedTurnsLeft = speedTurnsLeft;
        this.abilityCooldown = abilityCooldown;
        this.targetedCell = targetedCell;
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
                && Objects.equals(targetedCell, pac.targetedCell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, typeId, speedTurnsLeft, abilityCooldown, targetedCell);
    }

    @Override
    public String toString() {
        return "{" + " id='" + getId() + "'" + ", x='" + getX() + "'" + ", y='" + getY() + "'" + ", typeId='"
                + getTypeId() + "'" + ", speedTurnsLeft='" + getSpeedTurnsLeft() + "'" + ", abilityCooldown='"
                + getAbilityCooldown() + "'" + ", targetedCell='" + getTargetedCell() + "'" + "}";
    }

}

class Team {
    private Map<Integer, Pac> team;

    public Team() {
        team = new HashMap<>();
    }

    public void init(Scanner in) {
        System.err.println("init " + team.size());
        int myScore = in.nextInt();
        int opponentScore = in.nextInt();
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
                if (team.get(pacId) == null) {
                    Pac pac = new Pac(pacId, currentX, currentY, typeId, speedTurnsLeft, abilityCooldown, null);
                    team.put(pacId, pac);
                } else {
                    Pac pac = team.get(pacId);
                    pac.setX(currentX);
                    pac.setY(currentY);
                }
            }
        }
    }

    public String move(List<Cellule> listeCells) {

        String result = "";
        for (Integer id : team.keySet()) {
            Pac pac = team.get(id);

            if (pac.getTargetedCell() != null && pac.getX() == pac.getTargetedCell().getX()
                    && pac.getY() == pac.getTargetedCell().getY()) {
                pac.setTargetedCell(null);
            }

            if (pac.getTargetedCell() == null) {
                Optional<Cellule> item = listeCells.stream().filter(c -> c.getValeur() == 10).findFirst();
                if (item.isPresent()) {
                    pac.setTargetedCell(item.get());
                } else {
                    pac.setTargetedCell(listeCells.get(0));
                }
                listeCells.remove(pac.getTargetedCell());
            }

            if (result != "") {
                result += " | ";
            }

            result += "MOVE " + pac.getId() + " " + pac.getTargetedCell().getX() + " " + pac.getTargetedCell().getY();
        }

        return result;
    }

    public boolean isEmpty() {
        return team.isEmpty();
    }
}

class Ground {
    private List<Cellule> cells;

    public Ground() {
        cells = new ArrayList<Cellule>();
    }

    public void init(Scanner in) {
        // in.nextLine();

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