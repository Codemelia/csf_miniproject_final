package csf.finalmp.app.server.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

import csf.finalmp.app.server.models.Tip;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

// SERVICE FOR SENDING EMAIL USING SENDGRID

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.from.email}")
    private String vibeyEmail;

    @Value("${sendgrid.template.id}")
    private String templateId;

    // send email using dynamic sendgrid template
    public void sendTemplateEmail(Tip confirmedRequest, String artisteThankYouMessage) throws IOException {

        // get values from tip request
        String tipperEmail = confirmedRequest.getTipperEmail();

        if (tipperEmail == null || tipperEmail.isBlank()) throw new IllegalArgumentException("Viber email must not be null");

        String tipperName = confirmedRequest.getTipperName();
        String artisteStageName = confirmedRequest.getStageName();
        double amount = confirmedRequest.getAmount();

        Email from = new Email(vibeyEmail); // sender
        Email recipient = new Email(tipperEmail);

        Personalization personalization = new Personalization();
        personalization.addTo(recipient);

        // dynamic variables (tipperName, artisteStageName, amount, artisteThankYouMessage)
        personalization.addDynamicTemplateData("tipperName", tipperName != null ? tipperName : "Viber"); // default to Viber
        personalization.addDynamicTemplateData("artisteStageName", artisteStageName);
        personalization.addDynamicTemplateData("artisteThankYouMessage", artisteThankYouMessage); // default alr set in mysql
        personalization.addDynamicTemplateData("amount", amount);

        // build mail details
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(templateId);
        mail.addPersonalization(personalization);

        // build sg request
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        // send request
        Response response = sg.api(request);
        
        // check response
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
        System.out.println("Headers: " + response.getHeaders());
    }
}
