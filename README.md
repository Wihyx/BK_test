# 🎯 个人博客系统 — 毕设全栈项目

```
▎ 难度：中等  ▎ 技术栈：主流就业  ▎ 论文适配：优秀  ▎
```

---

## 📦 一、技术栈

### 后端
| 组件 | 技术 | 说明 |
|------|------|------|
| 框架 | **Spring Boot 2.7.18** | 内嵌 Tomcat，自动配置 |
| ORM | **MyBatis-Plus 3.5.5** | 分页插件、逻辑删除、自动填充 |
| 数据库 | **H2 (开发)** / **MySQL (生产)** | 文件持久化，零安装启动 |
| RSS | **Rome 2.1** | RSS/Atom 订阅解析 |
| 媒体 | **Jaudiotagger 2.0.3** | MP3 ID3标签、封面提取 |
| 工具 | **Hutool 5.8** + **Lombok** | 工具类 + 简化代码 |

### 前端
| 组件 | 技术 | 说明 |
|------|------|------|
| 框架 | **Vue 3 + Composition API** | 响应式开发 |
| 构建 | **Vite 5** | 极速 HMR 热更新 |
| UI 库 | **Element Plus 2.6** | 80+ 组件 |
| 图表 | **ECharts 5** | 访问统计折线图 |
| Markdown | **marked** | 文章 Markdown 渲染 |
| HTTP | **axios** | 请求拦截 + 统一错误处理 |

---

## 📂 二、项目结构

```
D:\Work-test\Bk-work\
│
├──  blog-backend/                          # 🔵 后端 SpringBoot
│   ├── pom.xml                             # Maven 依赖管理
│   ├── data/                               # H2 数据库文件持久化
│   ├── uploads/                            # 🟢 文件存储根目录
│   │   ├── bg/        背景图片
│   │   ├── covers/    文章封面
│   │   ├── images/    正文插图
│   │   ├── music/     MP3 音乐 + .lrc 歌词
│   │   ├── materials/ 登录背景、OPML 文件
│   │   └── resources/ 资源文件
│   │
│   └── src/main/
│       ├── java/com/blog/
│       │   ├── BlogApplication.java        # 🚀 SpringBoot 启动入口
│       │   ├── common/Result.java          # 统一响应 {code, msg, data}
│       │   ├── common/GlobalExceptionHandler.java  # 全局异常捕获
│       │   ├── config/                     # MyBatis分页 + CORS + 自动填充
│       │   ├── entity/                     # 📊 数据实体 (9张表)
│       │   ├── mapper/                     # MyBatis-Plus Mapper
│       │   ├── service/                    # 服务层 Interface
│       │   ├── service/impl/               # 服务层实现
│       │   ├── controller/                 # 🌐 REST API 控制器
│       │   │   ├── AdminController         登录 + 控制台统计
│       │   │   ├── ArticleController       文章 CRUD + 评论计数
│       │   │   ├── CategoryController      分类管理
│       │   │   ├── CommentController       评论管理
│       │   │   ├── FeedController          RSS订阅 + OPML导入
│       │   │   ├── ProfileController       个人信息
│       │   │   ├── QuoteController         名言管理
│       │   │   ├── TagController           标签管理
│       │   │   └── UploadController        文件上传 + 图片/音乐管理
│       │   └── dto/LoginDTO.java
│       └── resources/
│           ├── application.yml             # 公共配置 + 激活 dev
│           ├── application-dev.yml         # H2 开发环境
│           ├── application-prod.yml        # MySQL 生产环境
│           ├── schema.sql                  # 建表 SQL (H2兼容)
│           └── data.sql                    # 初始数据 (管理员等)
│
├──  blog-frontend/                         # 🟣 前端 Vue3
│   ├── index.html                          # Vite 入口 HTML
│   ├── vite.config.js                      # 代理 /api → :8080
│   ├── package.json                        # 依赖 + 脚本
│   │
│   └── src/
│       ├── main.js                         # Vue 实例化 + ElementPlus 注册
│       ├── App.vue                         # 🏠 根组件 (背景+主题初始化)
│       ├── style.css                       # 全局样式 (暗/亮双主题)
│       ├── musicStore.js                   # 🎵 音乐播放器单例状态
│       │
│       ├── router/index.js                 # 🗺 路由表 (前台5 + 后台10)
│       ├── api/index.js                    # 📡 Axios封装 + 所有API接口
│       │
│       ├── components/
│       │   └── Header.vue                  # 🔝 置顶栏 (搜索/音乐/主题/背景)
│       │
│       └── views/
│           ├── Home.vue                    # 首页 (6列网格)
│           ├── About.vue                   # 关于页 (个人信息)
│           ├── ArticleDetail.vue           # 文章详情 + 评论
│           ├── ArticleList.vue             # 分类/标签筛选
│           ├── News.vue                    # RSS 订阅资讯
│           ├── Recommend.vue               # 推荐专区 (热门文章)
│           └── admin/                      # 🔐 后台管理
│               ├── Login.vue               登录页 (斜面双色+封面)
│               ├── Layout.vue              后台布局 (侧栏导航)
│               ├── Dashboard.vue           控制台 (统计卡片+图表+名言)
│               ├── ArticleManage.vue       文章管理 (全部/草稿箱+状态标签)
│               ├── ArticleEdit.vue         文章编辑 (封面/分类/卡片类型)
│               ├── CategoryManage.vue      分类+标签管理
│               ├── CommentManage.vue       评论审核 (通过/驳回)
│               ├── ImageManage.vue         图片管理 (上传/预览/设背景)
│               ├── FeedManage.vue          RSS订阅 + 音乐库管理
│               └── ProfileEdit.vue         个人信息编辑
│
├── start.bat                               # ⚡ 一键启动前后端
├── start-backend.bat                       # 仅启动后端
├── start-frontend.bat                      # 仅启动前端
├── setup.bat                               # 环境安装 (JDK+Node+Maven)
└── README.md                               # 本文档
```

