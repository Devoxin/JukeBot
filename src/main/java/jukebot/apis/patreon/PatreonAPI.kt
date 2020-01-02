package jukebot.apis.patreon

import jukebot.JukeBot
import jukebot.utils.RequestUtil
import jukebot.utils.json
import okhttp3.HttpUrl
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture

class PatreonAPI(var accessToken: String) {
    fun fetchPledgesOfCampaign(campaignId: String): CompletableFuture<List<PatreonUser>> {
        val future = CompletableFuture<List<PatreonUser>>()
        getPageOfPledge(campaignId) { future.complete(it) }
        return future
    }

    private fun getPageOfPledge(campaignId: String, offset: String? = null,
                                users: MutableSet<PatreonUser> = mutableSetOf(), cb: (List<PatreonUser>) -> Unit) {
        request {
            addPathSegments("campaigns/$campaignId/pledges")
            setQueryParameter("include", "pledge,patron")
            offset?.let { setQueryParameter("page[cursor]", it) }
        }.queue({
            if (!it.isSuccessful) {
                JukeBot.LOG.error("Unable to get list of pledges ({}): {}", it.code(), it.body()?.string())
                it.close()

                return@queue cb(users.toList())
            }

            val json = it.json() ?: return@queue cb(users.toList())
            val pledges = json.getJSONArray("data")

            json.getJSONArray("included").forEachIndexed { index, user ->
                val obj = user as JSONObject

                if (obj.getString("type") == "user") {
                    val pledge = pledges.getJSONObject(index)
                    users.add(PatreonUser.fromJsonObject(obj, pledge))
                }
            }

            val nextPage = getNextPage(json) ?: return@queue cb(users.toList())
            getPageOfPledge(campaignId, nextPage, users, cb)
        }, {
            JukeBot.LOG.error("Unable to get list of pledges", it)
            return@queue cb(users.toList())
        })
    }

    private fun getNextPage(json: JSONObject): String? {
        val links = json.getJSONObject("links")

        if (!links.has("next")) {
            return null
        }

        return parseQueryString(links.getString("next"))["page[cursor]"]
    }

    private fun parseQueryString(url: String): Map<String, String> {
        val pairs = URI(url).query.split("&")

        return pairs
            .map { it.split("=") }
            .map { Pair(decode(it[0]), decode(it[1])) }
            .toMap()
    }

    private fun decode(s: String) = URLDecoder.decode(s, Charsets.UTF_8)

    private fun request(urlOpts: HttpUrl.Builder.() -> Unit): RequestUtil.PendingRequest {
        val url = baseUrl.newBuilder().apply(urlOpts).build()
        return JukeBot.httpClient.request {
            url(url)
            header("Authorization", "Bearer $accessToken")
        }
    }

    companion object {
        private val baseUrl = HttpUrl.get("https://www.patreon.com/api/oauth2/api")
    }
}
