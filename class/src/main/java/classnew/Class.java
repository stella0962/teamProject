package classnew;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Class_table")
public class Class {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long classId;
    private String studentName;
    private String addr;
    private String telephoneInfo;
    private String payAccount;
    private String applyStaus;

    @PostPersist
    public void onPostPersist(){
        ClassRegisted classRegisted = new ClassRegisted();
        BeanUtils.copyProperties(this, classRegisted);
        classRegisted.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        classnew.external.Payment payment = new classnew.external.Payment();
        // mappings goes here
        Application.applicationContext.getBean(classnew.external.PaymentService.class)
            .payApprove(payment);

    }
    @PostUpdate
    public void onPostUpdate(){
        ClassCanceled classCanceled = new ClassCanceled();
        BeanUtils.copyProperties(this, classCanceled);
        classCanceled.publishAfterCommit();

    }
    @PrePersist
    public void onPrePersist(){
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getTelephoneInfo() {
        return telephoneInfo;
    }

    public void setTelephoneInfo(String telephoneInfo) {
        this.telephoneInfo = telephoneInfo;
    }
    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }
    public String getApplyStaus() {
        return applyStaus;
    }

    public void setApplyStaus(String applyStaus) {
        this.applyStaus = applyStaus;
    }




}