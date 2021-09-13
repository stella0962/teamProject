package team;

public class ClassRegisted extends AbstractEvent {

    private Long id;
    private String studentName;
    private String classId;
    private String addr;
    private String telephoneInfo;
    private String payMethod;
    private String payAccount;
    private String payStatus;
    private String applyStaus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
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
    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
    public String getPayInfo() {
        return payAccount;
    }

    public void setPayInfo(String payAccount) {
        this.payAccount = payAccount;
    }
    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
    public String getApplyStaus() {
        return applyStaus;
    }

    public void setApplyStaus(String applyStaus) {
        this.applyStaus = applyStaus;
    }
}