package com.country.picker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.MyApplication;
import product.clicklabs.jugnoo.R;

public class CountryPicker
    implements CountryPickerDialog.CountryPickerDialogInteractionListener {



  // region Countries
  private static final Country[] COUNTRIES = {
      new Country("AD", MyApplication.getInstance().getmActivity().getString(R.string.andorra), "+376", R.drawable.flag_ad, "EUR"),
      new Country("AE", MyApplication.getInstance().getmActivity().getString(R.string.uae), "+971", R.drawable.flag_ae, "AED"),
      new Country("AF", MyApplication.getInstance().getmActivity().getString(R.string.afghanistan), "+93", R.drawable.flag_af, "AFN"),
      new Country("AG", MyApplication.getInstance().getmActivity().getString(R.string.antigua_and_barbuda), "+1", R.drawable.flag_ag, "XCD"),
      new Country("AI", MyApplication.getInstance().getmActivity().getString(R.string.anguilla), "+1", R.drawable.flag_ai, "XCD"),
      new Country("AL", MyApplication.getInstance().getmActivity().getString(R.string.albania), "+355", R.drawable.flag_al, "ALL"),
      new Country("AM", MyApplication.getInstance().getmActivity().getString(R.string.armenia), "+374", R.drawable.flag_am, "AMD"),
      new Country("AO", MyApplication.getInstance().getmActivity().getString(R.string.angola), "+244", R.drawable.flag_ao, "AOA"),
      new Country("AQ", MyApplication.getInstance().getmActivity().getString(R.string.antartica), "+672", R.drawable.flag_aq, "USD"),
      new Country("AR", MyApplication.getInstance().getmActivity().getString(R.string.argentina), "+54", R.drawable.flag_ar, "ARS"),
      new Country("AS", MyApplication.getInstance().getmActivity().getString(R.string.american_samoa), "+1", R.drawable.flag_as, "USD"),
      new Country("AT", MyApplication.getInstance().getmActivity().getString(R.string.austria), "+43", R.drawable.flag_at, "EUR"),
      new Country("AU", MyApplication.getInstance().getmActivity().getString(R.string.australia), "+61", R.drawable.flag_au, "AUD"),
      new Country("AW", MyApplication.getInstance().getmActivity().getString(R.string.aruba), "+297", R.drawable.flag_aw, "AWG"),
      new Country("AX", MyApplication.getInstance().getmActivity().getString(R.string.aland_island), "+358", R.drawable.flag_ax, "EUR"),
      new Country("AZ", MyApplication.getInstance().getmActivity().getString(R.string.azerbaijan), "+994", R.drawable.flag_az, "AZN"),
      new Country("BA", MyApplication.getInstance().getmActivity().getString(R.string.bosnia_and_herzegovina), "+387", R.drawable.flag_ba, "BAM"),
      new Country("BB", MyApplication.getInstance().getmActivity().getString(R.string.barbados), "+1", R.drawable.flag_bb, "BBD"),
      new Country("BD", MyApplication.getInstance().getmActivity().getString(R.string.bangladesh), "+880", R.drawable.flag_bd, "BDT"),
      new Country("BE", MyApplication.getInstance().getmActivity().getString(R.string.belgium), "+32", R.drawable.flag_be, "EUR"),
      new Country("BF", MyApplication.getInstance().getmActivity().getString(R.string.burkina_faso), "+226", R.drawable.flag_bf, "XOF"),
      new Country("BG", MyApplication.getInstance().getmActivity().getString(R.string.bulgaria), "+359", R.drawable.flag_bg, "BGN"),
      new Country("BH", MyApplication.getInstance().getmActivity().getString(R.string.bahrain), "+973", R.drawable.flag_bh, "BHD"),
      new Country("BI", MyApplication.getInstance().getmActivity().getString(R.string.burundi), "+257", R.drawable.flag_bi, "BIF"),
      new Country("BJ", MyApplication.getInstance().getmActivity().getString(R.string.benin), "+229", R.drawable.flag_bj, "XOF"),
      new Country("BL", MyApplication.getInstance().getmActivity().getString(R.string.saint_barthlelemy), "+590", R.drawable.flag_bl, "EUR"),
      new Country("BM", MyApplication.getInstance().getmActivity().getString(R.string.bermuda), "+1", R.drawable.flag_bm, "BMD"),
      new Country("BN", MyApplication.getInstance().getmActivity().getString(R.string.brunei_darussalam), "+673", R.drawable.flag_bn, "BND"),
      new Country("BO", MyApplication.getInstance().getmActivity().getString(R.string.bolivia_plurinational_state_of), "+591", R.drawable.flag_bo, "BOB"),
      new Country("BQ", MyApplication.getInstance().getmActivity().getString(R.string.bonaire), "+599", R.drawable.flag_bq, "USD"),
      new Country("BR", MyApplication.getInstance().getmActivity().getString(R.string.brazil), "+55", R.drawable.flag_br, "BRL"),
      new Country("BS", MyApplication.getInstance().getmActivity().getString(R.string.bahamas), "+1", R.drawable.flag_bs, "BSD"),
      new Country("BT", MyApplication.getInstance().getmActivity().getString(R.string.bhutan), "+975", R.drawable.flag_bt, "BTN"),
      new Country("BV", MyApplication.getInstance().getmActivity().getString(R.string.bouvet_island), "+47", R.drawable.flag_bv, "NOK"),
      new Country("BW", MyApplication.getInstance().getmActivity().getString(R.string.botswana), "+267", R.drawable.flag_bw, "BWP"),
      new Country("BY", MyApplication.getInstance().getmActivity().getString(R.string.belarus), "+375", R.drawable.flag_by, "BYR"),
      new Country("BZ", MyApplication.getInstance().getmActivity().getString(R.string.belize), "+501", R.drawable.flag_bz, "BZD"),
      new Country("CA", MyApplication.getInstance().getmActivity().getString(R.string.canada), "+1", R.drawable.flag_ca, "CAD"),
      new Country("CC", MyApplication.getInstance().getmActivity().getString(R.string.cocos_island), "+61", R.drawable.flag_cc, "AUD"),
      new Country("CD", MyApplication.getInstance().getmActivity().getString(R.string.congo_the_democratic_republic_of_the), "+243", R.drawable.flag_cd, "CDF"),
      new Country("CF", MyApplication.getInstance().getmActivity().getString(R.string.central_african_republic), "+236", R.drawable.flag_cf, "XAF"),
      new Country("CG", MyApplication.getInstance().getmActivity().getString(R.string.congo), "+242", R.drawable.flag_cg, "XAF"),
      new Country("CH", MyApplication.getInstance().getmActivity().getString(R.string.switzerland), "+41", R.drawable.flag_ch, "CHF"),
      new Country("CI", MyApplication.getInstance().getmActivity().getString(R.string.ivory_coast), "+225", R.drawable.flag_ci, "XOF"),
      new Country("CK", MyApplication.getInstance().getmActivity().getString(R.string.cook_islands), "+682", R.drawable.flag_ck, "NZD"),
      new Country("CL", MyApplication.getInstance().getmActivity().getString(R.string.chile), "+56", R.drawable.flag_cl, "CLP"),
      new Country("CM", MyApplication.getInstance().getmActivity().getString(R.string.cameroon), "+237", R.drawable.flag_cm, "XAF"),
      new Country("CN", MyApplication.getInstance().getmActivity().getString(R.string.china), "+86", R.drawable.flag_cn, "CNY"),
      new Country("CO", MyApplication.getInstance().getmActivity().getString(R.string.colombia), "+57", R.drawable.flag_co, "COP"),
      new Country("CR", MyApplication.getInstance().getmActivity().getString(R.string.costa_rica), "+506", R.drawable.flag_cr, "CRC"),
      new Country("CU", MyApplication.getInstance().getmActivity().getString(R.string.cuba), "+53", R.drawable.flag_cu, "CUP"),
      new Country("CV", MyApplication.getInstance().getmActivity().getString(R.string.cape_verde), "+238", R.drawable.flag_cv, "CVE"),
      new Country("CW", MyApplication.getInstance().getmActivity().getString(R.string.curacao), "+599", R.drawable.flag_cw, "ANG"),
      new Country("CX", MyApplication.getInstance().getmActivity().getString(R.string.christmas_island), "+61", R.drawable.flag_cx, "AUD"),
      new Country("CY", MyApplication.getInstance().getmActivity().getString(R.string.cyprus), "+357", R.drawable.flag_cy, "EUR"),
      new Country("CZ", MyApplication.getInstance().getmActivity().getString(R.string.czech_republic), "+420", R.drawable.flag_cz, "CZK"),
      new Country("DE", MyApplication.getInstance().getmActivity().getString(R.string.germany), "+49", R.drawable.flag_de, "EUR"),
      new Country("DJ", MyApplication.getInstance().getmActivity().getString(R.string.djibouti), "+253", R.drawable.flag_dj, "DJF"),
      new Country("DK", MyApplication.getInstance().getmActivity().getString(R.string.denmark), "+45", R.drawable.flag_dk, "DKK"),
      new Country("DM", MyApplication.getInstance().getmActivity().getString(R.string.dominica), "+1", R.drawable.flag_dm, "XCD"),
      new Country("DO", MyApplication.getInstance().getmActivity().getString(R.string.dominican_republic), "+1", R.drawable.flag_do, "DOP"),
      new Country("DZ", MyApplication.getInstance().getmActivity().getString(R.string.algeria), "+213", R.drawable.flag_dz, "DZD"),
      new Country("EC", MyApplication.getInstance().getmActivity().getString(R.string.ecuador), "+593", R.drawable.flag_ec, "USD"),
      new Country("EE", MyApplication.getInstance().getmActivity().getString(R.string.estonia), "+372", R.drawable.flag_ee, "EUR"),
      new Country("EG", MyApplication.getInstance().getmActivity().getString(R.string.egypt), "+20", R.drawable.flag_eg, "EGP"),
      new Country("EH", MyApplication.getInstance().getmActivity().getString(R.string.western_sahara), "+212", R.drawable.flag_eh, "MAD"),
      new Country("ER", MyApplication.getInstance().getmActivity().getString(R.string.eritrea), "+291", R.drawable.flag_er, "ERN"),
      new Country("ES", MyApplication.getInstance().getmActivity().getString(R.string.spain), "+34", R.drawable.flag_es, "EUR"),
      new Country("ET", MyApplication.getInstance().getmActivity().getString(R.string.ethiopia), "+251", R.drawable.flag_et, "ETB"),
      new Country("FI", MyApplication.getInstance().getmActivity().getString(R.string.finland), "+358", R.drawable.flag_fi, "EUR"),
      new Country("FJ", MyApplication.getInstance().getmActivity().getString(R.string.fiji), "+679", R.drawable.flag_fj, "FJD"),
      new Country("FK", MyApplication.getInstance().getmActivity().getString(R.string.falkland_islands_malvina), "+500", R.drawable.flag_fk, "FKP"),
      new Country("FM", MyApplication.getInstance().getmActivity().getString(R.string.micronesia_federated_states_of), "+691", R.drawable.flag_fm, "USD"),
      new Country("FO", MyApplication.getInstance().getmActivity().getString(R.string.faroe_islands), "+298", R.drawable.flag_fo, "DKK"),
      new Country("FR", MyApplication.getInstance().getmActivity().getString(R.string.france), "+33", R.drawable.flag_fr, "EUR"),
      new Country("GA", MyApplication.getInstance().getmActivity().getString(R.string.gabon), "+241", R.drawable.flag_ga, "XAF"),
      new Country("GB", MyApplication.getInstance().getmActivity().getString(R.string.united_kingdom), "+44", R.drawable.flag_gb, "GBP"),
      new Country("GD", MyApplication.getInstance().getmActivity().getString(R.string.grenada), "+1", R.drawable.flag_gd, "XCD"),
      new Country("GE", MyApplication.getInstance().getmActivity().getString(R.string.georgia), "+995", R.drawable.flag_ge, "GEL"),
      new Country("GF", MyApplication.getInstance().getmActivity().getString(R.string.french_guiana), "+594", R.drawable.flag_gf, "EUR"),
      new Country("GG", MyApplication.getInstance().getmActivity().getString(R.string.guernsey), "+44", R.drawable.flag_gg, "GGP"),
      new Country("GH", MyApplication.getInstance().getmActivity().getString(R.string.ghana), "+233", R.drawable.flag_gh, "GHS"),
      new Country("GI", MyApplication.getInstance().getmActivity().getString(R.string.gibraltar), "+350", R.drawable.flag_gi, "GIP"),
      new Country("GL", MyApplication.getInstance().getmActivity().getString(R.string.greenland), "+299", R.drawable.flag_gl, "DKK"),
      new Country("GM", MyApplication.getInstance().getmActivity().getString(R.string.gambia), "+220", R.drawable.flag_gm, "GMD"),
      new Country("GN", MyApplication.getInstance().getmActivity().getString(R.string.guinea), "+224", R.drawable.flag_gn, "GNF"),
      new Country("GP", MyApplication.getInstance().getmActivity().getString(R.string.guadeloupe), "+590", R.drawable.flag_gp, "EUR"),
      new Country("GQ", MyApplication.getInstance().getmActivity().getString(R.string.equatorial_guinea), "+240", R.drawable.flag_gq, "XAF"),
      new Country("GR", MyApplication.getInstance().getmActivity().getString(R.string.greece), "+30", R.drawable.flag_gr, "EUR"),
      new Country("GS", MyApplication.getInstance().getmActivity().getString(R.string.south_georgia_and_the_south_sandwich_island), "+500", R.drawable.flag_gs,"GBP"),
      new Country("GT", MyApplication.getInstance().getmActivity().getString(R.string.guatemala), "+502", R.drawable.flag_gt, "GTQ"),
      new Country("GU", MyApplication.getInstance().getmActivity().getString(R.string.guam), "+1", R.drawable.flag_gu, "USD"),
      new Country("GW", MyApplication.getInstance().getmActivity().getString(R.string.guinea_bissau), "+245", R.drawable.flag_gw, "XOF"),
      new Country("GY", MyApplication.getInstance().getmActivity().getString(R.string.guyana), "+592", R.drawable.flag_gy, "GYD"),
      new Country("HK", MyApplication.getInstance().getmActivity().getString(R.string.hong_kong), "+852", R.drawable.flag_hk, "HKD"),
      new Country("HM", MyApplication.getInstance().getmActivity().getString(R.string.heard_island_and_mcdonald_island), "+000", R.drawable.flag_hm, "AUD"),
      new Country("HN", MyApplication.getInstance().getmActivity().getString(R.string.honduras), "+504", R.drawable.flag_hn, "HNL"),
      new Country("HR", MyApplication.getInstance().getmActivity().getString(R.string.croatia), "+385", R.drawable.flag_hr, "HRK"),
      new Country("HT", MyApplication.getInstance().getmActivity().getString(R.string.haiti), "+509", R.drawable.flag_ht, "HTG"),
      new Country("HU", MyApplication.getInstance().getmActivity().getString(R.string.hungary), "+36", R.drawable.flag_hu, "HUF"),
      new Country("ID", MyApplication.getInstance().getmActivity().getString(R.string.indonesia), "+62", R.drawable.flag_id, "IDR"),
      new Country("IE", MyApplication.getInstance().getmActivity().getString(R.string.ireland), "+353", R.drawable.flag_ie, "EUR"),
      new Country("IL", MyApplication.getInstance().getmActivity().getString(R.string.israel), "+972", R.drawable.flag_il, "ILS"),
      new Country("IM", MyApplication.getInstance().getmActivity().getString(R.string.isle_of_man), "+44", R.drawable.flag_im, "GBP"),
      new Country("IN", MyApplication.getInstance().getmActivity().getString(R.string.india), "+91", R.drawable.flag_in, "INR"),
      new Country("IO", MyApplication.getInstance().getmActivity().getString(R.string.bristish_indian_ocean_territory), "+246", R.drawable.flag_io, "USD"),
      new Country("IQ", MyApplication.getInstance().getmActivity().getString(R.string.iraq), "+964", R.drawable.flag_iq, "IQD"),
      new Country("IR", MyApplication.getInstance().getmActivity().getString(R.string.iran_islamic_republic_of), "+98", R.drawable.flag_ir, "IRR"),
      new Country("IS", MyApplication.getInstance().getmActivity().getString(R.string.iceland), "+354", R.drawable.flag_is, "ISK"),
      new Country("IT", MyApplication.getInstance().getmActivity().getString(R.string.italy), "+39", R.drawable.flag_it, "EUR"),
      new Country("JE", MyApplication.getInstance().getmActivity().getString(R.string.jersey), "+44", R.drawable.flag_je, "JEP"),
      new Country("JM", MyApplication.getInstance().getmActivity().getString(R.string.jamaica), "+1", R.drawable.flag_jm, "JMD"),
      new Country("JO", MyApplication.getInstance().getmActivity().getString(R.string.jordan), "+962", R.drawable.flag_jo, "JOD"),
      new Country("JP", MyApplication.getInstance().getmActivity().getString(R.string.japan), "+81", R.drawable.flag_jp, "JPY"),
      new Country("KE", MyApplication.getInstance().getmActivity().getString(R.string.kenya), "+254", R.drawable.flag_ke, "KES"),
      new Country("KG", MyApplication.getInstance().getmActivity().getString(R.string.kyrgyzstan), "+996", R.drawable.flag_kg, "KGS"),
      new Country("KH", MyApplication.getInstance().getmActivity().getString(R.string.cambodia), "+855", R.drawable.flag_kh, "KHR"),
      new Country("KI", MyApplication.getInstance().getmActivity().getString(R.string.kiribati), "+686", R.drawable.flag_ki, "AUD"),
      new Country("KM", MyApplication.getInstance().getmActivity().getString(R.string.comoros), "+269", R.drawable.flag_km, "KMF"),
      new Country("KN", MyApplication.getInstance().getmActivity().getString(R.string.saint_kitts_and_nevis), "+1", R.drawable.flag_kn, "XCD"),
      new Country("KP", MyApplication.getInstance().getmActivity().getString(R.string.north_korea), "+850", R.drawable.flag_kp, "KPW"),
      new Country("KR", MyApplication.getInstance().getmActivity().getString(R.string.south_korea), "+82", R.drawable.flag_kr, "KRW"),
      new Country("KW", MyApplication.getInstance().getmActivity().getString(R.string.kuwait), "+965", R.drawable.flag_kw, "KWD"),
      new Country("KY", MyApplication.getInstance().getmActivity().getString(R.string.cayman_islands), "+345", R.drawable.flag_ky, "KYD"),
      new Country("KZ", MyApplication.getInstance().getmActivity().getString(R.string.kazakhstan), "+7", R.drawable.flag_kz, "KZT"),
      new Country("LA", MyApplication.getInstance().getmActivity().getString(R.string.lao_peoples_democratic_republic), "+856", R.drawable.flag_la, "LAK"),
      new Country("LB", MyApplication.getInstance().getmActivity().getString(R.string.lebanon), "+961", R.drawable.flag_lb, "LBP"),
      new Country("LC", MyApplication.getInstance().getmActivity().getString(R.string.saint_lucia), "+1", R.drawable.flag_lc, "XCD"),
      new Country("LI", MyApplication.getInstance().getmActivity().getString(R.string.liechtenstein), "+423", R.drawable.flag_li, "CHF"),
      new Country("LK", MyApplication.getInstance().getmActivity().getString(R.string.sri_lanka), "+94", R.drawable.flag_lk, "LKR"),
      new Country("LR", MyApplication.getInstance().getmActivity().getString(R.string.liberia), "+231", R.drawable.flag_lr, "LRD"),
      new Country("LS", MyApplication.getInstance().getmActivity().getString(R.string.lesotho), "+266", R.drawable.flag_ls, "LSL"),
      new Country("LT", MyApplication.getInstance().getmActivity().getString(R.string.lithuania), "+370", R.drawable.flag_lt, "LTL"),
      new Country("LU", MyApplication.getInstance().getmActivity().getString(R.string.luxembourg), "+352", R.drawable.flag_lu, "EUR"),
      new Country("LV", MyApplication.getInstance().getmActivity().getString(R.string.latvia), "+371", R.drawable.flag_lv, "LVL"),
      new Country("LY", MyApplication.getInstance().getmActivity().getString(R.string.libyan_arab_jamahiriya), "+218", R.drawable.flag_ly, "LYD"),
      new Country("MA", MyApplication.getInstance().getmActivity().getString(R.string.morocco), "+212", R.drawable.flag_ma, "MAD"),
      new Country("MC", MyApplication.getInstance().getmActivity().getString(R.string.monaco), "+377", R.drawable.flag_mc, "EUR"),
      new Country("MD", MyApplication.getInstance().getmActivity().getString(R.string.moldova_republic_of), "+373", R.drawable.flag_md, "MDL"),
      new Country("ME", MyApplication.getInstance().getmActivity().getString(R.string.montenegro), "+382", R.drawable.flag_me, "EUR"),
      new Country("MF", MyApplication.getInstance().getmActivity().getString(R.string.saint_martin), "+590", R.drawable.flag_mf, "EUR"),
      new Country("MG", MyApplication.getInstance().getmActivity().getString(R.string.madagascar), "+261", R.drawable.flag_mg, "MGA"),
      new Country("MH", MyApplication.getInstance().getmActivity().getString(R.string.marshall_islands), "+692", R.drawable.flag_mh, "USD"),
      new Country("MK", MyApplication.getInstance().getmActivity().getString(R.string.mer_yugoslav_republic_of), "+389", R.drawable.flag_mk,"MKD"),
      new Country("ML", MyApplication.getInstance().getmActivity().getString(R.string.mali), "+223", R.drawable.flag_ml, "XOF"),
      new Country("MM", MyApplication.getInstance().getmActivity().getString(R.string.myanmar), "+95", R.drawable.flag_mm, "MMK"),
      new Country("MN", MyApplication.getInstance().getmActivity().getString(R.string.mongolia), "+976", R.drawable.flag_mn, "MNT"),
      new Country("MO", MyApplication.getInstance().getmActivity().getString(R.string.macao), "+853", R.drawable.flag_mo, "MOP"),
      new Country("MP", MyApplication.getInstance().getmActivity().getString(R.string.northern_meriana_islands), "+1", R.drawable.flag_mp, "USD"),
      new Country("MQ", MyApplication.getInstance().getmActivity().getString(R.string.martinique), "+596", R.drawable.flag_mq, "EUR"),
      new Country("MR", MyApplication.getInstance().getmActivity().getString(R.string.mauritania), "+222", R.drawable.flag_mr, "MRO"),
      new Country("MS", MyApplication.getInstance().getmActivity().getString(R.string.montserrat), "+1", R.drawable.flag_ms, "XCD"),
      new Country("MT", MyApplication.getInstance().getmActivity().getString(R.string.malta), "+356", R.drawable.flag_mt, "EUR"),
      new Country("MU", MyApplication.getInstance().getmActivity().getString(R.string.mauritius), "+230", R.drawable.flag_mu, "MUR"),
      new Country("MV", MyApplication.getInstance().getmActivity().getString(R.string.maldives), "+960", R.drawable.flag_mv, "MVR"),
      new Country("MW", MyApplication.getInstance().getmActivity().getString(R.string.malawi), "+265", R.drawable.flag_mw, "MWK"),
      new Country("MX", MyApplication.getInstance().getmActivity().getString(R.string.mexico), "+52", R.drawable.flag_mx, "MXN"),
      new Country("MY", MyApplication.getInstance().getmActivity().getString(R.string.malaysia), "+60", R.drawable.flag_my, "MYR"),
      new Country("MZ", MyApplication.getInstance().getmActivity().getString(R.string.mozambique), "+258", R.drawable.flag_mz, "MZN"),
      new Country("NA", MyApplication.getInstance().getmActivity().getString(R.string.nambia), "+264", R.drawable.flag_na, "NAD"),
      new Country("NC", MyApplication.getInstance().getmActivity().getString(R.string.new_caledonia), "+687", R.drawable.flag_nc, "XPF"),
      new Country("NE", MyApplication.getInstance().getmActivity().getString(R.string.niger), "+227", R.drawable.flag_ne, "XOF"),
      new Country("NF", MyApplication.getInstance().getmActivity().getString(R.string.norfolk_island), "+672", R.drawable.flag_nf, "AUD"),
      new Country("NG", MyApplication.getInstance().getmActivity().getString(R.string.nigeria), "+234", R.drawable.flag_ng, "NGN"),
      new Country("NI", MyApplication.getInstance().getmActivity().getString(R.string.nicaragua), "+505", R.drawable.flag_ni, "NIO"),
      new Country("NL", MyApplication.getInstance().getmActivity().getString(R.string.netherlands), "+31", R.drawable.flag_nl, "EUR"),
      new Country("NO", MyApplication.getInstance().getmActivity().getString(R.string.norway), "+47", R.drawable.flag_no, "NOK"),
      new Country("NP", MyApplication.getInstance().getmActivity().getString(R.string.nepal), "+977", R.drawable.flag_np, "NPR"),
      new Country("NR", MyApplication.getInstance().getmActivity().getString(R.string.nauru), "+674", R.drawable.flag_nr, "AUD"),
      new Country("NU", MyApplication.getInstance().getmActivity().getString(R.string.niue), "+683", R.drawable.flag_nu, "NZD"),
      new Country("NZ", MyApplication.getInstance().getmActivity().getString(R.string.newzealand_), "+64", R.drawable.flag_nz, "NZD"),
      new Country("OM", MyApplication.getInstance().getmActivity().getString(R.string.oman), "+968", R.drawable.flag_om, "OMR"),
      new Country("PA", MyApplication.getInstance().getmActivity().getString(R.string.panama), "+507", R.drawable.flag_pa, "PAB"),
      new Country("PE", MyApplication.getInstance().getmActivity().getString(R.string.peru), "+51", R.drawable.flag_pe, "PEN"),
      new Country("PF", MyApplication.getInstance().getmActivity().getString(R.string.french_polynesia), "+689", R.drawable.flag_pf, "XPF"),
      new Country("PG", MyApplication.getInstance().getmActivity().getString(R.string.papua_new_guinea), "+675", R.drawable.flag_pg, "PGK"),
      new Country("PH", MyApplication.getInstance().getmActivity().getString(R.string.philippines), "+63", R.drawable.flag_ph, "PHP"),
      new Country("PK", MyApplication.getInstance().getmActivity().getString(R.string.pakistan), "+92", R.drawable.flag_pk, "PKR"),
      new Country("PL", MyApplication.getInstance().getmActivity().getString(R.string.poland), "+48", R.drawable.flag_pl, "PLN"),
      new Country("PM", MyApplication.getInstance().getmActivity().getString(R.string.saint_pierre_and_miquelon), "+508", R.drawable.flag_pm, "EUR"),
      new Country("PN", MyApplication.getInstance().getmActivity().getString(R.string.pitcairn), "+872", R.drawable.flag_pn, "NZD"),
      new Country("PR", MyApplication.getInstance().getmActivity().getString(R.string.puerto_rico), "+1", R.drawable.flag_pr, "USD"),
      new Country("PS", MyApplication.getInstance().getmActivity().getString(R.string.palestinian_territory_occupied), "+970", R.drawable.flag_ps, "ILS"),
      new Country("PT", MyApplication.getInstance().getmActivity().getString(R.string.portugal), "+351", R.drawable.flag_pt, "EUR"),
      new Country("PW", MyApplication.getInstance().getmActivity().getString(R.string.palau), "+680", R.drawable.flag_pw, "USD"),
      new Country("PY", MyApplication.getInstance().getmActivity().getString(R.string.paraguay), "+595", R.drawable.flag_py, "PYG"),
      new Country("QA", MyApplication.getInstance().getmActivity().getString(R.string.qatar), "+974", R.drawable.flag_qa, "QAR"),
      new Country("RE", MyApplication.getInstance().getmActivity().getString(R.string.reunion), "+262", R.drawable.flag_re, "EUR"),
      new Country("RO", MyApplication.getInstance().getmActivity().getString(R.string.romania), "+40", R.drawable.flag_ro, "RON"),
      new Country("RS", MyApplication.getInstance().getmActivity().getString(R.string.serbia), "+381", R.drawable.flag_rs, "RSD"),
      new Country("RU", MyApplication.getInstance().getmActivity().getString(R.string.russia), "+7", R.drawable.flag_ru, "RUB"),
      new Country("RW", MyApplication.getInstance().getmActivity().getString(R.string.rwanda), "+250", R.drawable.flag_rw, "RWF"),
      new Country("SA", MyApplication.getInstance().getmActivity().getString(R.string.saudi_arabia), "+966", R.drawable.flag_sa, "SAR"),
      new Country("SB", MyApplication.getInstance().getmActivity().getString(R.string.solomon_islands), "+677", R.drawable.flag_sb, "SBD"),
      new Country("SC", MyApplication.getInstance().getmActivity().getString(R.string.seychelles), "+248", R.drawable.flag_sc, "SCR"),
      new Country("SD", MyApplication.getInstance().getmActivity().getString(R.string.sudan), "+249", R.drawable.flag_sd, "SDG"),
      new Country("SE", MyApplication.getInstance().getmActivity().getString(R.string.sweden), "+46", R.drawable.flag_se, "SEK"),
      new Country("SG", MyApplication.getInstance().getmActivity().getString(R.string.singapore), "+65", R.drawable.flag_sg, "SGD"),
      new Country("SH", MyApplication.getInstance().getmActivity().getString(R.string.saint_helena_ascensio_and_tristan_da_cunha), "+290", R.drawable.flag_sh,"SHP"),
      new Country("SI", MyApplication.getInstance().getmActivity().getString(R.string.slovenia), "+386", R.drawable.flag_si, "EUR"),
      new Country("SJ", MyApplication.getInstance().getmActivity().getString(R.string.svalbard_and_jan_mayen), "+47", R.drawable.flag_sj, "NOK"),
      new Country("SK", MyApplication.getInstance().getmActivity().getString(R.string.slovakia), "+421", R.drawable.flag_sk, "EUR"),
      new Country("SL", MyApplication.getInstance().getmActivity().getString(R.string.sierra_leone), "+232", R.drawable.flag_sl, "SLL"),
      new Country("SM", MyApplication.getInstance().getmActivity().getString(R.string.san_marino), "+378", R.drawable.flag_sm, "EUR"),
      new Country("SN", MyApplication.getInstance().getmActivity().getString(R.string.senegal), "+221", R.drawable.flag_sn, "XOF"),
      new Country("SO", MyApplication.getInstance().getmActivity().getString(R.string.somalia), "+252", R.drawable.flag_so, "SOS"),
      new Country("SR", MyApplication.getInstance().getmActivity().getString(R.string.suriname), "+597", R.drawable.flag_sr, "SRD"),
      new Country("SS", MyApplication.getInstance().getmActivity().getString(R.string.south_sudan), "+211", R.drawable.flag_ss, "SSP"),
      new Country("ST", MyApplication.getInstance().getmActivity().getString(R.string.sao_tome_and_principe), "+239", R.drawable.flag_st, "STD"),
      new Country("SV", MyApplication.getInstance().getmActivity().getString(R.string.salvador), "+503", R.drawable.flag_sv, "SVC"),
      new Country("SX", MyApplication.getInstance().getmActivity().getString(R.string.sint_maarten), "+1", R.drawable.flag_sx, "ANG"),
      new Country("SY", MyApplication.getInstance().getmActivity().getString(R.string.syrian_arab_republic), "+963", R.drawable.flag_sy, "SYP"),
      new Country("SZ", MyApplication.getInstance().getmActivity().getString(R.string.swaziland), "+268", R.drawable.flag_sz, "SZL"),
      new Country("TC", MyApplication.getInstance().getmActivity().getString(R.string.turks_and_caicos_islands), "+1", R.drawable.flag_tc, "USD"),
      new Country("TD", MyApplication.getInstance().getmActivity().getString(R.string.chad), "+235", R.drawable.flag_td, "XAF"),
      new Country("TF", MyApplication.getInstance().getmActivity().getString(R.string.french_southern_territories), "+262", R.drawable.flag_tf, "EUR"),
      new Country("TG", MyApplication.getInstance().getmActivity().getString(R.string.togo), "+228", R.drawable.flag_tg, "XOF"),
      new Country("TH", MyApplication.getInstance().getmActivity().getString(R.string.thailand), "+66", R.drawable.flag_th, "THB"),
      new Country("TJ", MyApplication.getInstance().getmActivity().getString(R.string.tajikistan), "+992", R.drawable.flag_tj, "TJS"),
      new Country("TK", MyApplication.getInstance().getmActivity().getString(R.string.tokelau), "+690", R.drawable.flag_tk, "NZD"),
      new Country("TL", MyApplication.getInstance().getmActivity().getString(R.string.east_timor), "+670", R.drawable.flag_tl, "USD"),
      new Country("TM", MyApplication.getInstance().getmActivity().getString(R.string.turkmenistan), "+993", R.drawable.flag_tm, "TMT"),
      new Country("TN", MyApplication.getInstance().getmActivity().getString(R.string.tunisia), "+216", R.drawable.flag_tn, "TND"),
      new Country("TO", MyApplication.getInstance().getmActivity().getString(R.string.tonga), "+676", R.drawable.flag_to, "TOP"),
      new Country("TR", MyApplication.getInstance().getmActivity().getString(R.string.turkey), "+90", R.drawable.flag_tr, "TRY"),
      new Country("TT", MyApplication.getInstance().getmActivity().getString(R.string.trinidad_and_tobago), "+1868", R.drawable.flag_tt, "TTD"),
      new Country("TV", MyApplication.getInstance().getmActivity().getString(R.string.tuvalu), "+688", R.drawable.flag_tv, "AUD"),
      new Country("TW", MyApplication.getInstance().getmActivity().getString(R.string.taiwan), "+886", R.drawable.flag_tw, "TWD"),
      new Country("TZ", MyApplication.getInstance().getmActivity().getString(R.string.tanzania), "+255", R.drawable.flag_tz, "TZS"),
      new Country("UA", MyApplication.getInstance().getmActivity().getString(R.string.ukraine), "+380", R.drawable.flag_ua, "UAH"),
      new Country("UG", MyApplication.getInstance().getmActivity().getString(R.string.uganda), "+256", R.drawable.flag_ug, "UGX"),
      new Country("UM", MyApplication.getInstance().getmActivity().getString(R.string.minor_outlying), "+1", R.drawable.flag_um, "USD"),
      new Country("US", MyApplication.getInstance().getmActivity().getString(R.string.usa), "+1", R.drawable.flag_us, "USD"),
      new Country("UY", MyApplication.getInstance().getmActivity().getString(R.string.uruguay), "+598", R.drawable.flag_uy, "UYU"),
      new Country("UZ", MyApplication.getInstance().getmActivity().getString(R.string.uzbekistan), "+998", R.drawable.flag_uz, "UZS"),
      new Country("VA", MyApplication.getInstance().getmActivity().getString(R.string.vatican_city), "+379", R.drawable.flag_va, "EUR"),
      new Country("VC", MyApplication.getInstance().getmActivity().getString(R.string.vincent_grenadines), "+1", R.drawable.flag_vc, "XCD"),
      new Country("VE", MyApplication.getInstance().getmActivity().getString(R.string.venenzuala), "+58", R.drawable.flag_ve, "VEF"),
      new Country("VG", MyApplication.getInstance().getmActivity().getString(R.string.virgin_islands_british), "+1", R.drawable.flag_vg, "USD"),
      new Country("VI", MyApplication.getInstance().getmActivity().getString(R.string.virgin_islands_us), "+1", R.drawable.flag_vi, "USD"),
      new Country("VN", MyApplication.getInstance().getmActivity().getString(R.string.vietnam), "+84", R.drawable.flag_vn, "VND"),
      new Country("VU", MyApplication.getInstance().getmActivity().getString(R.string.vanuatu), "+678", R.drawable.flag_vu, "VUV"),
      new Country("WF", MyApplication.getInstance().getmActivity().getString(R.string.wallis_futuna), "+681", R.drawable.flag_wf, "XPF"),
      new Country("WS", MyApplication.getInstance().getmActivity().getString(R.string.samoa), "+685", R.drawable.flag_ws, "WST"),
      new Country("XK", MyApplication.getInstance().getmActivity().getString(R.string.kosovo), "+383", R.drawable.flag_xk, "EUR"),
      new Country("YE", MyApplication.getInstance().getmActivity().getString(R.string.yemen), "+967", R.drawable.flag_ye, "YER"),
      new Country("YT", MyApplication.getInstance().getmActivity().getString(R.string.mayotte), "+262", R.drawable.flag_yt, "EUR"),
      new Country("ZA", MyApplication.getInstance().getmActivity().getString(R.string.south_africa_), "+27", R.drawable.flag_za, "ZAR"),
      new Country("ZM", MyApplication.getInstance().getmActivity().getString(R.string.zambia), "+260", R.drawable.flag_zm, "ZMW"),
      new Country("ZW", MyApplication.getInstance().getmActivity().getString(R.string.zimbabwe), "+263", R.drawable.flag_zw, "USD"),
  };
  // endregion

  // region Variables
  public static final int SORT_BY_NONE = 0;
  public static final int SORT_BY_NAME = 1;
  public static final int SORT_BY_ISO = 2;
  public static final int SORT_BY_DIAL_CODE = 3;
  private static final String COUNTRY_TAG = "COUNTRY_PICKER";

  private Context context;
  private int sortBy = SORT_BY_NONE;
  private OnCountryPickerListener onCountryPickerListener;
  private boolean canSearch = true;

  private List<Country> countries;
  // endregion

  // region Constructors
  private CountryPicker() {
  }

  CountryPicker(Builder builder) {
    sortBy = builder.sortBy;
    onCountryPickerListener = builder.onCountryPickerListener;
    context = builder.context;
    canSearch = builder.canSearch;
    countries = new ArrayList<>(Arrays.asList(COUNTRIES));
    sortCountries(countries);
  }
  // endregion

  public void filterCountries(ArrayList<String> codesToFilter){
    if(codesToFilter != null){
      ArrayList<Country> countriesF = new ArrayList<>();
      countries = new ArrayList<>(Arrays.asList(COUNTRIES));
      for(Country country : countries){
        if(codesToFilter.contains(country.getCode())){
          countriesF.add(country);
        }
      }
      countries = countriesF;
      sortCountries(countries);
    }
  }

  // region Listeners
  @Override
  public void sortCountries(@NonNull List<Country> countries) {
    switch (sortBy) {
      case SORT_BY_NAME:
        Collections.sort(countries, new Comparator<Country>() {
          @Override
          public int compare(Country country1, Country country2) {
            return country1.getName().trim().compareToIgnoreCase(country2.getName().trim());
          }
        });
      case SORT_BY_ISO:
        Collections.sort(countries, new Comparator<Country>() {
          @Override
          public int compare(Country country1, Country country2) {
            return country1.getCode().trim().compareToIgnoreCase(country2.getCode().trim());
          }
        });
      case SORT_BY_DIAL_CODE:
        Collections.sort(countries, new Comparator<Country>() {
          @Override
          public int compare(Country country1, Country country2) {
            return country1.getDialCode().trim().compareToIgnoreCase(country2.getDialCode().trim());
          }
        });
    }
  }

  @Override
  public List<Country> getAllCountries() {
    return countries;
  }

  @Override
  public boolean canSearch() {
    return canSearch;
  }

  // endregion

  // region Utility Methods
  public void showDialog(@NonNull FragmentManager supportFragmentManager) {
    if (countries == null || countries.isEmpty()) {
      throw new IllegalArgumentException(context.getString(R.string.error_no_countries_found));
    } else {
      CountryPickerDialog countryPickerDialog = CountryPickerDialog.newInstance();
      countryPickerDialog.setCountryPickerListener(onCountryPickerListener);
      countryPickerDialog.setDialogInteractionListener(this);
      countryPickerDialog.show(supportFragmentManager, COUNTRY_TAG);
    }
  }

  public void setCountries(@NonNull List<Country> countries) {
    this.countries.clear();
    this.countries.addAll(countries);
    sortCountries(this.countries);
  }

  public static Country getCountryFromSIM(@NonNull Context context) {
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager != null
        && telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
      return getCountryByISO(telephonyManager.getSimCountryIso());
    }
    return null;
  }

  public Country getCountryByLocale(@NonNull Locale locale) {
    String countryIsoCode = locale.getISO3Country().substring(0, 2).toLowerCase();
    return getCountryByISO(countryIsoCode);
  }

  public Country getCountryByName(@NonNull String countryName) {
    countryName = countryName.toUpperCase();
    Country country = new Country();
    country.setName(countryName);
    int i = Arrays.binarySearch(COUNTRIES, country, new NameComparator());
    if (i < 0) {
      return null;
    } else {
      return COUNTRIES[i];
    }
  }

  public static Country getCountryByISO(@NonNull String countryIsoCode) {
    countryIsoCode = countryIsoCode.toUpperCase();
    Country country = new Country();
    country.setCode(countryIsoCode);
    int i = Arrays.binarySearch(COUNTRIES, country, new ISOCodeComparator());
    if (i < 0) {
      return null;
    } else {
      return COUNTRIES[i];
    }
  }
  public static Country getCountryByDialCode(@NonNull String countryDialCode) {
    Country country = new Country();
    country.setDialCode(countryDialCode);
    int i = Arrays.binarySearch(COUNTRIES, country, new DialCodeComparator());
    if (i < 0) {
      return null;
    } else {
      return COUNTRIES[i];
    }
  }
  // endregion

  // region Builder
  public static class Builder {
    private Context context;
    private int sortBy = SORT_BY_NONE;
    private boolean canSearch = true;
    private OnCountryPickerListener onCountryPickerListener;

    public Builder with(@NonNull Context context) {
      this.context = context;
      return this;
    }

    public Builder sortBy(@NonNull int sortBy) {
      this.sortBy = sortBy;
      return this;
    }

    public Builder listener(@NonNull OnCountryPickerListener onCountryPickerListener) {
      this.onCountryPickerListener = onCountryPickerListener;
      return this;
    }

    public Builder canSearch(@NonNull boolean canSearch) {
      this.canSearch = canSearch;
      return this;
    }

    public CountryPicker build() {
      if (onCountryPickerListener == null) {
        throw new IllegalArgumentException(
            context.getString(R.string.error_listener_not_set));
      }
      return new CountryPicker(this);
    }
  }
  // endregion

  // region Comparators
  public static class ISOCodeComparator implements Comparator<Country> {
    @Override
    public int compare(Country country, Country nextCountry) {
      return country.getCode().compareTo(nextCountry.getCode());
    }
  }
  public static class DialCodeComparator implements Comparator<Country> {
    @Override
    public int compare(Country country, Country nextCountry) {
      return country.getDialCode().compareTo(nextCountry.getDialCode());
    }
  }

  public static class NameComparator implements Comparator<Country> {
    @Override
    public int compare(Country country, Country nextCountry) {
      return country.getName().compareTo(nextCountry.getName());
    }
  }
  // endregion
}
