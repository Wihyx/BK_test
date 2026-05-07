package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Quote;
import com.blog.mapper.QuoteMapper;
import com.blog.service.QuoteService;
import org.springframework.stereotype.Service;

/**
 * Quote service implementation.
 * No custom methods — relies entirely on inherited CRUD from {@link ServiceImpl}.
 */
@Service
public class QuoteServiceImpl extends ServiceImpl<QuoteMapper, Quote> implements QuoteService {
}
