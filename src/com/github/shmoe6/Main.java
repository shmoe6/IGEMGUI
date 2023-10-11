package com.github.shmoe6;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;

/**
 * Class to represent a DNA Sequence.
 *
 * @author Anna Bontempo
 * @author OSU IGEM Team
 */
class DNASequence {

    /**
     * A String that will contain the nucleotide sequence for an arbitrary gene.
     */
    public String sequence = "";

    /**
     * A double that represents the activity of an arbitrary cell with this DNASequence
     */
    public double activity;
}

/**
 * Simulates the optimization of DNA lead sequences.
 * Displays results in a Java Swing GUI.
 *
 * @author Anna Bontempo
 * @author OSU IGEM Team
 */
public class Main {

    /**
     * Repeatedly prompts user for input of a DNA sequence until a valid one is provided.
     * Valid sequences are of length sequenceLength and only contain the characters (bases) A, C, G, and T.
     *
     * @param in the java.util.Scanner used to get user input
     * @param sequenceLength the length of each DNA sequence for the population
     * @return a String representing a valid DNA sequence of length sequenceLength
     */
    public static String getUserSequence(Scanner in, int sequenceLength) {
        String sequenceIn = "";
        boolean isValidSequence = false;

        // loop until a valid DNA sequence is provided
        while (!isValidSequence) {
            // prompt user, gather input
            System.out.print("Enter the initial DNA Sequence, with bases ACGT and length " + sequenceLength + ": ");
            sequenceIn = in.nextLine();
            // check for valid length
            if (sequenceIn.length() == sequenceLength) {
                // check for valid nitrogenous bases (A, C, G, T)
                if (sequenceIn.matches("^[ACGT]+$")) {
                    isValidSequence = true;
                } else {
                    System.out.println("ERROR: Invalid Sequence! Use only bases A, C, G, and T.");
                }
            } else {
                System.out.println("ERROR: Invalid Sequence! Must be length " + sequenceLength + " bases.");
            }
        }

        return sequenceIn;
    }

    /**
     * Repeatedly prompts user for input of a valid activity value until a valid one is provided.
     * Valid activity values are on the interval [0.0, 1.0)
     *
     * @param in the java.util.Scanner used to get user input
     * @return a double representing the desired activity level, lying on the interval [0.0, 1.0)
     */
    public static double getUserActivity(Scanner in) {
        double activityIn = -1.0;
        boolean isValidActivityValue = false;

        // loop until a valid activity value is provided
        while (!isValidActivityValue) {
            // prompt user, gather input
            System.out.print("Enter the activity value for the initial sequence, a decimal on interval [0.0, 1.0): ");
            String currentInput = in.nextLine();

            // input validation
            try {
                // check to see if input is a double
                activityIn = Double.parseDouble(currentInput);

                // if input is a double, then check to make sure it is on the interval [0.0, 1.0)
                if (Double.compare(activityIn, 0.0) < 0 || Double.compare(activityIn, 1.0) >= 0) {
                    System.out.println("ERROR: Invalid Activity Value! Must be on the interval [0.0, 1.0).");
                } else {
                    isValidActivityValue = true;
                }
            } catch (NumberFormatException nfe) {
                // notify user of invalid input type if Double.parseDouble() fails
                System.out.println("ERROR: Invalid Activity Value Data Type! Input must be a decimal number.");
            }
        }

        return activityIn;
    }

    /**
     * Calculates the average T3SS activity for the provided sequences.
     *
     * @param sequences a java.util.ArrayList containing the sequences to get the average activity of
     * @return a double representing the average T3SS activity for the sequences
     */
    static double calculateAverageActivity(ArrayList<DNASequence> sequences) {
        double sum = 0.0;

        // loop through all the provided sequences
        for (DNASequence seq : sequences) {
            sum += seq.activity;
        }

        return sum / sequences.size();
    }

