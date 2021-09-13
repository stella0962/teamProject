package classnew;

public class ClassRegisted extends AbstractEvent {

    private Long id;
    private String studentName;
    private String addr;
    private String telephoneInfo;
    private String payInfo;
    private String applyStaus;
    private Long classId;

    public ClassRegisted(){
        super();
    }

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
    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }
    public String getApplyStaus() {
        return applyStaus;
    }

    public void setApplyStaus(String applyStaus) {
        this.applyStaus = applyStaus;
    }
    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }
}
