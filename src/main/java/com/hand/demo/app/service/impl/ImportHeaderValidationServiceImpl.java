package com.hand.demo.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.demo.api.dto.InvoiceApplyHeaderDTO;
import com.hand.demo.domain.entity.InvoiceApplyHeader;
import com.hand.demo.domain.repository.InvoiceApplyHeaderRepository;
import org.apache.commons.lang.StringUtils;
import org.hzero.boot.imported.app.service.BatchValidatorHandler;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidator;
import org.hzero.boot.imported.infra.validator.annotation.ImportValidators;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class ImportHeaderValidationServiceImpl extends BatchValidatorHandler {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    InvoiceApplyHeaderRepository invoiceApplyHeaderRepository;

    @Override
    public boolean validate(List<String> data) {
//        if (data.isEmpty()){
//            return true;
//        }
        return false;
    }
}
