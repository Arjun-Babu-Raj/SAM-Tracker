package com.example.ui

sealed class FormField(val id: String, val label: String) {
    class Text(id: String, label: String, val isNumber: Boolean = false) : FormField(id, label)
    class Dropdown(id: String, label: String, val options: List<String>) : FormField(id, label)
    class Checkbox(id: String, label: String) : FormField(id, label)
}

object QuestionBank {
    val basicSociodemographic = listOf(
        FormField.Text("caregiver_age", "Primary Caregiver Age", true),
        FormField.Dropdown("caregiver_edu", "Primary Caregiver Education", listOf("No schooling", "Primary", "Secondary", "Higher")),
        FormField.Dropdown("caregiver_occ", "Primary Caregiver Occupation", listOf("Homemaker", "Labour", "Agriculture", "Service", "Other")),
        FormField.Dropdown("family_type", "Family Type", listOf("Nuclear", "Joint", "Three-generation / Extended")),
        FormField.Dropdown("migration_status", "Migration Status", listOf("No migration", "Seasonal migration", "Permanent migration"))
    )

    val maternalAndEarlyLife = listOf(
        FormField.Dropdown("place_of_delivery", "Place of Delivery", listOf("Home", "Government facility", "Private facility")),
        FormField.Text("birth_weight", "Birth Weight (kg)", true),
        FormField.Dropdown("preterm_birth", "Preterm Birth?", listOf("Yes", "No", "Don't know")),
        FormField.Dropdown("anc_visits", "Number of ANC Visits", listOf("0-3", ">=4", "Don't know"))
    )

    val morbidity = listOf(
        FormField.Dropdown("fever", "Fever in last 2 weeks?", listOf("Yes", "No")),
        FormField.Dropdown("diarrhea", "Diarrhea in last 2 weeks?", listOf("Yes", "No")),
        FormField.Dropdown("ari", "ARI in last 2 weeks?", listOf("Yes", "No")),
        FormField.Dropdown("hospitalization", "Any hospitalization?", listOf("Yes", "No"))
    )

    val feeding = listOf(
        FormField.Dropdown("currently_breastfed", "Currently breastfed?", listOf("Yes", "No")),
        FormField.Text("meals_yesterday", "Number of meals given yesterday", true),
        FormField.Dropdown("responsive_feeding", "Caregiver actively encourages eating?", listOf("Yes", "No"))
    )
    
    val healthSeeking = listOf(
        FormField.Dropdown("seek_care_first", "Where do you seek care first?", listOf("Government facility", "Private provider", "AYUSH provider", "Traditional healer", "No care sought")),
        FormField.Dropdown("time_taken_care", "Time taken to seek care after illness onset", listOf("Same day", "1-2 days", ">2 days"))
    )

    val wash = listOf(
        FormField.Dropdown("toilet_facility", "Toilet facility", listOf("Private", "Public", "Open defecation")),
        FormField.Dropdown("handwashing_before_food", "Handwashing before food", listOf("Water only", "Soap & water", "Not practiced"))
    )

    val knowledge = listOf(
        FormField.Dropdown("heard_sam", "Heard of SAM?", listOf("Yes", "No")),
        FormField.Dropdown("heard_csam", "Heard of CSAM programme?", listOf("Yes", "No"))
    )

    val baselineOnly = listOf(
        FormField.Dropdown("season", "Current season at baseline", listOf("Summer", "Monsoon", "Winter")),
        FormField.Text("awc_visits_12w", "Number of AWW visits in last 12 weeks", true),
        FormField.Dropdown("referred_nrc", "Referred to NRC during treatment?", listOf("Yes", "No"))
    )
    
    val followupOnly = listOf(
        FormField.Dropdown("caregiver_changed", "Since last visit, caregiver changed?", listOf("Yes", "No")),
        FormField.Dropdown("migrated_temp", "Since last visit, migrated temporarily?", listOf("Yes", "No")),
        FormField.Dropdown("major_event", "Major household event occurred?", listOf("Serious illness", "Death in family", "Income loss", "Migration", "None", "Other"))
    )

    val allBaselineFields = basicSociodemographic + maternalAndEarlyLife + morbidity + feeding + healthSeeking + wash + knowledge + baselineOnly
    val allFollowupFields = followupOnly + morbidity + feeding + healthSeeking + wash + knowledge
}
