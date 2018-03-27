package ro.db.appl.domain;

import ro.db.api.annotations.Column;
import ro.db.api.annotations.Id;
import ro.db.api.annotations.Table;

@Table(name = "departments")
public class Department {
    @Id()
    @Column(name = "department_id")
    private Long id;
	
    @Column(name = "department_name")
    private String departmentName;
    
	@Column(name = "location_id")
    private Long location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getLocation() {
        return location;
    }

    public void setLocation(Long location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;

        Department that = (Department) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getDepartmentName() != null ? !getDepartmentName().equals(that.getDepartmentName()) : that.getDepartmentName() != null)
            return false;
        return !(getLocation() != null ? !getLocation().equals(that.getLocation()) : that.getLocation() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getDepartmentName() != null ? getDepartmentName().hashCode() : 0);
        result = 31 * result + (getLocation() != null ? getLocation().hashCode() : 0);
        return result;
    }
}
