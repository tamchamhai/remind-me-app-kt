package com.example.remind_me.core.network

import android.content.Context
import com.example.remind_me.core.network.dto.KnowledgeApiJsonParser
import com.example.remind_me.core.network.dto.TopicBundleResponseDto
import org.json.JSONObject

interface KnowledgeDataSource {
    /**
     * Simulates `GET /api/v1/topics?lang={languageCode}` returning all topic bundles
     * localized for the requested language.
     */
    fun fetchTopicBundles(languageCode: String): List<TopicBundleResponseDto>
}

class MockAssetKnowledgeDataSource(
    private val context: Context
) : KnowledgeDataSource {

    override fun fetchTopicBundles(languageCode: String): List<TopicBundleResponseDto> {
        val assetManager = context.assets
        val indexRaw = assetManager.open("mock_api/index.json")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val indexJson = JSONObject(indexRaw)
        val supportedLangs = indexJson.optJSONArray("supported_languages")
        val normalizedLang = languageCode.lowercase().let { requested ->
            var matched = "vi"
            if (supportedLangs != null) {
                for (i in 0 until supportedLangs.length()) {
                    if (supportedLangs.getString(i).equals(requested, ignoreCase = true)) {
                        matched = supportedLangs.getString(i)
                        break
                    }
                }
            }
            matched
        }

        val filesArray = indexJson.getJSONArray("topic_files")
        val bundles = mutableListOf<TopicBundleResponseDto>()
        for (i in 0 until filesArray.length()) {
            val fileName = filesArray.getString(i)
            val path = "mock_api/$normalizedLang/topics/$fileName"
            val jsonContent = assetManager.open(path)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }
            bundles.add(KnowledgeApiJsonParser.parseTopicBundle(jsonContent))
        }
        return bundles
    }
}

