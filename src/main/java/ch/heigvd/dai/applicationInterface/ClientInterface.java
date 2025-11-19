package ch.heigvd.dai.applicationInterface;

import ch.heigvd.dai.network.ClientNetwork;

import java.util.Scanner;

public class ClientInterface {

    public void showInterface() {
        System.out.println("Menu");
    }

    public String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
