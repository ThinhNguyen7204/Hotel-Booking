package com.aplusplus.HotelBooking.service;

import com.aplusplus.HotelBooking.model.Booking;
import com.aplusplus.HotelBooking.model.Room;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);  // `true` để cho phép nội dung HTML
            helper.setFrom("anhduy8a1vx52412312022004@gmail.com", "A++ Hotel - Hỗ trợ khách hàng");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createBookingHtmlEmail(Booking booking) {
        Room room = booking.getRoom();
        return """
            <html>
            <body style="font-family: Arial, sans-serif; color: #333;">
                <h2 style="color: #0066cc;">Thông tin Đặt phòng</h2>
                <p>Chào <b>
                """ + booking.getUser().getName() + """
                </b>,</p>
                <p>Cảm ơn bạn đã đặt phòng của chúng tôi. Dưới đây là thông tin đặt phòng chi tiết của bạn:</p>
                
                <table style="width: 100%; border-collapse: collapse;">
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Mã đặt phòng:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + booking.getBookingCode() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Ngày nhận phòng:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + booking.getCheckInDate() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Ngày trả phòng:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + booking.getCheckOutDate() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Số người lớn:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + booking.getNumOfAdults() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Số trẻ em:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + booking.getNumOfChildren() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Tổng số khách:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + booking.getTotalNumOfGuest() + """
                        </td>
                    </tr>
                </table>

                <h3 style="color: #0066cc;">Chi tiết Phòng</h3>
                <table style="width: 100%; border-collapse: collapse;">
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Loại phòng:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + room.getRoomType() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Kích thước phòng:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + room.getRoomSize() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Sức chứa:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + room.getRoomCapacity() + """ 
                        khách</td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Mô tả:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + room.getRoomDescription() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Tình trạng phòng:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + room.getRoomStatus() + """
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Giá phòng (giá gốc):</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + room.getRoomPrice().longValue() + """
                        VND
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Voucher giảm giá (%):</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + booking.getPercentOfDiscount().intValue() + """
                        %
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;">Số tiền cần thanh toán:</td>
                        <td style="padding: 8px; border: 1px solid #ddd;">
                        """ + booking.getFinalPrice().longValue() + """
                        VND
                        </td>
                    </tr>
                </table>

                <p>Chúc bạn có một kỳ nghỉ tuyệt vời!</p>
                <p>Trân trọng,<br>Đội ngũ khách sạn</p>
            </body>
            </html>
            """;
    }

    public String createCancellationHtmlEmail(Booking booking) {
        return """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2 style="color: #cc0000;">Thông báo Hủy Đặt phòng</h2>
            <p>Chào <b>
            """ + booking.getUser().getName() + """
            </b>,</p>
            <p>Rất tiếc, nhưng chúng tôi phải thông báo rằng đơn đặt phòng của bạn đã bị hủy do quá hạn thanh toán. Dưới đây là thông tin chi tiết về booking của bạn:</p>
            
            <table style="width: 100%; border-collapse: collapse;">
                <tr>
                    <td style="padding: 8px; border: 1px solid #ddd;">Mã đặt phòng:</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">
                    """ + booking.getBookingCode() + """
                    </td>
                </tr>
                <tr>
                    <td style="padding: 8px; border: 1px solid #ddd;">Ngày nhận phòng:</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">
                    """ + booking.getCheckInDate() + """
                    </td>
                </tr>
                <tr>
                    <td style="padding: 8px; border: 1px solid #ddd;">Ngày trả phòng:</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">
                    """ + booking.getCheckOutDate() + """
                    </td>
                </tr>
                <tr>
                    <td style="padding: 8px; border: 1px solid #ddd;">Số người lớn:</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">
                    """ + booking.getNumOfAdults() + """
                    </td>
                </tr>
                <tr>
                    <td style="padding: 8px; border: 1px solid #ddd;">Số trẻ em:</td>
                    <td style="padding: 8px; border: 1px solid #ddd;">
                    """ + booking.getNumOfChildren() + """
                    </td>
                </tr>
            </table>

            <p>Chúng tôi rất tiếc vì sự bất tiện này và hy vọng sẽ có cơ hội phục vụ bạn trong tương lai.</p>
            <p>Trân trọng,<br>Đội ngũ khách sạn</p>
        </body>
        </html>
    """;
    }
    public void sendBookingConfirmationEmail(Booking booking) {
        String toEmail = booking.getUser().getEmail();
        String subject = "Xác nhận Đặt phòng của bạn";
        String htmlContent = createBookingHtmlEmail(booking);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    public void sendBookingCancellationEmail(Booking booking) {
        String toEmail = booking.getUser().getEmail();
        String subject = "Thông báo Hủy Đặt phòng do quá hạn thanh toán";
        String htmlContent = createCancellationHtmlEmail(booking);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }
}
