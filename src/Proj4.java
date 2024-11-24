/*******************************************************
 * @file: Proj4.java
 * @description: This program processes a Pokemon dataset
 *               and evaluates the performance of hash table
 *               operations (insert, search, delete) using
 *               separate chaining. The dataset is tested
 *               in three configurations: sorted, shuffled,
 *               and reversed. Results are measured and
 *               logged in seconds to an analysis file.
 * @author: Andrew Dwyer
 * @date: November 23, 2024
 *******************************************************/

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
        try (FileOutputStream fos = new FileOutputStream("analysis.txt", true); // Append mode
             PrintWriter writer = new PrintWriter(fos)) {

            // Perform operations on each dataset type
            System.out.println("Processing datasets...");
            performOperations(hashTable, sortedPokemon, writer, "Sorted", numLines);
            performOperations(hashTable, shuffledPokemon, writer, "Shuffled", numLines);
            performOperations(hashTable, reversedPokemon, writer, "Reversed", numLines);
        }
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
                                          String listType,
                                          int numLines) {
        long startTime, endTime;

        System.out.printf("Processing %s dataset with %d lines...%n", listType, numLines);

        // Insert
        startTime = System.nanoTime();
        for (Pokemon pokemon : dataset) {
            hashTable.insert(pokemon);
        }
        endTime = System.nanoTime();
        double insertTime = (endTime - startTime) / 1e9; // Convert to seconds

        // Search
        startTime = System.nanoTime();
        for (Pokemon pokemon : dataset) {
            hashTable.contains(pokemon);
        }
        endTime = System.nanoTime();
        double searchTime = (endTime - startTime) / 1e9; // Convert to seconds

        // Delete
        startTime = System.nanoTime();
        for (Pokemon pokemon : dataset) {
            hashTable.remove(pokemon);
        }
        endTime = System.nanoTime();
        double deleteTime = (endTime - startTime) / 1e9; // Convert to seconds

        // Print results to the console
        System.out.printf("Results for %s dataset:%n", listType);
        System.out.printf("Insert Time: %.6f seconds%n", insertTime);
        System.out.printf("Search Time: %.6f seconds%n", searchTime);
        System.out.printf("Delete Time: %.6f seconds%n%n", deleteTime);

        // Write results to the analysis file
        writer.printf("%s,%d,%.6f,%.6f,%.6f%n", listType, numLines, insertTime, searchTime, deleteTime);
        writer.flush();
    }
}