package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.Result;
import com.blog.entity.Quote;
import com.blog.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 名言/语录控制器 —— 随机展示和后台管理名言库
 */
@RestController
@RequestMapping("/api")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    // 随机返回一条名言，用于首页展示
    @GetMapping("/quotes/random")
    public Result<String> random() {
        List<Quote> list = quoteService.list(new LambdaQueryWrapper<Quote>()
            .orderByAsc(Quote::getId));
        if (list.isEmpty()) return Result.success("Stay hungry, stay foolish."); // 库为空时返回默认名言
        int idx = (int) (System.currentTimeMillis() % list.size()); // 用当前时间取模实现随机
        return Result.success(list.get(idx).getContent());
    }

    // 查询所有名言列表（后台管理用）
    @GetMapping("/admin/quotes")
    public Result<List<Quote>> list() {
        return Result.success(quoteService.list(new LambdaQueryWrapper<Quote>().orderByAsc(Quote::getId)));
    }

    // 新增一条名言（后台管理用）
    @PostMapping("/admin/quotes")
    public Result<?> save(@RequestBody Quote quote) {
        quoteService.save(quote);
        return Result.success();
    }

    // 删除指定 ID 的名言（后台管理用）
    @DeleteMapping("/admin/quotes/{id}")
    public Result<?> delete(@PathVariable Long id) {
        quoteService.removeById(id);
        return Result.success();
    }
}
