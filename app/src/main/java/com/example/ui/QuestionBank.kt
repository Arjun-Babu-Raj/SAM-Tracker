package com.example.ui

sealed class FormField(val id: String, val label: String) {
    class Text(id: String, label: String, val isNumber: Boolean = false) : FormField(id, label)
    class Dropdown(id: String, label: String, val options: List<String>) : FormField(id, label)
    class Checkbox(id: String, label: String) : FormField(id, label)
}

object QuestionBank {
    val basicSociodemographic = listOf(
        FormField.Text("child_age_months", "Child Age (completed months)", true),
        FormField.Dropdown("age_category", "Age Category", listOf("6-23 months", "24-59 months")),
        FormField.Text("birth_order", "Birth order", true),
        FormField.Text("num_siblings", "Number of siblings", true),
        FormField.Dropdown("primary_caregiver", "Primary Caregiver", listOf("Mother", "Father", "Grandmother", "Grandfather", "Other")),
        FormField.Text("caregiver_age", "Primary Caregiver Age", true),
        FormField.Text("mother_age", "Mother Age", true),
        FormField.Dropdown("mother_edu", "Mother Education", listOf("No schooling", "Primary", "Secondary", "Higher")),
        FormField.Dropdown("mother_occ", "Mother Occupation", listOf("Homemaker", "Labour", "Agriculture", "Service", "Other")),
        FormField.Text("mother_income", "Mother Income", true),
        FormField.Dropdown("mother_chronic", "Mother Chronic Illness?", listOf("Yes", "No")),
        FormField.Dropdown("mother_anaemia", "Mother Anaemia?", listOf("Yes", "No", "Unknown")),
        FormField.Text("father_age", "Father Age", true),
        FormField.Dropdown("father_edu", "Father Education", listOf("No schooling", "Primary", "Secondary", "Higher")),
        FormField.Dropdown("father_occ", "Father Occupation", listOf("Labour", "Agriculture", "Service", "Business", "Other")),
        FormField.Text("father_income", "Father Income", true),
        FormField.Dropdown("decides_feeding", "Who decides child feeding?", listOf("Mother", "Father", "Grandparent", "Joint")),
        FormField.Dropdown("decides_health", "Who decides health seeking?", listOf("Mother", "Father", "Grandparent", "Joint")),
        FormField.Dropdown("facility_alone", "Can mother take child alone?", listOf("Yes", "No")),
        FormField.Dropdown("family_type", "Family Type", listOf("Nuclear", "Joint", "Three-generation / Extended")),
        FormField.Dropdown("main_income_source", "Main Source of Income", listOf("Daily wage", "Agriculture", "Government", "Private", "Business", "Other")),
        FormField.Dropdown("migration_status", "Migration Status", listOf("No migration", "Seasonal migration", "Permanent migration"))
    )

    val maternalAndEarlyLife = listOf(
        FormField.Dropdown("place_of_delivery", "Place of Delivery", listOf("Home", "Government facility", "Private facility")),
        FormField.Text("birth_weight", "Birth Weight (kg)", true),
        FormField.Dropdown("preterm_birth", "Preterm Birth?", listOf("Yes", "No", "Don't know")),
        FormField.Dropdown("anc_visits", "Number of ANC Visits", listOf("0-3", ">=4", "Don't know")),
        FormField.Dropdown("complications_preg", "Complications during pregnancy?", listOf("Yes", "No", "Don't know")),
        FormField.Dropdown("complications_deliv", "Complications during delivery?", listOf("Yes", "No", "Don't know"))
    )

    val morbidity = listOf(
        FormField.Dropdown("fever", "Fever in last 2 weeks?", listOf("Yes", "No")),
        FormField.Dropdown("diarrhea", "Diarrhea in last 2 weeks?", listOf("Yes", "No")),
        FormField.Dropdown("ari", "ARI in last 2 weeks?", listOf("Yes", "No")),
        FormField.Dropdown("other_illness", "Any other diagnosed illness?", listOf("Yes", "No")),
        FormField.Dropdown("hospitalization", "Any hospitalization?", listOf("Yes", "No")),
        FormField.Dropdown("immunized", "Completely immunized for age?", listOf("Yes", "No", "Unknown"))
    )

    val feeding = listOf(
        FormField.Dropdown("main_food", "Main food consumed", listOf("Breastfeeding", "Complementary", "THR", "Other")),
        FormField.Dropdown("started_bf", "How soon breastfed", listOf("Within 1 hour", "After 1 hour", "Not breastfed")),
        FormField.Dropdown("excl_bf", "Exclusively breastfed 6m?", listOf("Yes", "No")),
        FormField.Dropdown("currently_breastfed", "Currently breastfed?", listOf("Yes", "No")),
        FormField.Text("age_cmp_feed", "Age complementary feeding (m)", true),
        FormField.Dropdown("thr_regular", "THR received?", listOf("Regular", "Irregular", "Not received")),
        FormField.Dropdown("thr_type", "THR Type provided", listOf("Balahar", "Khichdi", "Halwa", "Other")),
        FormField.Dropdown("thr_prep", "THR preparation?", listOf("As provided", "Cooked as recommended", "Modified", "Shared")),
        FormField.Text("meals_yesterday", "Number of meals given yesterday", true),
        FormField.Dropdown("foods_restricted", "Any foods restricted?", listOf("Yes", "No")),
        FormField.Dropdown("foods_worsen", "Believe foods worsen illness?", listOf("Yes", "No")),
        FormField.Dropdown("responsive_feeding", "Caregiver actively encourages eating?", listOf("Yes", "No")),
        FormField.Dropdown("separate_plate", "Eats from separate plate?", listOf("Yes", "No")),
        FormField.Dropdown("who_feeds", "Who usually feeds?", listOf("Child independent", "Mother", "Other caregiver", "Mixed")),
        FormField.Dropdown("distracted_feeding", "Distracted during feeding?", listOf("Yes", "No")),
        FormField.Dropdown("refuse_action", "Action if refuses?", listOf("Stops", "Tries later", "Coaxes", "Forces", "Other")),
        FormField.Dropdown("diff_feeding", "Any difficulty feeding?", listOf("Yes", "No"))
    )
    
