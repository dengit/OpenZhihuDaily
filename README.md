# OpenZhihuDaily
An open source version of ZhihuDaily for studying. Design and Resources are almost from original ZhihuDaily app of Zhihu.Inc.
All rights are belong to Zhihu.Inc.

## Gif movie of OpenZhihuDaily
![][0]

## Covered features
  - content presentation
    - splash animation as loading
    - list pull to refresh
    - list slide up to load more
    - top stories banner
    - action bar title switch
    - drawer
    - theme switch
    - browse favorites
    - download progress
    - read or unread status of story
    - story content show
    - long comments and short comments show
    
  - local cache
    - most downloaded datas are saved into sqlite. by contrast, official app saves those datas with files
  
## Uncovered features
  most of these are interactional features, they require an authorization via login
  - login
  - make commends or reply to others
  - like vote
  - share
  - subscribe theme

## These thirdpart libs do good jobs
  - [android-async-http][1]
  - [picasso][2]
  - [viewpagerindictor][3]
  
## These projects inspire me
  - [ZhihuPaper][4]
  - [ZhihuDailyPurify][5]
  - [AnimeTaste][6]

## [APIs][7]
  - all applauses are belong to [@izzyleung][8], becuase datas from APIs are the start point of all my works.
  
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
  
