//FileManager
// * ReadFile: read training files and test files
// * OutputFile: output predicted labels into a file
package com.dario.smartecg.knn;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Scanner;

public class FileManager {

    private static TrainRecord[] records;

    private static int NumOfAttributes = 0;

    private static int index = 0;

    //read training files
    public static TrainRecord[] readTrainFile(InputStream inputStream) {
        BufferedReader br = null;
        InputStreamReader isr = null;

        try {
            isr = new InputStreamReader(inputStream);
            br = new BufferedReader(isr);

            String line = br.readLine();

            setup(line);

            while ((line = br.readLine()) != null) {
                readLine(line);
            }

        } catch (IOException e) {
            Log.e("Log", "Error", e);
        } finally {
            try {
                if (br != null) br.close();
                if (isr != null) isr.close();
            } catch (IOException e) {
                Log.e("Log", "Error", e);
            }
        }

        index = 0;

        return records;
    }

    private static void setup(String line) {
        String[] info = line.split(" ");
        int NumOfSamples = Integer.parseInt(info[0]);
        NumOfAttributes = Integer.parseInt(info[1]);
        int LabelOrNot = Integer.parseInt(info[2]);

        assert LabelOrNot == 1 : "No classLabel";// ensure that C is present in this file

        records = new TrainRecord[NumOfSamples];
    }

    private static void readLine(String line) {
        double[] attributes = new double[NumOfAttributes];
        int classLabel;

        String[] attr = line.split("\t");
        //Read a whole line for a TrainRecord
        for (int i = 0; i < NumOfAttributes; i++) {
            attributes[i] = Double.parseDouble(attr[i]);
        }

        //Read classLabel
        classLabel = (int) Double.parseDouble(attr[NumOfAttributes]);
        assert classLabel != -1 : "Reading class label is wrong!";

        records[index++] = new TrainRecord(attributes, classLabel);
    }

    public static TestRecord[] readTestFile(String fileName) throws IOException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file).useLocale(Locale.US);

        //read file
        int NumOfSamples = scanner.nextInt();
        int NumOfAttributes = scanner.nextInt();
        int LabelOrNot = scanner.nextInt();
        scanner.nextLine();

        assert LabelOrNot == 1 : "No classLabel";

        TestRecord[] records = new TestRecord[NumOfSamples];
        int index = 0;
        while (scanner.hasNext()) {
            double[] attributes = new double[NumOfAttributes];
            int classLabel = -1;

            //read a whole line for a TestRecord
            for (int i = 0; i < NumOfAttributes; i++) {
                attributes[i] = scanner.nextDouble();
            }

            //read the true lable of a TestRecord which is later used for validation
            classLabel = (int) scanner.nextDouble();
            assert classLabel != -1 : "Reading class label is wrong!";

            records[index] = new TestRecord(attributes, classLabel);
            index++;
        }

        return records;
    }

    public static String outputFile(TestRecord[] testRecords, String trainFilePath) throws IOException {
        //construct the predication file name
        StringBuilder predictName = new StringBuilder();
        for (int i = 15; i < trainFilePath.length(); i++) {
            if (trainFilePath.charAt(i) != '_')
                predictName.append(trainFilePath.charAt(i));
            else
                break;
        }
        String predictPath = "classification\\" + predictName.toString() + "_prediction.txt";

        //ouput the prediction labels
        File file = new File(predictPath);
        if (!file.exists())
            file.createNewFile();

        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < testRecords.length; i++) {
            TestRecord tr = testRecords[i];
            bw.write(Integer.toString(tr.predictedLabel));
            bw.newLine();
        }

        bw.close();
        fw.close();

        return predictPath;
    }
}
