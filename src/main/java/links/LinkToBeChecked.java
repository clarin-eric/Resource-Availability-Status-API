package links;

public class LinkToBeChecked {
    private String url;
    private String record;
    private String collection;
    private String expectedMimeType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getExpectedMimeType() {
        return expectedMimeType;
    }

    public void setExpectedMimeType(String expectedMimeType) {
        this.expectedMimeType = expectedMimeType;
    }
}
