package com.sia.als.config;

import android.content.Context;
import android.os.Bundle;

import com.sia.als.activity.LoginActivity;
import com.sia.als.util.SessionManagement;

public class Config {

    public static String BASE_INIT_URL = "https://hrdapi.icso.biz.id/public/api/user/get-code";

    public static String NAMADOMAIN = "icso.biz.id";
    public static String BASE_URL = "https://"+ SessionManagement.getNamaSubdomain() +"."+NAMADOMAIN+"/public/";
    public static final String PREFS_NAME = "AlsKaryawanData";
    public static final String IS_LOGIN = "isLogin";
    public static final String KEY_NAME = "user_fullname";
    public static final String KEY_EMAIL = "user_email";
    public static final String KEY_ID = "user_id";
    public static final String HEAD_TEAM = BASE_URL + "api/v3/terlambat/head_team";
    public static final String KEY_IS_ADMIN = "is_admin";
    public static final String KEY_IS_PARTNER = "is_partner";
    public static final String KEY_MAC = "mac_address";
    public static final String KEY_SUBDOMAIN = "subdomain";
    public static String RES_QRCODE = "";
    public static final String TOPIC_GLOBAL = "global";
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 72;
    public static final String STATE_PROGRESS = "progress";
    public static final String STATE_EMPTY = "empty";
    public static final String STATE_ERROR = "error";
    public static final String STATE_NO_CONNECTION = "no_connection";
    public static final String SHARED_PREF = "ah_firebase";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    public static String LOGIN_URL = BASE_URL + "api/karyawan-login";
    public static String VERIFIKASI_URL = BASE_URL + "api/karyawan/verifikasi";
    public static String DETAIL_URL = BASE_URL + "api/karyawan/detail";
    public static String INSERT_URL = BASE_URL + "api/karyawan-register";
    public static String UPDATE_PROFILE_URL = BASE_URL + "api/karyawan/update-profile";
    public static String ABSENSI_URL = BASE_URL + "api/karyawan/absen";
    public static String HOME = BASE_URL + "api/karyawan/home";
    public static String HISTORY_URL = BASE_URL + "api/karyawan/history-absensi";
    public static String LIST_TERLAMBAT = BASE_URL + "api/karyawan/history-terlambat";
    public static String DETAIL_HISTORY_URL = BASE_URL + "api/karyawan/absensi-detail";
    public static String IZIN_HISTORY = BASE_URL + "api/karyawan/history-izin";
    public static String IZIN_URL = BASE_URL + "api/karyawan/list-izin";
    public static String ADD_IZIN_URL = BASE_URL + "api/karyawan/izin";
    public static String DETAIL_IZIN_URL = BASE_URL + "api/karyawan/izin-detail";
    public static String FORGOT_URL = BASE_URL + "api/karyawan/forgot";
    public static String RESET_URL = BASE_URL + "api/karyawan/reset";
    public static String PTKP_URL = BASE_URL + "api/daftar-ptkp";
    public static String UPDATE_PASSWORD_URL = BASE_URL + "api/karyawan/change-password";
    public static String PRIVACY_POLICY_URL = BASE_URL + "api/page/privacy-policy";
    public static String PERATURAN_URL = BASE_URL + "api/page/peraturan";
    public static String NOTIFICATION_URL = BASE_URL + "api/notifikasi/get-data-notifikasi";
    public static String UPDATE_NOTIFICATION = BASE_URL + "api/notifikasi/update-status";
    public static String DETAIL_NOTIFICATION_URL = BASE_URL + "api/notifikasi/detail";
    public static String TASK_URL = BASE_URL + "api/karyawan/task";
    public static String ADD_TASK_URL = BASE_URL + "api/task/save-data";
    public static String DETAIL_TASK_URL = BASE_URL + "api/task/detail";
    public static String PERDIN_URL = BASE_URL + "api/perdin/list-data";
    public static String DETAIL_PERDIN_URL = BASE_URL + "api/perdin/detail-data";
    public static String PENYELESAIAN_PERDIN_URL =  BASE_URL + "api/perdin/employee-complete";
    public static String SLIP_GAJI_URL = BASE_URL + "api/payroll/list-payroll";
    public static String DETAIL_SLIP_GAJI_URL = BASE_URL + "api/payroll/detail";


    public static String ADMIN_HOME_URL = BASE_URL + "hrm/hrmapi/admin_home";
    public static String ADMIN_REKAP_ABSENSI_URL = BASE_URL + "hrm/hrmapi/admin_rekap_absensi";
    public static String KARYAWAN_ALL_URL = BASE_URL + "api/karyawan/all";
    public static String ADMIN_IZIN_URL = BASE_URL + "hrm/hrmapi/admin_izin";
    public static String ADMIN_OFFICE_URL = BASE_URL + "marketing/api/lokasi/get_all_lokasi";
    public static String ADMIN_ADD_OFFICE_URL = BASE_URL + "hrm/hrmapi/admin_add_office";
    public static String ADMIN_DETAIL_OFFICE_URL = BASE_URL + "hrm/hrmapi/get_office";
    public static String ADMIN_STATUS_IZIN_URL = BASE_URL + "hrm/hrmapi/update_status_izin";
    public static String ADMIN_GET_STATUS_IZIN_URL = BASE_URL + "hrm/hrmapi/get_izin_karyawan";
    public static String ADMIN_STATUS_KARYAWAN_URL = BASE_URL + "api/karyawan/update_status_karyawan";
    public static String TERLAMBAT_HISTORY = BASE_URL + "api/v3/terlambat/terlambat_history";
    public static String TERLAMBAT_URL = BASE_URL + "api/v3/terlambat/terlambat";
    public static String ADD_TERLAMBAT_URL = BASE_URL + "api/v3/terlambat/add_terlambat";
    public static String DETAIL_TERLAMBAT_URL = BASE_URL + "api/v3/terlambat/detail_terlambat";
    public static String ADMIN_STATUS_TERLAMBAT_URL = BASE_URL + "api/v3/terlambat/update_status_terlambat";
    public static String NOTIF_PENGAJUAN = BASE_URL + "api/v3/notifikasi/get_data_notifikasi";
    public static String DETAIL_NOTIF_PENGAJUAN = BASE_URL + "api/v3/notifikasi/get_by_id";

}
