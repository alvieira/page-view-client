package guru.springframework.pageview;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import guru.springframework.config.RabbitConfig;
import guru.springframework.model.events.PageViewEvent;

/**
 * Created by jt on 2/25/17.
 */
@Service
public class PageViewServiceImpl implements PageViewService {

    private static final Logger log = LoggerFactory.getLogger(PageViewServiceImpl.class);

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public PageViewServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendPageViewEvent(PageViewEvent event) {
    	
        Writer w = new StringWriter();
        JAXB.marshal(event, w);
        
        String xmlString =  xmlString = w.toString();

        System.out.println("Sending Message");
        System.out.println(xmlString);

        log.debug("Sending Message");
        log.debug(xmlString.toString());

        rabbitTemplate.convertAndSend(RabbitConfig.OUTBOUND_QUEUE_NAME, xmlString);

        //send correlation id to audit queue
        rabbitTemplate.convertAndSend(RabbitConfig.OUTBOUND_AUDIT_QUEUE_NAME, event.getCorrelationId());
    }
}
