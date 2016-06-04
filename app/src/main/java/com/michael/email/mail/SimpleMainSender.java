package com.michael.email.mail;

import com.michael.email.util.L;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
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
import javax.mail.internet.MimeUtility;

/**
 * 简单邮件发送器
 *
 * @author dove *
 */
public class SimpleMainSender
{
    /**
     * 以文本格式发送邮件 (支持群发,带附件)
     *
     * @param senderInfo 待发送的邮件的信息
     * @return
     */
    public static boolean sendMail(MailSenderInfo senderInfo)
    {
        boolean flag = false;

        // 判断是否需要身份验证
        MyAuthenticator authenticator = null;
        Properties props = senderInfo.getProperties();
        if (senderInfo.isValidate())
        {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(senderInfo.getUserName(), senderInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(props, authenticator);

        try
        {
            // 根据session创建一个邮件消息
            Message sendMailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(senderInfo.getFromAddress());
            // 设置邮件消息的发送者
            sendMailMessage.setFrom(from);

            // 创建邮件接收者地址
            String[] tos = senderInfo.getToAddress();
            if (tos != null && tos.length != 0)
            {
                InternetAddress[] to = new InternetAddress[tos.length];
                // 设置邮件消息的发送者
                for (int i = 0; i < tos.length; i++)
                {
                    to[i] = new InternetAddress(tos[i]);
                }
                sendMailMessage.setRecipients(Message.RecipientType.TO, to);
            }

            // 设置邮件抄送者地址
            String[] toCCs = senderInfo.getToCarbonCopyAddress();
            if (toCCs != null && toCCs.length != 0)
            {
                InternetAddress[] toCC = new InternetAddress[toCCs.length];
                // 设置邮件消息的发送者
                for (int i = 0; i < toCCs.length; i++)
                {
                    toCC[i] = new InternetAddress(toCCs[i]);
                }
                sendMailMessage.addRecipients(Message.RecipientType.CC, toCC);
            }

            // 设置邮件密送者地址
            String[] toBCCs = senderInfo.getToBlindCarbonCopyAddress();
            if (toBCCs != null && toBCCs.length != 0)
            {
                InternetAddress[] toBCC = new InternetAddress[toBCCs.length];
                for (int i = 0; i < toBCCs.length; i++)
                {
                    toBCC[i] = new InternetAddress(toBCCs[i]);
                }
                sendMailMessage.addRecipients(Message.RecipientType.BCC, toBCC);
            }

            // 设置邮件主题
            sendMailMessage.setSubject(MimeUtility.encodeText(senderInfo.getSubject(), "utf-8", "B"));
            // 设置邮件内容
            //sendMailMessage.setText(senderInfo.getContent());
            Multipart multipart = new MimeMultipart();
            // 邮件文本内容
            if (senderInfo.getContent() != null && !"".equals(senderInfo.getContent()))
            {
                BodyPart part = new MimeBodyPart();
                part.setContent(senderInfo.getContent(), "text/plain;charset=utf-8");//设置邮件文本内容
                multipart.addBodyPart(part);
            }

            // 附件
            String attachFileNames[] = senderInfo.getAttachFileNames();
            int leng = attachFileNames == null ? 0 : attachFileNames.length;
            for (int i = 0; i < leng; i++)
            {
                BodyPart part = new MimeBodyPart();
                // 根据文件名获取数据源
                DataSource dataSource = new FileDataSource(attachFileNames[i]);
                DataHandler dataHandler = new DataHandler(dataSource);
                // 得到附件本身并至入BodyPart
                part.setDataHandler(dataHandler);
                // 得到文件名同样至入BodyPart
                part.setFileName(MimeUtility.encodeText(dataSource.getName()));
                multipart.addBodyPart(part);
            }

            sendMailMessage.setContent(multipart);
            // 设置邮件发送的时间
            sendMailMessage.setSentDate(new Date());
            // 发送邮件
            //Transport.send(sendMailMessage);
            Transport transport = sendMailSession.getTransport("smtp");
            transport.connect(senderInfo.getUserName(), senderInfo.getPassword());
            transport.send(sendMailMessage, sendMailMessage.getAllRecipients());
            transport.close();

            flag = true;
        } catch (AddressException e)
        {
            e.printStackTrace();
        } catch (MessagingException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return flag;
    }

    public static boolean sendHtmlMail(MailSenderInfo senderInfo)
    {
        boolean flag = false;

        // 判断是否需要身份验证
        MyAuthenticator authenticator = null;
        Properties props = senderInfo.getProperties();
        if (senderInfo.isValidate())
        {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(senderInfo.getUserName(), senderInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(props, authenticator);

        try
        {
            // 根据session创建一个邮件消息
            Message sendMailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(senderInfo.getFromAddress());
            // 设置邮件消息的发送者
            sendMailMessage.setFrom(from);

            // 创建邮件接收者地址
            String[] tos = senderInfo.getToAddress();
            if (tos != null && tos.length != 0)
            {
                InternetAddress[] to = new InternetAddress[tos.length];
                // 设置邮件消息的发送者
                for (int i = 0; i < tos.length; i++)
                {
                    to[i] = new InternetAddress(tos[i]);
                }
                sendMailMessage.setRecipients(Message.RecipientType.TO, to);
            }

            // 设置邮件抄送者地址
            String[] toCCs = senderInfo.getToCarbonCopyAddress();
            if (toCCs != null && toCCs.length != 0)
            {
                InternetAddress[] toCC = new InternetAddress[toCCs.length];
                // 设置邮件消息的发送者
                for (int i = 0; i < toCCs.length; i++)
                {
                    toCC[i] = new InternetAddress(toCCs[i]);
                }
                sendMailMessage.addRecipients(Message.RecipientType.CC, toCC);
            }

            // 设置邮件密送者地址
            String[] toBCCs = senderInfo.getToBlindCarbonCopyAddress();
            if (toBCCs != null && toBCCs.length != 0)
            {
                InternetAddress[] toBCC = new InternetAddress[toBCCs.length];
                for (int i = 0; i < toBCCs.length; i++)
                {
                    toBCC[i] = new InternetAddress(toBCCs[i]);
                }
                sendMailMessage.addRecipients(Message.RecipientType.BCC, toBCC);
            }

            // 设置邮件主题
            sendMailMessage.setSubject(MimeUtility.encodeText(senderInfo.getSubject(), "utf-8", "B"));
            // 设置邮件内容
            //sendMailMessage.setText(senderInfo.getContent());
            Multipart multipart = new MimeMultipart();
            // 邮件文本内容
            if (senderInfo.getContent() != null && !"".equals(senderInfo.getContent()))
            {
                BodyPart part = new MimeBodyPart();
                part.setContent(senderInfo.getContent(), "text/html;charset=utf-8");//设置邮件文本内容
                multipart.addBodyPart(part);
            }

            // 附件
            String attachFileNames[] = senderInfo.getAttachFileNames();
            int leng = attachFileNames == null ? 0 : attachFileNames.length;
            for (int i = 0; i < leng; i++)
            {
                BodyPart part = new MimeBodyPart();
                // 根据文件名获取数据源
                DataSource dataSource = new FileDataSource(attachFileNames[i]);
                DataHandler dataHandler = new DataHandler(dataSource);
                // 得到附件本身并至入BodyPart
                part.setDataHandler(dataHandler);
                // 得到文件名同样至入BodyPart
                part.setFileName(MimeUtility.encodeText(dataSource.getName()));
                multipart.addBodyPart(part);
            }

            sendMailMessage.setContent(multipart);
            // 设置邮件发送的时间
            sendMailMessage.setSentDate(new Date());
            // 发送邮件
            //Transport.send(sendMailMessage);
            Transport transport = sendMailSession.getTransport("smtp");
            transport.connect(senderInfo.getUserName(), senderInfo.getPassword());
            transport.send(sendMailMessage, sendMailMessage.getAllRecipients());

            // 关闭transport
            transport.close();

            flag = true;
        } catch (AddressException e)
        {
            e.printStackTrace();
        } catch (MessagingException e)
        {
            if(e.toString().equals("javax.mail.AuthenticationFailedException"))
            {
                L.e("SimpleMainSender", "用户认证失败啦！"+e);
            }
            e.printStackTrace();
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return flag;
    }
}
