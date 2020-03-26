import org.apache.commons.cli.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class SmtpServerTest {
    public static void main(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = addOptions();
        CommandLine command = parser.parse(options, args);

        String smtpHost = command.getOptionValue("h");
        String smtpPort = command.getOptionValue("port");
        String username = command.getOptionValue("u");
        String password = command.getOptionValue("p");

        SmtpMailSender mailSender = new SmtpMailSender(smtpHost, smtpPort, username, password);
        mailSender.init();
        mailSender.send(command.getOptionValue("from"), command.getOptionValue("to"), command.getOptionValue("msg"));
    }

    private static Options addOptions() {
        Options options = new Options();
        options.addOption(Option.builder("h").longOpt("host").hasArg(true).desc("SMTP server host").required(true).build());
        options.addOption(Option.builder("port").longOpt("port").hasArg(true).desc("SMTP server port").required(true).build());
        options.addOption(Option.builder("u").longOpt("user").hasArg(true).desc("SMTP username").required(true).build());
        options.addOption(Option.builder("p").longOpt("password").hasArg(true).desc("SMTP password").required(true).build());
        options.addOption(Option.builder("to").longOpt("toMail").hasArg(true).desc("Receiver Email-Id").required(true).build());
        options.addOption(Option.builder("from").longOpt("fromMail").hasArg(true).desc("Receiver Email-Id").required(true).build());
        options.addOption(Option.builder("msg").longOpt("message").hasArg(true).desc("Message to send").required(true).build());
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("help", options);
        return options;
    }
}

class SmtpMailSender {

    private final String host;
    private final String port;
    private final String username;
    private final String password;
    private JavaMailSenderImpl sender;

    SmtpMailSender(String host, String port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    void init() {
        sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(Integer.parseInt(port));
        sender.setUsername(username);
        sender.setPassword(password);
        Properties properties = sender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
    }

    void send(String from, String to, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setFrom(from);
        msg.setSubject(message);
        msg.setText(message);
        sender.send(msg);
        System.out.println(String.format("Message sent to %s successfully.", to));
    }
}