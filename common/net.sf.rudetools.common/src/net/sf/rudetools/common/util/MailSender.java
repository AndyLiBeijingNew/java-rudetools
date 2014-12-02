package net.sf.rudetools.common.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setAttachmenet(File attachmenet) {
        this.attachmenet = attachmenet;
    }

    protected String subject;
    protected String content;
    protected String to;
    protected String from;
    protected String host;
    protected File attachmenet;

    public String send(boolean isDebug) {
        String ret = null;
        Transport transport = null;
        try {
            // create some properties and get the default Session
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.auth", false);
            // props.put("mail.smtp.sendpartial", true);

            Session session = Session.getInstance(props, null);
            session.setDebug(isDebug);

            // create a message
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // create and fill the first message part
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText(content, "UTF-8");

            // create and fill the second message part
            MimeBodyPart mbp2 = new MimeBodyPart();
            // Use setText(text, charset), to show it off !
            mbp2.attachFile(attachmenet);

            // create the Multipart and its parts to it
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            mp.addBodyPart(mbp2);
            // add the Multipart to the message
            msg.setContent(mp);

            // send the message
            Transport.send(msg);
        } catch (AddressException e) {
            ret = e.getMessage();
        } catch (MessagingException e) {
            ret = e.getMessage();
        } catch (IOException e) {
            ret = e.getMessage();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    ret += "\n" + e.getMessage();
                }
            }
        }
        return ret;
    }
}
