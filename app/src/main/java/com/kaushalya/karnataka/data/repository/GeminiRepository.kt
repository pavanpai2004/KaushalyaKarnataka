package com.kaushalya.karnataka.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.kaushalya.karnataka.BuildConfig
import com.kaushalya.karnataka.data.model.Service
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepository @Inject constructor() {

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    private val isAvailable: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "YOUR_GEMINI_API_KEY"

    suspend fun generateWorkerBio(
        workerName: String,
        tradeCategory: String,
        services: List<Service>
    ): Result<String> = runCatching {
        if (!isAvailable) throw Exception("AI feature not configured. Add GEMINI_API_KEY to local.properties")
        val servicesList = services.joinToString(", ") { it.serviceName }
        val prompt = """
            You are writing a professional profile bio for a skilled blue-collar worker on a local services app in Karnataka, India.
            
            Worker Details:
            - Name: $workerName
            - Trade: $tradeCategory
            - Services offered: $servicesList
            
            Write a short, professional 3-sentence bio in English that:
            1. Introduces the worker and their trade
            2. Highlights their key services
            3. Ends with a trust-building statement
            
            Important rules:
            - Do NOT invent years of experience, certifications, or qualifications not mentioned
            - Keep it factual, warm, and professional
            - Write in third person
            - Maximum 80 words
            
            Return ONLY the bio text, no labels or extra formatting.
        """.trimIndent()

        val response = model.generateContent(prompt)
        response.text?.trim() ?: throw Exception("Empty response from Gemini")
    }

    suspend fun suggestCategoryTags(
        services: List<Service>,
        tradeCategory: String
    ): Result<List<String>> = runCatching {
        if (!isAvailable) throw Exception("AI feature not configured")
        val servicesList = services.joinToString(", ") { it.serviceName }
        val prompt = """
            A worker in Karnataka, India offers these services: $servicesList
            Their trade category is: $tradeCategory
            
            Suggest 5 short skill tags (1-3 words each) that describe their specializations.
            Return ONLY a comma-separated list of tags, nothing else.
            Example format: Wiring Repair, Circuit Installation, Fan Fitting, Switchboard Work, Safety Inspection
        """.trimIndent()

        val response = model.generateContent(prompt)
        response.text?.trim()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: emptyList()
    }
}
