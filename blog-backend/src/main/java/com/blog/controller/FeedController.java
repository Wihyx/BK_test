package com.blog.controller;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.Result;
import com.blog.entity.Feed;
import com.blog.entity.FeedArticle;
import com.blog.service.FeedService;
import com.blog.service.FeedArticleService;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.*;

/**
 * RSS feed management controller. Supports adding / editing / removing feed
 * sources, OPML import, manual sync of feed articles via Rome, and public
 * listing of fetched feed articles.
 */
@RestController
@RequestMapping("/api")
public class FeedController {

    @Autowired
    private FeedService feedService;
    @Autowired
    private FeedArticleService feedArticleService;

    /** List all configured RSS feed sources. */
    @GetMapping("/admin/feeds")
    public Result<List<Feed>> listFeeds() {
        return Result.success(feedService.list());
    }

    /** Add a new RSS feed source (strips "RSS:" / "rss：" prefix if present). */
    @PostMapping("/admin/feeds")
    public Result<?> addFeed(@RequestBody Feed feed) {
        feed.setUrl(feed.getUrl().replaceAll("^(RSS|rss)[：:]\\s*", "").trim());
        feedService.save(feed);
        return Result.success();
    }

    /** Update an existing feed source. */
    @PutMapping("/admin/feeds")
    public Result<?> updateFeed(@RequestBody Feed feed) {
        feedService.updateById(feed);
        return Result.success();
    }

    /** Test connectivity to a feed URL and return the article count. */
    @PostMapping("/admin/feeds/test")
    public Result<?> testFeed(@RequestBody Map<String, String> body) {
        String url = body.get("url");
        if (url == null || url.isEmpty()) return Result.error(400, "url is empty");
        url = url.replaceAll("^(RSS|rss)[：:]\\s*", "").trim();
        try {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed sf = input.build(new XmlReader(feedUrl));
            return Result.success("OK, " + sf.getEntries().size() + " articles found");
        } catch (Exception e) {
            return Result.error(400, "connect failed: " + e.getMessage());
        }
    }

    /** Delete a feed source and all its associated articles. */
    @DeleteMapping("/admin/feeds/{id}")
    public Result<?> deleteFeed(@PathVariable Long id) {
        feedService.removeById(id);
        feedArticleService.remove(new LambdaQueryWrapper<FeedArticle>().eq(FeedArticle::getFeedId, id));
        return Result.success();
    }

    /** Import feed sources from an OPML file (skips duplicates by URL). */
    @PostMapping("/admin/feeds/import")
    public Result<?> importOpml(@RequestParam("file") MultipartFile file) {
        try {
            String xml = new String(file.getBytes(), "UTF-8");
            xml = xml.replaceAll("<\\?xml[^?]*\\?>", "").trim();
            int imported = 0;
            String[] outlines = xml.split("</?outline[^>]*>");
            String name = null, url = null;
            for (String line : xml.split("\n")) {
                if (line.contains("xmlUrl=")) {
                    String n = extractAttr(line, "text");
                    String u = extractAttr(line, "xmlUrl");
                    if (n != null && u != null) {
                        long exists = feedService.count(new LambdaQueryWrapper<Feed>().eq(Feed::getUrl, u));
                        if (exists == 0) {
                            Feed feed = new Feed();
                            feed.setName(n);
                            feed.setUrl(u);
                            feedService.save(feed);
                            imported++;
                        }
                    }
                }
            }
            Map<String, Object> data = new HashMap<>();
            data.put("count", imported);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(400, "parse failed: " + e.getMessage());
        }
    }

    /** Extract the first img src URL from an HTML snippet. */
    private String extractImage(String html) {
        if (html == null) return null;
        int start = html.indexOf("<img ");
        if (start < 0) return null;
        int srcStart = html.indexOf("src=\"", start);
        if (srcStart < 0) srcStart = html.indexOf("src='", start);
        if (srcStart < 0) return null;
        srcStart = html.indexOf("\"", srcStart) + 1;
        if (srcStart <= 0) { srcStart = html.indexOf("'", html.indexOf("src='")) + 1; }
        if (srcStart <= 0) return null;
        int end = html.indexOf(html.charAt(srcStart - 1) == '"' ? "\"" : "'", srcStart);
        if (end < 0) return null;
        return html.substring(srcStart, end);
    }

    /** Extract an XML attribute value from an OPML outline line. */
    private String extractAttr(String line, String attr) {
        int start = line.indexOf(attr + "=\"");
        if (start < 0) return null;
        start += attr.length() + 2;
        int end = line.indexOf("\"", start);
        if (end < 0) return null;
        return line.substring(start, end);
    }

    /** Remove all cached feed articles. */
    @PostMapping("/admin/feeds/clear")
    public Result<?> clearArticles() {
        feedArticleService.remove(new LambdaQueryWrapper<>());
        return Result.success();
    }

    /** Fetch the latest articles from every configured RSS feed (skips duplicates by link). */
    @PostMapping("/admin/feeds/sync")
    public Result<?> syncFeeds() {
        List<Feed> feeds = feedService.list();
        if (feeds.isEmpty()) return Result.error(400, "no feeds to sync");
        int total = 0;
        for (Feed feed : feeds) {
            try {
                URL url = new URL(feed.getUrl());
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed sf = input.build(new XmlReader(url));
                for (SyndEntry entry : sf.getEntries()) {
                    String link = entry.getLink();
                    long exists = feedArticleService.count(new LambdaQueryWrapper<FeedArticle>()
                        .eq(FeedArticle::getLink, link));
                    if (exists == 0) {
                        FeedArticle fa = new FeedArticle();
                        fa.setFeedId(feed.getId());
                        fa.setFeedName(feed.getName());
                        fa.setTitle(entry.getTitle());
                        fa.setLink(link);
                        String descr = entry.getDescription() != null ? entry.getDescription().getValue() : "";
                        fa.setSummary(descr);
                        fa.setImage(extractImage(descr));
                        fa.setPubDate(entry.getPublishedDate() != null ? entry.getPublishedDate().toString() : "");
                        feedArticleService.save(fa);
                        total++;
                    }
                }
            } catch (Exception e) {
                // skip failed feeds
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("count", total);
        return Result.success(data);
    }

    /** List all cached feed articles ordered by publication date descending. */
    @GetMapping("/feed-articles")
    public Result<List<FeedArticle>> listArticles() {
        return Result.success(feedArticleService.list(new LambdaQueryWrapper<FeedArticle>()
            .orderByDesc(FeedArticle::getPubDate)));
    }
}
