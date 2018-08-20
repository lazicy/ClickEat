package beans;

import java.util.Date;

public class User {
	
	
	
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	// 0 - kupac; 1 - dostavljac; 2 - admin
	private int role;
	private String phone;
	private String email;
	private String registrationDate;
	private int errCode = 0;
	private boolean aktivan;
	
	public User () {
		
	}
	
	public User (String username, String password, String firstname, String lastname, int role, String phone, String email, String registrationDate, boolean aktivan) {
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.role = role;
		this.phone = phone;
		this.email = email;
		this.registrationDate = registrationDate;
		this.aktivan = true;
	}
	
	public User(int i) {
		// username not found
		if (i == 1) {
			this.errCode = 1;
		// wrong password
		} else if (i == 2) {
			this.errCode = 2;
		}
	}
	
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	
	public boolean isAktivan() {
		return aktivan;
	}

	public void setAktivan(boolean aktivan) {
		this.aktivan = aktivan;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", firstname=" + firstname + ", lastname="
				+ lastname + ", role=" + role + ", phone=" + phone + ", email=" + email + ", registrationDate="
				+ registrationDate + "]";
	}
	
	
	
	

}
