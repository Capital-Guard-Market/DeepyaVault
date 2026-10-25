package com.deepya.vault

data class SubjectItem(
    val code: String,
    val displayName: String,
    val category: String
)

data class CurriculumOption(
    val id: String,
    val name: String
)

val SCHOOL_BOARDS_LIST = listOf(
    CurriculumOption("CBSE_12", "CBSE Grade 11-12"),
    CurriculumOption("ISC_12", "ISC / ICSE Grade 11-12"),
    CurriculumOption("CAMBRIDGE_AL", "Cambridge A-Levels")
)

val COMPETITIVE_TRACKS_LIST = listOf(
    CurriculumOption("JEE_MAIN_ADV", "JEE (Main & Adv)"),
    CurriculumOption("NEET_UG", "NEET-UG"),
    CurriculumOption("BITSAT", "BITSAT"),
    CurriculumOption("IPMAT", "IPMAT (IIM Indore)"),
    CurriculumOption("CLAT", "CLAT (Law)"),
    CurriculumOption("CA_FOUNDATION", "CA Foundation"),
    CurriculumOption("CUET", "CUET Entrance")
)

val ALL_SUBJECTS_LIST = listOf(
    // CBSE / National Subjects
    SubjectItem("055", "Accountancy (CBSE)", "Commerce"),
    SubjectItem("054", "Business Studies (CBSE)", "Commerce"),
    SubjectItem("030", "Economics (CBSE)", "Commerce / Humanities"),
    SubjectItem("041", "Mathematics (CBSE)", "Science / Commerce"),
    SubjectItem("042", "Physics (CBSE)", "Science"),
    SubjectItem("043", "Chemistry (CBSE)", "Science"),
    SubjectItem("044", "Biology (CBSE)", "Science"),
    SubjectItem("083", "Computer Science (CBSE)", "Science"),
    SubjectItem("065", "Informatics Practices (CBSE)", "Commerce / Science"),
    SubjectItem("301", "English Core (CBSE)", "Language"),

    // ISC / ICSE Grade 11-12 Subjects
    SubjectItem("858", "Accounts (ISC)", "ISC Commerce"),
    SubjectItem("854", "Commerce (ISC)", "ISC Commerce"),
    SubjectItem("856", "Economics (ISC)", "ISC Commerce / Humanities"),
    SubjectItem("860", "Mathematics (ISC)", "ISC Science / Commerce"),
    SubjectItem("861", "Physics (ISC)", "ISC Science"),
    SubjectItem("862", "Chemistry (ISC)", "ISC Science"),
    SubjectItem("863", "Biology (ISC)", "ISC Science"),
    SubjectItem("868", "Computer Science (ISC)", "ISC Science"),
    SubjectItem("801", "English Language (ISC)", "ISC Language"),
    SubjectItem("855", "Business Studies (ISC)", "ISC Commerce"),

    // Cambridge International A-Level / IGCSE Subjects
    SubjectItem("9706", "Accounting (Cambridge A-Level)", "Cambridge Commerce"),
    SubjectItem("9609", "Business (Cambridge A-Level)", "Cambridge Commerce"),
    SubjectItem("9708", "Economics (Cambridge A-Level)", "Cambridge Humanities"),
    SubjectItem("9709", "Mathematics (Cambridge A-Level)", "Cambridge Science"),
    SubjectItem("9701", "Chemistry (Cambridge A-Level)", "Cambridge Science"),
    SubjectItem("9702", "Physics (Cambridge A-Level)", "Cambridge Science"),
    SubjectItem("9700", "Biology (Cambridge A-Level)", "Cambridge Science"),
    SubjectItem("9618", "Computer Science (Cambridge A-Level)", "Cambridge Science"),
    SubjectItem("9489", "History (Cambridge A-Level)", "Cambridge Humanities"),
    SubjectItem("9699", "Sociology (Cambridge A-Level)", "Cambridge Humanities"),
    SubjectItem("9990", "Psychology (Cambridge A-Level)", "Cambridge Humanities"),
    SubjectItem("9696", "Geography (Cambridge A-Level)", "Cambridge Humanities"),
    SubjectItem("9093", "English Language (Cambridge A-Level)", "Cambridge Language"),

    // National Humanities & General
    SubjectItem("027", "History (CBSE)", "Humanities"),
    SubjectItem("028", "Political Science (CBSE)", "Humanities"),
    SubjectItem("029", "Geography (CBSE)", "Humanities"),
    SubjectItem("037", "Psychology (CBSE)", "Humanities"),
    SubjectItem("039", "Sociology (CBSE)", "Humanities"),
    SubjectItem("048", "Physical Education", "General")
)