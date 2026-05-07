# 个人博客系统 - 项目概览

## 技术栈
| 层 | 技术 |
|-----|------|
| 后端框架 | Spring Boot 2.7 + MyBatis-Plus 3.5 |
| 数据库 | H2(dev) / MySQL(prod) + Redis(可选) |
| 前端框架 | Vue 3 + Vite + Element Plus + ECharts |
| RSS解析 | Rome 2.1 |
| 音乐解析 | Jaudiotagger (ID3封面提取) |

---

## 项目结构

```
blog-backend/           # Spring Boot 后端
├── src/main/java/com/blog/
│   ├── BlogApplication.java    # 启动入口
│   ├── common/Result.java      # 统一响应 {code,msg,data}
│   ├── config/                 # CORS、MyBatis分页、自动填充
│   ├── entity/                 # 数据库实体(User/Article/Category/Tag/Comment/Feed/Profile/Quote)
│   ├── mapper/                 # MyBatis-Plus Mapper 接口
│   ├── service/                # 业务逻辑层
│   └── controller/             # REST API 控制器
├── src/main/resources/
│   ├── application.yml         # 公共配置
│   ├── application-dev.yml     # H2 开发环境
│   ├── application-prod.yml    # MySQL 生产环境
│   ├── schema.sql              # 建表 DDL
│   └── data.sql                # 初始数据
└── uploads/                    # 文件存储 (bg/ covers/ music/ materials/)

blog-frontend/          # Vue3 前端
├── src/
│   ├── App.vue                  # 根组件 (背景+主题初始化)
│   ├── main.js                  # 入口 (ElementPlus 全局注册)
│   ├── style.css                # 全局样式 (暗/亮主题)
│   ├── musicStore.js            # 音乐播放器单例状态
│   ├── router/index.js          # 路由配置
│   ├── api/index.js             # Axios 封装 + 所有 API
│   ├── components/Header.vue    # 置顶导航栏 (搜索/音乐/主题/背景)
│   └── views/
│       ├── Home.vue             # 首页 (6列网格/文章卡片)
│       ├── About.vue            # 关于页 (个人信息)
│       ├── ArticleDetail.vue    # 文章详情 (Markdown渲染/评论)
│       ├── ArticleList.vue      # 分类/标签文章列表
│       ├── News.vue             # RSS订阅资讯
│       ├── Recommend.vue        # 推荐页
│       └── admin/               # 后台管理
│           ├── Login.vue         # 登录
│           ├── Layout.vue        # 后台布局 (侧栏+顶栏)
│           ├── Dashboard.vue     # 控制台 (统计+图表+名言管理)
│           ├── ArticleManage.vue # 文章管理 (发布/草稿+状态标签)
│           ├── ArticleEdit.vue   # 文章编辑 (Markdown/封面/卡片类型)
│           ├── CategoryManage.vue # 分类+标签管理
│           ├── CommentManage.vue # 评论审核
│           ├── ImageManage.vue   # 图片管理 (上传/背景设置)
│           ├── FeedManage.vue    # RSS订阅+音乐库管理
│           └── ProfileEdit.vue   # 个人信息编辑
```

---

## 数据表设计

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| user | 管理员 | username, password, role |
| article | 文章 | title, content, status(1发布/0草稿), cardType, viewCount |
| category | 分类 | name, sort |
| tag | 标签 | name |
| article_tag | 文章-标签关联 | articleId, tagId |
| comment | 评论 | articleId, content, status(0待审/1通过) |
| profile | 个人信息 | name, avatar, bio, techStack, email, github |
| quote | 每日名言 | content |
| feed | RSS订阅源 | name, url |
| feed_article | 订阅文章 | feedId, title, link, image |
| quote | 名言管理 | content |

---

## REST API 一览

| Method | Path | 说明 |
|--------|------|------|
| POST | /api/admin/login | 登录 |
| GET | /api/admin/dashboard | 控制台统计 |
| GET | /api/articles | 文章列表 (?status=0,1) |
| GET/POST/PUT/DELETE | /api/articles/{id} | 文章 CRUD |
| GET/POST/PUT/DELETE | /api/categories | 分类管理 |
| GET/POST/DELETE | /api/tags | 标签管理 |
| GET/POST/DELETE | /api/comments | 评论管理 |
| POST | /api/upload | 文件上传 (?type=bg/images/music) |
| GET/DELETE | /api/images | 图片列表/删除 |
| GET | /api/music | 音乐列表 |
| GET | /api/music/lyrics | 歌词内容 |
| GET | /api/music/cover | 专辑封面 (ID3提取) |
| GET | /api/profile | 个人信息 |
| PUT | /api/admin/profile | 更新个人信息 |
| GET | /api/quotes/random | 随机名言 |
| POST | /api/admin/feeds | 添加RSS源 |
| POST | /api/admin/feeds/sync | 同步RSS |
| POST | /api/admin/feeds/clear | 清空订阅文章 |
| POST | /api/admin/feeds/import | 导入OPML |
| GET | /api/feed-articles | 订阅文章列表 |
