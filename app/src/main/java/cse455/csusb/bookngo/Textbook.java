package cse455.csusb.bookngo;

import java.text.NumberFormat;
import java.util.Locale;

public class Textbook {

    private String title, isbn, condition, description;
    private int cents;

    private String school, professor, course;
    private String userName, userEmail, bookID;

    private boolean hasImage = false;

    private Textbook(){}

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
     * @param userName Name of textbook's user.
     * @param userEmail Email of textbook's user.
     */
    public Textbook(String title, String isbn, String condition, String description, int cents, String school, String professor, String course, String userName, String userEmail) {
        this.title = title;
        this.isbn = isbn;
        this.condition = condition;
        this.description = description;
        this.cents = cents;

        this.school = school;
        this.professor = professor;
        this.course = course;

        this.userName = userName;
        this.userEmail = userEmail;
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
        double dollars = cents/100.0;
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

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
