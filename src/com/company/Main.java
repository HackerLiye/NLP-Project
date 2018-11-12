package com.company;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Scanner;


public class Main {

    public static final String INDEX_PATH = "index";
    private static MyScoreQuery myScoreQuery;

    public static void main(String[] args) throws ParseException {

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("h", "hits", true, "set the amount of hits per page");
        options.addOption("c", "create", false, "create the index");
        options.addOption("r", "recreate", false, "recreate the index");
        options.addOption("s", "search", false, "search for query strings");
        CommandLine commandLine = parser.parse(options, args);
        int hitsPerPage = Integer.parseInt(commandLine.getOptionValue("h", "10"));
        if (commandLine.hasOption("c")) {
            System.out.println("Creating the index...");
            FileIndexUtil.index(false);
            System.out.println("Successfully finished!");
        }
        if (commandLine.hasOption("r")) {
            System.out.println("Recreating the index...");
            FileIndexUtil.index(true);
            System.out.println("Successfully finished!");
        }
        if (commandLine.hasOption("s")) {
            myScoreQuery = new MyScoreQuery();
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("\nPlease input the query string: ");
                String queryStr = sc.nextLine();
                System.out.printf("Your query string is: [%s]\n", queryStr);
                myScoreQuery.searchByScoreQuery(queryStr, hitsPerPage);
            }
        }
    }
}
