package com.hand.demo.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.api.dto.InvoiceApplyLineDTO;
import com.hand.demo.app.service.InvoiceApplyLineService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyLineRepository;
import com.hand.demo.infra.constant.Constants;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ImportService(templateCode = Constants.TEMPLATE_CODE_IMPORT, sheetName = "line")
public class ImportLineServiceImpl implements IBatchImportService {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    InvoiceApplyLineRepository invoiceApplyLineRepository;
    @Autowired
    InvoiceApplyLineService invoiceApplyLineService;

    @Override
    public Boolean doImport(List<String> data) {
        List<InvoiceApplyLineDTO> invoiceApplyLineList = new ArrayList<>();
        Long organizationId = DetailsHelper.getUserDetails().getOrganizationId();
        try {
            for (int i = 0; i < data.size(); i++) {
                InvoiceApplyLineDTO invoiceApplyLine = objectMapper.readValue(data.get(i), InvoiceApplyLineDTO.class);
                invoiceApplyLine.setTenantId(organizationId);

                // Retrieve the existing line
                InvoiceApplyLine existingLine = invoiceApplyLineRepository.selectByPrimary(invoiceApplyLine.getApplyLineId());

                if (existingLine != null) {;
                    invoiceApplyLine.setObjectVersionNumber(existingLine.getObjectVersionNumber());
                }

                invoiceApplyLineList.add(i, invoiceApplyLine);
            }
            invoiceApplyLineService.saveData(invoiceApplyLineList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
