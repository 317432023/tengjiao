# 项目原型
    tengjiao
        |-tengjiao-comm                           # 公共依赖
        |-tengjiao-tool                           # 工具库
        |-tengjiao-assembly                       # 组件封装
        |-tengjiao-part                           # 框架部件
        |-tengjiao-seed                           # 种子项目
            |-tengjiao-seed-admin                 # 后台管理系统原型
                |-tengjiao-seed-admin-sample      # 管理系统样板示例服务接口
                |-tengjiao-seed-admin-ui          # 管理系统样板示例界面
                |-doc
                    |-使用说明.md
                    |-script
                        |-tengjiao-seed-admin.sh  # Linux下运行脚本，可基于该文件进行修改
                        |-tengjiao-seed-admin.bat # Linux下运行脚本，可基于该文件进行修改
            |-tengjiao-seed-templet-admin         # 管理系统样板模板项目（直接拷贝此项目开发新项目，它仅是基于tengjiao-seed-admin-sample重新组装Maven）
## 使用

    cd tengjiao-seed
    mvn clean install -N # 安装pom模块，不安装子模块
    cd tengjiao-seed-admin
    mvn clean install -N # 安装pom模块，不安装子模块
    安装 tengjiao-seed-admin 目录下除了 sample 以外的所有模块
    启动 redis 和 mysql （如果使用 dev 模式自动使用h2，不需要启动mysql，）
    配置 好 sample 的 application*.yml，运行 tengjiao-seed-admin-sample 下的 主启动类
    运行 tengjiao-seed-admin-ui 下的 npm run serve
    访问 http://localhost:7070，输入 默认账号密码 admin/123456 

## 项目模板开发新项目

    新建目录 例如取名目录名称为 NewProject
    拷贝目录（以 admin 管理后台为例） tengjiao-seed-templet-admin 、 tengjiao-seed-admin-ui 到 NewProject
    拷贝文件 tengjiao-seed/pom.xml  到 NewProject
    idea 打开此工程目录，然后对模块和目录根据需要进行 改名 ：
        例如分别重命名为 NewProject-Admin 和 NewProject-Admin-UI
    最后形成如下目录格式：
        NewProject
            |-NewProject-Admin
            |-NewProject-Admin-UI
            |-...
            |-NewProject-CommUser
            |-NewProject-CommUser-UI
            |-NewProject-Merchant
            |-NewProject-Merchant-UI
            |-...