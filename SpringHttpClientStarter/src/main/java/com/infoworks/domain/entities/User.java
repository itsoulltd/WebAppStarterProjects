package com.infoworks.domain.entities;

import com.infoworks.domain.constraint.Gender.IsValidGender;
import com.infoworks.domain.models.Gender;
import com.infoworks.entity.PrimaryKey;
import com.infoworks.entity.TableName;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@TableName(value = "tbl_user")
@Entity(name = "tbl_user")
@Table(name="tbl_user", indexes = {@Index(name = "idx_email",columnList = "email")})
public class User extends Auditable<Integer, Long> {

	@PrimaryKey(name="id", auto=true)
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    @NotNull(message = "name must not be null.")
    private String name;

    @NotEmpty
    @Email(message = "Please enter valid Email address!")
    @Column(length = 250, unique = true, nullable = false)
    private String email = "";

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Pattern(regexp = "\\+?[0-9\\-\\s]{7,20}", message = "Invalid phone number")
    private String contact;

    @IsValidGender
    private String sex = Gender.NONE.name();

    @Min(value = 18, message = "age min Value is 18.")
	private int age = 18;

	//@NotNull(message = "dob Must Not Null")
	//@Past(message = "Date Of Birth must be greater-then now!")
    private Date dob = new java.sql.Date(new Date().getTime());

	private boolean active;

	public User() {}

    public User(@NotNull(message = "Name must not be null") String name
            , Gender sex, @Min(value = 18, message = "Min Value is 18.") int age) {
        this();
	    this.name = name;
        this.sex = sex.name();
        this.age = age;
        updateDOB(age, false);
    }

    public User(@NotNull(message = "Name must not be null") String name
            , @NotEmpty(message = "Email must not be null") String email
            , Gender sex, @Min(value = 18, message = "Min Value is 18.") int age) {
        this(name, sex, age);
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
