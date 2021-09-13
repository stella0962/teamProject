package team;

public class CourseUpdated extends AbstractEvent {

    private Long id;
    private String className;
    private String classInfo;
    private String teacherName;
    private String teacherInfo;
    private String startDate;
    private String endDate;

    public CourseUpdated(){
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    public String getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    public String getTeacherInfo() {
        return teacherInfo;
    }

    public void setTeacherInfo(String teacherInfo) {
        this.teacherInfo = teacherInfo;
    }
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
