package com.factusapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta para estadísticas del Dashboard
 *
 * @author FactusApp
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private BigDecimal totalSales;          // Ventas totales del mes
    private Long invoiceCount;              // Cantidad de facturas emitidas
    private Long productCount;              // Cantidad de productos
    private Long clientCount;               // Cantidad de clientes
    private BigDecimal salesChange;         // Cambio porcentual vs mes anterior
    private Long invoicesLimit;             // Límite de facturas según el plan
    private Long invoicesRemaining;         // Facturas restantes del mes
}
