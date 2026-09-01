package me.dizzykitty3.androidtoolkitty.utils

import androidx.annotation.StringRes
import me.dizzykitty3.androidtoolkitty.BG
import me.dizzykitty3.androidtoolkitty.CN
import me.dizzykitty3.androidtoolkitty.COM
import me.dizzykitty3.androidtoolkitty.COM_CN
import me.dizzykitty3.androidtoolkitty.CO_AR
import me.dizzykitty3.androidtoolkitty.CO_JP
import me.dizzykitty3.androidtoolkitty.CO_UK
import me.dizzykitty3.androidtoolkitty.EE
import me.dizzykitty3.androidtoolkitty.HTTPS
import me.dizzykitty3.androidtoolkitty.IR
import me.dizzykitty3.androidtoolkitty.JP
import me.dizzykitty3.androidtoolkitty.LA
import me.dizzykitty3.androidtoolkitty.ME
import me.dizzykitty3.androidtoolkitty.MX
import me.dizzykitty3.androidtoolkitty.NET
import me.dizzykitty3.androidtoolkitty.NZ
import me.dizzykitty3.androidtoolkitty.ORG
import me.dizzykitty3.androidtoolkitty.R
import me.dizzykitty3.androidtoolkitty.RU
import me.dizzykitty3.androidtoolkitty.SO
import me.dizzykitty3.androidtoolkitty.TO
import me.dizzykitty3.androidtoolkitty.TV
import me.dizzykitty3.androidtoolkitty.US
import me.dizzykitty3.androidtoolkitty.WIKI
import timber.log.Timber

object URLUtils {
    fun String.addURLScheme(): String = if (this.contains("://")) this else "$HTTPS$this"

    /**
     * Adding the appropriate suffix and returning the full URL.
     */
    fun String.addSuffix(): String {
        val suffix = this.getSuffix()
        Timber.d(
            if (suffix == COM)
                "suffix = com, input url: $this"
            else
                "suffix = $suffix"
        )

        return "${this.addURLScheme()}$suffix"
    }

    fun String.getSuffix(): String {
        if (this.contains(".")) return ""

        val suffixMap = mapOf(
            "remove" to BG, "feishu" to CN, "52pojie" to CN, "360" to CN, "mercadolibre" to CO_AR,
            "rakuten" to CO_JP, "dmm" to CO_JP, "autohome" to COM_CN, "zol" to COM_CN,
            "pconline" to COM_CN, "dailymail" to CO_UK, "bbc" to CO_UK, "linktr" to EE,
            "shaparak" to IR, "livedoor" to JP, "nicovideo" to JP, "hitomi" to LA, "csdn" to NET,
            "pixiv" to NET, "atlassian" to NET, "cnki" to NET, "doubleclick" to NET,
            "speedtest" to NET, "researchgate" to NET, "behance" to NET, "ali213" to NET,
            "savefrom" to NET, "cloudfront" to NET, "bytedance" to NET, "nhentai" to NET,
            "daum" to NET, "animeflv" to NET, "jb51" to NET, "manatoki215" to NET, "line" to ME,
            "yts" to MX, "mega" to NZ, "wikipedia" to ORG, "telegram" to ORG, "archive" to ORG,
            "mozilla" to ORG, "e-hentai" to ORG, "greasyfork" to ORG, "coursera" to ORG,
            "craigslist" to ORG, "yandex" to RU, "mail" to RU, "dzen" to RU, "avito" to RU,
            "ok" to RU, "ozon" to RU, "wildberries" to RU, "gosulugi" to RU, "ya" to RU,
            "notion" to SO, "zoro" to TO, "1337x" to TO, "twitch" to TV, "jable" to TV,
            "zoom" to US, "namu" to WIKI
        )

        return suffixMap[this.lowercase()] ?: COM
    }

