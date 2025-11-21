package ch.heigvd.dai.applicationInterface;

import ch.heigvd.dai.controller.*;

import java.util.Scanner;

public class ClientInterface {

    private Controller controller;

    public void showInterface() {
        System.out.println("Menu");
    }

    public String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