    val healthSeeking = listOf(
        FormField.Dropdown("seek_care_first", "Where do you seek care first?", listOf("Government facility", "Private provider", "AYUSH provider", "Traditional healer", "No care sought")),
        FormField.Dropdown("time_taken_care", "Time taken to seek care after illness onset", listOf("Same day", "1-2 days", ">2 days")),
        FormField.Dropdown("barriers", "Barriers to access", listOf("Cost", "Distance", "Time", "Awareness", "Other")),
        FormField.Dropdown("dist_awc", "Distance to AWC", listOf("<1 km", "1-5 km", ">5 km")),
        FormField.Dropdown("dist_health", "Distance to facility", listOf("<1 km", "1-5 km", ">5 km"))
    )

    val wash = listOf(
        FormField.Dropdown("water_source", "Drinking water source", listOf("Tap", "Handpump", "Well", "Other")),
        FormField.Dropdown("toilet_facility", "Toilet facility", listOf("Private", "Public", "Open defecation")),
        FormField.Dropdown("handwashing_before_food", "Handwashing before food", listOf("Water only", "Soap & water", "Not practiced")),
        FormField.Dropdown("handwashing_after_food", "Handwashing after food", listOf("Water only", "Soap & water", "Not practiced"))
    )

    val knowledge = listOf(
        FormField.Dropdown("heard_sam", "Heard of SAM?", listOf("Yes", "No")),
        FormField.Dropdown("heard_csam", "Heard of CSAM programme?", listOf("Yes", "No")),
        FormField.Dropdown("know_thr", "Know how THR given?", listOf("Yes", "No"))
    )

    val baselineOnly = listOf(
        FormField.Text("past_sam_episodes", "Total prior SAM/MAM episodes", true),
        FormField.Dropdown("month_enrolment", "Month of enrollment", listOf("Month 1", "Month 2", "Month 3")),
        FormField.Dropdown("baseline_timing", "Baseline Timing Category", listOf("Month 4", "Month 5", "Month 6")),
        FormField.Dropdown("season", "Current season at baseline", listOf("Summer", "Monsoon", "Winter")),
        FormField.Dropdown("oedema", "Bilateral pitting oedema", listOf("Present", "Absent")),
        FormField.Dropdown("appetite_test", "Appetite test", listOf("Passed", "Failed")),
        FormField.Dropdown("appetite_status", "Nutritional status at baseline", listOf("SAM", "MAM", "Normal")),
        FormField.Dropdown("aww_visits", "AWW Home Visits", listOf("Weekly", "Fortnightly", "Monthly", "None")),
        FormField.Text("awc_visits_12w", "Number of AWW visits in last 12 weeks", true),
        FormField.Dropdown("counselling", "Nutrition counselling provided?", listOf("Yes regularly", "Sometimes", "No")),
        FormField.Dropdown("referred_nrc", "Referred to NRC during treatment?", listOf("Yes", "No")),
        FormField.Dropdown("attended_follow", "Caregiver attended scheduled visits", listOf("Yes", "No"))
    )
    
    val followupOnly = listOf(
        FormField.Text("child_age_months", "Child Age (completed months)", true),
        FormField.Dropdown("caregiver_changed", "Since last visit, caregiver changed?", listOf("Yes", "No")),
        FormField.Dropdown("migrated_temp", "Since last visit, migrated temporarily?", listOf("Yes", "No")),
        FormField.Dropdown("major_event", "Major household event occurred?", listOf("Serious illness", "Death in family", "Income loss", "Migration", "None", "Other")),
        FormField.Dropdown("oedema", "Bilateral pitting oedema", listOf("Present", "Absent")),
        FormField.Dropdown("appetite_change", "Recent change in appetite", listOf("Improved", "Same", "Reduced")),
        FormField.Dropdown("nutritional_status", "Current nutritional status", listOf("SAM", "MAM", "Normal")),
        FormField.Dropdown("outcome_since", "Outcome since last visit", listOf("Relapse to SAM", "Remained SAM", "Improved MAM", "Recovered", "Died", "No change")),
        FormField.Dropdown("reenrolled", "Re-enrolled in CSAM", listOf("Yes", "No")),
        FormField.Dropdown("aww_visits", "AWW Home Visits", listOf("Weekly", "Fortnightly", "Monthly", "None")),
        FormField.Dropdown("counselling", "Nutrition counselling provided?", listOf("Yes regularly", "Sometimes", "No")),
        FormField.Dropdown("referred_nrc", "Referred to NRC since last visit?", listOf("Yes", "No"))
    )

    val allBaselineFields = basicSociodemographic + maternalAndEarlyLife + morbidity + feeding + healthSeeking + wash + knowledge + baselineOnly
    val allFollowupFields = followupOnly + morbidity + feeding + healthSeeking + wash + knowledge
}
