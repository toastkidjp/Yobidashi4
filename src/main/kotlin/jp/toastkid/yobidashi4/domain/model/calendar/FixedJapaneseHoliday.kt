package jp.toastkid.yobidashi4.domain.model.calendar

enum class FixedJapaneseHoliday(val month: Int, val date: Int, japaneseTitle: String) {
    NATIONAL_FOUNDATION_DAY(2, 11, "建国記念の日"),
    EMPERORS_BIRTHDAY(2, 23, "天皇誕生日"),
    SHOWA_DAY(4,29, "昭和の日"),
    CONSTITUTION_MEMORIAL_DAY(5, 3, "憲法記念日"),
    GREENERY_DAY(5, 4, "みどりの日"),
    CHILDREN_S_DAY(5, 5, "こどもの日"),
    MOUNTAIN_DAY(8, 11, "山の日"),
    CULTURE_DAY(11, 3, "文化の日"),
    LABOR_THANKSGIVING_DAY(11, 23, "勤労感謝の日"),
    ;
}