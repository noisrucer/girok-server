package com.girok.girokserver.infra.email;

public interface EmailManager {

    public void sendEmail(String recipient, String subject, String content);

}
