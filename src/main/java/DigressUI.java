import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.*;

public class DigressUI {

    private static Object JFrame;

    /**
     * Creates a story
     * @throws IllegalArgumentException if the title and content of the story is invalid
     * @post creates the story
     */
    public static Story createStory (){
        Scanner in = new Scanner(System.in);
        String title = "", root = "", userIn = "";
        int currId = 1, parentId;
        String content = "";
        boolean creating = true;
        boolean inputValid = true;
        Random ran = new Random();
        int id = ran.nextInt(1000);

        System.out.println("Enter story title: ");
        //todo: need error testing for invalid input here
        title = in.nextLine();
        System.out.println("Enter first story content: ");
        root = in.nextLine();

        Story story = new Story(id, title, root);
        enterCondition(story, 1, 1);
        do {
            System.out.println("Add another condition(c), work on a different node(n), or finalize story(x)?");
            do{
                inputValid = true;
                userIn = in.nextLine();
                if(!(userIn.equalsIgnoreCase("c")||userIn.equalsIgnoreCase("n")||userIn.equalsIgnoreCase("x"))){
                    System.out.println("Please enter a valid option");
                    inputValid = false;
                }
            } while(!inputValid);

            if(userIn.equalsIgnoreCase("c")){
                enterCondition(story, currId, story.getNodeConditions().size()+1);
            }
            else if(userIn.equalsIgnoreCase("n")){
                System.out.println(story.printAllNodes());
                System.out.println("Select a node by entering an ID: ");

                do{
                    inputValid = true;
                    userIn = in.nextLine();

                    if(!story.getStoryNodes().containsKey(Integer.parseInt(userIn))){
                        System.out.println("Please enter a valid ID");
                        inputValid = false;
                    }
                } while(!inputValid);

                currId = Integer.parseInt(userIn);
                System.out.println(story.printCurrentNode());
                enterCondition(story, currId, story.getNodeConditions().size()+1);
            }

            else { //if user selects quit
                //todo: Should call a helper function here to make sure every node has at least one child
                creating = false;
            }
        } while(creating);
        return story;
    }

    /**
     * Enter the conditions for a story
     * @throws IllegalArgumentException if the invalid condition number is chosen
     * @post Enter the conditions for the story
     * @params story, id, choiceVal : story to add more conditions to, id of the node,choice value of the node to connect to
     */
    public static void enterCondition(Story story, int id, int choiceVal) throws IllegalArgumentException{
        String condition = "", userIn = "";
        boolean inputValid;
        Scanner in = new Scanner(System.in);

        //todo Give option to go back
        System.out.print("Condition " + choiceVal + ": ");
        condition = in.nextLine();

        if(!Node.checkConditionValid(condition)){
            do {
                System.out.println("Please enter only letters.");
                System.out.print("Condition " + choiceVal + ": ");
                condition = in.nextLine();
            }while(!Node.checkConditionValid(condition));
        }
        if (id != 1) {
            System.out.println("Link this to an existing node (x) or a new node (n)?");
            do {
                inputValid = true;
                userIn = in.nextLine();
                if (!(userIn.equalsIgnoreCase("x") || userIn.equalsIgnoreCase("n"))) {
                    System.out.println("Please enter a valid option");
                    inputValid = false;
                }
            } while (!inputValid);
            if (userIn.equalsIgnoreCase("x")) {
                String nextId = "";
                System.out.println(story.printAllNodes());

                do {
                    System.out.print("Enter the ID of the node to link to: ");
                    inputValid = true;
                    nextId = in.nextLine();

                    //todo: check to see about node

                    do {
                        System.out.println("Can't choose the root ID");
                        System.out.print("Enter the ID of the node to link to: ");
                        nextId = in.nextLine();
                    } while (Integer.parseInt(nextId) == 1);

                    try {
                        Integer.parseInt(nextId);
                    } catch (Exception e) {
                        System.out.println("Please enter a valid number");
                    }
                    if (story.findNode(Integer.parseInt(nextId)) == null) {
                        System.out.println("ID must exist in the story");
                        inputValid = false;
                    }
                } while (!inputValid);
                story.linkNodes(id, Integer.parseInt(nextId), condition);
            } else {
                String content = "";
                System.out.println("Enter node content: ");
                content = in.nextLine();

                story.addNode(content);
                story.linkNodes(id, story.getCurrentNode().getId(), condition);
            }
        }
    }

