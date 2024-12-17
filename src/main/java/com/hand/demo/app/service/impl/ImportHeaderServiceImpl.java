package com.hand.demo.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.app.service.InvoiceApplyHeaderService;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.entity.InvoiceApplyLine;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import com.hand.demo.infra.constant.Constants;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.domian.Condition;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ImportService(templateCode = Constants.TEMPLATE_CODE_IMPORT, sheetName = "header")
public class ImportHeaderServiceImpl implements IBatchImportService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Autowired
    InvoiceApplyHeaderService invoiceApplyHeaderService;

    @Override
    public Boolean doImport(List<String> data) {
        List<InvoiceApplyHeaderDTO> invoiceApplyHeaderList = new ArrayList<>();
        Long organizationId = DetailsHelper.getUserDetails().getOrganizationId();
        try {
            for (int i = 0; i < data.size(); i++) {
                InvoiceApplyHeaderDTO invoiceApplyHeader = objectMapper.readValue(data.get(i), InvoiceApplyHeaderDTO.class);
                invoiceApplyHeader.setTenantId(organizationId);

                if (StringUtils.isNotBlank(invoiceApplyHeader.getApplyHeaderNumber())) {
                    InvoiceApplyHeader existingHeader = new InvoiceApplyHeader();
                    existingHeader.setApplyHeaderNumber(invoiceApplyHeader.getApplyHeaderNumber());

                    // Retrieve the existing header
                    existingHeader = invoiceApplyHeaderRepository.selectOne(existingHeader);
//                    Criteria criteria = new Criteria().where(InvoiceApplyHeader.FIELD_APPLY_HEADER_NUMBER);
//                    existingHeader = invoiceApplyHeaderRepository.selectOneOptional(existingHeader, criteria);

                    if (existingHeader != null) {
                        invoiceApplyHeader.setApplyHeaderId(existingHeader.getApplyHeaderId());
                        invoiceApplyHeader.setObjectVersionNumber(existingHeader.getObjectVersionNumber());
                    }
                }

                invoiceApplyHeaderList.add(i, invoiceApplyHeader);
            }
            invoiceApplyHeaderService.saveData(organizationId, invoiceApplyHeaderList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
