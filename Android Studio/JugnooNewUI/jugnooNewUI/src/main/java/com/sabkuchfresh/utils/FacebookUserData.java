package com.sabkuchfresh.utils;

public class FacebookUserData {

	public String accessToken;
    public String fbId;
    public String firstName;
    public String lastName;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFbId() {
        return fbId;
    }


    public String getAccessToken() {
        return accessToken;
    }


    public String userName;
    public String userEmail;
	
	public FacebookUserData(String accessToken, String fbId, String firstName, String lastName, String userName, String userEmail){
		this.accessToken = accessToken;
		this.fbId = fbId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.userEmail = userEmail;
	}
	
	@Override
	public String toString() {
		return fbId + " " + firstName + " " + lastName + " " + userName + " " + userEmail;
	}
	
}
