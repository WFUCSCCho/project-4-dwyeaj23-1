import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Proj4 {
    public static void main(String[] args) throws IOException {
        // Use command line arguments to specify the input file
        if (args.length != 2) {
            System.err.println("Usage: java Proj4 <input file> <number of lines>");
            System.exit(1);
        }

        String inputFileName = args[0];
        int numLines = Integer.parseInt(args[1]);

        // ArrayList to hold Pokémon objects
        ArrayList<Pokemon> pokemonList = new ArrayList<>();
        loadPokemonData(inputFileName, pokemonList, numLines);

        // Create the three different ArrayLists
        ArrayList<Pokemon> sortedPokemon = new ArrayList<>(pokemonList);
        ArrayList<Pokemon> shuffledPokemon = new ArrayList<>(pokemonList);
        ArrayList<Pokemon> reversedPokemon = new ArrayList<>(pokemonList);

        // Sort, shuffle, and reverse the lists
        Collections.sort(sortedPokemon);
        Collections.shuffle(shuffledPokemon);
        Collections.sort(reversedPokemon, Collections.reverseOrder());

        // Initialize the hash table
        SeparateChainingHashTable<Pokemon> hashTable = new SeparateChainingHashTable<>();

        // File to write analysis results
        FileOutputStream fos = new FileOutputStream("analysis.txt", true); // Append mode
        PrintWriter writer = new PrintWriter(fos);

        // Perform operations on each dataset type
        System.out.println("Processing sorted Pokémon...");
        performOperations(hashTable, sortedPokemon, writer, "Sorted");

        System.out.println("Processing shuffled Pokémon...");
        performOperations(hashTable, shuffledPokemon, writer, "Shuffled");

        System.out.println("Processing reversed Pokémon...");
        performOperations(hashTable, reversedPokemon, writer, "Reversed");

        // Close writer
        writer.close();
    }

    /**
     * Load Pokémon data from a CSV file.
     */
    private static void loadPokemonData(String csvFile, ArrayList<Pokemon> pokemonList, int numLines) {
        try (Scanner scanner = new Scanner(new FileInputStream(csvFile))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // Skip the header

            int count = 0;
            while (scanner.hasNextLine() && count < numLines) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] attributes = line.split(",");
                if (attributes.length >= 13) {
                    int id = Integer.parseInt(attributes[0]);
                    String name = attributes[1];
                    String type1 = attributes[2];
                    String type2 = attributes[3].equals("None") ? "" : attributes[3];
                    int total = Integer.parseInt(attributes[4]);
                    int hp = Integer.parseInt(attributes[5]);
                    int attack = Integer.parseInt(attributes[6]);
                    int defense = Integer.parseInt(attributes[7]);
                    int specialAttack = Integer.parseInt(attributes[8]);
                    int specialDefense = Integer.parseInt(attributes[9]);
                    int speed = Integer.parseInt(attributes[10]);
                    int generation = Integer.parseInt(attributes[11]);
                    boolean isLegendary = Boolean.parseBoolean(attributes[12]);

                    Pokemon pokemon = new Pokemon(id, name, type1, type2, total, hp, attack, defense,
                            specialAttack, specialDefense, speed, generation, isLegendary);
                    pokemonList.add(pokemon);
                    count++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading Pokémon data: " + e.getMessage());
        }
    }

    /**
     * Perform insert, search, and delete operations on the hash table and measure their performance.
     */
    private static void performOperations(SeparateChainingHashTable<Pokemon> hashTable,
                                          ArrayList<Pokemon> dataset,
                                          PrintWriter writer,
                                          String listType) {
        long startTime, endTime;

        // Insert
        startTime = System.nanoTime();
        for (Pokemon pokemon : dataset) {
            hashTable.insert(pokemon);
        }
        endTime = System.nanoTime();
        long insertTime = endTime - startTime;

        // Search
        startTime = System.nanoTime();
        for (Pokemon pokemon : dataset) {
            hashTable.contains(pokemon);
        }
        endTime = System.nanoTime();
        long searchTime = endTime - startTime;

        // Delete
        startTime = System.nanoTime();
        for (Pokemon pokemon : dataset) {
            hashTable.remove(pokemon);
        }
        endTime = System.nanoTime();
        long deleteTime = endTime - startTime;

        // Write results to file and console
        System.out.printf("%s List - Insert: %d ns, Search: %d ns, Delete: %d ns%n",
                listType, insertTime, searchTime, deleteTime);

        writer.printf("%s,%d,%d,%d%n", listType, insertTime, searchTime, deleteTime);
        writer.flush();
    }
}
