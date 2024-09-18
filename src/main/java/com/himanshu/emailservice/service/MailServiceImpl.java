package com.himanshu.emailservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.himanshu.emailservice.model.Mail;
import com.himanshu.emailservice.model.Product;

@Service
public class MailServiceImpl implements MailService {
	@Autowired
	private Environment environment;

	@Autowired
	private JavaMailSender javaMailSender;

	private final int BATCH_SIZE = 5; 

	@Override
	public CompletableFuture<String> sendEmailInBatches(Mail mail, List<Product> products, List<String> recipients) {
		List<List<String>> batches = splitIntoBatches(recipients, BATCH_SIZE);

		ExecutorService executor = Executors.newFixedThreadPool(5);

		List<CompletableFuture<String>> futures = new ArrayList<>();
		for (List<String> batch : batches) {
			CompletableFuture<String> BatchFuture = CompletableFuture.supplyAsync(() -> {
				StringBuilder batchResult = new StringBuilder();
				for (String recipient : batch) {
					mail.setMailTo(recipient);
					try {
						sendEmail(mail, products);
						String threadName = Thread.currentThread().getName();
						System.out.println("---------------> " + threadName);
						batchResult.append("Email sent successfully! to ").append(recipient).append(" by ")
								.append(threadName).append("\n");
					} catch (Exception e) {
						String threadName = Thread.currentThread().getName();
						batchResult.append("Failed: Email to ").append(recipient).append(" failed in ")
								.append(threadName).append(". Error: ").append(e.getMessage()).append("\n");
					}
				}
				return batchResult.toString();
			}, executor);
			futures.add(BatchFuture);
		}

		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(v -> {
			StringBuilder finalResult = new StringBuilder();
			for (CompletableFuture<String> future : futures) {
				try {
					finalResult.append(future.get()); // This will still block until the result is available, but only
														// once for all futures
				} catch (Exception e) {
					finalResult.append("Error retrieving thread result: ").append(e.getMessage()).append("\n");
				}
			}
			executor.shutdown();
			System.out.println(finalResult.toString());
			return finalResult.toString();
		});

	}

	private List<List<String>> splitIntoBatches(List<String> recipients, int batchSize) {
		List<List<String>> batches = new ArrayList<>();
		for (int i = 0; i < recipients.size(); i += batchSize) {
			int end = Math.min(recipients.size(), i + batchSize);
			batches.add(recipients.subList(i, end));
		}
		return batches;
	}

	public CompletableFuture<String> sendEmail(Mail mail, List<Product> prodList) {

		return CompletableFuture.supplyAsync(() -> {
			try {
				MimeMessage mimeMessage = javaMailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

				// Set email attributes
				helper.setTo(mail.getMailTo());
				helper.setFrom(mail.getMailFrom() + "@chandanrajstone.com");
				helper.setSubject(mail.getMailSubject());

				StringBuilder htmlContent = new StringBuilder();
				htmlContent.append("<!DOCTYPE html>").append("<html>").append("<head>")
						.append("  <title>Your Email Template</title>").append("  <style>")
						.append("    body { font-family: Arial, sans-serif; background-color: #F5F5F5; }")
						.append("    .header { background-color: #E9E9E9; padding: 10px; text-align: center; }")
						.append("    .content { padding: 20px; }")
						.append("    .footer { background-color: #E9E9E9; padding: 10px; text-align: center; }")
						.append("    .product { margin-bottom: 20px; padding: 10px; border: 1px solid #ddd; }")
						.append("    .product img { max-width: 100px; margin-right: 10px; }")
						.append("    .product-content { display: flex; align-items: center; }")
						.append("    .product-info { padding-left: 15px; }").append("  </style>").append("</head>")
						.append("<body>").append("  <div class=\"header\">")
						.append("    <h1>Greeting From Himanshu</h1>").append("  </div>")
						.append("  <div class=\"content\">").append("    <h2>Hello!</h2>")
						.append("    <p>Welcome to our newsletter. We have some exciting updates to share with you.</p>");

				// Add dynamic product content
				for (Product product : prodList) {
					htmlContent.append("<div class=\"product\">").append("<div class=\"product-content\">")
							.append("<img src=\"").append(product.getImgUrl()).append("\" alt=\"")
							.append(product.getName()).append("\">").append("<div class=\"product-info\">")
							.append("<h3>").append(product.getName()).append("</h3>").append("<p>")
							.append(product.getDesc()).append("</p>").append("<a href=\"").append(product.getLink())
							.append("\">View Product</a>").append("</div>").append("</div>").append("</div>");
				}

				htmlContent.append("  </div>").append("  <div class=\"footer\">")
						.append("    <p>© 2023 The House of Marble. All rights reserved.</p>").append("  </div>")
						.append("</body>").append("</html>");

				helper.setText(htmlContent.toString(), true);
				String ccEmail = environment.getProperty("himv.mail.ccEmail");
				if (ccEmail != null) {
					if (ccEmail.length() > 0) {
						helper.setCc(ccEmail);
					}
				}
				javaMailSender.send(mimeMessage);
				return "Email sent successfully!";
			} catch (Exception e) {
				e.printStackTrace();
				return "Unexpected error occurred: " + e.getMessage();
			}
		});

	}
}
