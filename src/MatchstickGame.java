
import java.io.*;
import java.util.Scanner;
import java.util.regex.*;

public class MatchstickGame {
    // Standardize the equation.
    private static String Standardize(String str) {
        String result = str.replaceAll("\\s+", "");

        StringBuffer sb = new StringBuffer(result);

        String RegExList = "`-=[]\\;',./~!@#$%^&*()_+{}|\":<>?"; // All recognized special symbols.
        for (int i = 0; i < RegExList.length(); i++) {
            int x = 0;
            while (true) {
                int index = sb.toString().indexOf(RegExList.charAt(i), x);
                if (index != -1) {
                    x = index + 2;
                    if (index == sb.toString().length() - 1)
                        sb.insert(index, " ");
                    else {
                        sb.insert(index, " ");
                        sb.insert((index + 2), " ");
                    }
                } else
                    break;
            }
        }

        result = sb.toString().replaceAll("\\s+", " ").trim();

        return result;
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        int DigitNo; // The number of maximal digits.
        int LeftNo; // Number of figures at the left part of the equation.
        int ServiceNo; // The number of service the user wants.
        int MatchNo; // Number of matches to use corresponding to the service.

        String UserInput; // The result that the user inputs.
        String UserAnswer; // Standardized `UserInput`.
        String Question; // Question.
        boolean Correctness = false; // The correctness of the answer.

        System.out.println("----------------------------------------------------------------------");
        System.out.println("|                             Match Game                             |");
        System.out.println("|--------------------------------------------------------------------|");
        System.out.println("|                  Programmer: Xu Zi Jun, 3160100056                 |");
        System.out.println("----------------------------------------------------------------------");
        System.out.println();

        System.out.println("----------------------------------------------------------------------");
        System.out.println();
        System.out.println("Please input the maximal digits: ");
        System.out.print(">>> ");
        DigitNo = in.nextInt();
        System.out.println();
        System.out.println("----------------------------------------------------------------------");
        System.out.println();

        System.out.println("Please input the number of figures at the left part of the equation: ");
        System.out.print(">>> ");
        LeftNo = in.nextInt();
        System.out.println();
        System.out.println("----------------------------------------------------------------------");
        System.out.println();

        System.out.println("1 -- Move");
        System.out.println("2 -- Remove");
        System.out.println("3 -- Add");
        System.out.println("Please input the Service Number: ");
        System.out.print(">>> ");
        ServiceNo = in.nextInt();
        System.out.println();
        System.out.println("----------------------------------------------------------------------");
        System.out.println();

        System.out.println("Please input the number of matches you want to use: ");
        System.out.print(">>> ");
        MatchNo = in.nextInt();
        System.out.println();
        System.out.println("----------------------------------------------------------------------");
        System.out.println();

        MatchGame MatchGameGenerator = new MatchGame(DigitNo, LeftNo, ServiceNo, MatchNo); // Use to randomly generate
                                                                                           // questions.
        Question = MatchGameGenerator.Generator();

        System.out.println("Notice: The answer must be an equation, so inequality is NOT permitted.");
        System.out.println("        Spaces can be omitted when inputting the answer.");
        System.out.println("        The equality sign can be on the both sides.");
        System.out.println("e.g.    3 + 5 = 8 -- Possible");
        System.out.println("        3 = 8 - 5 -- Possible");
        System.out.println("        3+5 =8    -- Possible");

        UserInput = in.nextLine(); // To avoid the last input been read.

        while (!Correctness) {
            System.out.println();
            System.out.println("----------------------------------------------------------------------");
            System.out.print("Service:               ");
            switch (ServiceNo) {
            case 1:
                System.out.print("Move");
                break;
            case 2:
                System.out.print("Remove");
                break;
            case 3:
                System.out.print("Add");
                break;
            }
            System.out.println();
            System.out.println("Left-side Figures:     " + LeftNo);
            System.out.println("MAX Number of Digits:  " + DigitNo);
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Movable:               " + MatchNo);
            System.out.println("Question:              " + Question);
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Please input the answer: ");
            System.out.println("Notice: Exit `f` to exit this program.");
            System.out.print(">>> ");
            UserInput = in.nextLine();
            System.out.println("----------------------------------------------------------------------");

            if (UserInput.trim().equals("f")) { // Exit the program.
                System.out.println();
                System.out.println("----------------------------------------------------------------------");
                System.out.println("Successfully Exit. Bye.");
                System.out.println("----------------------------------------------------------------------");
                System.exit(0);
            }
            UserAnswer = Standardize(UserInput);
            int count = 0; // Calculate the number of groups.
            for (int i = 0; i < UserAnswer.length(); i++)
                if (UserAnswer.charAt(i) == ' ')
                    count++;
            if (count == LeftNo * 2) { // Check the format first.
                Correctness = MatchGameGenerator.Correctness(UserAnswer);
                if (Correctness)
                    System.out.println();
                System.out.println("----------------------------------------------------------------------");
                System.out.println("Question:              " + Question);
                System.out.println("Your answer:           " + UserAnswer);
                System.out.println("----------------------------------------------------------------------");
                System.out.println("Correctness:           " + (Correctness ? "Yes" : "No"));
                System.out.println("----------------------------------------------------------------------");
            } else {
                System.out.println();
                System.out.println("----------------------------------------------------------------------");
                System.out.println("|                                ERROR                               |");
                System.out.println("|--------------------------------------------------------------------|");
                System.out.println("|               Your answer is NOT matched the format.               |");
                System.out.println("----------------------------------------------------------------------");
            }
        }

        in.close();
    }
}

