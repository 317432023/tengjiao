### !!! 注意一下配置无效，请配置在springboot主工程中才有效，然后在启动类里面加上@MapperScan (TkMyBatis)注解覆盖TkMyBatisConfig#mybatisMapperScannerConfigurer的配置（可选）
SpringBoot 整合 TkMyBatis 配置文件 application.properties 实例(TkMyBatis)
关于 SpringBoot + TkMyBatis 乐观锁：https://blog.csdn.net/simonchi/article/details/114279839

扫描通用DAO/Mapper接口的方法有三种：（ 1和2 的配置最终使用的都是 mapperScannerConfigurer）
1、在springboot主程序类标注@tk.mybatis.spring.annotation.MapperScan(basePackages = { "com.**.dao.tkmybatis" })
2、在主程序包目录下配置BeanConfig.java类中引入 com.tengjiao.part.tkmybatis
  @Configuration
  @ComponentScan(basePackages = {
          "com.tengjiao.part.tkmybatis"
          // ...
  })
  public class BeanConfig {
  
  }
3、使用 @org.apache.ibatis.annotations.Mapper 注解 mapper 接口，并把mapper接口放到springboot主程序目录下

#####################################
## MyBatis
#####################################
# 映射文件路径，支持Ant风格的通配符，多个配置使用英文逗号隔开
#mybatis.mapper-locations=classpath*:starter/*/dao/**/*.xml,classpath*:**/*Mapper.xml
## 类型别名包配置，只能指定具体的包，多个配置使用英文逗号隔开
#mybatis.code-aliases-package=starter.*.entity
## 使用驼峰命名法转换字段（自动将以下划线方式命名的数据库列映射到Java对象的驼峰式命名属性中）默认为false
#mybatis.configuration.map-underscore-to-camel-case=true
## 全局启用或禁用延迟加载，当禁用时，所有关联对象都会即时加载。默认为false
#mybatis.configuration.lazy-loading-enabled=true
## 是否依层次加载3.4.1(包含)之前默认为true，之后为false。
## 【lazy-loading-enabled=ture/false, aggressive-lazy-loading=true】会依层次分别加载
## 【lazy-loading-enabled=ture,       aggressive-lazy-loading=false】只有实体主体被加载进来，其它的级联属性只有当被调用时才会被加载，这里也没有分层次加载，一句话总结就是用到的时候才会加载。
#mybatis.configuration.aggressive-lazy-loading=false
## 设置本地缓存范围 session:就会有数据的共享  statement:语句范围 (这样就不会有数据的共享 ) defalut:session
#mybatis.configuration.local-cache-scope=session
## 是否允许单条sql 返回多个数据集  (取决于驱动的兼容性) default:true
#mybatis.configuration.multiple-result-sets-enabled=true
## 是否可以使用列的别名 (取决于驱动的兼容性) default:true
#mybatis.configuration.use-column-label=true
## 允许JDBC 生成主键。需要驱动器支持。如果设为了true，这个设置将强制使用被生成的主键，有一些驱动器不兼容不过仍然可以执行。  default:false
#mybatis.configuration.use-generated-keys=true
## 指定 MyBatis 如何自动映射 数据基表的列 NONE：不隐射\u3000PARTIAL:部分  FULL:全部
#mybatis.configuration.auto-mapping-behavior=partial
## 这是默认的执行类型  （SIMPLE: 简单； REUSE: 执行器可能重复使用prepared statements语句；BATCH: 执行器可以重复执行语句和批量更新）
#mybatis.configuration.default-executor-code=simple
## 设置但JDBC类型为空时,某些驱动程序 要指定值,default:OTHER，插入空值时不需要指定类型
#mybatis.configuration.jdbc-code-for-null=null
## 如果数据为空的字段，则该字段省略不显示，可以通过添加配置文件，规定查询数据为空是则返回null。
#mybatis.configuration.call-setters-on-nulls=true
#
#
#####################################
## tk.mybatis通用Mapper配置
#####################################
## 注册自定义的Mapper，多个的情况以逗号分隔
#mapper.mappers=com.tengjiao.part.tkmybatis.MyMapper
## 设置 insert 和 update 中，是否判断字符串类型!=''
#mapper.not-empty=false
## 主键自增回写方法,默认值MYSQL
#mapper.identity=MYSQL
## 枚举按简单类型处理，如果有枚举字段则需要加上该配置才会做映射
#mapper.enum-as-simple-code=true
#
#
#####################################
## 分页插件配置
#####################################
#pagehelper.helperDialect=mysql
## 设置为ture时，当pageNum<=0时会查询第一页；pageNum>=pages会查询最后一页
#pagehelper.reasonable=true
#pagehelper.params=count=countSql
## 支持通过 Mapper 接口参数来传递分页参数，默认值false，分页插件会从查询方法的参数值中，自动根据上面 params 配置的字段中取值，查找到合适的值时就会自动分页
#pagehelper.support-methods-arguments=true