    /**
     * Creates a population of DNA sequences.
     *
     * @param populationSize the desired size of the population
     * @param sequenceLength the desired length of the sequences, matches the user inputted sequence
     * @return a java.util.ArrayList containing all the DNASequence for a population
     */
    static ArrayList<DNASequence> initializePopulation(int populationSize, int sequenceLength) {
        final ArrayList<DNASequence> population = new ArrayList<>();

        // new random engine each method call to provide as much variation as possible
        final Random activityDistribution = new Random(System.currentTimeMillis());

        // create the population based on the provided constraints populationSize and sequenceLength
        for (int i = 0; i < populationSize; i++) {

            // create a DNASequence representing an individual in the population
            final DNASequence seq = new DNASequence();
            for (int j = 0; j < sequenceLength; j++) {
                // initialize DNASequence with random nucleotides (A, C, G, T);
                final int nucleotide = activityDistribution.nextInt(4);
                seq.sequence += "ACGT".substring(nucleotide, nucleotide + 1);
            }

            // set the activity of the newly created individual
            seq.activity = activityDistribution.nextDouble();

            // add the DNASequence to the total population
            population.add(seq);
        }

        return population;
    }

    /**
     * Simulates the crossing over of DNASequences.
     *
     * @param population a java.util.ArrayList containing all the DNASequences in a population
     */
    static void crossover(ArrayList<DNASequence> population) {
        // new random engine each method call to provide as much variation as possible
        final Random generator = new Random(System.currentTimeMillis());

        // bounds for the percent of the gene that crosses over
        final double crossoverMinPct = 0.2;
        final double crossoverMaxPct = 0.8;

        // loop through population and simulate crossing over
        for (int i = 0; i < population.size(); i+= 2) {

            // randomly select two parent sequences
            int parentIndex1 = generator.nextInt(population.size());
            int parentIndex2 = generator.nextInt(population.size());

            // make sure parentIndex1 is not equal to parentIndex2, i.e. the parents cannot be the same
            while (parentIndex2 == parentIndex1) {
                parentIndex2 = generator.nextInt(population.size());
            }

            // randomly select a crossover point and get its corresponding index in the sequence
            final int sequenceLength = population.get(parentIndex1).sequence.length();
            final double crossoverPercent = crossoverMinPct + (crossoverMaxPct - crossoverMinPct) * generator.nextDouble();
            final int crossoverPoint = (int) (crossoverPercent * sequenceLength);

            // create two offspring sequences through crossover
            final DNASequence offspring1 = new DNASequence();
            final DNASequence offspring2 = new DNASequence();

            // synthesize sequence for offspring
            offspring1.sequence = population.get(parentIndex1).sequence.substring(0, crossoverPoint);
            offspring1.sequence += population.get(parentIndex2).sequence.substring(crossoverPoint);
            offspring2.sequence = population.get(parentIndex2).sequence.substring(0, crossoverPoint);
            offspring2.sequence += population.get(parentIndex1).sequence.substring(crossoverPoint);

            // assign activities to offspring sequences
            offspring1.activity = generator.nextDouble();
            offspring2.activity = generator.nextDouble();

            // replace the parents with the offspring
            population.set(i, offspring1);
            population.set(i + 1, offspring2);
        }
    }

    /**
     * Simulates the mutation of DNA sequences.
     *
     * @param population a java.util.ArrayList containing all the DNASequences in a population
     */
    static void mutate(ArrayList<DNASequence> population) {
        // new random engine each method call to provide as much variation as possible
        final Random generator = new Random(System.currentTimeMillis());

        // hardcoded mutation rates, adjust as/if needed
        final double minMutationRate = 0.0, maxMutationRate = 1.0;
        final double minActivityMutation = -0.1, maxActivityMutation = 0.1;

        // loop through each member of the population
        for (DNASequence seq : population) {

            // loop through each individual nucleotide
            for (int nucleotide = 0; nucleotide < seq.sequence.length(); nucleotide++) {

                // calculate mutation rate based off of constraints
                // note: ignore simplification suggestion in ide, current implementation is required to work with future hardcoded rates
                final double mutationRate = (minMutationRate + (maxMutationRate - minMutationRate) * generator.nextDouble());

                // cause a mutation if the random rate falls below the hardcoded threshold, adjust as needed
                if (mutationRate < 0.01) {
                    // mutate the nucleotide (e.g. change it randomly)
                    final StringBuilder sb = new StringBuilder(seq.sequence);
                    sb.setCharAt(nucleotide, "ACTG".charAt(generator.nextInt(4)));
                    seq.sequence = sb.toString();
                }

                // mutate sequence activity
                final double activityMutation = (minActivityMutation + (maxActivityMutation - minActivityMutation) * generator.nextDouble());
                seq.activity += activityMutation;
            }
        }
    }

