package com.factusapp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de Factus API al crear una factura electrónica
 * Basado en documentación: https://developers.factus.com.co/facturas/crear-y-validar-factura/factura-avanzada/
 *
 * @author FactusApp
 * @version 2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactusInvoiceResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private InvoiceData data;

    @JsonProperty("mensaje")
    private String mensaje;

    /**
     * Datos de la factura en la respuesta de Factus API
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InvoiceData {

        @JsonProperty("bill")
        private Bill bill;
    }

    /**
     * Información de la factura (bill)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Bill {

        @JsonProperty("id")
        private String id;

        @JsonProperty("number")
        private String numero;

        @JsonProperty("cufe")
        private String cufe;

        @JsonProperty("qr")
        private String qr;

        @JsonProperty("qr_image")
        private String qrImage;

        @JsonProperty("status")
        private Integer status;

        @JsonProperty("validated")
        private String validated;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("url_pdf")
        private String urlPdf;

        @JsonProperty("url_xml")
        private String urlXml;

        @JsonProperty("gross_value")
        private String grossValue;

        @JsonProperty("taxable_amount")
        private String taxableAmount;

        @JsonProperty("tax_amount")
        private String taxAmount;

        @JsonProperty("total")
        private String total;
    }

    // Métodos de compatibilidad para código existente
    public String getId() {
        return data != null && data.getBill() != null ? data.getBill().getId() : null;
    }

    public String getNumero() {
        return data != null && data.getBill() != null ? data.getBill().getNumero() : null;
    }

    public String getCufe() {
        return data != null && data.getBill() != null ? data.getBill().getCufe() : null;
    }

    public String getQr() {
        return data != null && data.getBill() != null ? data.getBill().getQr() : null;
    }

    public String getEstado() {
        return status;
    }

    public String getUrlPdf() {
        return data != null && data.getBill() != null ? data.getBill().getUrlPdf() : null;
    }

    public String getUrlXml() {
        return data != null && data.getBill() != null ? data.getBill().getUrlXml() : null;
    }
}