class MatchGame {
    private int DigitNo; // The number of maximal digits.
    private int LeftNo; // Number of figures at the left part of the equation.
    private int ServiceNo; // Number of matches to use corresponding to the service.
    private int MatchNo; // The number of service the user wants.
    private int[][] Changeable; // If the corresponding column index can be changed to the row number, then the
                                // element is set to 1; and 0 otherwise.
    private int[][] Changeable_Inv; // If the corresponding row index can be changed to the column number, then the
                                    // element is set to 1; and 0 otherwise.
    private int[] Changeable_Num; // Save the the sum of each element as corresponding column index.
    private int[] Changeable_Inv_Num; // Save the the sum of each element as corresponding row index.
    private int[] No; // Randomly generated numbers.
    private int[][] EachDigit; // Divided from `No`.
    private int[][] ChangeableNum2No; // Save the the sum of each element as corresponding column index for each
                                      // `EachDigit`.
    private int[][] ChangeableNumInv2No; // Save the the sum of each element as corresponding row index for each
                                         // `EachDigit`.
    private int[] Changeable2No_Num; // Sum up each element in `ChangeableNum2No` and `ChangeableNumInv2No`, and save
                                     // to the first and second element.
    private String Question; // Generated question.

    // Segment the numbers and special symbols with tian in Chinese as template
    // (similar to 7-segment LED).
    // -----
    // | | |
    // |---| : Template.
    // | | |
    // -----
    // - ----- -----
    // | | |
    // | : 1; |---| : 2; |---| : 3; etc.
    // | | |
    // - ----- -----
    // Top: 1, Upper Right: 2, Upper Down: 3, Bottom: 4, Left Upper: 5, Left Down:
    // 6, Central horizontal: 7, Central Vertical: 8. (The left and right is divided
    // into 2 parts for each
    // If the number a part, I mark the part of the number to 1; and 0 otherwise.
    // Particularly, the equal sign is marked parts of 4 and 7 to 1, and other parts
    // is 0.
    final private int NoList[] = {
            // 12345678 Meaning Index
            0b01100000, // 1 0
            0b11011010, // 2 1
            0b11110010, // 3 2
            0b01100110, // 4 3
            0b10110110, // 5 4
            0b10111110, // 6 5
            0b11100000, // 7 6
            0b11111110, // 8 7
            0b11100110, // 9 8
            0b11111100, // 0 9

            0b00000011, // + 10
            0b00000010, // - 11
            0b00010010 // = 12
    };

