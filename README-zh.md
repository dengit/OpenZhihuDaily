# OpenZhihuDaily
[![Build Status](https://travis-ci.org/dengit/OpenZhihuDaily.svg?branch=master)](https://travis-ci.org/dengit/OpenZhihuDaily) [![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Release](https://img.shields.io/github/release/dengit/OpenZhihuDaily.svg)](https://github.com/dengit/OpenZhihuDaily/releases) ![Tag](https://img.shields.io/github/tag/dengit/OpenZhihuDaily.svg)

一个ZhihuDaily的开源客户端实现，仅用来学习研究。大部分UI设计、资源均来自Zhihu.Inc的ZhihuDaily, 包含的所有信息与内容的版权皆归Zhihu.Inc所有。

## GIF演示
![][0]

## 已覆盖的特性
  - 内容展示
    - splash 加载动画
    - 列表下拉刷新数据
    - 列表上滑加载更多数据
    - top stories滚动横幅
    - action bar 标题的实时切换
    - 抽屉布局
    - 日报主题切换
    - 收藏夹功能
    - 离线下载
    - 条目已读状态
    - 正文内容显示
    - 长短评论显示
    
  - 数据本地缓存
    - 大部分浏览过的内容都存储在SQLite数据库中，相比之下，官方的APP则存储在文件内
  
## 未覆盖的特性
  这些未覆盖的特性大部分是互交型的，它们需要通过登录进行授权

  - 登录
  - 编辑评论或回复评论
  - 点赞
  - 分享
  - 订阅主题

## 感谢以下第三方库
  - [android-async-http][1]
  - [picasso][2]
  - [viewpagerindictor][3]
  
## 感谢以下开源项目
  - [ZhihuPaper][4]
  - [ZhihuDailyPurify][5]
  - [AnimeTaste][6]

## [APIs][7]
  - 感谢[@izzyleung][8]在APIs分析方面做出的努力。[*Fiddler*][9]是一个不错的HTTP分析工具。

## License
    Copyright 2015 dengit
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

  [0]: https://cloud.githubusercontent.com/assets/11329773/10941824/1700950a-8348-11e5-9b9a-6ed4c024e8db.gif
  [1]: https://github.com/loopj/android-async-http
  [2]: https://github.com/square/picasso
  [3]: https://github.com/JakeWharton/Android-ViewPagerIndicator
  [4]: https://github.com/cundong/ZhihuPaper
  [5]: https://github.com/izzyleung/ZhihuDailyPurify
  [6]: https://github.com/daimajia/AnimeTaste
  [7]: https://github.com/izzyleung/ZhihuDailyPurify/wiki/%E7%9F%A5%E4%B9%8E%E6%97%A5%E6%8A%A5-API-%E5%88%86%E6%9E%90
  [8]: https://github.com/izzyleung
  [9]: http://www.telerik.com/fiddler
  
