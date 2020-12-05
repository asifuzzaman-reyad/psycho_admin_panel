package com.reyad.psycheadminpanel.main.teacher

data class TeacherItemList(
    val name: String,
    val post: String,
    val phd: String,
    val facebook: String,
    val email: String,
    val mobile: String,
    val publication: String,
    val interest: String,
    val imageUrl: String,
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )
}
