package com.himanshu.emailservice.model;

import java.util.List;

public class EmailRequest {
    private String mailTo;      // Recipient email
    private String mailFrom;    // Sender email prefix (before @domain.com)
    private String mailSubject; // Subject of the email
    private List<Product> products;

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
