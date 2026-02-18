package com.factusapp.service;

import com.factusapp.dto.request.FactusInvoiceRequest;
import com.factusapp.dto.request.InvoiceRequest;
import com.factusapp.dto.response.FactusDocumentResponse;
import com.factusapp.dto.response.FactusInvoiceResponse;
import com.factusapp.dto.response.InvoiceResponse;
import com.factusapp.model.Invoice;
import com.factusapp.model.InvoiceItem;
import com.factusapp.model.Invoice.InvoiceStatus;
import com.factusapp.model.Product;
import com.factusapp.model.User;
import com.factusapp.repository.ClientRepository;
import com.factusapp.repository.InvoiceRepository;
import com.factusapp.repository.InvoiceItemRepository;
import com.factusapp.repository.ProductRepository;
import com.factusapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestión de facturas
 *
 * @author FactusApp
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final FactusService factusService;

    @Value("${factus.api.demo-mode:false}")
    private boolean demoMode;

    /**
     * Crear una nueva factura
     */
    @Transactional
    public InvoiceResponse createInvoice(Long userId, InvoiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar límites del plan
        if (!user.canCreateMoreInvoices()) {
            throw new RuntimeException("Has alcanzado el límite de facturas de tu plan (" +
                    user.getMaxInvoices() + " facturas/mes). " +
                    "Haz upgrade a un plan superior para continuar.");
        }

        // Buscar cliente
        if (request.getClientId() == null) {
            throw new RuntimeException("El cliente es obligatorio");
        }

        var client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Verificar que el cliente pertenezca al usuario
        if (!client.getUser().getId().equals(userId)) {
            throw new RuntimeException("El cliente no existe o no te pertenece");
        }

        // Crear factura
        Invoice invoice = new Invoice();
        invoice.setUser(user);
        invoice.setClient(client);
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setPaymentMethod(request.getPaymentMethod());
        invoice.setNotes(request.getNotes());
        invoice.setIssueDate(LocalDateTime.now());

        // Crear items de factura
        List<InvoiceItem> items = new ArrayList<>();
        for (var itemRequest : request.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProductName(itemRequest.getProductName());
            item.setQuantity(itemRequest.getQuantity());
            item.setPriceUnit(itemRequest.getPriceUnit());

            // Si viene de un producto registrado, buscarlo
            if (itemRequest.getProductId() != null) {
                Product product = productRepository.findById(itemRequest.getProductId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                // Verificar que el producto pertenezca al usuario
                if (!product.getUser().getId().equals(userId)) {
                    throw new RuntimeException("El producto no existe o no te pertenece");
                }

                item.setProduct(product);

                // Actualizar stock del producto
                if (product.getStock() < itemRequest.getQuantity()) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
                }
                product.setStock(product.getStock() - itemRequest.getQuantity());
                productRepository.save(product);
            }

            item.calculateTotals();
            items.add(item);
        }

        invoice.setItems(items);

        // Calcular totales de la factura
        invoice.calculateTotals();

        // Guardar factura
        invoice = invoiceRepository.save(invoice);

        // Actualizar contador de facturas del usuario
        user.setInvoicesCountMonthly(user.getInvoicesCountMonthly() + 1);
        userRepository.save(user);

        log.info("Factura creada: {} para usuario: {}", invoice.getId(), userId);

        // Si el usuario tiene plan BASIC o FULL, enviar a DIAN automáticamente
        if (user.getPlan() != User.Plan.FREE && request.getEmitToDIAN() != null && request.getEmitToDIAN()) {
            try {
                emitInvoiceToDIAN(invoice.getId(), userId);
            } catch (Exception e) {
                log.warn("No se pudo emitir factura a DIAN: {}", e.getMessage());
                // No fallamos el proceso si falla DIAN, la factura se guarda localmente
            }
        }

        return InvoiceResponse.fromInvoice(invoice);
    }

    /**
     * Emitir factura a DIAN a través de Factus API
     */
    @Transactional
    public void emitInvoiceToDIAN(Long invoiceId, Long userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // Verificar que la factura pertenezca al usuario
        if (!invoice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para emitir esta factura");
        }

        // Verificar que la factura esté en borrador
        if (!invoice.isDraft()) {
            throw new RuntimeException("Solo se pueden emitir facturas en estado borrador");
        }

        User user = invoice.getUser();

        // Verificar que el usuario tenga plan que permite DIAN
        // En modo demo, permitimos emisión sin importar el plan
        if (!demoMode && user.getPlan() == User.Plan.FREE) {
            throw new RuntimeException("El plan FREE no permite facturación electrónica DIAN. " +
                    "Actualiza al plan BASIC o FULL.");
        }

        // Preparar request para Factus API
        FactusInvoiceRequest factusRequest = buildFactusRequest(invoice);

        // Enviar a Factus API
        FactusInvoiceResponse factusResponse = factusService.sendElectronicInvoice(factusRequest);

        // Actualizar factura con respuesta DIAN
        invoice.setFactusInvoiceId(factusResponse.getId());
        invoice.setFactusInvoiceNumber(factusResponse.getNumero());
        invoice.setCufe(factusResponse.getCufe());
        invoice.setQrCode(factusResponse.getQr());
        invoice.setFactusPdfUrl(factusResponse.getUrlPdf());
        invoice.setFactusXmlUrl(factusResponse.getUrlXml());
        invoice.setDianStatus("REGISTERED");
        invoice.setFactusStatus(factusResponse.getEstado());
        invoice.setStatus(Invoice.InvoiceStatus.EMITTED);

        invoiceRepository.save(invoice);

        log.info("Factura {} emitida a DIAN exitosamente. CUFE: {}", invoiceId, factusResponse.getCufe());
    }

    /**
     * Construye request para Factus API a partir de una factura local
     * Basado en documentación: https://developers.factus.com.co/facturas/crear-y-validar-factura/factura-avanzada/
     */
    private FactusInvoiceRequest buildFactusRequest(Invoice invoice) {
        List<FactusInvoiceRequest.Item> items = new ArrayList<>();

        // Mapear items de factura
        for (InvoiceItem item : invoice.getItems()) {
            FactusInvoiceRequest.Item factusItem = FactusInvoiceRequest.Item.builder()
                .code_reference(item.getProductName().substring(0, Math.min(20, item.getProductName().length())))
                .name(item.getProductName())
                .quantity(item.getQuantity())
                .discount_rate(0.0f) // Por defecto sin descuento
                .price(item.getPriceUnit().floatValue())
                .tax_rate("19.00") // TODO: Configurar según producto
                .unit_measure_id(70) // 70 = Unidad
                .standard_code_id(1) // 1 = Estándar de adopción del contribuyente
                .is_excluded(0) // 0 = No excluido de IVA
                .tribute_id(1) // 1 = IVA
                .build();

            items.add(factusItem);
        }

        // Mapear cliente
        // TODO: Configurar IDs correctos según las tablas de referencia de Factus
        FactusInvoiceRequest.Customer customer = FactusInvoiceRequest.Customer.builder()
            .identification_document_id(getDocumentTypeId(invoice.getClient().getDocumentType()))
            .identification(invoice.getClient().getDocumentNumber())
            .dv(null) // TODO: Calcular DV para NIT
            .company(invoice.getClient().getName())
            .trade_name(invoice.getClient().getName())
            .names(invoice.getClient().getName()) // API requires names to be a valid string
            .address(invoice.getClient().getAddress() != null ? invoice.getClient().getAddress() : "Calle 1 # 1-1")
            .email(invoice.getClient().getEmail())
            .phone(invoice.getClient().getPhone())
            .legal_organization_id(1) // 1 = Persona jurídica
            .tribute_id(21) // 21 = No aplica (común para clientes)
            .municipality_id(980) // TODO: Buscar ID correcto del municipio
            .build();

        // Establecimiento (requerido por Factus API)
        // TODO: Obtener esta información del perfil del usuario
        FactusInvoiceRequest.Establishment establishment = FactusInvoiceRequest.Establishment.builder()
            .name(invoice.getUser().getName())
            .address("Calle Principal #123") // TODO: Configurar dirección real
            .phone_number("3001234567") // TODO: Configurar teléfono real
            .email(invoice.getUser().getEmail())
            .municipality_id(980) // TODO: Buscar ID correcto del municipio
            .build();

        // Generar código de referencia único
        String referenceCode = "FAC-" + invoice.getId() + "-" + System.currentTimeMillis();

        return FactusInvoiceRequest.builder()
                .document("01") // 01 = Factura electrónica de Venta
                .numbering_range_id(null) // Si hay un solo rango, es opcional
                .reference_code(referenceCode)
                .observation(invoice.getNotes())
                .payment_method_code(getPaymentMethodCode(invoice.getPaymentMethod()))
                .customer(customer)
                .items(items)
                .establishment(establishment)
                .build();
    }

    /**
     * Convierte el tipo de documento al ID de Factus API
     */
    private Integer getDocumentTypeId(com.factusapp.model.Client.DocumentType documentType) {
        // Mapeo según documentación de Factus API
        // Valores comunes: 3=Cédula, 6=NIT, etc.
        return switch (documentType) {
            case CC -> 3;
            case NIT -> 6;
            case CE -> 4;
            case TI -> 2;
            case PP -> 7;
            case IDC -> 8; // Identificador Único de Cliente
        };
    }

    /**
     * Convierte el método de pago al código de Factus API
     */
    private Integer getPaymentMethodCode(Invoice.PaymentMethod paymentMethod) {
        // Mapeo según documentación de Factus API
        // Valores comunes: 10=Efectivo, 42=Consignación, 48=Transferencia, etc.
        return switch (paymentMethod) {
            case CASH -> 10;
            case TRANSFER -> 48;
            case CARD -> 41;
            case NEQUI -> 42; // Consignación
            case DAVIPLATA -> 42; // Consignación
        };
    }

    /**
     * Sincronizar estado de factura con DIAN
     */
    @Transactional
    public void syncInvoiceStatusWithDIAN(Long invoiceId, Long userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (!invoice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para verificar esta factura");
        }

        if (invoice.getFactusInvoiceId() == null) {
            throw new RuntimeException("Esta factura no ha sido emitida a DIAN");
        }

        try {
            FactusInvoiceResponse factusResponse = factusService.getInvoiceStatus(invoice.getFactusInvoiceId());

            // Actualizar estado
            invoice.setFactusStatus(factusResponse.getEstado());
            invoice.setDianStatus(factusResponse.getEstado());

            if ("RECHAZADA".equalsIgnoreCase(factusResponse.getEstado())) {
                invoice.setStatus(Invoice.InvoiceStatus.DRAFT); // Volver a borrador si fue rechazada
            }

            invoiceRepository.save(invoice);

            log.info("Estado de factura {} sincronizado con DIAN: {}", invoiceId, factusResponse.getEstado());
        } catch (Exception e) {
            log.error("Error sincronizando estado con DIAN: {}", e.getMessage());
            throw new RuntimeException("Error sincronizando con DIAN: " + e.getMessage());
        }
    }

    /**
     * Obtener todas las facturas de un usuario
     */
    public List<InvoiceResponse> getAllInvoices(Long userId) {
        return invoiceRepository.findByUserId(userId)
                .stream()
                .map(InvoiceResponse::fromInvoice)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Buscar factura por ID
     */
    public InvoiceResponse getInvoiceById(Long userId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // Verificar que la factura pertenezca al usuario
        if (!invoice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver esta factura");
        }

        return InvoiceResponse.fromInvoice(invoice);
    }

    /**
     * Eliminar una factura (solo si está en borrador)
     */
    @Transactional
    public void deleteInvoice(Long userId, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // Verificar que la factura pertenezca al usuario
        if (!invoice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta factura");
        }

        // Solo permitir eliminar facturas en borrador
        if (!invoice.isDraft()) {
            throw new RuntimeException("Solo se pueden eliminar facturas en estado borrador");
        }

        invoiceRepository.delete(invoice);

        log.info("Factura eliminada: {}", invoiceId);
    }

    /**
     * Obtener facturas por estado
     */
    public List<InvoiceResponse> getInvoicesByStatus(Long userId, InvoiceStatus status) {
        return invoiceRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(InvoiceResponse::fromInvoice)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Descargar XML de una factura desde Factus API
     */
    public FactusDocumentResponse downloadInvoiceXml(Long invoiceId, Long userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (!invoice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para descargar esta factura");
        }

        if (invoice.getFactusInvoiceNumber() == null) {
            throw new RuntimeException("Esta factura no ha sido emitida a DIAN");
        }

        // Usar el número de factura, no el ID
        FactusDocumentResponse document = factusService.downloadInvoiceXml(invoice.getFactusInvoiceNumber());

        log.info("XML descargado para factura {}: {}", invoiceId, document.getFilename());

        return document;
    }

    /**
     * Descargar PDF de una factura desde Factus API
     */
    public FactusDocumentResponse downloadInvoicePdf(Long invoiceId, Long userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (!invoice.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para descargar esta factura");
        }

        if (invoice.getFactusInvoiceNumber() == null) {
            throw new RuntimeException("Esta factura no ha sido emitida a DIAN");
        }

        // Usar el número de factura, no el ID
        FactusDocumentResponse document = factusService.downloadInvoicePdf(invoice.getFactusInvoiceNumber());

        log.info("PDF descargado para factura {}: {}", invoiceId, document.getFilename());

        return document;
    }
}
