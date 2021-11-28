import java.util.ArrayList;
import java.util.Scanner;

public class TextToConstructor {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> constructor = new ArrayList<>();
        ArrayList<String> allStrings = new ArrayList<>();
        while (scanner.hasNextLine())
        {
            String s = scanner.nextLine();
            if (s.equals("")){
                scanner.close();
                break;
            }
            s = s.replace(".","_");
            allStrings.add(s);
        }

        for (String line: allStrings){
            String[] tokens = line.split("\\s");
            if (tokens.length >= 3) {
                try {
                    int num = Integer.parseInt(tokens[1]);
                    switch (num) {
                        case 2:
//                            System.out.println(String.format("public short %s;", tokens[2]));
                            constructor.add(String.format("%s = buffer.getShort();", tokens[2]));
                            break;
                        case 4:
//                            System.out.println(String.format("public int %s;", tokens[2]));
                            constructor.add(String.format("%s = buffer.getInt();", tokens[2]));
                            break;
                        case 8:
//                            System.out.println(String.format("public long %s;", tokens[2]));
                            constructor.add(String.format("%s = buffer.getLong();", tokens[2]));
                            break;
                        default:
//                            System.out.println(String.format("public byte[] %s = new byte[%d];", tokens[2], num));
                            constructor.add(String.format("buffer.get(%s);", tokens[2], num));
                            break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }

//        System.out.println();
        for (String s: constructor) {
//            System.out.println(s);
        }

    }
}
