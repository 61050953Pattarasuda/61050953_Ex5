/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package producer;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.util.*;

/**
 *
 * @author pattarasuda
 */
public class Main {
    @Resource(mappedName = "jms/SimpleJMSTopic")
    private static Topic topic;
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/SimpleJMSQueue")
    private static Queue queue;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final int NUM_MSGS;
        Connection connection = null;
        
        Scanner sc = new Scanner(System.in);    //

        if ((args.length < 1) || (args.length > 2)) {
            System.err.println(
                    "Program takes one or two arguments: "
                    + "<dest_type> [<number-of-messages>]");
            System.exit(1); //ผู้ใช้ต้องป้อนว่า เป็น destination อะไร + message(ใส่หรือไม่ก็ได้)
        }

        String destType = args[0];
        
       

        if (!(destType.equals("queue") || destType.equals("topic"))) {
            System.err.println("Argument must be \"queue\" or " + "\"topic\"");
            System.exit(1);
        }

        if (args.length == 2) {
            NUM_MSGS = (new Integer(args[1])).intValue();
        } else {
            NUM_MSGS = 1;
        }

        Destination dest = null; //ประกาศ obj reference ของ dest

        try {
            if (destType.equals("queue")) {
                dest = (Destination) queue; //ถ้าผู้ใช้เลือก queue ให้ dest เป็น queue
            } else {
                dest = (Destination) topic; 
            }
        } catch (Exception e) {
            System.err.println("Error setting destination: " + e.toString());
            System.exit(1);
        }

        
        try { //ไม่ว่า dest จะเป็น topic หรือ queue จะเริ่มจาก...
            connection = connectionFactory.createConnection(); //สร้าง connection จาก obj ของconnectionfactory

            Session session = connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE); //สร้าง session จาก connection
            MessageProducer producer = session.createProducer(dest); //session จะสร้าง producer โดยระบุว่า dest เป็นอะไร
            TextMessage scoreball = session.createTextMessage(); // session จะสร้าง message
            //producer.setTimeToLive(10000);  //message live is set to 10 seconds
            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            for (int i = 0; i < NUM_MSGS; i++) { //NUM_MSGS = จำนวน message ที่จะส่งไป
                
               System.out.print("Enter Live Score: ");
               String score = sc.nextLine();
               scoreball.setText(score);
                
//                message.setText("This is message " + (i + 1)); 
//                System.out.println("Sending message: " + message.getText());

                producer.send(scoreball); //คำสั่งที่ใช้ในการส่ง message
                  /*if (i == 2) {
                    producer.send(message, DeliveryMode.NON_PERSISTENT, 4, 5000);
                }
                  else {
                      producer.send(message);
                  }*/
            }

            /*
             * Send a non-text control message indicating end of
             * messages.
             */
            producer.send(session.createMessage()); //ส่ง message ว่างไป เพื่อบอกว่า message หมดแล้ว
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close(); //จบแล้วจะ close connection
                } catch (JMSException e) {
                }
            }
        }
    }

    
    
}