    /**
     * Simulates natural selection by selecting the "best-performing" sequences in a population.
     *
     * @param population a java.util.ArrayList containing all the DNASequences in a population
     * @param eliteSize the size of the "elite" population that has the advantage over the rest of the population
     */
    static void selection(ArrayList<DNASequence> population, int eliteSize) {
        // note: ignore simplification suggestion in ide, current implementation is required to work with future hardcoded values
        // sort the population based on activity in descending order
        population.sort(Comparator.comparingDouble(a -> a.activity));

        // create a new population with the top-performing individuals (elites)
        final ArrayList<DNASequence> newPopulation = new ArrayList<>();
        for (int i = 0; i < eliteSize && i < population.size(); i++) {
            newPopulation.add(population.get(i));
        }

        // replace current population with the new population
        population = newPopulation;
    }

    /**
     * Carries out the simulation to optimize DNA Sequences.
     * Displays the resulting data in a Java Swing GUI.
     *
     * @param args user-defined command line arguments
     */
    public static void main(String[] args) {
        // the number of generations to simulate, change as/if needed
        final int numGenerations = 10;

        // the size of the population, change as/if needed
        final int populationSize = 50;

        // the length of each DNA sequence in the population, change as/if needed, but 20 seems to be the most effective
        final int sequenceLength = 20;

        // initialize scanner to gather user input
        final Scanner scanner = new Scanner(System.in);

        // get valid user inputs for the initial DNASequence's sequence and activity
        final String userSequence = getUserSequence(scanner, sequenceLength);
        final double userActivity = getUserActivity(scanner);

        // close Scanner for good resource management
        scanner.close();

        // initialize user's sequence with the inputted values
        final DNASequence initialSequence = new DNASequence();
        initialSequence.sequence = userSequence;
        initialSequence.activity = userActivity;

        // initialize the population, containing the user's sequence
        ArrayList<DNASequence> population = initializePopulation(populationSize - 1, sequenceLength);
        population.add(initialSequence);

        // create data array for the GUI's table
        final Object[][] data = new Object[populationSize][3];

        // loop through each generation, performing the actual simulation of DNA sequence optimization
        for (int generation = 1; generation <= numGenerations; generation++) {

            // perform selection to choose the best-performing sequences
            selection(population, populationSize / 2);

            // perform crossover to create new sequences
            crossover(population);

            // perform mutation to increase genetic variation
            mutate(population);

            // calculate the average T3SS activity for the current generation
            double averageActivity = calculateAverageActivity(population);
            System.out.println("Generation " + " - Average T3SS activity: " + averageActivity);

            // add the new data to the array so it can be displayed in the GUI
            data[generation - 1] = new Object[] { generation, averageActivity };
        }

        // find and print the optimized DNA sequence
        DNASequence optimizedSequence = Collections.max(population, Comparator.comparingDouble(a -> a.activity));
        System.out.print("Optimized DNA sequence: " + optimizedSequence.sequence);
        System.out.println(" (Activity: " + optimizedSequence.activity + ")");

        // variables for the GUI's initialization
        final String version = "2.4.1";
        final JFrame display = new JFrame("IGEMGUI " + version);

        // specify headers for the data table
        final String[] columns = {"Generation", "Average Activity"};

        // create data table
        final JTable table = new JTable(data, columns) {
            // prevent user from editing cells
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // create a JLabel to house the table's title
        final JLabel lblHeading = new JLabel("Optimization of Lead Sequences");
        lblHeading.setFont(new Font("Arial", Font.PLAIN,24));

        // specify alignment for table cells
        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // apply the alignment, centering the table headers and cells
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // create a JScrollPane to house the table, useful if more generations desired
        final JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // display the optimized DNA sequence at the bottom
        final JLabel optimizedSequenceLabel = new JLabel("Optimized Sequence: " + optimizedSequence.sequence);

        // set layout for component rendering
        display.getContentPane().setLayout(new BorderLayout());

        // add table title, data table, optimized sequence to the GUI (respectively)
        display.getContentPane().add(lblHeading, BorderLayout.PAGE_START);
        display.getContentPane().add(scrollPane, BorderLayout.CENTER);
        display.getContentPane().add(optimizedSequenceLabel, BorderLayout.PAGE_END);

        // close window when x button is clicked
        display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // set GUI size to comfortably fit the data table
        display.setSize(400, 275);

        // disable resizing of the window, helps to prevent bugs/rendering issues
        display.setResizable(false);

        // display GUI
        display.setVisible(true);
    }
}