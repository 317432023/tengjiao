package com.tengjiao.seed.admin.sample;

import com.alibaba.fastjson.JSON;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.tengjiao.seed.admin.model.security.pojo.RouteDo;
import com.tengjiao.seed.admin.model.sys.entity.Role;
import com.tengjiao.seed.admin.model.sys.pojo.MenuRolesDo;
import com.tengjiao.seed.admin.service.sys.MenuService;
import org.junit.Test;

import java.util.List;

/**
 * ServicesTest
 * 业务方法测试
 * @author Administrator
 * @since 2020/11/18 19:06
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServicesTest {

  @Autowired
  private MenuService menuService;
  @Autowired
  private RedisTool redisTool;

  @Test
  public void testSelectAllMenuRoles() {
    // 取得 URL列表，每个URL中包含了允许访问的角色列表
    List<MenuRolesDo> menuRolesDOs = menuService.selectAllMenuRoles();
    for (MenuRolesDo menuRolesDO : menuRolesDOs) {
      System.out.println(menuRolesDO.getPerm());
      menuRolesDO.getRoles().stream().map(Role::getName).forEach(System.out::println);
    }
  }

  @Test
  public void testLoadRoutes() {
    List<RouteDo> list = menuService.loadRoutesByAdminId(1L);
    list.forEach(e-> System.out.println(JSON.toJSONString(e)));
  }
  @Test
  public void testRedis() {
    boolean has = redisTool.hasKey("menu:selectAllMenuRoles", ModeDict.APP);
    Assert.assertTrue(has);
    String ss = redisTool.getString("menu:selectAllMenuRoles", ModeDict.APP);
    System.out.println(ss);
  }
}
