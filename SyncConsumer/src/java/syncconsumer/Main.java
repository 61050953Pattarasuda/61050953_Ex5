/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package syncconsumer;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

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
        String destType = null;
        Connection connection = null;
        Session session = null;
        Destination dest = null;
        MessageConsumer consumer = null;
        TextMessage message = null;

        if (args.length != 1) {
            System.err.println("Program takes one argument: <dest_type>");
            System.exit(1);
        }

        destType = args[0];
   //     System.out.println("Destination type is " + destType);
        

        if (!(destType.equals("queue") || destType.equals("topic"))) {
            System.err.println("Argument must be \"queue\" or \"topic\"");
            System.exit(1);
        }

        try {
            if (destType.equals("queue")) {
                dest = (Destination) queue;
            } else {
                dest = (Destination) topic;
            }
        } catch (Exception e) {
            System.err.println("Error setting destination: " + e.toString());
            System.exit(1);
        }

        
        try { //start
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(dest); //สร้าง messageconsumer
            connection.start(); //start connection
            //int num = 0; /* use in the case to show session recover */
            while (true) {
                Message m = consumer.receive(); // ถ้าไม่มีข้อมูลจะหยุดรอ
                
                if (m != null) {
                    if (m instanceof TextMessage) { //เช็คว่าเป็น textmessage?
                        message = (TextMessage) m;
                        System.out.println( //print
                                "Updated!: " + message.getText());
                        /* use in the case to show session recover */
                        //num++;
                        /*if (num == 10) {
                            System.out.println("Stop connection");
                            connection.stop();
                            System.out.println("Recover session");
                            connection.start();
                            session.recover();
                            
                        }*/
                    } else { //message ว่าง
                        break;
                    }
                }
                else {
                    break;
                }
            }
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }

    
    
}
