package travel.journal.api.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import travel.journal.api.entities.PasswordResetToken;
import travel.journal.api.entities.User;
import travel.journal.api.repositories.TokenRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final TokenRepository tokenRepository;
    private final JavaMailSender javaMailSender;
    @Value("${travel-journal.appBaseUrl}")
    private String appBaseUrl;
    @Value("${spring.mail.username}")
    private String senderEmail;

    public PasswordResetTokenServiceImpl(TokenRepository tokenRepository, JavaMailSender javaMailSender) {
        this.tokenRepository = tokenRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void saveToken(PasswordResetToken passwordResetToken){
         tokenRepository.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken findByToken(String token){
        return tokenRepository.findByToken(token);
    }

    @Override
    public boolean sendEmail(User user) {
        try {
            PasswordResetToken resetToken=generateResetToken(user);
            String resetLink = appBaseUrl + "/resetPassword?token=";

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(senderEmail);
            msg.setTo(user.getEmail());

            msg.setSubject("Reset Password");
            LocalDateTime dateTime = LocalDateTime.now().plusMinutes(30);
            String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            msg.setText("Hello " + user.getFirstName() + " " + user.getLastName() + "\n\n" + "Please click on this link to reset your Password: " + resetLink + resetToken.getToken() + " . \n\n" + "This link will automatically expire on the hour: " + formattedTime);

            javaMailSender.send(msg);
            tokenRepository.save(resetToken);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public PasswordResetToken generateResetToken(User user) {
        UUID uuid = UUID.randomUUID();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(uuid.toString());
        resetToken.setExpiryDateTime(LocalDateTime.now().plusMinutes(30));
        resetToken.setUser(user);
        resetToken.setUsed(false);
       return resetToken;
    }

    @Override
    public boolean hasExpired(LocalDateTime expiryDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return expiryDateTime.isAfter(currentDateTime);
    }

    @Override
    public boolean canGenerateNewResetToken(User user) {
        List<PasswordResetToken> userTokens = user.getPasswordResetTokens();
        if(userTokens.isEmpty()){
            return true;
        }
        for (PasswordResetToken token : userTokens) {
            if (isTokenUsedOrDateExpired(token)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isTokenUsedOrDateExpired(PasswordResetToken passwordResetToken){
        return (!passwordResetToken.isUsed() && passwordResetToken.getExpiryDateTime().isAfter(LocalDateTime.now()));
    }

    @Override
    public boolean hasValidResetToken(PasswordResetToken passwordResetToken){
        return passwordResetToken != null && hasExpired(passwordResetToken.getExpiryDateTime()) && !passwordResetToken.isUsed();
    }
}