    // Constructor.
    public MatchGame(int Local_DigitNo, int Local_LeftNo, int Local_ServiceNo, int Local_MatchNo) {
        DigitNo = Local_DigitNo;
        LeftNo = Local_LeftNo;
        ServiceNo = Local_ServiceNo;
        MatchNo = Local_MatchNo;

        Changeable = new int[13][13];
        Changeable_Inv = new int[13][13];
        Changeable_Num = new int[13]; // Use to save the possibilities of each number or special symbol.
        Changeable_Inv_Num = new int[13];

        for (int i = 0; i < 13; i++) {
            int[] count = { 0, 0 };
            for (int j = 0; j < 13; j++) {
                int tmp = NoList[i] ^ NoList[j]; // XOR operation to find if each two numbers or special symbols is
                                                 // changeable to each other with only 1 match movement.

                if ((tmp & (tmp - 1)) == 0 && (NoList[i] > NoList[j])) { // If changeable and the first element has more
                                                                         // matches, the corresponding element is set to
                                                                         // 1; and 0 otherwise.
                    Changeable[i][j] = 1;
                    count[0]++;
                } else {
                    Changeable[i][j] = 0;
                }

                if ((tmp & (tmp - 1)) == 0 && (NoList[i] < NoList[j])) { // If changeable and the second element has
                                                                         // more matches, the corresponding element is
                                                                         // set to 1; and 0 otherwise.
                    Changeable_Inv[i][j] = 1;
                    count[1]++;
                } else {
                    Changeable_Inv[i][j] = 0;
                }
            }

            Changeable_Num[i] = count[0];
            Changeable_Inv_Num[i] = count[1];
        }
    }

    public String Generator() {
        Random(); // Generate random numbers to initialize `No`.
        DivideNo(); // Divide the numbers which is not 1-digit to many 1-digit numbers.

        ChangeableNum2No = new int[EachDigit.length][];
        ChangeableNumInv2No = new int[EachDigit.length][];
        for (int i = 0; i < EachDigit.length; i++) {
            ChangeableNum2No[i] = new int[EachDigit[i].length];
            ChangeableNumInv2No[i] = new int[EachDigit[i].length];
        }

        Changeable2No_Num = new int[2];

        for (int i = 0; i < EachDigit.length; i++) {
            for (int j = 0; j < EachDigit[i].length; j++) {
                ChangeableNum2No[i][j] = Changeable_Num[EachDigit[i][j]];
                ChangeableNumInv2No[i][j] = Changeable_Inv_Num[EachDigit[i][j]];

                if (Changeable_Num[EachDigit[i][j]] != 0)
                    Changeable2No_Num[0]++;
                if (Changeable_Inv_Num[EachDigit[i][j]] != 0)
                    Changeable2No_Num[1]++;
            }
        }

        switch (ServiceNo) { // Generate the Question according to `ServiceNo`.
        case 1:
            Game_Move();
            break;
        case 2:
            Game_Remove();
            break;
        case 3:
            Game_Add();
            break;
        }

        return Question;
    }

    private void Random() {
        No = new int[LeftNo * 2 + 1]; // According to the number of digit, the range can be 0~9 or 0~99.
                                      // e.g. 23 11 7 10 5 12 21 means 23 - 7 + 5 = 21

        while (true) {
            for (int i = 0; i < LeftNo * 2 - 1; i++) { // The right-side is NOT needed.
                if (i % 2 == 1)
                    No[i] = Math.random() > 0.5 ? 10 : 11; // 10 indicates Addition, and 11 indicates subtraction.
                else
                    No[i] = (int) (Math.random() * (int) Math.pow(10, DigitNo));
            }

            int sum = No[0]; // To judge if the sum of left-side of the equation is greater than the
                             // limitation.
            for (int i = 2; i < LeftNo * 2 - 1; i += 2) {
                if (No[i - 1] == 10)
                    sum += No[i];
                else
                    sum -= No[i];
            }

            if (sum < (int) Math.pow(10, DigitNo) && sum > 0) {
                No[LeftNo * 2 - 1] = 12; // =
                No[LeftNo * 2] = sum;
                break;
            }
        }
    }

