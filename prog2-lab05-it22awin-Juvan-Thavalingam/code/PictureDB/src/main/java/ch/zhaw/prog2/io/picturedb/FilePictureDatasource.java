package ch.zhaw.prog2.io.picturedb;



import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements the PictureDatasource Interface storing the data in
 * Character Separated Values (CSV) format, where each line consists of a record
 * whose fields are separated by the DELIMITER value ";"
 * See example file: db/picture-data.csv
 */
public class FilePictureDatasource implements PictureDatasource {
    // Charset to use for file encoding.
    protected static final Charset CHARSET = StandardCharsets.UTF_8;
    // Delimiter to separate record fields on a line
    protected static final String DELIMITER = ";";
    // Date format to use for date specific record fields
    protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private ArrayList<Integer> idList = new ArrayList<>();
    private File databaseFile;
    private final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);



    /**
     * Creates the FilePictureDatasource object with the given file path as datafile.
     * Creates the file if it does not exist.
     * Also creates an empty temp file for write operations.
     *
     * @param filepath of the file to use as database file.
     * @throws IOException if accessing or creating the file fails
     */
    public FilePictureDatasource(String filepath) throws IOException {
        databaseFile = new File(filepath);
        databaseFile.createNewFile();

    }

    public File getDatabaseFile () {
        return databaseFile;
    }



    private ArrayList<Integer> getIdList() {
        String captureId = "(\\d*);";
        Pattern capturePattern = Pattern.compile(captureId);
        ArrayList<Integer> list = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(databaseFile))) {

            while(true)
            {
                String line = bufferedReader.readLine();
                if (line == null) break;
                Matcher m = capturePattern.matcher(line);
                m.find();
                int currentId = Integer.parseInt(m.group(1));
                list.add(currentId);
            }

        } catch (IOException e) {
            System.out.println("IO-Error: " + e.getMessage());
        }
        return list;
    }

    private int getNewId () {
        ArrayList<Integer> idList = getIdList();
        if (idList.size() == 0) return 1;
        int highestId = idList.stream().max(Comparator.naturalOrder())
            .get();

        return highestId + 1;
    }


    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void insert(Picture picture) {
        try {
            if(picture.getId() == -1) {
                picture.setId(getNewId());
            }
            String newEntry = picture.toString();
            FileWriter fileWriter = new FileWriter(databaseFile, true);
            fileWriter.append(newEntry + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void update(Picture picture) throws RecordNotFoundException {
        delete(picture);
        insert(picture);
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void delete(Picture picture) throws RecordNotFoundException {
        if (picture == null) throw new NullPointerException();
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp", "csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean recordFound = false;
        try {
            FileReader fr = new FileReader(databaseFile, CHARSET);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(tempFile, CHARSET, true);
            recordFound = false;
            while (true) {
                String currentLine = br.readLine();
                if (currentLine == null) break;
                Picture currentPic = Picture.fromString(currentLine);
                if (currentPic.getId() == picture.getId()) {
                    recordFound = true;
                    continue;
                }
                fw.append(currentLine + "\n");
            }
            br.close();
            fr.close();
            fw.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        if (!recordFound) throw new RecordNotFoundException();
        databaseFile.delete();
        tempFile.renameTo(databaseFile);
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public long count() {
        int lineCount = 0;
        try (BufferedReader bf = new BufferedReader(new FileReader(databaseFile))) {
            while (bf.readLine() != null) {
                lineCount ++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineCount;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Picture findById(long id) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(databaseFile))) {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) break;
                if (line.startsWith(id + "")) {
                    return Picture.fromString(line);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Collection<Picture> findAll() {
        ArrayList<Picture> pics = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(databaseFile))) {
            while (true) {
                String currentLine = bf.readLine();
                if (currentLine == null) break;
                Picture currentPic = Picture.fromString(currentLine);
                pics.add(currentPic);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return pics;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Collection<Picture> findByPosition(float longitude, float latitude, float deviation) {
        ArrayList<Picture> pics = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(databaseFile))) {
            while (true) {
                String currentLine = br.readLine();
                if (currentLine == null) break;
                Picture currentPic = Picture.fromString(currentLine);
                if (isInRange(currentPic.getLongitude(), longitude, deviation) && isInRange(currentPic.getLatitude(), latitude, deviation)) {
                    pics.add(currentPic);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return pics;
    }

    private boolean isInRange(float currentDegree, float degree, float deviation) {


        return (currentDegree < degree + deviation) && (currentDegree > degree - deviation);
    }

}
