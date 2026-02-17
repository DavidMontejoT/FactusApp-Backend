package com.factusapp.service;

import com.factusapp.dto.request.FactusInvoiceRequest;
import com.factusapp.dto.response.FactusAuthResponse;
import com.factusapp.dto.response.FactusDocumentResponse;
import com.factusapp.dto.response.FactusInvoiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio para integración con Factus API
 * Documentación: https://docs.factus.com.co
 *
 * @author FactusApp
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FactusService {

    @Value("${factus.api.base-url}")
    private String factusBaseUrl;

    @Value("${factus.api.client-id}")
    private String clientId;

    @Value("${factus.api.client-secret}")
    private String clientSecret;

    @Value("${factus.api.username}")
    private String factusUsername;

    @Value("${factus.api.password}")
    private String factusPassword;

    @Value("${factus.api.demo-mode:false}")
    private boolean demoMode;

    private final RestTemplate restTemplate = new RestTemplate();

    private String accessToken;
    private LocalDateTime tokenExpiration;

    /**
     * Obtener token de acceso OAuth2 de Factus API
     */
    public String getAccessToken() {
        // Si el token aún es válido, retornarlo
        if (accessToken != null && tokenExpiration != null &&
            tokenExpiration.isAfter(LocalDateTime.now().plusMinutes(5))) {
            return accessToken;
        }

        // Obtener nuevo token
        return authenticate();
    }

    /**
     * Autenticar con Factus API usando OAuth2 Client Credentials
     */
    private String authenticate() {
        String url = factusBaseUrl + "/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Body con client_credentials y password
        Map<String, Object> body = new HashMap<>();
        body.put("grant_type", "password");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("username", factusUsername);
        body.put("password", factusPassword);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<FactusAuthResponse> response = restTemplate.postForEntity(
                url, request, FactusAuthResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                FactusAuthResponse authResponse = response.getBody();
                this.accessToken = authResponse.getAccessToken();

                // Calcular expiración (expires_in está en segundos)
                this.tokenExpiration = LocalDateTime.now()
                    .plusSeconds(authResponse.getExpiresIn() - 60); // 1 min de margen

                log.info("Autenticación exitosa con Factus API. Token expira en: {}",
                    authResponse.getExpiresIn());

                return accessToken;
            } else {
                log.error("Error en autenticación con Factus API: {}",
                    response.getStatusCode());
                throw new RuntimeException("Error autenticando con Factus API");
            }
        } catch (Exception e) {
            log.error("Excepción al autenticar con Factus API: {}", e.getMessage(), e);
            throw new RuntimeException("Error conectando con Factus API: " + e.getMessage());
        }
    }

    /**
     * Enviar factura electrónica a Factus API
     */
    public FactusInvoiceResponse sendElectronicInvoice(FactusInvoiceRequest request) {
        // MODO DEMO: Simular respuesta de Factus API
        if (demoMode) {
            return simulateEmitInvoice(request);
        }

        // Endpoint correcto según documentación de Factus API
        String url = factusBaseUrl + "/v1/bills/validate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());

        HttpEntity<FactusInvoiceRequest> httpRequest = new HttpEntity<>(request, headers);

        try {
            log.info("Enviando factura electrónica a Factus API para receptor: {}",
                request.getCustomer() != null ? request.getCustomer().getIdentification() : "N/A");

            log.debug("Request payload: {}", new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request));

            ResponseEntity<FactusInvoiceResponse> response = restTemplate.postForEntity(
                url, httpRequest, FactusInvoiceResponse.class);

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                FactusInvoiceResponse factusResponse = response.getBody();

                if (factusResponse != null) {
                    log.info("Factura electrónica creada exitosamente. Número: {}, CUFE: {}, Estado: {}",
                        factusResponse.getNumero(),
                        factusResponse.getCufe(),
                        factusResponse.getEstado());

                    return factusResponse;
                }
            }

            log.error("Error creando factura electrónica: {}", response.getStatusCode());
            throw new RuntimeException("Error creando factura electrónica");

        } catch (Exception e) {
            log.error("Excepción enviando factura a Factus API: {}", e.getMessage(), e);
            throw new RuntimeException("Error enviando factura a Factus API: " + e.getMessage());
        }
    }

    /**
     * Simula la emisión de una factura (MODO DEMO)
     */
    private FactusInvoiceResponse simulateEmitInvoice(FactusInvoiceRequest request) {
        log.info("MODO DEMO: Simulando emisión de factura electrónica");

        // Generar datos simulados realistas
        String invoiceNumber = "SETP" + String.format("%09d", (int)(Math.random() * 1000000));
        String cufe = generateCufe();
        String qrCode = generateQrCode(cufe);
        String factusId = "FACTUS-" + System.currentTimeMillis();

        // Construir objeto con estructura anidada correcta
        FactusInvoiceResponse.Bill bill = FactusInvoiceResponse.Bill.builder()
                .id(factusId)
                .numero(invoiceNumber)
                .cufe(cufe)
                .qr(qrCode)
                .urlPdf("#demo-pdf-" + invoiceNumber)
                .urlXml("#demo-xml-" + invoiceNumber)
                .validated("true")
                .build();

        FactusInvoiceResponse.InvoiceData data = FactusInvoiceResponse.InvoiceData.builder()
                .bill(bill)
                .build();

        return FactusInvoiceResponse.builder()
                .status("Created")
                .message("Factura registrada exitosamente (MODO DEMO)")
                .mensaje("Factura registrada exitosamente (MODO DEMO)")
                .data(data)
                .build();
    }

    /**
     * Genera un CUDE falso para demo (formato DIAN Colombia)
     */
    private String generateCufe() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder cufe = new StringBuilder();
        for (int i = 0; i < 96; i++) {
            cufe.append(chars.charAt((int)(Math.random() * chars.length())));
        }
        return cufe.toString();
    }

    /**
     * Genera un código QR falso para demo
     */
    private String generateQrCode(String cufe) {
        return "https://catalogo-vpfe-homo.dian.gov.co/Document/DownloadDocument?cuce=" + cufe;
    }

    /**
     * Consultar estado de una factura en Factus API
     */
    public FactusInvoiceResponse getInvoiceStatus(String invoiceId) {
        String url = factusBaseUrl + "/v1/bills/" + invoiceId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<FactusInvoiceResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, request, FactusInvoiceResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            throw new RuntimeException("Error consultando estado de factura");

        } catch (Exception e) {
            log.error("Excepción consultando factura: {}", e.getMessage(), e);
            throw new RuntimeException("Error consultando factura en Factus API: " + e.getMessage());
        }
    }

    /**
     * Anular una factura electrónica
     */
    public FactusInvoiceResponse cancelInvoice(String invoiceId, String motivo) {
        String url = factusBaseUrl + "/v1/bills/" + invoiceId + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());

        Map<String, String> body = new HashMap<>();
        body.put("motive", motivo);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            log.info("Anulando factura electrónica: {}. Motivo: {}", invoiceId, motivo);

            ResponseEntity<FactusInvoiceResponse> response = restTemplate.postForEntity(
                url, request, FactusInvoiceResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Factura anulada exitosamente: {}", invoiceId);
                return response.getBody();
            }

            throw new RuntimeException("Error anulando factura");

        } catch (Exception e) {
            log.error("Excepción anulando factura: {}", e.getMessage(), e);
            throw new RuntimeException("Error anulando factura en Factus API: " + e.getMessage());
        }
    }

    /**
     * Descargar XML de una factura electrónica
     * Documentación: https://developers.factus.com.co/facturas/descargar-xml/
     * Endpoint: GET /v1/bills/download-xml/{{number}}
     */
    public FactusDocumentResponse downloadInvoiceXml(String invoiceNumber) {
        // MODO DEMO: Si la URL empieza con #demo-, simular descarga
        if (demoMode) {
            return simulateDownloadXml(invoiceNumber);
        }

        // Usar el número de factura, no el ID
        String url = factusBaseUrl + "/v1/bills/download-xml/" + invoiceNumber;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            log.info("Descargando XML de factura: {} con URL: {}", invoiceNumber, url);

            ResponseEntity<FactusDocumentResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, request, FactusDocumentResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("XML descargado exitosamente. Archivo: {}",
                    response.getBody().getFilename());
                return response.getBody();
            }

            throw new RuntimeException("Error descargando XML de factura");

        } catch (Exception e) {
            log.error("Excepción descargando XML: {}", e.getMessage(), e);
            throw new RuntimeException("Error descargando XML de Factus API: " + e.getMessage());
        }
    }

    /**
     * Descargar PDF de una factura electrónica
     * Documentación: https://developers.factus.com.co/facturas/descargar-factura/
     * Endpoint: GET /v1/bills/download-pdf/{{number}}
     */
    public FactusDocumentResponse downloadInvoicePdf(String invoiceNumber) {
        // MODO DEMO: Si la URL empieza con #demo-, simular descarga
        if (demoMode) {
            return simulateDownloadPdf(invoiceNumber);
        }

        // Usar el número de factura, no el ID
        String url = factusBaseUrl + "/v1/bills/download-pdf/" + invoiceNumber;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            log.info("Descargando PDF de factura: {} con URL: {}", invoiceNumber, url);

            ResponseEntity<FactusDocumentResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, request, FactusDocumentResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("PDF descargado exitosamente. Archivo: {}",
                    response.getBody().getFilename());
                return response.getBody();
            }

            throw new RuntimeException("Error descargando PDF de factura");

        } catch (Exception e) {
            log.error("Excepción descargando PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error descargando PDF de Factus API: " + e.getMessage());
        }
    }

    /**
     * Simula la descarga de XML (MODO DEMO)
     */
    private FactusDocumentResponse simulateDownloadXml(String invoiceNumber) {
        log.info("MODO DEMO: Simulando descarga de XML para factura {}", invoiceNumber);

        // Construir data object con estructura correcta
        FactusDocumentResponse.DocumentData data = FactusDocumentResponse.DocumentData.builder()
                .fileName("FACTURA_" + invoiceNumber + ".xml")
                .xmlBase64Encoded("PD94bWwgdmVyc2lvbj0iLz4+PC94eG1sPg==") // XML base64 simulado
                .build();

        return FactusDocumentResponse.builder()
                .status("Success")
                .message("XML descargado exitosamente (MODO DEMO)")
                .data(data)
                .build();
    }

    /**
     * Simula la descarga de PDF (MODO DEMO)
     */
    private FactusDocumentResponse simulateDownloadPdf(String invoiceNumber) {
        log.info("MODO DEMO: Simulando descarga de PDF para factura {}", invoiceNumber);

        // Construir data object con estructura correcta
        FactusDocumentResponse.DocumentData data = FactusDocumentResponse.DocumentData.builder()
                .fileName("FACTURA_" + invoiceNumber + ".pdf")
                .pdfBase64Encoded("JVBERi0xL...") // PDF base64 simulado
                .build();

        return FactusDocumentResponse.builder()
                .status("Success")
                .message("PDF descargado exitosamente (MODO DEMO)")
                .data(data)
                .build();
    }
}
