package com.hand.demo.api.controller.v1;

import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.infra.util.Utils;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import springfox.documentation.annotations.ApiIgnore;
import com.hand.demo.api.dto.ExcelReportReqDTO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

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

    @Autowired
    private Utils utils;

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
        return Results.success(invoiceApplyHeaderService.selectDetail(applyHeaderId));
    }

    @ApiOperation(value = "Create or Update")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<InvoiceApplyHeaderDTO>> save(@PathVariable Long organizationId,
                                                            @RequestBody List<InvoiceApplyHeaderDTO> invoiceApplyHeaders) {
        validObject(invoiceApplyHeaders);
        SecurityTokenHelper.validTokenIgnoreInsert(invoiceApplyHeaders);
        invoiceApplyHeaders.forEach(item -> item.setTenantId(organizationId));
        invoiceApplyHeaderService.saveData(organizationId, invoiceApplyHeaders);
        return Results.success(invoiceApplyHeaders);
    }

    @ApiOperation(value = "delete")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@PathVariable Long organizationId, @RequestBody List<InvoiceApplyHeaderDTO> invoiceApplyHeaders) {
        SecurityTokenHelper.validToken(invoiceApplyHeaders);
        //V 1.1 [S]
//        invoiceApplyHeaderRepository.batchDeleteByPrimaryKey(invoiceApplyHeaders);
        invoiceApplyHeaderService.remove(organizationId, invoiceApplyHeaders);
        //V 1.1 [E]
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "export")
    @GetMapping("/export")
    @ExcelExport(value = InvoiceApplyHeaderDTO.class)
    public ResponseEntity<List<InvoiceApplyHeaderDTO>> export(InvoiceApplyHeaderDTO invoiceApplyHeaderDTO,
                                                              ExportParam exportParam,
                                                              HttpServletResponse response,
                                                              @PathVariable String organizationId) {
        return Results.success(invoiceApplyHeaderService.exportData(invoiceApplyHeaderDTO));
    }

    @ApiOperation(value = "excelReport")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/excel-report")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<ExcelReportReqDTO> excelReport(@PathVariable Long organizationId,
                                                         ExcelReportReqDTO excelReportReqDTO) {
        return Results.success(invoiceApplyHeaderService.selectExcelReport(organizationId, excelReportReqDTO));
    }

}

