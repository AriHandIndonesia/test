package com.hand.demo.api.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.mybatis.util.StringUtil;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import com.fasterxml.jackson.core.type.TypeReference;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.cache.ProcessCacheValue;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * (InvoiceApplyHeader)表控制层
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:36
 */

@RestController("invoiceApplyHeaderController.v1")
@RequestMapping("/v1/{organizationId}/invoice-apply-headers")
public class InvoiceApplyHeaderController extends BaseController {

    @Autowired
    private InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Autowired
    private InvoiceApplyHeaderService invoiceApplyHeaderService;

    @Autowired
    private RedisHelper redisHelper;

    @ApiOperation(value = "List")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<InvoiceApplyHeaderDTO>> list(InvoiceApplyHeaderDTO invoiceApplyHeader, @PathVariable Long organizationId,
                                                            @ApiIgnore @SortDefault(value = InvoiceApplyHeaderDTO.FIELD_APPLY_HEADER_ID,
                                                                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<InvoiceApplyHeaderDTO> list = invoiceApplyHeaderService.selectList(pageRequest, invoiceApplyHeader);
        return Results.success(list);
    }

    @ApiOperation(value = "Detail")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{applyHeaderId}/detail")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<InvoiceApplyHeaderDTO> detail(@PathVariable Long applyHeaderId) {
//        InvoiceApplyHeaderDTO invoiceApplyHeader = invoiceApplyHeaderRepository.selectByPrimary(applyHeaderId);
        //check redis
        InvoiceApplyHeaderDTO invoiceApplyHeader = new InvoiceApplyHeaderDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        String redisData = redisHelper.strGet("InvoiceHeader-234-" + applyHeaderId);

        if (StringUtil.isEmpty(redisData)) {
            invoiceApplyHeader = invoiceApplyHeaderRepository.selectByPrimary(applyHeaderId);
            //add to redis
            try {
                String invoiceString = objectMapper.writeValueAsString(invoiceApplyHeader);
                redisHelper.strSet("invoiceHeader" + applyHeaderId, invoiceString, 300, TimeUnit.SECONDS);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                invoiceApplyHeader = objectMapper.readValue(redisData, InvoiceApplyHeaderDTO.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return Results.success(invoiceApplyHeader);
    }

    @ApiOperation(value = "Create or Update")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<InvoiceApplyHeader>> save(@PathVariable Long organizationId, @RequestBody List<InvoiceApplyHeader> invoiceApplyHeaders) {
        validObject(invoiceApplyHeaders);
        SecurityTokenHelper.validTokenIgnoreInsert(invoiceApplyHeaders);
        invoiceApplyHeaders.forEach(item -> item.setTenantId(organizationId));
        invoiceApplyHeaderService.saveData(organizationId, invoiceApplyHeaders);
        return Results.success(invoiceApplyHeaders);
    }

    @ApiOperation(value = "delete")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable Long organizationId, @RequestBody List<InvoiceApplyHeader> invoiceApplyHeaders) {
        SecurityTokenHelper.validToken(invoiceApplyHeaders);
        //V 1.1 [S]
//        invoiceApplyHeaderRepository.batchDeleteByPrimaryKey(invoiceApplyHeaders);
        invoiceApplyHeaders.forEach(item -> {
            item.setTenantId(organizationId);
            item.setDelFlag(1);
        });
        invoiceApplyHeaderRepository.batchUpdateByPrimaryKeySelective(invoiceApplyHeaders);
        //V 1.1 [E[
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "export")
    @GetMapping("/export")
    @ExcelExport(value = InvoiceApplyHeaderDTO.class)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<InvoiceApplyHeaderDTO>> export(InvoiceApplyHeaderDTO invoiceApplyHeaderDTO,
                                                              ExportParam exportParam,
                                                              HttpServletResponse response,
                                                              @PathVariable String organizationId) {
        return Results.success(invoiceApplyHeaderService.exportData(invoiceApplyHeaderDTO));
    }

}

