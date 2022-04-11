package com.tengjiao.seed.admin.comm;

import static com.tengjiao.comm.Constants.KEY_DELIMITER;

/**
 * 常量定义
 * @author rise
 * @date 2020/06/04
 */
public interface Constants {

  String
    /**系统配置哈希缓存总键*/
    SYS_CONFIG_KEY = "systemConfig",
    /**站点配置缓存总键*/
    STA_CONFIG_KEY = "stationConfig";

    /**角色性质：0-普通用户 1-管理员*/
  String ROLE_KIND_KEY = "roleKind";

  String METADATA_KEY = "metadata";

  String
    ROLE_KIND_KEY_PREFIX = ROLE_KIND_KEY  + KEY_DELIMITER,
    METADATA_KEY_PREFIX = METADATA_KEY + KEY_DELIMITER;


  /**总站ID*/
  int MASTER_STATION_ID = 1;
  /**系统管理员ID*/
  long ADMINISTRATOR_ID = 1;

  /**默认盐*/
  String DEFAULT_SALT = "rise";

  enum SecurityStrategyType {
    session(1), token(2)
    ;
    private SecurityStrategyType(int type) {this.type = type;}
    private int type;

    public int getType() {
      return type;
    }
  }

  enum LoginProcessorType {
    controller(1), filter(2)
    ;
    private LoginProcessorType(int type) {this.type = type;}
    private int type;

    public int getType() {
      return type;
    }
  }


}
