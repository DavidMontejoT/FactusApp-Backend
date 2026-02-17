package com.factusapp.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request para enviar factura a Factus API
 * Basado en documentación oficial: https://developers.factus.com.co/facturas/crear-y-validar-factura/factura-avanzada/
 *
 * @author FactusApp
 * @version 2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactusInvoiceRequest {

    /**
     * Código del tipo de documento (01 = Factura electrónica de Venta)
     */
    private String document;

    /**
     * ID del rango de numeración
     * Si tienes un solo rango activo, este campo es opcional
     */
    private Integer numbering_range_id;

    /**
     * Código único de referencia para la factura (obligatorio)
     */
    @NotNull(message = "El código de referencia es obligatorio")
    private String reference_code;

    /**
     * Observación de la factura (opcional, máximo 250 caracteres)
     */
    private String observation;

    /**
     * Código del método de pago (opcional, default: 10 = efectivo)
     */
    private Integer payment_method_code;

    /**
     * Información del cliente (obligatorio)
     */
    @NotNull(message = "El cliente es obligatorio")
    private Customer customer;

    /**
     * Items de la factura (obligatorio)
     */
    @NotEmpty(message = "La factura debe tener al menos un item")
    private List<Item> items;

    /**
     * Establecimiento (opcional)
     */
    private Establishment establishment;

    /**
     * Información del cliente
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customer {
        /**
         * ID del tipo de documento de identidad
         * Valores comunes: 3=Cédula, 6=NIT, etc.
         */
        @NotNull(message = "El ID documento de identidad es obligatorio")
        private Integer identification_document_id;

        /**
         * Número de identificación del cliente
         */
        @NotNull(message = "El número documento de identidad es obligatorio")
        private String identification;

        /**
         * Dígito de verificación (requerido para NIT)
         */
        private Integer dv;

        /**
         * Razón social (requerido para personas jurídicas)
         */
        private String company;

        /**
         * Nombre comercial
         */
        private String trade_name;

        /**
         * Nombres (requerido para personas naturales)
         */
        private String names;

        /**
         * Dirección
         */
        private String address;

        /**
         * Correo electrónico
         */
        private String email;

        /**
         * Teléfono
         */
        private String phone;

        /**
         * ID del tipo de organización legal
         * Valores comunes: 1=Persona jurídica, 2=Persona natural
         */
        @NotNull(message = "El ID organización jurídica es obligatorio")
        private Integer legal_organization_id;

        /**
         * ID del tipo de tributo
         * Valores comunes: 21=No aplica, 1=IVA, etc.
         */
        @NotNull(message = "El ID tributo es obligatorio")
        private Integer tribute_id;

        /**
         * ID del municipio
         */
        private Integer municipality_id;
    }

    /**
     * Item de factura
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        /**
         * Código de referencia del producto
         */
        @NotNull(message = "El código de referencia del item es obligatorio")
        private String code_reference;

        /**
         * Nombre del producto
         */
        @NotNull(message = "El nombre del item es obligatorio")
        private String name;

        /**
         * Cantidad (entero)
         */
        @NotNull(message = "La cantidad es obligatoria")
        private Integer quantity;

        /**
         * Porcentaje de descuento (opcional, máximo 2 decimales)
         */
        private Float discount_rate;

        /**
         * Precio unitario con impuestos incluidos (máximo 2 decimales)
         */
        @NotNull(message = "El precio es obligatorio")
        private Float price;

        /**
         * Porcentaje del impuesto (ej: "19.00")
         */
        @NotNull(message = "El porcentaje de IVA es obligatorio")
        private String tax_rate;

        /**
         * ID de la unidad de medida
         * Valores comunes: 70=Unidad, 94=Unidad de servicio, etc.
         */
        @NotNull(message = "El ID unidad de medida es obligatorio")
        private Integer unit_measure_id;

        /**
         * ID del código estándar
         * Valores comunes: 1=Estándar de adopción del contribuyente
         */
        @NotNull(message = "El ID código estándar es obligatorio")
        private Integer standard_code_id;

        /**
         * Indica si está excluido de IVA (0=no, 1=sí)
         */
        @NotNull(message = "El indicador de exclusión es obligatorio")
        private Integer is_excluded;

        /**
         * ID del tipo de tributo
         * Valores comunes: 1=IVA, etc.
         */
        @NotNull(message = "El ID tributo del item es obligatorio")
        private Integer tribute_id;
    }

    /**
     * Información del establecimiento
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Establishment {
        private String name;
        private String address;
        private String phone_number;
        private String email;
        private Integer municipality_id;
    }
}