    public static Story importUI() throws IOException, NullPointerException {
//        Scanner fileName = new Scanner(System.in);
//        Scanner filePath = new Scanner(System.in);
        JFileChooser fc = new JFileChooser();
        File directory = new File("user.home");
        fc.setCurrentDirectory(new File(System.getProperty(String.valueOf(directory))));
        JFrame frame = new JFrame();
        frame.toFront();
        frame.setVisible(true);
        int result = fc.showOpenDialog(frame.getContentPane());

        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {

                return "json File (*.json)";
            }
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    String filename = file.getName().toLowerCase();
                    return filename.endsWith(".json");
                }
            }
        });

        if (result == JFileChooser.APPROVE_OPTION) {
            // user selects a file
            File selectedFile = fc.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            storyFile newFile = new storyFile(selectedFile.getName(), selectedFile.getAbsolutePath());
            frame.setVisible(false);
            return newFile.importFile();
        }
//        else if (result == JFileChooser.ERROR_OPTION){
//            // user selects a wrong file
//        }
        return null;
    }

    public static void exportUI() throws IOException, FileNotFoundException {
//        Scanner fileName = new Scanner(System.in);
//        String filePath = "Desktop";
        JFileChooser fc1 = new JFileChooser();
        fc1.setCurrentDirectory(new File(System.getProperty("user.home")));
        JFrame frame = new JFrame();
        frame.toFront();
        frame.setVisible(true);
        int result = fc1.showOpenDialog((Component) JFrame);

        fc1.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "TXT File (*.txt)";
            }

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    String filename = file.getName().toLowerCase();
                    return filename.endsWith(".txt");

                }
            }
        });

        if (result == JFileChooser.APPROVE_OPTION) {
            // user selects a file
            File selectedFile = fc1.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            frame.setVisible(false);
            Story exFile = new Story(1, selectedFile.getName(), selectedFile.toString());

            JFileChooser fc2 = new JFileChooser();
            fc2.setDialogTitle("Specify a file to save");
            int userSelection = fc2.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fc2.getSelectedFile();
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                exFile.exportStory(fileToSave.getAbsolutePath());
            }
