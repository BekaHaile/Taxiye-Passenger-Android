package com.jugnoo.pay.utils;

/**
 * Created by ankit on 28/09/16.
 */
public enum ApiResponseFlags {

    SILENT_PUSH(51),

    PARAMETER_MISSING(100),
    EXECUTION_ERROR(102),
    INVALID_ACCESS_TOKEN(101),
    ACTION_COMPLETE(143),
    ACTION_FAILED(144),
    SHOW_ERROR_MESSAGE(103),
    SHOW_MESSAGE(104),

    USER_NOT_EXISTS(201),

    TXN_ALREADY_EXISTS(301),
    TXN_INITIATED(302),
    TXN_CANCELLED(303),
    TXN_NOT_EXISTS(304),
    TXN_ALREADY_CLOSED(305),
    TXN_COMPLETED(306),
    TXN_FAILED(307),
    TXN_DECLINED(308),

    AUTH_DUPLICATE_REGISTRATIONS(400),
    AUTH_REGISTRATION_SUCCESSFUL(401),
    AUTH_REGISTRATION_FAILURE(402),
    AUTH_ALREADY_REGISTERED(403),
    AUTH_NOT_REGISTERED(404),
    AUTH_VERIFICATION_REQUIRED(405),
    AUTH_VERIFICATION_FAILURE(406),
    AUTH_LOGIN_SUCCESSFUL(407),
    AUTH_LOGIN_FAILURE(408),
    AUTH_LOGOUT_SUCCESSFUL(409),
    AUTH_LOGOUT_FAILURE(410),
    AUTH_REFERRAL_UNSUCCESSFUL(411),
    AUTH_REFERRAL_SUCCESSFUL(412),
    AUTH_PASSWORD_RESET_FAILURE(413),
    AUTH_PASSWORD_RESET_SUCCESS(414),
    AUTH_ALREADY_VERIFIED(415),
    PROFILE_INFORMATION(416),
    AUTH_USER_MAPPING_ID(417),
    PAYMENT_INFORMATION_WRONG(420),
    PAYMENT_UPDATE_COMPLETE(421),
    PAYMENT_UPDATE_FAILURE(422),
    TRANSACTION_HISTORY(423),
    TRANSACTION_FAILED(424),
    TRANSACTION_COMPLETE(425),
    WALLET_BALANCE(426),
    INSUFFICIENT_FUNDS(427),
    INCOMPLETE_REGISTRATION(430),
    PASSWORD_RESET_SUCCESS(432),

    PAY_REGISTRATION_SUCCSSFUL(800),
    TOKEN_GENERATED_SUCCSSFULLY(801),
    DUPLICATE_EMAIL(802),
    TOKEN_EXPIRED(803),
    NO_RECIEVER(804),
    INVALID_RECIEVER(805),
    DUPLICATE_REQUEST(806),
    INVALID_PHONE_NUMBER(807),
    INVALID_REQUEST(808),
    PAYTM_WALLET_NOT_ADDED(705),
    VPA_NOT_FOUND(809),

    ;

    private int ordinal;

    ApiResponseFlags(int ordinal){
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
