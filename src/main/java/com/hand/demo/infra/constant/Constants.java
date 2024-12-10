package com.hand.demo.infra.constant;

/**
 * Utils
 */
public class Constants {

    public static final String LOV_CODE_STATUS = "HEXAM-47835-STATUS";
    public static final String LOV_CODE_INVOICE_COLOR = "HEXAM-47835-INVOICE-COLOR";
    public static final String LOV_CODE_INVOICE_TYPE = "HEXAM-47835-INVOICE-TYPE";
    public static final String ERROR_CODE_STATUS = "hexam-47835.status_invalid";
    public static final String ERROR_CODE_INVOICE_COLOR = "hexam-47835.inv_color_invalid";
    public static final String ERROR_CODE_INVOICE_TYPE = "hexam-47835.inv_type_invalid";
    public static final String ERROR_CODE_HEADER_INVALID = "hexam-47835.invalid_header_id";
    public static final String RULE_CODE_HEADER_NUMBER = "HEXAM-47835-HEADER-NUMBER";
    public static final String TEMPLATE_CODE_IMPORT = "HEXAM-47835-HEADER";
    public static final String TEMPLATE_CODE_MESSAGE = "HEXAM-47835-MESSAGE";
    public static final String EMAIL_SENDER = "muhammad.ari@hand-global.com";
    public static final String REDIS_QUEUE = "invoiceInfo_47835";
    public static final String REDIS_INVOICE_HEADER_PREFIX = "hexam-47835:InvoiceHeader:";
    public static final String REDIS_LOV_CODE_PREFIX = "hexam-47835:lov:";
    public static final String REDIS_INVOICE_LINE_PREFIX = "hexam-47835:InvoiceLine:";

    private Constants() {}


}
