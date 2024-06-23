package com.infoworks.lab.domain.entities;

import com.infoworks.lab.domain.validation.constraint.Gender.IsValidGender;
import com.it.soul.lab.sql.entity.Ignore;
import com.it.soul.lab.sql.entity.PrimaryKey;
import com.it.soul.lab.sql.entity.TableName;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Entity(name = "User")
@TableName(value = "User")
public class User extends com.it.soul.lab.sql.entity.Entity {
	
	@PrimaryKey(name="id", auto=true)
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

    @NotNull(message = "name must not be null.")
    private String name;

    @IsValidGender
    private String sex = Gender.NONE.name();

    @Min(value = 18, message = "age min Value is 18.")
	private int age = 18;


	//@NotNull(message = "dob Must Not Null")
	//@Past(message = "Date Of Birth Must Be Greater Then Now")
    private Date dob = new java.sql.Date(new Date().getTime());

	private boolean active;

	@Ignore
	private static int _autoIncrement = -1;

	public User() {
	    this.id = ++_autoIncrement;
    }

    public User(@NotNull(message = "Name must not be null") String name
            , Gender sex
            , @Min(value = 18, message = "Min Value is 18.") int age) {
        this();
	    this.name = name;
        this.sex = sex.name();
        this.age = age;
        updateDOB(age, false);
    }

    private void updateDOB(@Min(value = 18, message = "Min Value is 18.") int age, boolean isPositive) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.nonNull(getDob()) ? getDob() : new Date());
        int year = calendar.get(Calendar.YEAR) - ((isPositive) ? -age : age);
        calendar.set(Calendar.YEAR, year);
        setDob(calendar.getTime());
    }

    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = new java.sql.Date(dob.getTime());
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
