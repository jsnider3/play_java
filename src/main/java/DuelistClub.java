import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class DuelistClub {
    public static void main(String[] args) {
        int n = 10; // Number of duelists (can be any value now)
        ArrayList<Duelist> duelists = generateDuelists(n);

        // Pair and eliminate duelists with lower skill in each pair
        int numSurvivors = n / 2;
        for (int i = 0; i < numSurvivors; i++) {
            // Get the two duelists for the current pair
            Duelist duelist1 = duelists.get(n - 1 - (2 * i));
            Duelist duelist2 = duelists.get(n - 1 - (2 * i + 1));

            // Simulate a duel (remove the one with lower skill)
            if (duelist1.getSkill() < duelist2.getSkill()) {
                duelists.remove(duelist1);
            } else {
                duelists.remove(duelist2);
            }
        }

        // Apportion the remaining trophies using Huntington-Hill
        apportionTrophiesHuntingtonHill(duelists, n - numSurvivors);

        // Print the results
        System.out.println("Duelists with trophies:");
        for (Duelist duelist : duelists) {
            System.out.println(duelist.getName() + " (Skill: " + duelist.getSkill() + ", Trophies: " + duelist.getTrophies() + ")");
        }
    }

    // Helper method to create Duelist objects with random skills
    static ArrayList<Duelist> generateDuelists(int numDuelists) {
        ArrayList<Duelist> duelists = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numDuelists; i++) {
            String name = "Duelist-" + (i + 1);
            double skill = random.nextDouble() * 99 + 1; // Skill between 1 and 100
            duelists.add(new Duelist(name, skill));
        }

        return duelists;
    }

    static void apportionTrophiesHuntingtonHill(ArrayList duelists, int numTrophies) {
        for (int i = 0; i < numTrophies; i++) {
            Duelist bestDuelist = findBestDuelistHuntingtonHill(duelists);
            bestDuelist.addTrophy(); // Assign a trophy to the best duelist
        }
    }

    static Duelist findBestDuelistHuntingtonHill(ArrayList<Duelist> duelists) {
        Duelist bestDuelist = null;
        double bestPriority = -1;

        for (Duelist duelist : duelists) {
            double priority = calculateHuntingtonHillPriority(duelist, duelists);
            if (priority > bestPriority) {
                bestPriority = priority;
                bestDuelist = duelist;
            }
        }

        return bestDuelist;
    }

    static double calculateHuntingtonHillPriority(Duelist duelist, ArrayList duelists) {
        double population = duelist.getTrophies(); // Consider the current trophy being assigned
        double geometricMean = Math.sqrt(population * (population + 1));
        return duelist.getSkill() / geometricMean;
    }
}

// Duelist class to represent a duelist
class Duelist {
    private String name;
    private double skill;
	private int trophies;

    public Duelist(String name, double skill) {
        this.name = name;
        this.skill = skill;
		this.trophies = 1;
    }

	public int addTrophy() {
		trophies += 1;
		return trophies;
	}
    public String getName() {
        return name;
    }

    public double getSkill() {
        return skill;
    }
	
	public int getTrophies() {
		return trophies;
	}
}