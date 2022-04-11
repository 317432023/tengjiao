rem 注释：这是一行注释
:: 注释：这是一行注释
@echo off

:: 运行端口号
set PORT=8080

:: JDK启动参数
set JAVA_OPT=-server -Duser.timezone=GMT+08 -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8
set JAVA_OPT=%JAVA_OPT% -XX:+PrintCommandLineFlags
set JAVA_OPT=%JAVA_OPT% -XX:MetaspaceSize=512m
set JAVA_OPT=%JAVA_OPT% -XX:MaxMetaspaceSize=512m
set JAVA_OPT=%JAVA_OPT% -Xms512m
set JAVA_OPT=%JAVA_OPT% -Xmx512m
set JAVA_OPT=%JAVA_OPT% -Xmn128m
set JAVA_OPT=%JAVA_OPT% -Xss256k
set JAVA_OPT=%JAVA_OPT% -XX:SurvivorRatio=8
set JAVA_OPT=%JAVA_OPT% -XX:+UseConcMarkSweepGC

rem 注释：loader.path为springboot加载lib路径专享参数；java.ext.dirs为java加载扩展lib路径
:: 注释：set JAVA_OPT=%JAVA_OPT% -Dloader.path=lib
:: 注释：set JAVA_OPT=%JAVA_OPT% -Djava.ext.dirs=lib

set BOOT_JAR_NAME=tengjiao-seed-admin

:: 系统参数
set SYS_PRO= 
set SYS_PRO=%SYS_PRO% -DserverId=%BOOT_JAR_NAME%

:: SpringBoot 启动参数
set BOOT_PAR= 
set BOOT_PAR=%BOOT_PAR% --server.port=%PORT%
rem tomcat 优化参数
:: 线程池的最小备用线程数，tomcat启动时的初始化的线程数
set BOOT_PAR=%BOOT_PAR% --server.tomcat.min-spare-threads=30
:: 线程池的最大线程数
set BOOT_PAR=%BOOT_PAR% --server.tomcat.max-threads=1000
:: 最大连接数
set BOOT_PAR=%BOOT_PAR% --server.tomcat.max-connections=5000
:: 排队数
set BOOT_PAR=%BOOT_PAR% --server.tomcat.accept-count=2000
:: 最长等待时间
set BOOT_PAR=%BOOT_PAR% --server.tomcat.connection-timeout=5000ms

cd /d %~dp0
java %JAVA_OPT% %SYS_PRO% -jar %BOOT_JAR_NAME%.jar %BOOT_JAR_NAME%.pid %BOOT_PAR%

pause