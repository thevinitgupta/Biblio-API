package tech.biblio.BookListing.entities;

public class Image {
    private ImageType type;
    private byte[] data;
    private String filename;

    public Image(ImageType type, byte[] data, String filename) {
        this.type = type;
        this.data = data;
        this.filename = filename;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
