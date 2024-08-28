package com.booksajo.bookPanda.user.service;

import com.booksajo.bookPanda.user.repository.UserRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository memberRepository;
    private final RedisService redisUtil;
    private String authNum;

    private void checkDuplicatedEmail(String email) {
        memberRepository.findByUserEmail(email).ifPresent(
            m -> {
                throw new IllegalArgumentException(" ");
            });
    }

    public MimeMessage createMessage(String to)
        throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);    //보내는 대상
        message.setSubject("북판다 회원가입 인증");        //제목

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> ★☆★☆★☆★북판다 이메일 무료 인증★☆★☆★☆★</h1>";
        msgg += "<br>";
        msgg += "<p>★☆★☆★☆★☆★☆★☆★☆★☆회원가입시 100% 인증코드 증정 EVENT!!!★☆★☆★☆★☆★☆★☆★☆★☆★<p>";
        msgg += "<p>☆★☆★☆★☆★☆★☆★☆★☆★☆북판다에서 책 구매시 책 증정 EVENT!!★☆★☆★☆★☆★☆★☆★☆★☆★☆<p>";
        msgg += "<p>★☆★☆★☆★☆★☆★☆★☆★엘리스 회원 특별 100000포인트 미증정 EVENT!!!★☆★☆★☆★☆★☆★☆★☆★<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana; width: 60%';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += authNum + "</strong>";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("woomoon1107@gmail.com", "북판다"));

        //해당 메시지는 아래에 예시

        return message;
    }

    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {    //인증 코드 8자리
            int index = random.nextInt(3);    //0~2까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) ((int) random.nextInt(26) + 97));
                case 1 -> key.append((char) (int) random.nextInt(26) + 65);
                case 2 -> key.append(random.nextInt(9));
            }
        }
        return authNum = key.toString();
    }

    //메일 발송
    //등록해둔 javaMail 객체를 사용해서 이메일 send
    public String sendSimpleMessage(String email) throws Exception {
        try {
            authNum = createCode();    //랜덤 인증번호 생성
            redisUtil.setDataExpire(email, authNum, 300000); //5분
            MimeMessage message = createMessage(email);    //메일 발송
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        return authNum;
    }



}