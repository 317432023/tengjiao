# 使用方法
    准备工作
主机上先准备好项目配置文件，以192.168.99.100虚拟机为例：
mkdir -p /data/application/tengjiao-seed-templet-admin/config
上传配置文件application.yml、application-prod.xml、application-test.yml、application-dev.yml三个配置文件到上面创建的目录中
修改配置文件的配置，包括 spring.profiles.active 参数和 mysql，redis 等配置，确保能正确启动。

## 命令行方式
mvn package docker:build
ssh docker@192.168.99.100 # 连接DockerToolbox虚拟机输入默认密码 tcuser
docker run -it -p 8081:8080 \
-v /data/application/tengjiao-seed-templet-admin/config:/data/config \
-v /data/applogs:/data/applogs \
--name c-tengjiao-seed-templet-admin tengjiao-seed-templet-admin \
tail -f /data/applogs/tengjiao-seed-templet-admin*/logs/info.log
返回主机 CTRL+Q+P
## idea Docker 插件方式
略

