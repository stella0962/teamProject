package classnew;

import classnew.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired ClassRepository classRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCourseDeleted_ClassDelete(@Payload CourseDeleted courseDeleted){

        if(!courseDeleted.validate()) return;

        System.out.println("\n\n##### listener ClassDelete : " + courseDeleted.toJson() + "\n\n");



        // Sample Logic //
        // Class class = new Class();
        // classRepository.save(class);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
