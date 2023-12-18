package ch.zhaw.prog2.io.picturedb;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static ch.zhaw.prog2.io.picturedb.FilePictureDatasource.DATE_FORMAT;
import static ch.zhaw.prog2.io.picturedb.FilePictureDatasource.DELIMITER;

public class Picture extends Record {

    private final URL url;
    private final Date date;
    private final String title;
    private final float longitude;
    private final float latitude;
    private static final DateFormat df = new SimpleDateFormat(DATE_FORMAT);

    public Picture(URL url, Date date, String title, float longitude, float latitude) {
        this.url = url;
        this.date = date;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Picture(URL url, String title) {
        this(url, new Date(), title, 0, 0);
    }

    protected Picture(long id, URL url, Date date, String title, float longitude, float latitude) {
        this(url,date, title, longitude, latitude);
        this.id = id;
    }

    public URL getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return Float.compare(picture.longitude, longitude) == 0 &&
            Float.compare(picture.latitude, latitude) == 0 &&
            url.equals(picture.url) &&
            date.equals(picture.date) &&
            title.equals(picture.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, date, title, longitude, latitude);
    }

    @Override
    public String toString() {
        return String.join(DELIMITER,
            String.valueOf(this.getId()),
            df.format(this.getDate()),
            String.valueOf(this.getLongitude()),
            String.valueOf(this.getLatitude()),
            this.getTitle(),
            this.getUrl().toExternalForm()
        );
    }

    public synchronized void setId (int newId) {
        this.id = newId;
    }

    public static Picture fromString (String csvLine) throws MalformedURLException, ParseException {
        String[] elements = csvLine.split(DELIMITER);
        long id = Long.parseLong(elements[0]);
        URL url = new URL(elements[5]);
        Date date = df.parse(elements[1]);
        String title = elements[4];
        float longitude = Float.parseFloat(elements[2]);
        float latitude = Float.parseFloat(elements[3]);


        return new Picture(id, url, date, title, longitude, latitude);
    }

}
