package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService{

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Redefinição de senha");

            String htmlContent = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <body style="
                    margin: 0;
                    padding: 0;
                    background-color: #f4f4f5;
                    font-family: Arial, sans-serif;
                    color: #27272a;
                ">
                    <table width="100%%" cellpadding="0" cellspacing="0"
                           style="padding: 40px 16px; background-color: #f4f4f5;">
                        <tr>
                            <td align="center">
                                <table width="100%%" cellpadding="0" cellspacing="0"
                                       style="
                                           max-width: 600px;
                                           background-color: #ffffff;
                                           border-radius: 12px;
                                           padding: 40px;
                                           box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                                       ">

                                    <tr>
                                        <td>
                                            <h1 style="
                                                margin: 0 0 16px;
                                                font-size: 24px;
                                                color: #18181b;
                                            ">
                                                Redefinição de senha
                                            </h1>

                                            <p style="
                                                margin: 0 0 16px;
                                                font-size: 16px;
                                                line-height: 1.6;
                                            ">
                                                Recebemos uma solicitação para redefinir
                                                a senha da sua conta.
                                            </p>

                                            <p style="
                                                margin: 0 0 28px;
                                                font-size: 16px;
                                                line-height: 1.6;
                                            ">
                                                Clique no botão abaixo para criar uma nova senha:
                                            </p>

                                            <table cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="
                                                        background-color: #6d28d9;
                                                        border-radius: 8px;
                                                    ">
                                                        <a href="%s"
                                                           style="
                                                               display: inline-block;
                                                               padding: 14px 24px;
                                                               color: #ffffff;
                                                               text-decoration: none;
                                                               font-size: 16px;
                                                               font-weight: bold;
                                                           ">
                                                            Redefinir senha
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <p style="
                                                margin: 28px 0 8px;
                                                font-size: 14px;
                                                line-height: 1.5;
                                                color: #71717a;
                                            ">
                                                Se você não solicitou a redefinição,
                                                ignore este e-mail.
                                            </p>

                                            <p style="
                                                margin: 0;
                                                font-size: 14px;
                                                line-height: 1.5;
                                                color: #71717a;
                                            ">
                                                Por segurança, este link expira após um período determinado.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(resetUrl);

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException exception) {
            throw new EmailSendingException(
                    "Não foi possível enviar o e-mail de redefinição de senha",
                    exception
            );
        }
    }
}
