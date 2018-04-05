package com.jugnoo.pay.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import product.clicklabs.jugnoo.R;

public class Validator {

    public boolean validateEmail(String email) {
        email.toLowerCase();
        if (email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*(\\.[A-Za-z]{2,})$")) {
            String[] check = email.split("@");
            if (check.length > 2) {
                return false;
            } else {
//                String afterPart = check[1];
//                String[] check2 = afterPart.split("\\.");
//                for (int i = 0; i < check2.length; i++) {
//                    if (check2[i].compareTo("edu") == 0)
//                        return true;
//                }
                return true;
            }
        }
        return false;
    }


    public final boolean containsDigit(String s) {
        boolean containsDigit = false;
        s = s.trim();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))) {
                containsDigit = true;
                break;
            }
        }
        return containsDigit;
    }


    public boolean validateCard(Context ctx,String ccNumber, EditText ccNumEt, String ccMonth, String ccYear, EditText expiryDateET, String ccCVV, EditText ccvET, String fName, EditText fNameET, String lName, EditText lNameET) {
        String error = "";
        Boolean flag = false;

        if (ccNumber.length() < 16) {
            ccNumEt.requestFocus();
            ccNumEt.setHovered(true);
            Toast.makeText(ctx,ctx.getString(R.string.card_number_invalid),Toast.LENGTH_LONG).show();
             return false;
        }
        if (ccMonth.length() == 0) {
            expiryDateET.requestFocus();
            expiryDateET.setHovered(true);
            Toast.makeText(ctx,ctx.getString(R.string.please_enter_correct_expiry_date),Toast.LENGTH_LONG).show();
            return false;

        }
        if (ccYear.length() == 0) {
            expiryDateET.requestFocus();
            expiryDateET.setHovered(true);
            Toast.makeText(ctx,ctx.getString(R.string.please_enter_correct_expiry_date),Toast.LENGTH_LONG).show();
            return false;

        }
        if (Integer.parseInt(ccMonth) > 12) {
            expiryDateET.requestFocus();
            expiryDateET.setHovered(true);
            Toast.makeText(ctx,ctx.getString(R.string.expiry_month_invalid),Toast.LENGTH_LONG).show();
            return false;

        }
        if ((Integer.parseInt(ccYear) + 100) - Calendar.getInstance().getTime().getYear() < 0){
//                (Integer.parseInt(ccYear) + 100) - Calendar.getInstance().getTime().getYear() > 20) {
            expiryDateET.requestFocus();
            expiryDateET.setHovered(true);
            Toast.makeText(ctx,ctx.getString(R.string.expiry_year_invalid),Toast.LENGTH_LONG).show();
            return false;

        }
        if (ccCVV.length() == 0) {
            ccvET.requestFocus();
            ccvET.setHovered(true);
            Toast.makeText(ctx,ctx.getString(R.string.please_enter_cvv),Toast.LENGTH_LONG).show();
            return false;

        }
        if (ccCVV.length() < 3 || ccCVV.length() > 4) {
            ccvET.requestFocus();
            ccvET.setHovered(true);
            Toast.makeText(ctx,ctx.getString(R.string.invalid_cvv),Toast.LENGTH_LONG).show();
            return false;

        }
        if (fName.length() == 0) {
            fNameET.setHovered(true);
            fNameET.requestFocus();
            Toast.makeText(ctx,ctx.getString(R.string.please_fill_first_name),Toast.LENGTH_LONG).show();
            return false;
        }

        if (lName.length() == 0) {
            lNameET.setHovered(true);
            lNameET.requestFocus();
            Toast.makeText(ctx,ctx.getString(R.string.please_fill_last_name),Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public boolean validatePassword(String oldPass, EditText etOldPass, String newPass, EditText etNewPass, String conPass, EditText etConPass) {


        if (newPass.length() < 6 || newPass.length() > 15) {
            etNewPass.requestFocus();
            etNewPass.setHovered(true);
            etNewPass.setError(etNewPass.getContext().getString(R.string.password_should_be_long));
            return false;
        }
        if (conPass.compareTo(newPass) != 0) {
            etConPass.requestFocus();
            etConPass.setHovered(true);
            etConPass.setError(etConPass.getContext().getString(R.string.should_be_same_as_new_password));
            return false;
        }
        if (newPass.compareTo(oldPass) == 0) {
            etNewPass.requestFocus();
            etNewPass.setHovered(true);
            etNewPass.setError("Shouldn't be same as old password");
            return false;
        }
        etConPass.setError(null);
        etNewPass.setError(null);
        etOldPass.setError(null);
        return true;
    }

    public boolean validateSamePassword( String newPass, EditText etNewPass, String conPass, EditText etConPass) {


        if (newPass.length() < 6 || newPass.length() > 15) {
            etNewPass.requestFocus();
            etNewPass.setHovered(true);
            etNewPass.setError("Password should be 6-15 chars long");
            return false;
        }
        if (conPass.compareTo(newPass) != 0) {
            etConPass.requestFocus();
            etConPass.setHovered(true);
            etConPass.setError("Should be same as new password");
            return false;
        }

        etConPass.setError(null);
        etNewPass.setError(null);
        return true;
    }

    public boolean validateNotSamePassword( String oldPass, EditText etOldPass, String newPass, EditText etNewPass) {


        if (oldPass.length() < 6 || oldPass.length() > 15) {
            etOldPass.requestFocus();
            etOldPass.setHovered(true);
            etOldPass.setError("Password should be 6-15 chars long");
            return false;
        }
        if (newPass.length() < 6 || newPass.length() > 15) {
            etNewPass.requestFocus();
            etNewPass.setHovered(true);
            etNewPass.setError("Password should be 6-15 chars long");
            return false;
        }
        if (newPass.compareTo(oldPass) == 0) {
            etNewPass.requestFocus();
            etNewPass.setHovered(true);
            etNewPass.setError("New password should not be same as Old Password");
            return false;
        }

        etOldPass.setError(null);
        etNewPass.setError(null);
        return true;
    }

    public boolean validateLoginUser(String phone, EditText etPhone, String password, EditText etPassword)
    {
        if (phone.length() == 0) {
            etPhone.setHovered(true);
            etPhone.requestFocus();
            etPhone.setError("Please fill your Mobile number");

            return false;
        }

        if (phone.length() != 10) {
            etPhone.setHovered(true);
            etPhone.requestFocus();
            etPhone.setError("Please enter 10 digit Mobile Number");
            return false;
        }

            if (password.length() < 6 || password.length() > 15) {

            etPassword.requestFocus();
            etPassword.setHovered(true);
            etPassword.setError("Password should be 6-15 chars long");

            return false;
        }

        return true;

    }


    /**
     * registration validations
     */

    public boolean regValidateScreenOne( String fName, EditText etFname,  String email, EditText etEmail,  String phone, EditText etPhone,String password, EditText etPassword) {

        if (fName.length() == 0) {
            etFname.setHovered(true);
            etFname.requestFocus();
            etFname.setError("Please fill your full name");
            return false;
        }


        if (email.length() == 0) {
            etEmail.setHovered(true);
            etEmail.requestFocus();
            etEmail.setError("Please fill your email id");
            return false;
        }

        if (!validateEmail(email)) {
            etEmail.setHovered(true);
            etEmail.requestFocus();
            etEmail.setError("Please enter valid email id");
            return false;
        }
        if (phone.length() < 10) {
            etPhone.setHovered(true);
            etPhone.requestFocus();
            etPhone.setError("Please fill your mobile no.");
            return false;
        }


        if (password.length() < 6 || password.length() > 15) {

            etPassword.requestFocus();
            etPassword.setHovered(true);
            etPassword.setError("Password should be 6-15 chars long");
            return false;
        }



        return true;
    }


    // validating facebook screen
    public boolean regValidateFacebookScreen(int fbTag, String fName, EditText etFname, String lName, EditText etLname, String email, EditText etEmail, String mobile, EditText etMobile,String verifyCode, EditText verifyET, boolean termscheck, Activity termsCB) {

        if (fName.length() == 0) {
            etFname.setHovered(true);
            etFname.requestFocus();
            etFname.setError("Please fill your first name");
            return false;
        }

        if (lName.length() == 0) {
            etLname.setHovered(true);
            etLname.requestFocus();
            etLname.setError("Please fill your last name");
            return false;
        }


        if (email.length() == 0) {
            etEmail.setHovered(true);
            etEmail.requestFocus();
            etEmail.setError("Please fill your email id");
            return false;
        }

        if (!validateEmail(email)) {
            etEmail.setHovered(true);
            etEmail.requestFocus();
            etEmail.setError("Please enter valid email id");
            return false;
        }

        if (mobile.length() == 0) {
            etMobile.setHovered(true);
            etMobile.requestFocus();
            etMobile.setError("Please fill your mobile no.");
            return false;
        }
//        if (mobile.length() != 12) {
//            etMobile.setHovered(true);
//            etMobile.requestFocus();
//            etMobile.setError("Please enter 10 digit Mobile Number");
//            return false;
//        }

        if (verifyCode.length() == 0) {
            verifyET.setHovered(true);
            verifyET.requestFocus();
            verifyET.setError("Please fill your verification code");
            return false;
        }
        if (termscheck==false)
        {
            CommonMethods.displayToast(termsCB,"Please accept terms & conditions/privacy policies");
            return false;
        }



        return true;
    }






    public boolean validateOTP(String editTxt1, EditText edit1, String editTxt2, EditText edit2,
                                String editTxt3, EditText edit3, String editTxt4, EditText edit4)
    {
        if (editTxt1.length() == 0) {
            edit1.setHovered(true);
            edit1.requestFocus();
            edit1.setError("Please enter OTP Ist Digit .");
            return false;
        }
        if (editTxt2.length() == 0) {
            edit2.setHovered(true);
            edit2.requestFocus();
            edit2.setError("Please enter OTP 2nd Digit .");
            return false;
        }
        if (editTxt3.length() == 0) {
        edit3.setHovered(true);
        edit3.requestFocus();
        edit3.setError("Please enter OTP 3rd Digit .");
        return false;
    }
        if (editTxt4.length() == 0) {
            edit4.setHovered(true);
            edit4.requestFocus();
            edit4.setError("Please enter OTP 4th Digit .");
            return false;
        }

        return true;
    }



    public boolean validateAddAddressScreen(String editTxt1, EditText edit1, String editTxt2, EditText edit2,
                               String editTxt3, EditText edit3, String editTxt4, EditText edit4, String editTxt5, EditText edit5, String editTxt6, EditText edit6)
    {
        if (editTxt1.length() == 0) {
            edit1.setHovered(true);
            edit1.requestFocus();
            edit1.setError("Please enter label of your address.");
            return false;
        }
        if (editTxt2.length() == 0) {
            edit2.setHovered(true);
            edit2.requestFocus();
            edit2.setError("Please enter property name.");
            return false;
        }
        if (editTxt3.length() == 0) {
            edit3.setHovered(true);
            edit3.requestFocus();
            edit3.setError("Please enter unit name.");
            return false;
        }
        if (editTxt4.length() == 0) {
            edit4.setHovered(true);
            edit4.requestFocus();
            edit4.setError("Please enter street name.");
            return false;
        }
        if (editTxt5.length() == 0) {
            edit5.setHovered(true);
            edit5.requestFocus();
            edit5.setError("Please enter city name.");
            return false;
        }
        if (editTxt6.length() == 0) {
            edit6.setHovered(true);
            edit6.requestFocus();
            edit6.setError("Please enter state name.");
            return false;
        }
        return true;
    }


}