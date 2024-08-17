import java.io.*;

public class Main {
    char memory[][] = new char[100][4];
    char buffer[] = new char[40];
    char IR[] = new char[4];
    char R[] = new char[4];

    int SI = 0, C = 0, IC = 0;
    int memory_used = 0;

    FileReader inputfile;
    FileWriter outputfile;
    BufferedReader readbuffer;

    Main() {
        try {
            this.inputfile = new FileReader("input_phase.txt");
            this.outputfile = new FileWriter("output.txt");
            this.readbuffer = new BufferedReader(inputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        memory = null;
        memory = new char[100][4];
        memory_used = 0;
        C = 0;
        IC = 0;
    }

    public void READ() {
        IR[3] = '0';
        String line = new String(IR);
        int num = Integer.parseInt(line.substring(2));

        try {
            line = readbuffer.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        buffer = line.toCharArray();
        for (int i = 0; i < line.length();) {
            memory[num][(i % 4)] = buffer[i];
            i++;
            if (i % 4 == 0)
                num++;
        }

    }

    public void WRITE() {
        IR[3] = '0';
        String line = new String(IR);
        int num = Integer.parseInt(line.substring(2));
        String string = "";
        String buff = "";
        for (int i = 0; i < 10; i++) {
            buff = new String(memory[num + i]);
            string = string.concat(buff);
        }

        try {
            outputfile.write(string);
            outputfile.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void TERMINATE() {
        try {
            outputfile.write("\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void MOS(int SI) {
        this.SI = SI;
        switch (SI) {
            case 1:
                READ();
                break;
            case 2:
                WRITE();
                break;
            case 3:
                TERMINATE();
                break;

        }
    }

    public void EXECUTE_USER_FUNCTION() {
        while (true) {
            if (IC == 100)
                break;

            IR[0] = memory[IC][0];
            IR[1] = memory[IC][1];
            IR[2] = memory[IC][2];
            IR[3] = memory[IC][3];
            IC += 1;

            if (IR[0] == 'L' && IR[1] == 'R') {
                String operand = new String(IR);
                int num = Integer.parseInt(operand.substring(2));

                R[0] = memory[num][0];
                R[1] = memory[num][1];
                R[2] = memory[num][2];
                R[3] = memory[num][3];

            } else if (IR[0] == 'S' && IR[1] == 'R') {
                String operand = new String(IR);
                int num = Integer.parseInt(operand.substring(2));

                memory[num][0] = R[0];
                memory[num][1] = R[1];
                memory[num][2] = R[2];
                memory[num][3] = R[3];

            } else if (IR[0] == 'C' && IR[1] == 'R') {
                String operand = new String(IR);
                int num = Integer.parseInt(operand.substring(2));

                if (memory[num][0] == R[0] && memory[num][1] == R[1] && memory[num][2] == R[2]
                        && memory[num][3] == R[3]) {
                    C = 1;
                } else {
                    C = 0;
                }

            } else if (IR[0] == 'B' && IR[1] == 'T') {
                if (C == 1) {
                    String operand = new String(IR);
                    int num = Integer.parseInt(operand.substring(2));
                    IC = num;
                    C = 0;
                }

            } else if (IR[0] == 'G' && IR[1] == 'D') {
                MOS(1);
            } else if (IR[0] == 'P' && IR[1] == 'D') {
                MOS(2);
            } else if (IR[0] == 'H') {
                MOS(3);
            }

        }
    }

    public void LOAD() {
        String line;
        String jobid = "";
        try {
            while ((line = readbuffer.readLine()) != null) {
                buffer = line.toCharArray();
                if (memory_used == 100) {
                    System.out.println("Memory exceeded\n");
                    return;
                }
                if (buffer[0] == '$' && buffer[1] == 'A' && buffer[2] == 'M' && buffer[3] == 'J') {
                    System.out.println("Program Card Detected");
                    System.out.println("Job ID: " + jobid);
                    init();
                } else if (buffer[0] == '$' && buffer[1] == 'D' && buffer[2] == 'T' && buffer[3] == 'A') {
                    System.out.println("Data Card Detected");
                    EXECUTE_USER_FUNCTION();
                    continue;
                } else if (buffer[0] == '$' && buffer[1] == 'E' && buffer[2] == 'N' && buffer[3] == 'D') {
                    System.out.println("End Card Detected");
                    System.out.println("Job with " + jobid + " is ended");
                    MOS(3);
                }

                for (int i = 0; i < line.length();) {
                    memory[memory_used][i % 4] = buffer[i];
                    i++;
                    if (i % 4 == 0)
                        memory_used++;
                }

            }

            readbuffer.close();
            outputfile.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Operating System Phase I");
        Main main = new Main();
        main.LOAD();
    }

}
