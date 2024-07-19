import okhttp3.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Admin {

    private final static String SELECTION_OUT_OF_BOUNDS = "Input option number from list.";
    private final static String MAIN_MENU ="Chose action:";

    public void menu() {
        boolean exit = false;
        while (!exit) {
            showMenu();
            try {
                switch (getUserSelection()) {
                    case LOADXML:
                        addFile();
                        break;
                    case DISPLAYXML:
                        //showGameParameters();
                        break;
                    case VIEWGAME:
                        //startGame();
                        break;
                    case EXIT:
                        //displayExitMessage();
                        exit = true;
                        break;
                }
            } catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }
    private void showMenu(){
        /*Enum of menu options*/
        System.out.println();
        System.out.println(MAIN_MENU);
        Arrays.stream(AdminMenuOptions.values()).forEach(c->System.out.println((c.ordinal()+1)+") "+ c));
    }

    private AdminMenuOptions getUserSelection(){
        Scanner scanner = new Scanner(System.in);
        int userInput;
        AdminMenuOptions userSelection = null;
        int numOptions = AdminMenuOptions.values().length;

        while (userSelection==null) {
            try {
                userInput = Integer.parseInt(scanner.nextLine());
                if (userInput > 0 && userInput <=numOptions) {
                    userSelection = AdminMenuOptions.values()[userInput-1];
                }
                else {
                    System.out.println(SELECTION_OUT_OF_BOUNDS);
                }
            }catch (RuntimeException e){
                System.out.println(SELECTION_OUT_OF_BOUNDS);
            }
        }
        return userSelection;
    }

    private void addFile() {

        System.out.print("Enter full path of XML file: ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();

        String xmlPath;

        try {
            xmlPath = URLEncoder.encode(path, StandardCharsets.UTF_8.toString());
        }
        catch (UnsupportedEncodingException e){
            throw new RuntimeException();
        }

        RequestBody body = new FormBody.Builder()
                .add("xmlPath", xmlPath)
                .build();

        Request request = new Request.Builder()
                .url(Constants.ADD_FILE)
                .post(body)
                .build();

        try (Response response = Constants.HTTP_CLIENT.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            System.out.println("Response Code: " + response.code());
            System.out.println("Response Body: " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
