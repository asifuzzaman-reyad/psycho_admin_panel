package com.reyad.psycheadminpanel.main.student


data class StudentItems(
    val name: String,
    val session: String,
    val id: String,
    val mobile: String,
    val imageUrl: String
) {
    constructor() : this("", "", "", "", "")
}