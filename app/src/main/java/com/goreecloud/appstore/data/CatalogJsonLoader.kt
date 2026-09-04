package com.goreecloud.appstore.data

import android.content.Context
import com.goreecloud.appstore.domain.AccessRule
import com.goreecloud.appstore.domain.ReleaseChannel
import com.goreecloud.appstore.domain.StoreItem
import com.goreecloud.appstore.domain.StoreItemType
import org.json.JSONObject

object CatalogJsonLoader {
    fun load(context: Context): List<StoreItem> {
        val source = context.assets
            .open("development-catalog.json")
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(source)
        val entries = root.getJSONArray("items")

        return buildList {
            repeat(entries.length()) { index ->
                val item = entries.getJSONObject(index)
                val access = item.getJSONObject("access")
                val audiences = access.getJSONArray("anyAudience")
                val audienceSet = buildSet {
                    repeat(audiences.length()) { audienceIndex ->
                        add(audiences.getString(audienceIndex))
                    }
                }

                add(
                    StoreItem(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        summary = item.getString("summary"),
                        type = StoreItemType.valueOf(item.getString("type").uppercase()),
                        category = item.getString("category"),
                        version = item.optString("version").takeIf(String::isNotBlank),
                        releaseChannel = ReleaseChannel.valueOf(
                            item.getString("releaseChannel").uppercase(),
                        ),
                        packageName = item.optString("packageName").takeIf(String::isNotBlank),
                        serviceUrl = item.optString("serviceUrl").takeIf(String::isNotBlank),
                        accessRule = AccessRule(
                            requireSignedIn = access.optBoolean("requireSignedIn", true),
                            anyAudience = audienceSet,
                        ),
                    ),
                )
            }
        }
    }
}
