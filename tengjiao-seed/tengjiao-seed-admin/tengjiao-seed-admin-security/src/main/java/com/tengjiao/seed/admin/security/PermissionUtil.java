package com.tengjiao.seed.admin.security;

import com.alibaba.fastjson.JSON;
import com.tengjiao.part.redis.ModeDict;
import com.tengjiao.part.redis.RedisTool;
import com.tengjiao.seed.admin.model.sys.entity.Admin;
import com.tengjiao.seed.admin.model.sys.pojo.BaseQueryVo;
import com.tengjiao.seed.admin.security.config.SettingProperties;
import com.tengjiao.seed.admin.security.domain.AdminUserDetails;
import com.tengjiao.seed.admin.service.sys.AdminService;
import com.tengjiao.tool.indep.json.BasicJsonTool;
import com.tengjiao.tool.indep.model.RC;
import com.tengjiao.tool.indep.model.SystemException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.tengjiao.comm.Constants.LOGIN_TOKEN_PREFIX;
import static com.tengjiao.seed.admin.comm.Constants.*;

@Component
public class PermissionUtil {

    private SettingProperties settingProperties;
    private RedisTool redisTool;
    private AdminService adminService;

    public PermissionUtil(SettingProperties settingProperties, RedisTool redisTool, AdminService adminService) {
        this.settingProperties = settingProperties;
        this.redisTool = redisTool;
        this.adminService = adminService;
    }

    /*
    @Value("${securityStrategy}")
    private String securityStrategy;
    @Value("${tokenHeaderKey}")
    private String tokenHeaderKey;
    @Value("${tokenValPrefix}")
    private String tokenValPrefix;
    */

    private Map<String, Integer> stationIdCache = new ConcurrentHashMap<>();

    /**
     * 取得当前登录者的站点ID
     * @param request
     * @return
     */
    public Integer extract(HttpServletRequest request) {
        SecurityStrategyType securityStrategyType = Enum.valueOf(SecurityStrategyType.class, settingProperties.getSecurityStrategy());
        String loginToken = SecurityUtil.loginToken(request, securityStrategyType, settingProperties.getTokenHeaderKey(), settingProperties.getTokenValPrefix());
        if(StringUtils.isEmpty(loginToken)) {
            throw SystemException.create().setCode(RC.NOT_AUTHENTICATED);
        }
        AdminUserDetails userDetails = null;
        if(SecurityStrategyType.session == securityStrategyType) {
            userDetails = SecurityUtil.getCurrentUser();
        } else {
            String stringAdminUserDetails = redisTool.getString(LOGIN_TOKEN_PREFIX+loginToken, ModeDict.APP);
            if(StringUtils.isEmpty(stringAdminUserDetails)) {
                throw SystemException.create("出错了,原因: username cannot be blank");
            }
            userDetails = JSON.parseObject(stringAdminUserDetails, AdminUserDetails.class);
        }

        String username = userDetails.getUsername();
        // 用户所属站点
        Integer stationId = stationIdCache.get(username);
        if(stationId != null) {
            return stationId;
        }

        String metadataS = redisTool.getString(METADATA_KEY_PREFIX +username, ModeDict.APP);
        Map<String, Object> metadataMap = null;
        if(metadataS != null && !"".equals(metadataS.trim())) {
            metadataMap = BasicJsonTool.parseMap(metadataS);

            stationId = ((Long)metadataMap.get("stationId")).intValue();
        } else {
            Admin adminUser = adminService.findByUsername(username);
            Assert.notNull(adminUser, "用户"+username+"不存在");
            metadataMap = new HashMap<>(2);
            metadataMap.put("stationId", adminUser.getStationId());
            metadataMap.put("id", adminUser.getId());
            redisTool.setString(METADATA_KEY_PREFIX +username, BasicJsonTool.toJson(metadataMap), ModeDict.APP);

            stationId = adminUser.getStationId();
        }

        stationIdCache.put(username, stationId);
        return stationId;
    }

    public boolean isRoot(Integer stationId) {
        return stationId.intValue() == MASTER_STATION_ID;
    }

    public boolean isRoot(HttpServletRequest request) {
        Integer stationId = extract(request);
        return isRoot(stationId);
    }

    public Integer checkPermission(HttpServletRequest request, Integer stationId) {

        Integer realStationId = extract(request);

        // 当前用户不是总站用户的话，才要去进一步检查
        if(!isRoot(realStationId)) {
            if(stationId != null) {
                if(realStationId.intValue() != stationId.intValue()) {
                    throw SystemException.create("用户无权限操作站点" + stationId + "的数据");
                }
                //Assert.isTrue(realStationId.intValue() == stationId.intValue(),
                //        () -> "用户无权限操作站点" + stationId + "的数据");
            }
            return realStationId;
        } else {

            return stationId;
        }
    }

    public void filterQuery(HttpServletRequest request, BaseQueryVo baseQueryVo) {

        Integer realStationId = extract(request);
        if(!isRoot(realStationId) ) {
            baseQueryVo.setStationId(realStationId);
        }
    }
}
