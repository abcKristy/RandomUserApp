package com.example.horseinacoat.presentation.viewModel.custom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.horseinacoat.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeViewModel @Inject constructor() : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _resumeData = MutableStateFlow(ResumeData())
    val resumeData: StateFlow<ResumeData> = _resumeData.asStateFlow()

    init {
        loadResumeData()
    }

    private fun loadResumeData() {
        viewModelScope.launch {
            _isLoading.value = true

            _resumeData.value = createSampleResumeData()

            _isLoading.value = false
        }
    }

    private fun createSampleResumeData(): ResumeData {
        return ResumeData(
            name = "Kristina Olegovna",
            position = "Junior Mobile Developer (Android)",
            photoRes = R.drawable.res_photo,
            profile = "Android developer specializing in Kotlin, Jetpack Compose, and modern mobile architectures. Passionate about creating intuitive user interfaces and implementing cutting-edge technologies.",
            aboutMe = ResumeData.AboutMe(
                birthDate = "22.03.2005",
                gender = "Female",
                education = "Bachelor's Degree",
                citizenship = "Russia",
                maritalStatus = "Single"
            ),
            contact = ResumeData.Contact(
                phone = "+7(918)975-33-09",
                telegram = "@j_kristy",
                email = "loluta2005@mail.ru",
                github = "https://github.com/abcKristy"
            ),
            workPreferences = ResumeData.WorkPreferences(
                desiredSalary = "Negotiable",
                schedule = "Flexible, Remote",
                employment = "Part-time, Internship (up to 30h)"
            ),
            education = listOf(
                ResumeData.Education(
                    institution = "RTU MIREA",
                    period = "September 2023 - June 2027",
                    degree = "Bachelor of Software Engineering",
                    faculty = "Institute of Information Technologies"
                )
            ),
            skills = listOf(
                "English (Intermediate)",
                "German (Pre-Intermediate)",
                "Friendly, courteous, customer-oriented",
                "Maintains professionalism in stressful situations",
                "Creative, initiative-driven",
                "Organizational abilities",
                "Excellent verbal and written communication skills"
            ),
            technicalSkills = ResumeData.TechnicalSkills(
                mobileDevelopment = listOf(
                    "Kotlin", "Jetpack Compose", "Coroutines", "Clean Architecture",
                    "SOLID principles", "OOP", "Design Patterns (Factory, Observer, Adapter, Singleton)",
                    "Room ORM", "Retrofit", "REST API", "Complex Animations",
                    "Dagger Hilt", "WebSocket", "MVVM/MVI/MVP", "Firebase",
                    "Navigation Component", "Kotlin Flow",
                    "Figma", "Postman"
                ),
                backendDevelopment = listOf(
                    "Java", "Spring Boot", "Spring Core", "Spring Security",
                    "PostgreSQL", "MySQL", "Hibernate", "JPA", "Git", "Docker"
                ),
                dataScience = listOf(
                    "Python", "pandas", "NumPy", "matplotlib", "seaborn",
                    "scikit-learn", "TensorFlow", "Keras", "PyTorch", "CatBoost"
                )
            ),
            additionalInfo = listOf(
                "Graduated with honors from the digital department \"Software Tools for Applied AI Problems\"",
                "Completed Samsung Academy course \"Neural Networks: Computer Vision and Text Processing\"",
                "Comprehensive technology stack for Android mobile development"
            ),
            hobbies = listOf(
                "Studying various process automation systems",
                "Reading fiction and scientific literature",
                "Active sports: snowboarding and skateboarding"
            )
        )
    }
}

data class ResumeData(
    val name: String = "",
    val position: String = "",
    val photoRes: Int = 0,
    val profile: String = "",
    val aboutMe: AboutMe = AboutMe(),
    val contact: Contact = Contact(),
    val workPreferences: WorkPreferences = WorkPreferences(),
    val education: List<Education> = emptyList(),
    val skills: List<String> = emptyList(),
    val technicalSkills: TechnicalSkills = TechnicalSkills(),
    val additionalInfo: List<String> = emptyList(),
    val hobbies: List<String> = emptyList()
) {
    data class AboutMe(
        val birthDate: String = "",
        val gender: String = "",
        val education: String = "",
        val citizenship: String = "",
        val maritalStatus: String = ""
    )

    data class Contact(
        val phone: String = "",
        val telegram: String = "",
        val email: String = "",
        val github: String = ""
    )

    data class WorkPreferences(
        val desiredSalary: String = "",
        val schedule: String = "",
        val employment: String = ""
    )

    data class Education(
        val institution: String = "",
        val period: String = "",
        val degree: String = "",
        val faculty: String = ""
    )

    data class TechnicalSkills(
        val mobileDevelopment: List<String> = emptyList(),
        val backendDevelopment: List<String> = emptyList(),
        val dataScience: List<String> = emptyList()
    )
}