    private void DivideNo() { // Save all digits of all numbers.
        EachDigit = new int[LeftNo * 2 + 1][];
        int left[] = new int[LeftNo + 1];
        int[] count = { 0, 0 };

        try {
            for (int i = 0; i < LeftNo * 2 + 1; i += 2) // Only save the numbers.
                left[i / 2] = No[i];

            for (int i = 0; i < LeftNo + 1; i++) { // Calculate the digit of each number.
                int tmp = (int) Math.log10(left[i]) + 1;
                EachDigit[i * 2] = new int[tmp];
            }

            for (int i = 1; i < LeftNo * 2; i += 2) { // Save the special symbols.
                EachDigit[i] = new int[1];
            }

            for (int i = 1; i < LeftNo * 2; i += 2) { // Save the special symbols.
                EachDigit[i][0] = No[i];
            }

            for (int i = 0; i < LeftNo + 1; i++) { // Divide each digit.
                int tmp = left[i];
                int j = EachDigit[i * 2].length - 1;
                for (; j >= 0; j--) {
                    int mod = tmp % 10;
                    tmp /= 10;

                    EachDigit[i * 2][j] = (mod + 9) % 10; // Encoding.
                }
            }

            for (int[] i : EachDigit) {
                for (int j : i) {
                    if (Changeable_Num[j] != 0)
                        count[0]++;
                    if (Changeable_Inv_Num[j] != 0)
                        count[1]++;
                }
            }

            if (count[0] * count[1] == 0) { // To ensure that the elements are changeable.
                Random();
                DivideNo();
            }
        } catch (NegativeArraySizeException e) {
            Random();
            DivideNo();
        }
    }

    private void Game_Move() {
        int[][] Imm = new int[EachDigit.length][];
        for (int i = 0; i < EachDigit.length; i++) {
            Imm[i] = new int[EachDigit[i].length];
        }
        for (int i = 0; i < EachDigit.length; i++)
            for (int j = 0; j < EachDigit[i].length; j++)
                Imm[i][j] = EachDigit[i][j];
        int[] times = new int[2];
        int re = 0;

        while (re != MatchNo) {
            times[0] = (int) (Math.random() * Changeable2No_Num[0]);
            times[1] = (int) (Math.random() * Changeable2No_Num[1]);

            int[][] index = new int[2][2];
            int[] index2 = new int[2];

            int tmp = times[0] + 1;
            int flag = 0;
            for (int i = 0; i < EachDigit.length; i++) {
                for (int j = 0; j < EachDigit[i].length; j++) {
                    if (ChangeableNum2No[i][j] != 0)
                        tmp--;
                    if (tmp == 0) {
                        index[0][0] = i;
                        index[0][1] = j;
                        flag = 1;
                        break;
                    }
                }

                if (flag == 1)
                    break;
            }

            tmp = times[1] + 1;
            flag = 0;
            for (int i = 0; i < EachDigit.length; i++) {
                for (int j = 0; j < EachDigit[i].length; j++) {
                    if (ChangeableNumInv2No[i][j] != 0)
                        tmp--;
                    if (tmp == 0) {
                        index[1][0] = i;
                        index[1][1] = j;
                        flag = 1;
                        break;
                    }
                }

                if (flag == 1)
                    break;
            }

            times[0] = (int) (Math.random() * Changeable_Num[EachDigit[index[0][0]][index[0][1]]]);
            times[1] = (int) (Math.random() * Changeable_Inv_Num[EachDigit[index[1][0]][index[1][1]]]);

            tmp = times[0] + 1;
            for (int i = 0; i < 13; i++) {
                if (Changeable[EachDigit[index[0][0]][index[0][1]]][i] != 0)
                    tmp--;

                if (tmp == 0) {
                    index2[0] = i;
                    break;
                }
            }

            Imm[index[0][0]][index[0][1]] = index2[0];

            tmp = times[1] + 1;
            for (int i = 0; i < 13; i++) {
                if (Changeable_Inv[EachDigit[index[1][0]][index[1][1]]][i] != 0)
                    tmp--;

                if (tmp == 0) {
                    index2[1] = i;
                    break;
                }
            }

            Imm[index[1][0]][index[1][1]] = index2[1];

            re++;
        }

        Question = No2String(Imm); // Generate the question.
    }

