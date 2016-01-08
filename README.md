# OpenZhihuDaily
[![Build Status](https://travis-ci.org/dengit/OpenZhihuDaily.svg?branch=master)](https://travis-ci.org/dengit/OpenZhihuDaily) [![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

> ## [中文看这里](https://github.com/dengit/OpenZhihuDaily/edit/master/README-zh.md)

An open source version of ZhihuDaily for studying. Design and Resources are almost from original ZhihuDaily APP of Zhihu.Inc.
All rights are belong to Zhihu.Inc.。

## GIF
![][0]

## Covered features
  - content presentation
    - splash animation as loading
    - list pulls to refresh
    - list slides up to load more
    - top stories banner
    - action bar title switch
    - drawer
    - theme switch
    - browse favorites
    - download offline
    - read or unread status of story
    - story content show
    - long comments and short comments show
    
  - local cache
    - most downloaded datas are saved into SQLite. by contrast, official APP saves those datas with files.
  
## Uncovered features
  most of these are interactive features, they require an authorization via login.

  - login
  - make commends or reply to others
  - like vote
  - share
  - subscribe theme

## These thirdparty libs do good jobs
  - [android-async-http][1]
  - [picasso][2]
  - [viewpagerindictor][3]
  
## These projects inspire me
  - [ZhihuPaper][4]
  - [ZhihuDailyPurify][5]
  - [AnimeTaste][6]

## [APIs][7]
  - all applauses are belong to [@izzyleung][8], becuase datas from APIs are the start point of all my works. Also, you may      need a nice tool to analyse the http data, [*Fiddler*][9] is worth you to taste.。

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
  
