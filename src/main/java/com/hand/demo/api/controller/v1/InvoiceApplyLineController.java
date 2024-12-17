package com.hand.demo.api.controller.v1;

import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
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
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hand.demo.app.service.InvoiceApplyLineService;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * (InvoiceApplyLine)表控制层
 *
 * @author Zamzam
 * @since 2024-12-03 11:01:37
 */

@RestController("invoiceApplyLineController.v1")
@RequestMapping("/v1/{organizationId}/invoice-apply-lines")
public class InvoiceApplyLineController extends BaseController {

    @Autowired
    private InvoiceApplyLineRepository invoiceApplyLineRepository;

    @Autowired
    private InvoiceApplyLineService invoiceApplyLineService;

    @Autowired
    private InvoiceApplyHeaderService invoiceApplyHeaderService;

    @ApiOperation(value = "List")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<InvoiceApplyLine>> list(InvoiceApplyLineDTO invoiceApplyLine, @PathVariable Long organizationId,
                                                       @ApiIgnore @SortDefault(value = InvoiceApplyLine.FIELD_APPLY_LINE_ID,
                                                               direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<InvoiceApplyLine> list = invoiceApplyLineService.selectList(pageRequest, invoiceApplyLine);
        return Results.success(list);
    }

    @ApiOperation(value = "Detail")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{applyLineId}/detail")
    public ResponseEntity<InvoiceApplyLine> detail(@PathVariable Long applyLineId) {
        //V 1.1 [S]
//        InvoiceApplyLine invoiceApplyLine = invoiceApplyLineRepository.selectByPrimary(applyLineId);
        return Results.success(invoiceApplyLineService.selectDetail(applyLineId));
        //V 1.1 [E]
    }

    @ApiOperation(value = "Save or Update")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<List<InvoiceApplyLineDTO>> save(@PathVariable Long organizationId, @RequestBody List<InvoiceApplyLineDTO> invoiceApplyLines) {
        validList(invoiceApplyLines, InvoiceApplyLine.Save.class);
        SecurityTokenHelper.validTokenIgnoreInsert(invoiceApplyLines);
        invoiceApplyLines.forEach(item -> item.setTenantId(organizationId));
        invoiceApplyLineService.saveData(invoiceApplyLines);
        return Results.success(invoiceApplyLines);
    }

    @ApiOperation(value = "删除")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody List<InvoiceApplyLineDTO> invoiceApplyLines) {
        SecurityTokenHelper.validToken(invoiceApplyLines);
        //V 1.1 [S] get full data before deleted
        for (int i = 0; i < invoiceApplyLines.size(); i++) {
            invoiceApplyLines.set(i, invoiceApplyLineRepository.selectByPrimary(invoiceApplyLines.get(i).getApplyLineId()));
        }
        //V 1.1 [E]
        List<InvoiceApplyLine> invoiceApplyLines2 = new ArrayList<>();
        BeanUtils.copyProperties(invoiceApplyLines, invoiceApplyLines2);
        invoiceApplyLineRepository.batchDeleteByPrimaryKey(invoiceApplyLines2);
        //V 1.1 [S] recalculate
        invoiceApplyLineService.calculate(invoiceApplyLines,true);
        invoiceApplyLineService.redisUpdate(invoiceApplyLines);
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderDTOList = new ArrayList<>();
        BeanUtils.copyProperties(invoiceApplyLines, invoiceApplyHeaderDTOList);
        invoiceApplyHeaderService.redisUpdate(invoiceApplyHeaderDTOList);
        //V 1.1 [E]
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "export")
    @GetMapping("/export")
    @ExcelExport(value = InvoiceApplyLineDTO.class)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<InvoiceApplyLineDTO>> export(InvoiceApplyLineDTO invoiceApplyLineDTO,
                                                            ExportParam exportParam,
                                                            HttpServletResponse response,
                                                            @PathVariable String organizationId){
        return Results.success(invoiceApplyLineService.exportData(invoiceApplyLineDTO));
    }

}