//            storyFile newStoryFile = (storyFile) selectedFile;
//            Story newStory = new Story(1, selectedFile.getName(), Files.readAllBytes(Paths.get(((storyFile) selectedFile).getFileName())));
//            newStoryFile.outputFile(newStory);
        }

    }

    /**
     * Plays a story
     * @throws IllegalArgumentException if the title and content of the story is invalid
     * @post Plays the story
     * @params story : story user chose to play
     */
    public static void playStory (Story story){
        String choice;
        String userIn = "y";
        Scanner in = new Scanner(System.in);
        do {
            boolean playing = true;
            story.setCurrentNode(story.getRoot());

            while (playing) {
                System.out.println(story.printCurrentNode());
                do {
                    try {
                        choice = in.nextLine();
                        Integer.parseInt(choice);
                    } catch (Exception e) {
                        System.out.println("Please enter a valid number");
                        System.out.println(story.printCurrentNode());
                        choice = in.nextLine();
                    }

                   /* if (Integer.parseInt(choice) > story.getNodeConditions().size() || Integer.parseInt(choice) < 1) {
                        System.out.println("Please enter a valid number");
                        System.out.println(story.printCurrentNode());
                        choice = in.nextLine();
                    }*/
                } while (Integer.parseInt(choice) > story.getNodeConditions().size() || Integer.parseInt(choice) < 1);
                if(Integer.parseInt(choice)>story.getNodeConditions().size()){
                    System.out.println("This condition number doesn't exist.");
                }else{
                    story.getNext(Integer.parseInt(choice));
                }
                if (story.getCurrentNode().isEndNode()) {
                    System.out.println(story.getCurrentNode().getStoryContent());
                    playing = false;
                }
            }
            System.out.println("Done with story");
            System.out.println("Play again? (Y/N)");
            userIn = in.nextLine();
        } while(!userIn.equalsIgnoreCase("n"));
    }

    /**
     * Creates a story
     * @throws IllegalArgumentException if content of the story is invalid
     * @post edits the content of a particular node of the story
     * @params storyChosen, newContent, contentToEdit : the story chosen by the user,
     * the edited content for the story, the older content of the story node
     */
    public static void editStoryContent(Story storyChosen, String newContent, int contentToEdit) throws IllegalArgumentException{
        storyChosen.editNodeStoryContent(contentToEdit, newContent);

        /*if (newContent == " " || newContent == "") throw new IllegalArgumentException("New content cannot be empty");
        /*else if (storyChosen.getTitle().equalsIgnoreCase(newContent))
            throw new IllegalArgumentException("New content entered is same as previous content");
        else{
            for (int i=0;i<storyChosen.)
            storyChosen.getRoot();
        }
        else if (!storyChosen.getTitle().equalsIgnoreCase(newContent)){
            storyChosen.getRoot();
            editStoryContent
        }*/
    }

    /**
     * Uploads a story to play or edit
     * @throws IllegalArgumentException if the title and content of the story is invalid
     * @post creates the story
     * @params fileForUpload : file name user wants to play or edit
     */
    public static void uploadStory () throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter file name");
        String fileName = in.next();
        int count;
        FileReader fileEx=null;
        try {
            fileEx = new FileReader(fileName);
        }catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.out.println("Enter valid file name:");
            fileName = in.next();
            fileEx = new FileReader(fileName);
        }
        //assert fileEx != null;
        while ((count=fileEx.read())!=-1)
            System.out.print((char)count);
        fileEx.close();
    }

    /**
     * Creates a test story for the UI
     * @post creates the test story
     */
    public static Story getTestStory(){
        String node1 = "You are walking down a dark, dingy hallway. Where would you like to go?";
        String node2 = "You see cobwebs in front of you and a dark shadowy figure. What would you like to do next?";
        String node3 = "There is a giant spider coming down the hallway towards you.";
        String node4 = "You see a vial on the ground with a strange glowing liquid.";
        String node5 = "You approach the figure and you can see nothing but darkness beneath his hood. 'What brings you this way, traveler?'  he asks.";
        String node6 = "You have no weapons and were slain by the spider. Try again?";
        String node7 = "You charmed the spider with your great personality. He will now fight alongside you.";
        String node8 = "You picked up the vial and put it in your bag.";
        String node9 = "For some reason you decided to drink the weird liquid. You died. Whoops.";
        String node10 = "You continue down the hallway. You see a glowing door to your right.";
        String node11 = "He pulls out a dagger and slays you. Stronger than he looks!";
        String endNode = "I don't feel like writing this story anymore to tell you the truth";

        Story testStory = new Story(44, "Labyrinth", node1);

        testStory.addNode(node2);
        testStory.linkNodes(1, 2, "Continue straight");

        testStory.addNode(node3);
        testStory.linkNodes(1, 3,"Take a left");

        testStory.addNode(node4);
        testStory.linkNodes(1, 4, "Turn right");

        testStory.addNode(node5);
        testStory.linkNodes(2, 5, "Approach the figure");

        testStory.linkNodes(2, testStory.getRoot().getId(), "Retreat backwards");

        testStory.addNode(node6);
        testStory.linkNodes(3, 6, "Fight the spider");
        testStory.findNode(6).setEndNode();

        testStory.addNode(node7);
        testStory.linkNodes(3, 7, "Befriend the spider");

        testStory.linkNodes(3, testStory.getRoot().getId(), "Run back");

        testStory.addNode(node8);
        testStory.linkNodes(4, 8, "Pick it up for later");

        testStory.addNode(node9);
        testStory.linkNodes(4, 9, "Drink it");
        testStory.findNode(9).setEndNode();

        testStory.addNode(node10);
        testStory.linkNodes(4, 10, "Keep moving");

        testStory.addNode(node11);
        testStory.linkNodes(5, 11, "'Shut up and fight me fool'");
        testStory.findNode(11).setEndNode();

        testStory.addNode(endNode);
        testStory.findNode(12).setEndNode();

        String node13 = "'Well you've come to the right place. Allow me to aid you in your search.'";
        testStory.addNode(node13);
        testStory.linkNodes(5, 13, "'I am looking for the holy grail'");

        String node14 = "You continue down the hallway with the mysterious stranger at your feet.";
        testStory.addNode(node14);
        testStory.linkNodes(13, 14, "'Let's do this thing!'");

        String node15 = "'Well have it your way then.'";
        testStory.addNode(node15);
        testStory.linkNodes(13, 15, "'No way dude.'");

        String node16 = "Suddenly he pulls a knife out of his pocket and lunges at you! You sense his attack and are able to parry and throw him onto the ground.";
        testStory.addNode(node16);
        testStory.linkNodes(14, 16, "Continue walking");

        String node17 = "You spare the stranger. He thanks you, and offers you a potion as an apology. He claims it grants you super strength.";
        testStory.addNode(node17);
        testStory.linkNodes(16, 17, "Spare him");

        String node18 = "You murder the stranger with his own knife. Karma shall get you soon...";
        testStory.addNode(node18);
        testStory.linkNodes(16, 18, "Kill him");

        String n19 = "You drink the potion and immediately feel an unbelievable strength flowing through your veins.";
        testStory.addNode(n19);
        testStory.linkNodes(17, 19, "Drink the potion");

        String n20 = "You shatter the vial in his face. He immediately grows super strong and rips through his shirt. He grabs your throat and strangles you.";
        testStory.addNode(n20);
        testStory.linkNodes(17, 20, "Shatter it in his face");
        testStory.findNode(20).setEndNode();

        String n21 = "It seems he cast some sort of curse on you. You find yourself getting weaker and weaker until you can hardly stand up. Soon you pass away in great agony.";
        testStory.addNode(n21);
        testStory.linkNodes(18, 21, "Continue walking");
        testStory.findNode(21).setEndNode();

        testStory.linkNodes(19, 12, "Continue");

        testStory.linkNodes(15,10, "Continue walking");

        String n22 = "It's locked.";
        testStory.addNode(n22);
        testStory.linkNodes(10, 22, "Try the handle to the door");

        String n23 = "You kick down the door. There's a huge explosion- seems like you set off some sort of nuclear reaction. You died.";
        testStory.addNode(n23);
        testStory.linkNodes(10, 23, "Kick down the door");
        testStory.findNode(23).setEndNode();

        String n24 = "You round the corner and come across a strange.. bowling ball?";
        testStory.addNode(n24);
        testStory.linkNodes(10, 24, "Continue moving");

        testStory.linkNodes(22, 23, "Kick down the door");
        testStory.linkNodes(22, 24, "Continue moving");

        testStory.linkNodes(24, 12, "Continue");

        String n25 = "You and your new spider friend continue on your way. You run into a creepy-looking old man who offers you 4000 rupees for the spider.";
        testStory.addNode(n25);
        testStory.linkNodes(7, 25, "Continue moving");

        String n26 = "'Well uhh.. I'll trade you this really cute puppy for him'";
        testStory.addNode(n26);
        testStory.linkNodes(25, 26, "'He's not for sale'");

        String n27 = "The spider turns around and bites your head off for your betrayal.";
        testStory.addNode(n27);
        testStory.linkNodes(25, 27, "'Deal!");
        testStory.findNode(27).setEndNode();

        String n28 = "You trade the giant spider for a really cute puppy. Now the puppy accompanies you on your way. 'I get it,' says the spider";
        testStory.addNode(n28);
        testStory.linkNodes(26, 28, "'Okay you got yourself a trade.");

        String n29 = "'Okay whatever man,' he says, and lets you on your way.";
        testStory.addNode(n29);
        testStory.linkNodes(26, 29, "No way Jose!");

        String n30 = "A huge marshmallow appears in front of you and starts speaking Polish. You have no idea what he's saying because you don't speak Polish.";
        testStory.addNode(n30);
        testStory.linkNodes(8, 30, "Continue down the hallway");
        testStory.linkNodes(29, 30, "Continue down the hallway");

        testStory.linkNodes(30, 12, "um... what");
        return testStory;
    }


    /**
     * Edits title of the story
     * @throws IllegalArgumentException if the title of the story is invalid
     * @post edits title of the story
     * @params storyChosen, newTitle  : story chosen by user to alter title and the new title
     */
    public static void editTitle (Story storyChosen, String newTitle) throws IllegalArgumentException {
        if (newTitle == " " || newTitle == "") throw new IllegalArgumentException("Title cannot be empty");
        else if (storyChosen.getTitle().equalsIgnoreCase(newTitle))
            throw new IllegalArgumentException("New title entered is same as previous title");
        else if (!storyChosen.getTitle().equalsIgnoreCase(newTitle)){
            storyChosen.setTitle(newTitle);
        }
    }

    public static void checkStoryComplete(Story story){
        Scanner in = new Scanner(System.in);
        int userIn;
        Map missingConns = new HashMap<>();
        Iterator itr = story.getNodeConnections().entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry thisNode = (Map.Entry)itr.next();
            int thisId = (Integer)thisNode.getKey();
            ArrayList thisConns = (ArrayList)thisNode.getValue();
            if(thisConns.size() == 0 && !story.findNode(thisId).isEndNode()){
                missingConns.put(thisNode.getKey(), thisNode.getValue());
            }
        }
        //if there are nodes with missing connections
        if(missingConns.size() > 0) {
            System.out.println("Each node must have at least one connection before the story can be complete.");
            int i = missingConns.size();
            while(i > 0) {
                System.out.println("These nodes are still missing connections:");
                itr = story.getNodeConnections().entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry thisNode = (Map.Entry) itr.next();
                    int thisId = (Integer) thisNode.getKey();
                    if(thisNode.getValue()!= null) {
                        System.out.println("(" + thisNode.getKey() + ") " + story.findNode(thisId).getStoryContent());
                    }
                }
                System.out.println("Enter node to add connection: ");
                do {
                    userIn = in.nextInt();
                    if (!missingConns.containsKey(userIn)) {
                        System.out.println("Please enter a valid ID: ");
                    }
                } while(!missingConns.containsKey(userIn));
                missingConns.remove(userIn);
                enterCondition(story, userIn, story.getNodeConnections().get(userIn).size() + 1);
                i--;
            }
        }
    }

    public static void main(String[] args) throws IOException, NullPointerException {
        Scanner scanner = new Scanner(System.in);
        int userChoice=0;
        String title=null;
        Story storySelected=null;
        LinkedList<Story> storyCol=new LinkedList<Story>();
        storyCol.add(getTestStory());
        do {
            System.out.println("What would you like to do?");
            System.out.println("1. Create a Story");
            System.out.println("2. Load a Story");
            System.out.println("3. File Manager");
            System.out.println("4. Exit");
            try{
                userChoice = scanner.nextInt();
            }catch(InputMismatchException e){
                System.out.println("Invalid input");
                userChoice = scanner.nextInt();
            }

            if (userChoice==1){
                Story tempStory = createStory();
                storyCol.add(tempStory);
            }
            else if (userChoice==2){
                System.out.println("How you want to read the story?");
                System.out.println("1. Read a story already in the database");
                System.out.println("2. Read an uploaded story");
                int userChoice2= scanner.nextInt();
                if (userChoice2 == 1) {
                    System.out.println("How you want to find the story");
                    System.out.println("1. Search through the name");
                    System.out.println("2. Search through database");
                    int storyChoice1 = scanner.nextInt();
                    if(storyChoice1==1) {
                        System.out.println("Enter title of the story:");
                        String titleEntered = scanner.nextLine();
                        for(int i=0;i<storyCol.size();i++) {
                            try {
                                if (titleEntered.equalsIgnoreCase(storyCol.get(i).getTitle())){
                                    storySelected=storyCol.get(i);
                                }
                            }catch (IllegalArgumentException e) {
                                System.out.println("No story exist with that title");
                                System.out.println("Please Enter a valid story title: ");
                                titleEntered = scanner.nextLine();                             }
                        }
                        System.out.println("What do you want to do with the story? (Play/Edit)");
                        String storyChoice2 = scanner.nextLine();
                        if (storyChoice2.equalsIgnoreCase("Play")) {
                            playStory(storySelected);
                            System.out.println("You want to play it again?(Y/N)");
                            String choice = scanner.nextLine();
                            do {
                                playStory(storySelected);
                            }
                            while (choice.equalsIgnoreCase("N"));
                        } else if (storyChoice2.equalsIgnoreCase("Edit")) {
                            int editChoice;
                            do {
                                System.out.println("What you want to edit? ");
                                System.out.println("1. Edit the title");
                                System.out.println("2. Add new content");
                                System.out.println("3. Edit existing content");
                                System.out.println("4. Done editing");
                                editChoice = scanner.nextInt();
                                if (editChoice == 1){
                                    System.out.println("Enter new title:");
                                    String newTitle = scanner.next();
                                    editTitle(storySelected, newTitle);
                                    System.out.println("You successfully changed the title of the story");
                                } else if (editChoice == 2) {
                                    System.out.println("Enter parent ID:");
                                    int parentID = scanner.nextInt();
                                    System.out.println("Enter new content:");
                                    String newContent = scanner.nextLine();

                                    //ask for end node condition
                                    storySelected.addNode(newContent);
                                    storySelected.linkNodes(parentID, storySelected.getCurrentNode().getId(), "temp condition");
                                    System.out.println("You successfully added content to the story");
                                } else if (editChoice==3) {
                                    System.out.println(storySelected.printAllNodes());
                                    System.out.println("Enter node number you want to edit:");
                                    int numEntered = scanner.nextInt();
                                    System.out.println("Enter content for edit:");
                                    String editedContent = scanner.next();
                                    editStoryContent(storySelected, editedContent, numEntered);
                                    System.out.println("You successfully changed the content of the story");
                                }
                            } while (editChoice != 4);
                        }else System.out.println("invalid input");
                    }
                    else if (storyChoice1==2) {
                        for (int i = 0; i < storyCol.size(); i++) {
                            System.out.println((i+1)+ ". "+ storyCol.get(i).getTitle());
                        }
                        System.out.println("Enter title of the story:");
                        String storyChoice = scanner.next();

                        for (int i = 0; i < storyCol.size(); i++) {
                            if (storyChoice.equalsIgnoreCase(storyCol.get(i).getTitle())){
                                storySelected = storyCol.get(i);
                                break;
                            }
                        }

                        //todo: check to see if the story doesnt exist

                        System.out.println("What do you want to do with the story? (Play/Edit)");
                        String storyChoice2 = scanner.next();
                        if (storyChoice2.equalsIgnoreCase("Play")) {
                            playStory(storySelected);
                            System.out.println("You want to play it again?(Y/N)");
                            String choice = scanner.next();
                            do {
                                playStory(storySelected);
                            }
                            while (choice.equalsIgnoreCase("N"));
                        } else if (storyChoice2.equalsIgnoreCase("Edit")) {
                            int editChoice;
                            do {
                                System.out.println("What you want to edit? ");
                                System.out.println("1. Edit the title");
                                System.out.println("2. Add new content");
                                System.out.println("3. Edit existing content");
                                System.out.println("4. Done editing");
                                editChoice = scanner.nextInt();
                                if (editChoice == 1) {
                                    System.out.println("Enter new title:");
                                    String newTitle = scanner.next();
                                    editTitle(storySelected, newTitle);
                                    System.out.println("You successfully changed the title of the story");
                                } else if (editChoice == 2) {
                                    System.out.println("Enter ID:");
                                    int parentID = scanner.nextInt();
                                    System.out.println("Enter new content:");
                                    String newContent = scanner.nextLine();

                                    //ask for end node condition
                                    storySelected.addNode(newContent);
                                    storySelected.linkNodes(parentID, storySelected.getCurrentNode().getId(), "Temp condition");
                                    System.out.println("You successfully added content to the story");
                                } else if (editChoice==3){
                                    System.out.println(storySelected.printAllNodes());
                                    System.out.println("Enter node number you want to edit:");
                                    int numEntered = scanner.nextInt();
                                    System.out.println("Enter content for edit:");
                                    String editedContent = scanner.next();
                                    editStoryContent(storySelected, editedContent, numEntered);
                                    System.out.println("You successfully changed the content of the story");
                                }
                            } while (editChoice != 4);
                        } else System.out.println("invalid input");
                    }
                }else if (userChoice2 == 2) {
                    uploadStory();
                    //todo: play or edit the story in function
                }
            }
            else if(userChoice == 3){ 
                System.out.println("What would you like to do with you files?"); 
                System.out.println("1. Import"); 
                System.out.println("2. Export"); 
                int fileChoice = scanner.nextInt();
                if (fileChoice==1){ 
                    Story importedFile = importUI();
                    System.out.println("What do you want to do with the story? (Play/Edit)");
                    String storyChoice3 = scanner.nextLine();
                    if (storyChoice3.equalsIgnoreCase("Play")) {
                        playStory(importedFile);
                        System.out.println("You want to play it again?(Y/N)");
                        String choice = scanner.nextLine();
                        do {
                            playStory(importedFile);
                        }
                        while (choice.equalsIgnoreCase("N"));
                    } else if (storyChoice3.equalsIgnoreCase("Edit")) {
                        int editChoice;
                        do {
                            System.out.println("What you want to edit? ");
                            System.out.println("1. Edit the title");
                            System.out.println("2. Add new content");
                            System.out.println("3. Edit existing content");
                            System.out.println("4. Done editing");
                            editChoice = scanner.nextInt();
                            if (editChoice == 1){
                                System.out.println("Enter new title:");
                                String newTitle = scanner.next();
                                editTitle(importedFile, newTitle);
                                System.out.println("You successfully changed the title of the story");
                            } else if (editChoice == 2) {
                                System.out.println("Enter parent ID:");
                                int parentID = scanner.nextInt();
                                System.out.println("Enter new content:");
                                String newContent = scanner.nextLine();

                                //ask for end node condition
                                importedFile.addNode(newContent);
                                importedFile.linkNodes(parentID, importedFile.getCurrentNode().getId(), "temp condition");
                                System.out.println("You successfully added content to the story");
                            } else if (editChoice==3) {
                                System.out.println(importedFile.printAllNodes());
                                System.out.println("Enter node number you want to edit:");
                                int numEntered = scanner.nextInt();
                                System.out.println("Enter content for edit:");
                                String editedContent = scanner.next();
                                editStoryContent(importedFile, editedContent, numEntered);
                                System.out.println("You successfully changed the content of the story");
                            }
                        } while (editChoice != 4);
                    }else System.out.println("invalid input");
                } 
                else if (fileChoice==2){ 
                    exportUI(); 
                } 
//                else if (fileChoice==3){ 
//                    printFile(); 
//                } 
            }
        }while (userChoice!=4 || userChoice>4 || userChoice==0);
    }
}

