import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Grab the pellets as fast as you can!
 * seed=782918738956375040
 **/
class Player {

    static List<Cellule> listeCells = new ArrayList<Cellule>();
    static Team team = new Team();
    static Map<Integer, List<Cellule>> carte;

    public static void main(String args[]) {
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
            team.init(in);

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

            System.out.println(team.action(carte));

        }
    }
}

class Pac {
    int id;
    int x;
    int y;
    String typeId;
    int speedTurnsLeft;
    int abilityCooldown;
    Cellule targetedCell;
    Pac targetedPac;

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

}

class Team {
    Map<Integer, Pac> team;
    Map<Integer, Pac> otherTeam;

    public Team() {
        team = new HashMap<>();
        otherTeam = new HashMap<>();
    }

    public void init(Scanner in) {

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
                    pac.x = currentX;
                    pac.y = currentY;
                }
            } else {
                if (otherTeam.get(pacId) == null) {
                    Pac pac = new Pac(pacId, currentX, currentY, typeId, speedTurnsLeft, abilityCooldown, null);
                    otherTeam.put(pacId, pac);
                } else {
                    Pac pac = otherTeam.get(pacId);
                    pac.x = currentX;
                    pac.y = currentY;
                }
            }
        }
    }

    /**
     * 
     * @param carte
     * @return
     */
    public String chasse() {
        String result = "";
        for (Integer pacId : team.keySet()) {
            Pac pac = team.get(pacId);
            if (pac == null) {
                continue;
            }

            Pac otherPac;
        }
        return result;
    }

    /**
     * Parcours des cellules.
     * @param carte
     * @return
     */
    public String action(Map<Integer, List<Cellule>> carte) {

        String result = "";
        for (Integer pacId : team.keySet()) {
            Pac pac = team.get(pacId);
            if (pac == null) {
                continue;
            }

            if (pac.targetedCell != null && pac.x == pac.targetedCell.x && pac.y == pac.targetedCell.y) {
                pac.targetedCell = null;
            }
            System.err.println("avant => " + pac.targetedCell);
            if (pac.targetedCell == null) {
                int x = pac.x + pac.id;
                if (x >= 34) {
                    x = 0;
                }
                List<Cellule> cells = carte.get(x);

                if (pac.y > 7) {
                    cells.sort((Cellule c1, Cellule c2) -> new CelluleComparator().compare(c1, c2));
                } else {
                    cells.sort((Cellule c1, Cellule c2) -> new CelluleComparator().compare(c2, c1));
                }
                pac.targetedCell = cells.get(0);

            }
            if (result != "") {
                result += " | ";
            }
            if (pac.targetedCell != null) {
                System.err.println("apres => " + pac.targetedCell);
                result += "MOVE " + pac.id + " " + pac.targetedCell.x + " " + pac.targetedCell.y;
            }

        }

        return result;
    }

    /**
     * Parcours des cellules visibles.
     * @param listeCells
     * @return
     */
    public String action(List<Cellule> listeCells) {

        String result = "";
        for (Integer id : team.keySet()) {
            Pac pac = team.get(id);

            if (pac.targetedCell != null && pac.x == pac.targetedCell.x && pac.y == pac.targetedCell.y) {
                pac.targetedCell = null;
            }

            if (pac.targetedCell == null) {
                Optional<Cellule> item = listeCells.stream()
                        .filter(c -> ((c.x == pac.x && c.y != pac.y) || (c.x != pac.x && c.y == pac.y))).findFirst();
                if (item.isPresent()) {
                    pac.targetedCell = item.get();
                } else {
                    pac.targetedCell = null;
                }
                if (pac.targetedCell != null) {
                    listeCells.remove(pac.targetedCell);
                }
            }

            if (result != "") {
                result += " | ";
            }
            if (pac.targetedCell != null)
                result += "MOVE " + pac.id + " " + pac.targetedCell.x + " " + pac.targetedCell.y;
        }

        return result;
    }

}

class CelluleComparator implements Comparator<Cellule> {

    @Override
    public int compare(Cellule o1, Cellule o2) {
        int result = 0;
        if (o1.y > o2.y) {
            result = 1;
        } else {
            result = -1;
        }
        return result;
    }

}

class Cellule {

    int x, y, valeur;

    public Cellule() {
    }

    public Cellule(int x, int y, int valeur) {
        this.x = x;
        this.y = y;
        this.valeur = valeur;
    }

}