---

## 🗄 三、数据库设计 (9张表)

| 表名 | 用途 | 核心字段 |
|------|------|----------|
| `user` | 管理员账户 | username, password, role |
| `article` | 博客文章 | title, content, status(1/2/3), card_type, view_count |
| `category` | 文章分类 | name, sort |
| `tag` | 文章标签 | name |
| `article_tag` | 文章-标签关联 | article_id, tag_id |
| `comment` | 用户评论 | article_id, content, status |
| `profile` | 博主个人信息 | name, avatar, bio, tech_stack, email, github |
| `quote` | 每日名言 | content |
| `feed` | RSS 订阅源 | name, url |
| `feed_article` | 订阅文章缓存 | feed_id, title, link, image |

---

## 🔄 四、核心工作流

### 文章发布流程
```
写文章 → 选择状态(发布/审核/草稿) → 保存 → 首页展示
                                          ↓
                                    草稿箱可编辑后发布
```

### RSS 订阅流程
```
添加RSS源 → 点击同步 → Rome解析XML → 存入 feed_article 表
                                            ↓
                                      首页「热门资讯」展示
                                       ← 按 source 分类筛选
```

### 音乐播放流程
```
上传 MP3 → 存入 uploads/music/ → 前端拉取列表 → 点击播放
                                        ↓
                              Jaudiotagger 提取封面 → 圆形头像旋转
                              同名 .lrc 文件 → 歌词流动显示
```

### 主题切换流程
```
Header 按钮 → toggle body.light class → 全局CSS变量切换
                                         ├── 暗色: #2d2d2d 背景 + 暗色卡片
                                         └── 亮色: #f0f0f0 背景 + 白色卡片
```

---

## 💾 五、文件存储策略

| 目录 | 存储内容 | 前端访问路径 |
|------|----------|-------------|
| `uploads/bg/` | 网页背景图片 | `/uploads/bg/xxx.jpg` |
| `uploads/covers/` | 文章封面图 | `/uploads/covers/xxx.jpg` |
| `uploads/images/` | 文章插图 | `/uploads/images/xxx.png` |
| `uploads/music/` | MP3 + .lrc 歌词 + 封面同名字 | `/uploads/music/xxx.mp3` |
| `uploads/materials/` | 登录背景图、OPML等 | `/uploads/materials/xxx` |
| `uploads/resources/` | 通用资源文件 | `/uploads/resources/xxx` |

> ⚠ **注意**: 后端必须从 `blog-backend/` 目录启动，否则 H2 数据库路径偏移导致数据丢失。

---

## 🎨 六、UI 特性

### 全局
- ✅ 暗色/亮色双主题，Header 切换不跟随
- ✅ 自定义背景图 + 模糊 + 纯色模式
- ✅ 微软雅黑全局字体
- ✅ 毛玻璃卡片效果 (backdrop-filter)
- ✅ 6 列网格响应式首页布局
- ✅ 切页数据缓存 (keep-alive)

### 首页
- ✅ 左侧个人信息卡 (头像/名称/简介/统计)
- ✅ 左侧导航 (首页/推荐/导航/资讯/管理) 带图标
- ✅ 中间文章卡片: 大卡(上图下文) + 小卡(左图右文)
- ✅ 每卡显示 👁 浏览 + 💬 评论 + ⭐ 点赞
- ✅ 右侧最新文章 + 热门资讯 (RSS), 按源分类
- ✅ 每日随机名言

### 音乐播放器
- ✅ 封面圆形旋转 (播放时旋转，暂停保持位置)
- ✅ 上一首/播放/下一首 图标按钮
- ✅ 内嵌进度条可拖动
- ✅ 音乐列表弹窗 (搜索+切换)
- ✅ 歌词流动显示 (同名 .lrc 文件)
- ✅ 切页音乐无缝继续播放

### 后台
- ✅ 控制台统计面板 + ECharts 折线图
- ✅ 文章管理: 全部/草稿箱 切换 + 状态标签
- ✅ 文章编辑: Markdown + 封面上传 + 卡片类型选择
- ✅ 图片管理: 三个 Tab (插图/封面/背景) + 宫格预览
- ✅ 订阅管理: RSS源 + OPML导入 + 音乐库
- ✅ 评论审核: 通过/驳回/删除
- ✅ 名言管理: 增删表格

---

## 🚀 七、部署

### 开发环境启动
```bash
# 前置: JDK 17 + Node.js 18+ + Maven 3.8+
# 或直接双击 setup.bat 自动安装环境

# 启动后端 (端口 8080)
cd blog-backend
mvn spring-boot:run -DskipTests

# 启动前端 (端口 3000)
cd blog-frontend
npm install && npm run dev

# 访问
# 前台: http://localhost:3000
# 后台: http://localhost:3000/admin/login
# 账号: admin / admin123
```

### 生产部署
```bash
# 后端打包
cd blog-backend
mvn clean package -DskipTests
java -jar target/blog-backend-1.0.0.jar --spring.profiles.active=prod

# 前端打包
cd blog-frontend
npm run build    # 输出到 dist/
# 将 dist/ 放到 Nginx 或 SpringBoot 静态资源目录
```