    private void Game_Remove() {
        int[][] Imm = new int[EachDigit.length][];
        for (int i = 0; i < EachDigit.length; i++) {
            Imm[i] = new int[EachDigit[i].length];
        }
        for (int i = 0; i < EachDigit.length; i++)
            for (int j = 0; j < EachDigit[i].length; j++)
                Imm[i][j] = EachDigit[i][j];
        int[] times = new int[MatchNo];
        int i = 0;

        int[][] ChosenNo = new int[MatchNo][2];
        int k = 0;
        while (k != MatchNo) {
            int x = (int) (Math.random() * (LeftNo * 2 + 1));
            int y = (int) (Math.random() * (EachDigit[x].length));

            if (Changeable_Inv_Num[EachDigit[x][y]] != 0) { // To make sure that the chosen element is changeable.
                ChosenNo[k][0] = x;
                ChosenNo[k][1] = y;

                k++;
            } else
                continue;
        }

        while (i != MatchNo) {
            int tmp = Changeable_Num[EachDigit[ChosenNo[i][0]][ChosenNo[i][1]]];
            times[i] = (int) (Math.random() * tmp);

            if (i > 0 && times[i] == times[i - 1]) // To avoid the same changes.
                continue;

            int index = 0;
            int steps = times[i] + 1;
            for (int j = 0; j < 13; j++) {
                if (Changeable_Inv[EachDigit[ChosenNo[i][0]][ChosenNo[i][1]]][j] == 1)
                    steps--;
                if (steps == 0) {
                    index = j;
                    break;
                }
            }

            Imm[ChosenNo[i][0]][ChosenNo[i][1]] = index;

            i++;
        }

        Question = No2String(Imm); // Generate the question.
    }

    private void Game_Add() {
        int[][] Imm = new int[EachDigit.length][];
        for (int i = 0; i < EachDigit.length; i++) {
            Imm[i] = new int[EachDigit[i].length];
        }
        for (int i = 0; i < EachDigit.length; i++)
            for (int j = 0; j < EachDigit[i].length; j++)
                Imm[i][j] = EachDigit[i][j];
        int[] times = new int[MatchNo];
        int i = 0;

        int[][] ChosenNo = new int[MatchNo][2];
        int k = 0;
        while (k != MatchNo) {
            int x = (int) (Math.random() * (LeftNo * 2 + 1));
            int y = (int) (Math.random() * (EachDigit[x].length));

            if (Changeable_Num[EachDigit[x][y]] != 0) { // To make sure that the chosen element is changeable.
                ChosenNo[k][0] = x;
                ChosenNo[k][1] = y;

                k++;
            } else
                continue;
        }

        while (i != MatchNo) {
            int tmp = Changeable_Num[EachDigit[ChosenNo[i][0]][ChosenNo[i][1]]];
            times[i] = (int) (Math.random() * tmp);
            if (i > 0 && times[i] == times[i - 1]) // To avoid the same changes.
                continue;

            int index = 0;
            int steps = times[i] + 1;
            for (int j = 0; j < 13; j++) {
                if (Changeable[EachDigit[ChosenNo[i][0]][ChosenNo[i][1]]][j] == 1)
                    steps--;
                if (steps == 0) {
                    index = j;
                    break;
                }
            }

            Imm[ChosenNo[i][0]][ChosenNo[i][1]] = index;

            i++;
        }

        Question = No2String(Imm); // Generate the question.
    }

