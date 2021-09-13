package team;

public class CourseDeleted extends AbstractEvent {

    private Long id;

    public CourseDeleted(){
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
