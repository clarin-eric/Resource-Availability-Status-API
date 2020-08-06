package eu.clarin.cmdi.rasa.DAO.Statistics;

import eu.clarin.cmdi.rasa.helpers.statusCodeMapper.Category;
import org.jooq.Record;

import java.util.Objects;

/**
 * Category statistics: general statistics specific to one category
 */
public class CategoryStatistics extends Statistics {
    private Category category;

    public CategoryStatistics(Category category, long count, double avgRespTime, long maxRespTime) {
        super(count, avgRespTime, maxRespTime);
        this.category = category;
    }

    public CategoryStatistics(Record record) {
        super(record);
        this.category = Category.valueOf((String) record.getValue("category"));
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CategoryStatistics that = (CategoryStatistics) o;
        return category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), category);
    }

    @Override
    public String toString() {
        return "CategoryStatistics{" +
                "category=" + category +
                '}';
    }
}