    private String No2String(int[][] arr) {
        String result = "";

        for (int[] i : arr) { // Decoding.
            for (int j : i)
                switch (j) {
                case 10:
                    result += '+';
                    break;
                case 11:
                    result += '-';
                    break;
                case 12:
                    result += '=';
                    break;
                default:
                    result += (j + 1) % 10;
                }

            result += ' ';
        }

        return result.trim(); // Remove the spaces of head and tail.
    }

    public boolean Correctness(String UserAnswer) {
        String UserStandard = UserAnswer.replaceAll("\\s*", ""); // Remove all the spaces.

        String Pattern1 = "(\\d+)(.)(\\d+)(.)(\\d+)";
        String Pattern2 = "(\\d+)(.)(\\d+)(.)(\\d+)(.)(\\d+)";
        Pattern p;

        if (LeftNo == 2)
            p = Pattern.compile(Pattern1);
        else
            p = Pattern.compile(Pattern2);

        Matcher m = p.matcher(UserStandard);

        try {
            if (m.find()) {
                for (int i = 1; i <= EachDigit.length; i += 2) { // Address the part of numbers.
                    int tmp = Integer.parseInt(m.group(i));
                    int length = (int) Math.log10(tmp) + 1; // Digits.
                    int j = length - 1;

                    for (; j >= 0; j--) {
                        int mod = tmp % 10;
                        tmp /= 10;

                        int match = (mod + 9) % 10;
                        if (match != EachDigit[i - 1][j]) {
                            System.out.println();
                            System.out
                                    .println("----------------------------------------------------------------------");
                            System.out
                                    .println("|                                ERROR                               |");
                            System.out
                                    .println("|--------------------------------------------------------------------|");
                            System.out
                                    .println("|                            Not matched.                            |");
                            System.out
                                    .println("----------------------------------------------------------------------");
                            System.out.println();
                            return false;
                        }
                    }
                }

                for (int i = 2; i <= EachDigit.length; i += 2) { // Address the part of special symbols.
                    String tmp = m.group(i);
                    int match;

                    switch (tmp) {
                    case "+":
                        match = 10;
                        break;
                    case "-":
                        match = 11;
                        break;
                    case "=":
                        match = 12;
                        break;
                    default:
                        System.out.println();
                        System.out.println("----------------------------------------------------------------------");
                        System.out.println("|                                ERROR                               |");
                        System.out.println("|--------------------------------------------------------------------|");
                        System.out.println("|                           Wrong symbols.                           |");
                        System.out.println("----------------------------------------------------------------------");
                        System.out.println();
                        return false;
                    }

                    if (match != EachDigit[i - 1][0]) {
                        System.out.println();
                        System.out.println("----------------------------------------------------------------------");
                        System.out.println("|                                ERROR                               |");
                        System.out.println("|--------------------------------------------------------------------|");
                        System.out.println("|                            Not matched.                            |");
                        System.out.println("----------------------------------------------------------------------");
                        System.out.println();
                        return false;
                    }
                }
            } else {
                System.out.println();
                System.out.println("----------------------------------------------------------------------");
                System.out.println("|                                ERROR                               |");
                System.out.println("|--------------------------------------------------------------------|");
                System.out.println("|               Your answer is NOT matched the format.               |");
                System.out.println("----------------------------------------------------------------------");
                System.out.println();
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println();
            System.out.println("----------------------------------------------------------------------");
            System.out.println("|                                ERROR                               |");
            System.out.println("|--------------------------------------------------------------------|");
            System.out.println("|                  The number of digits is exceeded.                 |");
            System.out.println("----------------------------------------------------------------------");
            System.out.println();
            return false;
        }

        return true;
    }
}
