package cse455.csusb.bookngo;

import java.text.NumberFormat;
import java.util.Locale;

public class Textbook {

    private String title, isbn, condition, description;
    private int cents;

    private String school, professor, course;

    /**
     *
     * @param title Name of textbook.
     * @param isbn ISBN of textbook.
     * @param condition Condition of textbook.
     * @param description Description of textbook.
     * @param cents Price of textbook in cents.
     * @param school School textbook used at.
     * @param professor Professor textbook used with.
     * @param course Course textbook used with.
     */
    public Textbook(String title, String isbn, String condition, String description, int cents, String school, String professor, String course) {
        this.title = title;
        this.isbn = isbn;
        this.condition = condition;
        this.description = description;
        this.cents = cents;

        this.school = school;
        this.professor = professor;
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCondition() {
        return condition;
    }

    public String getDescription() {
        return description;
    }

    public int getCents() {
        return cents;
    }

    public String getPrice() {
        double dollars = cents/100;
        return NumberFormat.getCurrencyInstance(Locale.US).format(dollars);
    }

    public String getSchool() {
        return school;
    }

    public String getProfessor() {
        return professor;
    }

    public String getCourse() {
        return course;
    }
}
