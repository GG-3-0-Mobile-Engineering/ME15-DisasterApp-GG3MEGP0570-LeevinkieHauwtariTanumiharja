package com.gigih.disastermap.data

enum class ExistingProvince(val code: String, val provinceName: String) {
    Aceh("ID-AC", "Aceh"),
    Bali("ID-BA", "Bali"),
    KepBangkaBelitung("ID-BB", "Kep Bangka Belitung"),
    Banten("ID-BT", "Banten"),
    Bengkulu("ID-BE", "Bengkulu"),
    JawaTengah("ID-JT", "Jawa tengah"),
    KalimantanTengah("ID-KT", "Kalimantan Tengah"),
    SulawesiTengah("ID-ST", "Sulawesi Tengah"),
    JawaTimur("ID-JI", "Jawa Timur"),
    KalimantanTimur("ID-KI", "Kalimantan Timur"),
    NusaTenggaraTimur("ID-NT", "Nusa Tenggara Timur"),
    Gorontalo("ID-GO", "Gorontalo"),
    DKIJakarta("ID-JK", "DKI Jakarta"),
    Jambi("ID-JA", "Jambi"),
    Lampung("ID-LA", "Lampung"),
    Maluku("ID-MA", "Maluku"),
    KalimantanUtara("ID-KU", "Kalimantan Utara"),
    MalukuUtara("ID-MU", "Maluku Utara"),
    SulawesiUtara("ID-SA", "Sulawesi Utara"),
    SumateraUtara("ID-SU", "Sumatera Utara"),
    Papua("ID-PA", "Papua"),
    Riau("ID-RI", "Riau"),
    KepulauanRiau("ID-KR", "Kepulauan Riau"),
    SulawesiTenggara("ID-SG", "Sulawesi Tenggara"),
    KalimantanSelatan("ID-KS", "Kalimantan Selatan"),
    SulawesiSelatan("ID-SN", "Sulawesi Selatan"),
    SumateraSelatan("ID-SS", "Sumatera Selatan"),
    DIYogyakarta("ID-YO", "DI Yogyakarta"),
    JawaBarat("ID-JB", "Jawa Barat"),
    KalimantanBarat("ID-KB", "Kalimantan Barat"),
    NusaTenggaraBarat("ID-NB", "Nusa Tenggara Barat"),
    PapuaBarat("ID-PB", "Papua Barat"),
    SulawesiBarat("ID-SR", "Sulawesi Barat"),
    SumateraBarat("ID-SB", "Sumatera Barat"),
    Unknown("", "Unknown");
    companion object {
        fun getProvinceByCode(code: String): ExistingProvince {
            return values().find { it.code == code } ?: Unknown
        }
    }
}