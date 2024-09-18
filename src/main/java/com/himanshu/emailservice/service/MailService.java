package com.himanshu.emailservice.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.himanshu.emailservice.model.Mail;
import com.himanshu.emailservice.model.Product;

public interface MailService 
{
	public CompletableFuture<String> sendEmailInBatches(Mail mail,List<Product>listOfProd,List<String>recipients);
}