    /**
     * @see me.dizzykitty3.androidtoolkitty.ui.home.toProfileFullURL
     */
    enum class Platform(val prefix: String, @param:StringRes val platform: Int) {
        AFDIAN("afdian.com/a/", R.string.afdian),
        ARTSTATION("artstation.com/", R.string.artstation),
        BAHAMUT("gamer.com.tw/home/home.php?owner=", R.string.bahamut),
        BILIBILI_SEARCH("bilibili://search?keyword=", R.string.bilibili_search),
        BILIBILI_UUID("space.bilibili.com/", R.string.bilibili_uuid),
        BILIBILI_AV("bilibili.com/video/", R.string.bilibili_av_id),
        BILIBILI_BV("bilibili.com/video/", R.string.bilibili_bv_id),
        BLUESKY("bsky.app/profile/", R.string.bluesky),
        BOOTH(".booth.pm", R.string.booth), // Prefix
        CARRD(".carrd.co", R.string.carrd), // Prefix
        CHROMIUM_ISSUE_TRACKER("issues.chromium.org/issues/", R.string.chromium_issue_tracker),
        FACEBOOK("facebook.com/", R.string.facebook),
        FANBOX(".fanbox.cc", R.string.fanbox), // Prefix
        FANSLY("fansly.com/", R.string.fansly),
        FANTIA("fantia.jp/", R.string.fantia),
        GITHUB("github.com/", R.string.github),
        GOOGLE_ISSUE_TRACKER("issuetracker.google.com/issues/", R.string.google_issue_tracker),
        INSTABIO("linkbio.co/", R.string.instabio),
        INSTAGRAM("instagram.com/", R.string.instagram),
        KO_FI("ko-fi.com/", R.string.ko_fi),
        LINKEDIN_COMPANY("linkedin.com/company/", R.string.linkedin_company),
        LINKEDIN_USER("linkedin.com/in/", R.string.linkedin_user),
        LINKTREE("linktr.ee/", R.string.linktree),
        LIT_LINK("lit.link/", R.string.lit_link),
        MARSHMALLOW_QA("marshmallow-qa.com/", R.string.marshmallow_qa),
        MASTODON("mastodon.social/@", R.string.mastodon),
        MISSKEY("misskey.io/@", R.string.misskey),
        NOTE("note.com/", R.string.note),
        PATREON("patreon.com/", R.string.patreon),
        PAYPAL("paypal.me/", R.string.paypal),
        PINTEREST("pinterest.com/", R.string.pinterest),
        PIXIV_ARTWORK("pixiv.net/artworks/", R.string.pixiv_artwork_id),
        PIXIV_USER_CUSTOM_URL("pixiv.me/", R.string.pixiv_user_custom_url),
        PIXIV_UUID("pixiv.net/users/", R.string.pixiv_uuid),
        PLURK("plurk.com/", R.string.plurk),
        POTOFU("potofu.me/", R.string.potofu),
        PROFCARD("profcard.info/u/", R.string.profcard),
        REDDIT_SUBREDDIT("reddit.com/r/", R.string.reddit_subreddit),
        SKEB("skeb.jp/@", R.string.skeb),
        SNAPCHAT("snapchat.com/add/", R.string.snapchat),
        STACK_OVERFLOW("stackoverflow.com/questions/", R.string.stack_overflow),
        STEAM_SEARCH_STORE("store.steampowered.com/search/?term=", R.string.steam_search_store),
        STEAM_USER_CUSTOM_URL("steamcommunity.com/id/", R.string.steam_user_custom_url),
        STEAM_UUID("steamcommunity.com/profiles/", R.string.steam_uuid),
        TELEGRAM("t.me/", R.string.telegram),
        THREADS("threads.net/@", R.string.threads),
        TIKTOK("tiktok.com/@", R.string.tiktok),
        TUMBLR(".tumblr.com", R.string.tumblr), // Prefix
        TWITCASTING("twitcasting.tv/c:", R.string.twitcasting),
        TWITCH("twitch.tv/", R.string.twitch),
        UNIFANS("app.unifans.io/c/", R.string.unifans),
        UNSPLASH("unsplash.com/@", R.string.unsplash),
        V2EX("v2ex.com/member/", R.string.v2ex),
        WAVEBOX("wavebox.me/wave/", R.string.wavebox),
        WEIBO_USERNAME("weibo.com/n/", R.string.weibo_username),
        WEIBO_USER_CUSTOM_URL("weibo.com/", R.string.weibo_user_custom_url),
        WEIBO_UUID("weibo.com/u/", R.string.weibo_uuid),
        X("x.com/", R.string.x),
        YOUTUBE_SEARCH("youtube.com/results?search_query=", R.string.youtube_serach),
        YOUTUBE_USER_CUSTOM_URL("youtube.com/@", R.string.youtube_handle)
    }
}
