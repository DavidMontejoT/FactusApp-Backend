package com.factusapp.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de Factus API al descargar un documento (PDF/XML)
 * El archivo viene en formato Base64
 *
 * @author FactusApp
 * @version 2.0 - Actualizado según estructura real de API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactusDocumentResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private DocumentData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DocumentData {
        @JsonProperty("file_name")
        private String fileName;

        @JsonProperty("xml_base_64_encoded")
        private String xmlBase64Encoded;

        @JsonProperty("pdf_base_64_encoded")
        private String pdfBase64Encoded;
    }

    // Métodos de compatibilidad para código existente
    public String getContent() {
        if (data != null) {
            return data.xmlBase64Encoded != null ? data.xmlBase64Encoded : data.pdfBase64Encoded;
        }
        return null;
    }

    public String getFilename() {
        return data != null ? data.fileName : null;
    }
}
