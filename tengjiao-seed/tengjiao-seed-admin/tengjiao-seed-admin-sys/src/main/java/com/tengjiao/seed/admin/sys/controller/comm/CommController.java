package com.tengjiao.seed.admin.sys.controller.comm;

import com.tengjiao.part.springmvc.ResponseResult;
import com.tengjiao.seed.admin.model.sys.entity.Station;
import com.tengjiao.seed.admin.security.PermissionUtil;
import com.tengjiao.seed.admin.service.sys.StationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@ResponseResult
@RequestMapping("common")
@Api(tags = "common-公共接口")
@AllArgsConstructor
public class CommController {
    private final StationService stationService;
    private final PermissionUtil permissionUtil;

    @PostMapping("listAllStation")
    @ApiOperation("查询所有站点")
    public List<Station> listAllStation(HttpServletRequest request) {
        Integer stationId = permissionUtil.extract(request);
        if( permissionUtil.isRoot( stationId ) ) {
            return stationService.list();
        }
        Station self = stationService.getOne(stationId);
        List<Station> stationList = new ArrayList<>(1);
        stationList.add(self);
        return stationList;
    }


}
